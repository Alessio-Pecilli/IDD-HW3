package it.uniroma3.idd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Stream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;

// Misura i tempi di indicizzazione al crescere del numero di documenti e il tempo medio delle query.
// Per ogni dimensione: 1 esecuzione di riscaldamento della JVM (scartata) + RIPETIZIONI esecuzioni, si riporta la mediana.
public class Benchmark {

    static final int RIPETIZIONI = 5;
    static final int[] DIMENSIONI = {100, 250, 500, 1000, 1500};

    static long dimensioneSuDisco(Path cartella) throws IOException {
        try (Stream<Path> s = Files.walk(cartella)) {
            return s.filter(Files::isRegularFile).mapToLong(p -> p.toFile().length()).sum();
        }
    }

    static double mediana(double[] v) {
        double[] c = v.clone();
        Arrays.sort(c);
        return c.length % 2 == 1 ? c[c.length / 2] : (c[c.length / 2 - 1] + c[c.length / 2]) / 2;
    }

    // uso: Benchmark [cartellaDocumenti] [query] [output]
    public static void main(String[] args) throws Exception {
        Path documenti = Paths.get(args.length > 0 ? args[0] : "dataset-wiki");
        Path fileQuery = Paths.get(args.length > 1 ? args[1] : "eval/queries.tsv");
        Path output = Paths.get(args.length > 2 ? args[2] : "eval/benchmark.md");

        // sottoinsiemi casuali ma riproducibili (seed fisso) della collezione
        List<Path> tutti = new ArrayList<>(Indexer.fileTxt(documenti.toString()));
        Collections.shuffle(tutti, new Random(42));
        List<Integer> dimensioni = new ArrayList<>();
        for (int d : DIMENSIONI) {
            if (d < tutti.size()) {
                dimensioni.add(d);
            }
        }
        dimensioni.add(tutti.size());

        Analyzer analyzer = Indexer.analyzer();
        Path indice = Files.createTempDirectory("idd-benchmark");
        StringBuilder md = new StringBuilder("# Benchmark\n\n");
        md.append("Java ").append(System.getProperty("java.version")).append(", ")
                .append(Runtime.getRuntime().availableProcessors()).append(" core, mediana di ")
                .append(RIPETIZIONI).append(" esecuzioni dopo un riscaldamento.\n\n");
        md.append("| Documenti | Testo (MB) | Tempo (ms) | Documenti/s | MB/s | Indice (MB) |\n");
        md.append("|---|---|---|---|---|---|\n");

        Indexer.index(tutti.subList(0, Math.min(500, tutti.size())), indice, analyzer); // riscaldamento
        for (int n : dimensioni) {
            List<Path> sotto = tutti.subList(0, n);
            long byteTesto = 0;
            for (Path p : sotto) {
                byteTesto += Files.size(p);
            }
            double[] tempi = new double[RIPETIZIONI];
            for (int r = 0; r < RIPETIZIONI; r++) {
                long inizio = System.nanoTime();
                Indexer.index(sotto, indice, analyzer);
                tempi[r] = (System.nanoTime() - inizio) / 1e6;
            }
            double ms = mediana(tempi);
            double mb = byteTesto / 1e6;
            String riga = String.format(Locale.ROOT, "| %d | %.1f | %.0f | %.0f | %.2f | %.1f |%n",
                    n, mb, ms, n / (ms / 1000), mb / (ms / 1000), dimensioneSuDisco(indice) / 1e6);
            md.append(riga);
            System.out.print(riga);
        }

        // tempo delle query sull'indice completo (l'ultimo costruito): ogni query ripetuta 50 volte
        List<Valutazione.QueryGiudicata> query = Valutazione.leggiQuery(fileQuery);
        try (DirectoryReader reader = DirectoryReader.open(FSDirectory.open(indice))) {
            IndexSearcher searcher = new IndexSearcher(reader);
            List<Query> parsed = new ArrayList<>();
            for (Valutazione.QueryGiudicata q : query) {
                parsed.add(Searcher.parse(q.testo(), analyzer));
            }
            for (Query q : parsed) { // riscaldamento
                searcher.search(q, 10);
            }
            int giri = 50;
            double[] tempi = new double[parsed.size() * giri];
            int i = 0;
            for (int g = 0; g < giri; g++) {
                for (Query q : parsed) {
                    long inizio = System.nanoTime();
                    searcher.search(q, 10);
                    tempi[i++] = (System.nanoTime() - inizio) / 1e6;
                }
            }
            double media = Arrays.stream(tempi).average().orElse(0);
            String riga = String.format(Locale.ROOT,
                    "%nTempo di risposta (top 10, %d query x %d ripetizioni, indice di %d documenti): "
                            + "media %.3f ms, mediana %.3f ms.%n",
                    parsed.size(), giri, reader.numDocs(), media, mediana(tempi));
            md.append(riga);
            System.out.print(riga);
        }
        Files.writeString(output, md);
        System.out.println("Risultati scritti in " + output);
    }
}
