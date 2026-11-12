package it.uniroma3.idd;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

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

    private IndexSearcher searcher;

    public Searcher(IndexSearcher searcher) {
        this.searcher = searcher;
    }

    // trasforma la riga scritta dall'utente in una query di lucene
    public static Query parse(String riga) throws IOException, ParseException {
        // la prima parola è il campo, il resto sono i termini
        String[] parti = riga.trim().split(" ", 2);
        String campo = parti[0].toLowerCase();

        if (!campo.equals(Indexer.NOME) && !campo.equals(Indexer.CONTENUTO)) {
            throw new IllegalArgumentException("la query deve iniziare con 'nome' o 'contenuto'");
        }
        if (parti.length < 2) {
            throw new IllegalArgumentException("mancano i termini da cercare");
        }

        // uso il QueryParser di lucene che gestisce già le virgolette e AND/OR
        QueryParser parser = new QueryParser(campo, Indexer.analyzer());
        return parser.parse(parti[1]);
    }

    public TopDocs search(String riga, int k) throws IOException, ParseException {
        return searcher.search(parse(riga), k);
    }

    public static void main(String[] args) throws IOException {
        DirectoryReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("index")));
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        Searcher s = new Searcher(indexSearcher);
        Scanner scanner = new Scanner(System.in);

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
                // stampo i primi 10 risultati
                TopDocs risultati = s.search(riga, 10);
                System.out.println("Query Lucene: " + parse(riga));
                System.out.println("Risultati trovati: " + risultati.totalHits.value());
                for (ScoreDoc sd : risultati.scoreDocs) {
                    Document doc = indexSearcher.storedFields().document(sd.doc);
                    System.out.println("  " + sd.score + "  " + doc.get(Indexer.PATH));
                }
            } catch (IllegalArgumentException | ParseException e) {
                System.out.println("Errore: " + e.getMessage());
            }
        }

        scanner.close();
        reader.close();
    }
}
