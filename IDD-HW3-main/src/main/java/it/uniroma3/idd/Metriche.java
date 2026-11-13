package it.uniroma3.idd;

import java.util.List;
import java.util.Set;

// Metriche di valutazione con rilevanza binaria.
// ranking = documenti restituiti dal sistema in ordine di score, rilevanti = ground truth della query
public final class Metriche {

    private Metriche() {
    }

    // numero di documenti rilevanti tra i primi k del ranking
    static int rilevantiTraIPrimi(List<String> ranking, Set<String> rilevanti, int k) {
        int n = 0;
        for (int i = 0; i < Math.min(k, ranking.size()); i++) {
            if (rilevanti.contains(ranking.get(i))) {
                n++;
            }
        }
        return n;
    }

    // P@k: frazione dei primi k risultati che sono rilevanti (se ne escono meno di k conto i mancanti come errori)
    public static double precisionAt(List<String> ranking, Set<String> rilevanti, int k) {
        return (double) rilevantiTraIPrimi(ranking, rilevanti, k) / k;
    }

    // R@k: frazione dei rilevanti che compare nei primi k risultati
    public static double recallAt(List<String> ranking, Set<String> rilevanti, int k) {
        if (rilevanti.isEmpty()) {
            return 0;
        }
        return (double) rilevantiTraIPrimi(ranking, rilevanti, k) / rilevanti.size();
    }

    // precision sull'intero insieme restituito
    public static double precision(List<String> ranking, Set<String> rilevanti) {
        if (ranking.isEmpty()) {
            return 0;
        }
        return (double) rilevantiTraIPrimi(ranking, rilevanti, ranking.size()) / ranking.size();
    }

    // recall sull'intero insieme restituito
    public static double recall(List<String> ranking, Set<String> rilevanti) {
        return recallAt(ranking, rilevanti, ranking.size());
    }

    public static double f1(List<String> ranking, Set<String> rilevanti) {
        double p = precision(ranking, rilevanti);
        double r = recall(ranking, rilevanti);
        return p + r == 0 ? 0 : 2 * p * r / (p + r);
    }

    // R-Precision: P@R con R = numero di rilevanti (in quel punto precision = recall)
    public static double rPrecision(List<String> ranking, Set<String> rilevanti) {
        if (rilevanti.isEmpty()) {
            return 0;
        }
        return precisionAt(ranking, rilevanti, rilevanti.size());
    }

    // Average Precision: media delle P@k nelle posizioni dei rilevanti trovati, divisa per tutti i rilevanti
    // (i rilevanti non trovati contribuiscono con 0). La media sulle query è la MAP.
    public static double averagePrecision(List<String> ranking, Set<String> rilevanti) {
        if (rilevanti.isEmpty()) {
            return 0;
        }
        double somma = 0;
        int trovati = 0;
        for (int i = 0; i < ranking.size(); i++) {
            if (rilevanti.contains(ranking.get(i))) {
                trovati++;
                somma += (double) trovati / (i + 1);
            }
        }
        return somma / rilevanti.size();
    }

    // Reciprocal Rank: 1 / posizione del primo rilevante (0 se non ce ne sono). La media è la MRR.
    public static double reciprocalRank(List<String> ranking, Set<String> rilevanti) {
        for (int i = 0; i < ranking.size(); i++) {
            if (rilevanti.contains(ranking.get(i))) {
                return 1.0 / (i + 1);
            }
        }
        return 0;
    }

    // nDCG@k con guadagno binario: DCG = somma 1/log2(i+1) sulle posizioni rilevanti,
    // normalizzato con il DCG del ranking ideale (tutti i rilevanti in testa)
    public static double ndcgAt(List<String> ranking, Set<String> rilevanti, int k) {
        double dcg = 0;
        for (int i = 0; i < Math.min(k, ranking.size()); i++) {
            if (rilevanti.contains(ranking.get(i))) {
                dcg += 1 / log2(i + 2);
            }
        }
        double ideale = 0;
        for (int i = 0; i < Math.min(k, rilevanti.size()); i++) {
            ideale += 1 / log2(i + 2);
        }
        return ideale == 0 ? 0 : dcg / ideale;
    }

    private static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }
}
