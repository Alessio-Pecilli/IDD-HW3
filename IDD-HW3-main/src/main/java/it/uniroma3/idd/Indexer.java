package it.uniroma3.idd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

// Classe che crea l'indice: prende i file .txt dalla cartella e li indicizza
// con due campi, il nome del file e il contenuto
public class Indexer {

    public static final String NOME = "nome";
    public static final String CONTENUTO = "contenuto";
    public static final String PATH = "path";

    // restituisce l'analyzer, lo uso sia nell'Indexer che nel Searcher così sono uguali
    public static Analyzer analyzer() throws IOException {
        // per il contenuto uso l'ItalianAnalyzer perché i testi sono in italiano
        // (toglie le stopword, gli apostrofi tipo l' e fa lo stemming)
        return analyzer(new ItalianAnalyzer());
    }

    // stesso analyzer per il nome, ma con un analyzer diverso per il contenuto:
    // serve alla classe Valutazione per confrontare più configurazioni
    public static Analyzer analyzer(Analyzer analyzerContenuto) throws IOException {
        // per il nome uso un analyzer fatto da me: divido sugli spazi, metto tutto minuscolo
        // e poi divido anche su _ - . e parentesi (i file si chiamano tipo storia_di_roma)
        Analyzer analyzerNome = CustomAnalyzer.builder()
                .withTokenizer(WhitespaceTokenizerFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(WordDelimiterGraphFilterFactory.class)
                .build();

        Map<String, Analyzer> perCampo = new HashMap<>();
        perCampo.put(NOME, analyzerNome);
        return new PerFieldAnalyzerWrapper(analyzerContenuto, perCampo);
    }

    // restituisce i file .txt della cartella, in ordine alfabetico
    public static List<Path> fileTxt(String cartellaDocumenti) throws IOException {
        try (Stream<Path> s = Files.list(Paths.get(cartellaDocumenti))) {
            return s.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".txt"))
                    .sorted()
                    .toList();
        }
    }

    // indicizza i file .txt della cartella e restituisce quanti ne ha indicizzati
    public static int index(String cartellaDocumenti, String cartellaIndice) throws IOException {
        return index(fileTxt(cartellaDocumenti), Paths.get(cartellaIndice), analyzer());
    }

    public static int index(List<Path> files, Path cartellaIndice, Analyzer analyzer) throws IOException {
        try (Directory dir = FSDirectory.open(cartellaIndice);
             IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(analyzer))) {

            // cancello l'indice vecchio, altrimenti ogni volta che lo lancio i documenti si duplicano
            writer.deleteAll();

            for (Path f : files) {
                String nomeFile = f.getFileName().toString();
                String contenuto = Files.readString(f);

                Document doc = new Document();
                doc.add(new StringField(PATH, f.toString(), Field.Store.YES));
                // tolgo il .txt dal nome (solo in fondo)
                doc.add(new TextField(NOME, nomeFile.substring(0, nomeFile.length() - 4), Field.Store.YES));
                doc.add(new TextField(CONTENUTO, contenuto, Field.Store.NO));
                writer.addDocument(doc);
            }
            writer.commit();
            return files.size();
        }
    }

    // uso: Indexer [cartellaDocumenti] [cartellaIndice]   (default: dataset index)
    public static void main(String[] args) throws IOException {
        String documenti = args.length > 0 ? args[0] : "dataset";
        String indice = args.length > 1 ? args[1] : "index";

        // misuro il tempo per metterlo nella relazione
        long inizio = System.nanoTime();
        int n = index(documenti, indice);
        long fine = System.nanoTime();

        System.out.println("File indicizzati: " + n);
        System.out.println("Tempo di indicizzazione: " + (fine - inizio) / 1_000_000 + " ms");
    }
}
