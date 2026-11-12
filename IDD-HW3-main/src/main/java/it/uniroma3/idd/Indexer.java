package it.uniroma3.idd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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
        // per il nome uso un analyzer fatto da me: divido sugli spazi, metto tutto minuscolo
        // e poi divido anche su _ e - (i file si chiamano tipo storia_di_roma)
        Analyzer analyzerNome = CustomAnalyzer.builder()
                .withTokenizer(WhitespaceTokenizerFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(WordDelimiterGraphFilterFactory.class)
                .build();

        Map<String, Analyzer> perCampo = new HashMap<>();
        perCampo.put(NOME, analyzerNome);

        // per il contenuto uso l'ItalianAnalyzer perché i testi sono in italiano
        // (toglie le stopword, gli apostrofi tipo l' e fa lo stemming)
        return new PerFieldAnalyzerWrapper(new ItalianAnalyzer(), perCampo);
    }

    // indicizza i file .txt della cartella e restituisce quanti ne ha indicizzati
    public static int index(String cartellaDocumenti, String cartellaIndice) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(cartellaIndice));
        IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(analyzer()));

        // cancello l'indice vecchio, altrimenti ogni volta che lo lancio i documenti si duplicano
        writer.deleteAll();

        int contatore = 0;
        File[] files = new File(cartellaDocumenti).listFiles();
        for (File f : files) {
            String nomeFile = f.getName();
            if (!nomeFile.endsWith(".txt")) {
                continue;
            }
            String contenuto = Files.readString(f.toPath());

            Document doc = new Document();
            doc.add(new StringField(PATH, f.getPath(), Field.Store.YES));
            // tolgo il .txt dal nome
            doc.add(new TextField(NOME, nomeFile.replace(".txt", ""), Field.Store.YES));
            doc.add(new TextField(CONTENUTO, contenuto, Field.Store.NO));
            writer.addDocument(doc);
            contatore++;
        }

        writer.commit();
        writer.close();
        dir.close();
        return contatore;
    }

    public static void main(String[] args) throws IOException {
        // misuro il tempo per metterlo nella relazione
        long inizio = System.currentTimeMillis();
        int n = index("dataset", "index");
        long fine = System.currentTimeMillis();

        System.out.println("File indicizzati: " + n);
        System.out.println("Tempo di indicizzazione: " + (fine - inizio) + " ms");
    }
}
