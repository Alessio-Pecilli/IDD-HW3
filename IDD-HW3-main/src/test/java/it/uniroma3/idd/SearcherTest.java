package it.uniroma3.idd;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

// controllo che le query della relazione diano i risultati giusti
class SearcherTest {

    @TempDir
    static Path cartellaIndice;
    static DirectoryReader reader;
    static Searcher searcher;

    // prima dei test creo l'indice in una cartella temporanea
    @BeforeAll
    static void setUp() throws Exception {
        assertEquals(12, Indexer.index("dataset", cartellaIndice.toString()));
        reader = DirectoryReader.open(FSDirectory.open(cartellaIndice));
        searcher = new Searcher(new IndexSearcher(reader));
    }

    @AfterAll
    static void tearDown() throws Exception {
        reader.close();
    }

    // restituisce i nomi dei file trovati dalla query
    private List<String> nomi(String query) throws Exception {
        TopDocs risultati = searcher.search(query, 20);
        List<String> nomi = new ArrayList<>();
        for (ScoreDoc sd : risultati.scoreDocs) {
            nomi.add(reader.storedFields().document(sd.doc).get(Indexer.NOME));
        }
        return nomi;
    }

    @Test
    void nomeSingoloTermine() throws Exception {
        assertEquals(List.of("storia_di_roma"), nomi("nome roma"));
    }

    @Test
    void nomeConTrattino() throws Exception {
        assertEquals(List.of("impero-romano"), nomi("nome romano"));
    }

    @Test
    void nomeMaiuscoleESpazi() throws Exception {
        assertEquals(List.of("Ricetta Carbonara"), nomi("nome carbonara"));
    }

    @Test
    void nomePhrase() throws Exception {
        assertEquals(List.of("motori-di-ricerca"), nomi("nome \"motori di ricerca\""));
    }

    @Test
    void nomePhraseOrdineSbagliato() throws Exception {
        assertTrue(nomi("nome \"ricerca motori\"").isEmpty());
    }

    @Test
    void contenutoStemming() throws Exception {
        assertTrue(nomi("contenuto pianeti").contains("sistema_solare"));
    }

    @Test
    void contenutoPhrase() throws Exception {
        assertEquals(List.of("divina_commedia"), nomi("contenuto \"selva oscura\""));
    }

    @Test
    void contenutoElisione() throws Exception {
        assertTrue(nomi("contenuto impero").contains("impero-romano"));
    }

    @Test
    void contenutoBooleana() throws Exception {
        assertEquals(List.of("Ricetta Carbonara"), nomi("contenuto +guanciale +pecorino"));
    }

    @Test
    void contenutoSoloStopword() throws Exception {
        assertTrue(nomi("contenuto il di la").isEmpty());
    }

    @Test
    void campoNonValido() {
        assertThrows(IllegalArgumentException.class, () -> Searcher.parse("titolo roma"));
    }
}
