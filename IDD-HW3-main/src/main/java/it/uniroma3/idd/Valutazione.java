package it.uniroma3.idd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.ElisionFilter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

// Valutazione dell'efficacia: per ogni query del file eval/queries.tsv calcola le metriche
// confrontando il ranking con i documenti rilevanti (ground truth ricavata dal manifest del dataset).
// Ripete la valutazione con tre analyzer diversi per il campo contenuto, così si vede l'effetto delle scelte.
public class Valutazione {

    // una query di valutazione: id, codici degli argomenti rilevanti, testo della query (con il campo)
    public record QueryGiudicata(String id, Set<String> argomenti, String testo) {
    }

    // risultato di una query: le metriche, nello stesso ordine di INTESTAZIONE
    // primi10 serve per controllare a mano i risultati non giudicati (analisi della recall in relazione)
    public record Risultato(QueryGiudicata query, int rilevanti, int restituiti, double[] valori,
            List<String> primi10) {
    }

    static final String[] INTESTAZIONE = {"P@10", "R@100", "Recall", "R-Prec", "AP", "RR", "nDCG@10"};

    // file del manifest: file \t pageid \t revid \t chars \t topics (codici separati da virgola)
    public static Map<String, Set<String>> leggiManifest(Path manifest) throws IOException {
        Map<String, Set<String>> argomenti = new HashMap<>();
        List<String> righe = Files.readAllLines(manifest);
        for (String riga : righe.subList(1, righe.size())) {
            String[] c = riga.split("\t", -1);
            String nome = c[0].substring(0, c[0].length() - 4); // tolgo .txt come nell'indice
            argomenti.put(nome, insieme(c[4]));
        }
        return argomenti;
    }

    // file delle query: id \t argomenti \t query; righe vuote e che iniziano con # ignorate
    public static List<QueryGiudicata> leggiQuery(Path file) throws IOException {
        List<QueryGiudicata> query = new ArrayList<>();
        for (String riga : Files.readAllLines(file)) {
            if (riga.isBlank() || riga.startsWith("#")) {
                continue;
            }
            String[] c = riga.split("\t");
            query.add(new QueryGiudicata(c[0], insieme(c[1]), c[2]));
        }
        return query;
    }

    private static Set<String> insieme(String codici) {
        Set<String> s = new HashSet<>();
        for (String c : codici.split(",")) {
            if (!c.isBlank()) {
                s.add(c.trim());
            }
        }
        return s;
    }

    // ground truth: documenti che hanno almeno uno degli argomenti della query
    public static Set<String> rilevanti(QueryGiudicata q, Map<String, Set<String>> manifest) {
        Set<String> r = new HashSet<>();
        manifest.forEach((doc, arg) -> {
            if (!java.util.Collections.disjoint(arg, q.argomenti())) {
                r.add(doc);
            }
        });
        return r;
    }

    public static Risultato valuta(QueryGiudicata q, List<String> ranking, Set<String> rilevanti) {
        double[] v = {
                Metriche.precisionAt(ranking, rilevanti, 10),
                Metriche.recallAt(ranking, rilevanti, 100),
                Metriche.recall(ranking, rilevanti),
                Metriche.rPrecision(ranking, rilevanti),
                Metriche.averagePrecision(ranking, rilevanti),
                Metriche.reciprocalRank(ranking, rilevanti),
                Metriche.ndcgAt(ranking, rilevanti, 10),
        };
        return new Risultato(q, rilevanti.size(), ranking.size(), v,
                List.copyOf(ranking.subList(0, Math.min(10, ranking.size()))));
    }

    // le tre configurazioni confrontate per il campo contenuto
    public static Map<String, Analyzer> configurazioni() throws IOException {
        Map<String, Analyzer> conf = new LinkedHashMap<>();
        conf.put("StandardAnalyzer", new StandardAnalyzer());
        // come ItalianAnalyzer ma senza stemmer: serve a isolare l'effetto dello stemming
        conf.put("Italiano senza stemming", new ItalianoSenzaStemming());
        conf.put("ItalianAnalyzer (scelto)", new ItalianAnalyzer());
        return conf;
    }

    // stessa catena di ItalianAnalyzer (tokenizer, elisione, minuscolo, stopword) ma senza ItalianLightStemFilter
    public static class ItalianoSenzaStemming extends Analyzer {
        // articoli elisi, gli stessi di ItalianAnalyzer (l', dell', all', un', ...)
        static final CharArraySet ARTICOLI = new CharArraySet(List.of("c", "l", "all", "dall", "dell", "nell",
                "sull", "coll", "pell", "gl", "agl", "dagl", "degl", "negl", "sugl", "un", "m", "t", "s", "v", "d"),
                true);

        @Override
        protected TokenStreamComponents createComponents(String campo) {
            Tokenizer sorgente = new StandardTokenizer();
            TokenStream ts = new ElisionFilter(sorgente, ARTICOLI);
            ts = new LowerCaseFilter(ts);
            ts = new StopFilter(ts, ItalianAnalyzer.getDefaultStopSet());
            return new TokenStreamComponents(sorgente, ts);
        }

        @Override
        protected TokenStream normalize(String campo, TokenStream in) {
            return new LowerCaseFilter(in);
        }
    }

    // costruisce l'indice con l'analyzer dato e valuta tutte le query; restituisce anche il tempo di indicizzazione
    public static List<Risultato> esegui(List<Path> documenti, Path indice, Analyzer contenuto,
            List<QueryGiudicata> query, Map<String, Set<String>> manifest) throws Exception {
        Analyzer analyzer = Indexer.analyzer(contenuto);
        Indexer.index(documenti, indice, analyzer);
        List<Risultato> risultati = new ArrayList<>();
        try (DirectoryReader reader = DirectoryReader.open(FSDirectory.open(indice))) {
            Searcher s = new Searcher(new IndexSearcher(reader), analyzer);
            for (QueryGiudicata q : query) {
                // tutto il ranking (non solo i primi 10), altrimenti la recall non si può calcolare
                List<String> ranking = s.ranking(q.testo(), Math.max(1, reader.numDocs()));
                risultati.add(valuta(q, ranking, rilevanti(q, manifest)));
            }
        }
        return risultati;
    }

    public static double[] medie(List<Risultato> risultati) {
        double[] m = new double[INTESTAZIONE.length];
        for (Risultato r : risultati) {
            for (int i = 0; i < m.length; i++) {
                m[i] += r.valori()[i] / risultati.size();
            }
        }
        return m;
    }

    private static String riga(String... celle) {
        return "| " + String.join(" | ", celle) + " |\n";
    }

    private static String[] formatta(double[] v) {
        return Arrays.stream(v).mapToObj(x -> String.format(Locale.ROOT, "%.3f", x)).toArray(String[]::new);
    }

    // uso: Valutazione [cartellaDocumenti] [manifest] [query] [output]
    public static void main(String[] args) throws Exception {
        Path documenti = Paths.get(args.length > 0 ? args[0] : "dataset-wiki");
        Path fileManifest = Paths.get(args.length > 1 ? args[1] : "eval/manifest.tsv");
        Path fileQuery = Paths.get(args.length > 2 ? args[2] : "eval/queries.tsv");
        Path output = Paths.get(args.length > 3 ? args[3] : "eval/risultati.md");

        Map<String, Set<String>> manifest = leggiManifest(fileManifest);
        List<QueryGiudicata> query = leggiQuery(fileQuery);
        List<Path> files = Indexer.fileTxt(documenti.toString());

        StringBuilder sommario = new StringBuilder();
        sommario.append(riga(concat("Analyzer contenuto", "MAP", INTESTAZIONE)));
        sommario.append(riga(new String[INTESTAZIONE.length + 2]).replace("null", "---"));
        StringBuilder dettaglio = new StringBuilder();
        List<Risultato> ultimi = List.of();

        for (Map.Entry<String, Analyzer> conf : configurazioni().entrySet()) {
            Path indice = Files.createTempDirectory("idd-valutazione");
            List<Risultato> risultati = esegui(files, indice, conf.getValue(), query, manifest);
            double[] m = medie(risultati);
            // la MAP è la media delle AP: la ripeto in seconda colonna per leggibilità
            String[] celle = formatta(m);
            sommario.append(riga(concat(conf.getKey(), celle[4], celle)));

            dettaglio.append("\n### ").append(conf.getKey()).append("\n\n");
            dettaglio.append(riga(concat("Id", "Query", concat("Rilevanti", "Restituiti", INTESTAZIONE))));
            dettaglio.append(riga(new String[INTESTAZIONE.length + 4]).replace("null", "---"));
            for (Risultato r : risultati) {
                dettaglio.append(riga(concat(r.query().id(), "`" + r.query().testo() + "`",
                        concat(String.valueOf(r.rilevanti()), String.valueOf(r.restituiti()), formatta(r.valori())))));
            }
            dettaglio.append(riga(concat("", "**media**", concat("", "", formatta(m)))));
            ultimi = risultati;
            System.out.printf("%-28s MAP=%.3f P@10=%.3f Recall=%.3f%n", conf.getKey(), m[4], m[0], m[2]);
        }

        String testo = "# Risultati della valutazione\n\n"
                + files.size() + " documenti, " + query.size() + " query.\n\n"
                + "## Medie\n\n" + sommario + "\n## Dettaglio per query\n" + dettaglio;
        Files.writeString(output, testo);

        // primi 10 risultati della configurazione scelta (l'ultima), con il giudizio derivato dal manifest
        StringBuilder primi = new StringBuilder("query\trank\tdocumento\tgiudicato_rilevante\n");
        for (Risultato r : ultimi) {
            Set<String> ril = rilevanti(r.query(), manifest);
            for (int i = 0; i < r.primi10().size(); i++) {
                String doc = r.primi10().get(i);
                primi.append(r.query().id()).append('\t').append(i + 1).append('\t').append(doc).append('\t')
                        .append(ril.contains(doc) ? 1 : 0).append('\n');
            }
        }
        Files.writeString(output.resolveSibling("top10.tsv"), primi);
        System.out.println("Risultati scritti in " + output);
    }

    private static String[] concat(String a, String b, String[] resto) {
        String[] r = new String[resto.length + 2];
        r[0] = a;
        r[1] = b;
        System.arraycopy(resto, 0, r, 2, resto.length);
        return r;
    }
}
