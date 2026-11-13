package it.uniroma3.idd;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

// Programma per fare le query da console.
// Si scrive "nome" o "contenuto" e poi le parole da cercare (tra virgolette se è una frase)
public class Searcher {

    private final IndexSearcher searcher;
    private final Analyzer analyzer;

    public Searcher(IndexSearcher searcher) throws IOException {
        this(searcher, Indexer.analyzer());
    }

    // l'analyzer deve essere lo stesso usato per costruire l'indice
    public Searcher(IndexSearcher searcher, Analyzer analyzer) {
        this.searcher = searcher;
        this.analyzer = analyzer;
    }

    // trasforma la riga scritta dall'utente in una query di lucene
    public static Query parse(String riga) throws IOException, ParseException {
        return parse(riga, Indexer.analyzer());
    }

    public static Query parse(String riga, Analyzer analyzer) throws ParseException {
        // la prima parola è il campo, il resto sono i termini
        String[] parti = riga.trim().split("\\s+", 2);
        String campo = parti[0].toLowerCase();

        if (!campo.equals(Indexer.NOME) && !campo.equals(Indexer.CONTENUTO)) {
            throw new IllegalArgumentException("la query deve iniziare con 'nome' o 'contenuto'");
        }
        if (parti.length < 2 || parti[1].isBlank()) {
            throw new IllegalArgumentException("mancano i termini da cercare");
        }

        // uso il QueryParser di lucene che gestisce già le virgolette e AND/OR
        QueryParser parser = new QueryParser(campo, analyzer);
        return parser.parse(parti[1]);
    }

    public TopDocs search(String riga, int k) throws IOException, ParseException {
        return searcher.search(parse(riga, analyzer), k);
    }

    // restituisce i nomi dei file trovati (senza .txt), nell'ordine del ranking
    public List<String> ranking(String riga, int k) throws IOException, ParseException {
        List<String> nomi = new ArrayList<>();
        for (ScoreDoc sd : search(riga, k).scoreDocs) {
            nomi.add(searcher.storedFields().document(sd.doc).get(Indexer.NOME));
        }
        return nomi;
    }

    // uso: Searcher [cartellaIndice]   (default: index)
    public static void main(String[] args) throws IOException {
        String indice = args.length > 0 ? args[0] : "index";
        DirectoryReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indice)));
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        Searcher s = new Searcher(indexSearcher);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Indice: " + indice + " (" + reader.numDocs() + " documenti)");
        System.out.println("Scrivi una query (es: nome roma oppure contenuto \"impero romano\"), exit per uscire");
        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String riga = scanner.nextLine();
            if (riga.equals("exit")) {
                break;
            }
            if (riga.isBlank()) {
                continue;
            }

            try {
                // stampo i primi 10 risultati e il tempo della query
                long inizio = System.nanoTime();
                TopDocs risultati = s.search(riga, 10);
                double ms = (System.nanoTime() - inizio) / 1e6;
                System.out.println("Query Lucene: " + parse(riga));
                System.out.printf("Risultati trovati: %d (%.2f ms)%n", risultati.totalHits.value(), ms);
                for (ScoreDoc sd : risultati.scoreDocs) {
                    Document doc = indexSearcher.storedFields().document(sd.doc);
                    System.out.printf("  %7.3f  %s%n", sd.score, doc.get(Indexer.PATH));
                }
            } catch (IllegalArgumentException | ParseException e) {
                System.out.println("Errore: " + e.getMessage());
            }
        }

        scanner.close();
        reader.close();
    }
}
