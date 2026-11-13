package it.uniroma3.idd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

// controllo le metriche su un esempio calcolato a mano:
// ranking = a x b y c   (rilevanti in posizione 1, 3, 5), rilevanti = {a, b, c, d}  (d non viene trovato)
class MetricheTest {

    static final double EPS = 1e-6;
    static final List<String> RANKING = List.of("a", "x", "b", "y", "c");
    static final Set<String> RILEVANTI = Set.of("a", "b", "c", "d");

    @Test
    void precisionAtK() {
        assertEquals(1.0, Metriche.precisionAt(RANKING, RILEVANTI, 1), EPS);
        assertEquals(2.0 / 3, Metriche.precisionAt(RANKING, RILEVANTI, 3), EPS);
        assertEquals(3.0 / 5, Metriche.precisionAt(RANKING, RILEVANTI, 5), EPS);
        // con meno di k risultati le posizioni vuote contano come non rilevanti
        assertEquals(3.0 / 10, Metriche.precisionAt(RANKING, RILEVANTI, 10), EPS);
    }

    @Test
    void recallAtK() {
        assertEquals(1.0 / 4, Metriche.recallAt(RANKING, RILEVANTI, 1), EPS);
        assertEquals(2.0 / 4, Metriche.recallAt(RANKING, RILEVANTI, 3), EPS);
        assertEquals(3.0 / 4, Metriche.recallAt(RANKING, RILEVANTI, 100), EPS);
    }

    @Test
    void precisionRecallF1SullInsieme() {
        assertEquals(3.0 / 5, Metriche.precision(RANKING, RILEVANTI), EPS);
        assertEquals(3.0 / 4, Metriche.recall(RANKING, RILEVANTI), EPS);
        assertEquals(2 * 0.6 * 0.75 / (0.6 + 0.75), Metriche.f1(RANKING, RILEVANTI), EPS);
    }

    @Test
    void rPrecision() {
        // R = 4: nei primi 4 ci sono a e b
        assertEquals(2.0 / 4, Metriche.rPrecision(RANKING, RILEVANTI), EPS);
    }

    @Test
    void averagePrecision() {
        // (1/1 + 2/3 + 3/5) / 4: d non trovato conta 0 ma resta al denominatore
        assertEquals((1 + 2.0 / 3 + 3.0 / 5) / 4, Metriche.averagePrecision(RANKING, RILEVANTI), EPS);
    }

    @Test
    void reciprocalRank() {
        assertEquals(1.0, Metriche.reciprocalRank(RANKING, RILEVANTI), EPS);
        assertEquals(1.0 / 3, Metriche.reciprocalRank(List.of("x", "y", "c"), RILEVANTI), EPS);
        assertEquals(0.0, Metriche.reciprocalRank(List.of("x", "y"), RILEVANTI), EPS);
    }

    @Test
    void ndcg() {
        double log2 = Math.log(2);
        double dcg = 1 + 1 / (Math.log(4) / log2) + 1 / (Math.log(6) / log2);
        double ideale = 1 + 1 / (Math.log(3) / log2) + 1 / (Math.log(4) / log2) + 1 / (Math.log(5) / log2);
        assertEquals(dcg / ideale, Metriche.ndcgAt(RANKING, RILEVANTI, 5), EPS);
        assertEquals(1.0, Metriche.ndcgAt(RANKING, RILEVANTI, 1), EPS);
    }

    @Test
    void rankingPerfetto() {
        List<String> perfetto = List.of("a", "b", "c", "d");
        assertEquals(1.0, Metriche.averagePrecision(perfetto, RILEVANTI), EPS);
        assertEquals(1.0, Metriche.rPrecision(perfetto, RILEVANTI), EPS);
        assertEquals(1.0, Metriche.ndcgAt(perfetto, RILEVANTI, 10), EPS);
        assertEquals(1.0, Metriche.f1(perfetto, RILEVANTI), EPS);
    }

    @Test
    void casiLimite() {
        List<String> vuoto = List.of();
        assertEquals(0.0, Metriche.precision(vuoto, RILEVANTI), EPS);
        assertEquals(0.0, Metriche.recall(vuoto, RILEVANTI), EPS);
        assertEquals(0.0, Metriche.averagePrecision(vuoto, RILEVANTI), EPS);
        assertEquals(0.0, Metriche.f1(vuoto, RILEVANTI), EPS);
        // nessun documento rilevante: le metriche valgono 0 invece di dividere per zero
        assertEquals(0.0, Metriche.recall(RANKING, Set.of()), EPS);
        assertEquals(0.0, Metriche.averagePrecision(RANKING, Set.of()), EPS);
        assertEquals(0.0, Metriche.ndcgAt(RANKING, Set.of(), 10), EPS);
    }
}
