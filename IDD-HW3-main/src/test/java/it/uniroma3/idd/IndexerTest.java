package it.uniroma3.idd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.FSDirectory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

// test sull'indicizzazione e sui token prodotti dagli analyzer
class IndexerTest {

    // token prodotti dall'analyzer per un certo campo
    static List<String> token(String campo, String testo) throws IOException {
        List<String> token = new ArrayList<>();
        Analyzer analyzer = Indexer.analyzer();
        try (TokenStream ts = analyzer.tokenStream(campo, new StringReader(testo))) {
            CharTermAttribute termine = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                token.add(termine.toString());
            }
            ts.end();
        }
        return token;
    }

    @Test
    void nomeSpezzatoSuSeparatori() throws Exception {
        assertEquals(List.of("storia", "di", "roma"), token(Indexer.NOME, "storia_di_roma"));
        assertEquals(List.of("motori", "di", "ricerca"), token(Indexer.NOME, "motori-di-ricerca"));
        assertEquals(List.of("ricetta", "carbonara"), token(Indexer.NOME, "Ricetta Carbonara"));
    }

    @Test
    void nomeConParentesiComeInWikipedia() throws Exception {
        // i titoli di Wikipedia hanno spesso un disambiguatore tra parentesi
        assertEquals(List.of("io", "astronomia"), token(Indexer.NOME, "Io (astronomia)"));
    }

    @Test
    void nomeSenzaStemmingNeStopword() throws Exception {
        assertEquals(List.of("satelliti", "di", "giove"), token(Indexer.NOME, "Satelliti di Giove"));
    }

    @Test
    void contenutoStopwordElisioneStemming() throws Exception {
        assertEquals(List.of("imper", "roman"), token(Indexer.CONTENUTO, "L'Impero Romano"));
        assertEquals(List.of("pianet", "pianet"), token(Indexer.CONTENUTO, "il pianeta e i pianeti"));
        assertEquals(List.of("informazion"), token(Indexer.CONTENUTO, "dell'informazione"));
    }

    @Test
    void ignoraFileNonTxtESottocartelle(@TempDir Path docs, @TempDir Path indice) throws Exception {
        Files.writeString(docs.resolve("uno.txt"), "testo uno");
        Files.writeString(docs.resolve("due.txt"), "testo due");
        Files.writeString(docs.resolve("note.md"), "non va indicizzato");
        Files.createDirectory(docs.resolve("sotto.txt")); // una cartella che finisce per .txt
        assertEquals(2, Indexer.index(docs.toString(), indice.toString()));
    }

    @Test
    void reindicizzareNonDuplica(@TempDir Path docs, @TempDir Path indice) throws Exception {
        Files.writeString(docs.resolve("a.txt"), "alfa");
        Files.writeString(docs.resolve("b.txt"), "beta");
        Indexer.index(docs.toString(), indice.toString());
        Indexer.index(docs.toString(), indice.toString());
        try (DirectoryReader reader = DirectoryReader.open(FSDirectory.open(indice))) {
            assertEquals(2, reader.numDocs());
        }
    }

    @Test
    void estensioneTolta(@TempDir Path docs, @TempDir Path indice) throws Exception {
        // solo il .txt finale viene tolto, non eventuali ".txt" in mezzo al nome
        Files.writeString(docs.resolve("file.txt.backup.txt"), "x");
        Indexer.index(docs.toString(), indice.toString());
        try (DirectoryReader reader = DirectoryReader.open(FSDirectory.open(indice))) {
            assertEquals("file.txt.backup", reader.storedFields().document(0).get(Indexer.NOME));
        }
    }
}
