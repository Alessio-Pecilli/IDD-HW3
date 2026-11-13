package it.uniroma3.idd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

// valutazione end-to-end sul dataset piccolo, con una ground truth scritta a mano
// (src/test/resources/manifest-piccolo.tsv): ROM = Roma antica, CUC = cucina, IR = informatica/ricerca
class ValutazioneTest {

    static final double EPS = 1e-6;
    static Map<String, Set<String>> manifest;
    static List<Valutazione.QueryGiudicata> query;

    @BeforeAll
    static void carica() throws Exception {
        manifest = Valutazione.leggiManifest(Path.of("src/test/resources/manifest-piccolo.tsv"));
        query = Valutazione.leggiQuery(Path.of("src/test/resources/query-piccolo.tsv"));
    }

    @Test
    void letturaFile() {
        assertEquals(12, manifest.size());
        assertEquals(Set.of("CUC"), manifest.get("Ricetta Carbonara"));
        assertEquals(Set.of(), manifest.get("calcio_serie_a"));
        assertEquals(3, query.size()); // la riga di commento è ignorata
        assertEquals(Set.of("IR", "CUC"), query.get(2).argomenti());
    }

    @Test
    void groundTruthUnioneDegliArgomenti() {
        assertEquals(Set.of("impero-romano", "storia_di_roma"), Valutazione.rilevanti(query.get(0), manifest));
        assertEquals(5, Valutazione.rilevanti(query.get(2), manifest).size());
    }

    @Test
    void metricheEndToEnd(@TempDir Path indice) throws Exception {
        List<Valutazione.Risultato> r = Valutazione.esegui(Indexer.fileTxt("dataset"), indice,
                new ItalianAnalyzer(), query, manifest);
        // indici: 0 P@10, 1 R@100, 2 Recall, 3 R-Prec, 4 AP, 5 RR, 6 nDCG@10

        // q1 "impero romano": i due rilevanti in testa, poi la carbonara (pecorino romano)
        double[] q1 = r.get(0).valori();
        assertEquals(3, r.get(0).restituiti());
        assertEquals(2.0 / 10, q1[0], EPS);
        assertEquals(1.0, q1[2], EPS);
        assertEquals(1.0, q1[4], EPS);
        assertEquals(1.0, q1[6], EPS);

        // q3 "ricerca": trova solo motori-di-ricerca, ma i rilevanti sono 5 -> recall bassa, precision alta
        double[] q3 = r.get(2).valori();
        assertEquals(1, r.get(2).restituiti());
        assertEquals(1.0 / 5, q3[2], EPS);
        assertEquals(1.0 / 5, q3[4], EPS);
        assertEquals(1.0, q3[5], EPS);
    }

    @Test
    void medie() {
        Valutazione.QueryGiudicata q = query.get(0);
        List<Valutazione.Risultato> r = List.of(
                Valutazione.valuta(q, List.of("impero-romano"), Set.of("impero-romano")),
                Valutazione.valuta(q, List.of("x"), Set.of("impero-romano")));
        // AP 1 e 0 -> MAP 0.5
        assertEquals(0.5, Valutazione.medie(r)[4], EPS);
    }
}
