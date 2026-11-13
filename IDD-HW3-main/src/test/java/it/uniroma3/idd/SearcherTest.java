package it.uniroma3.idd;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.List;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

// controllo che le query della relazione diano i risultati giusti sul dataset piccolo (12 file scritti a mano)
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
        return searcher.ranking(query, 20);
    }

    // ---- campo nome ----

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
        assertEquals(List.of("Ricetta Carbonara"), nomi("nome CARBONARA"));
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
    void nomePiuTerminiInOr() throws Exception {
        // storia_di_roma e sistema_solare non hanno termini in comune: l'OR li trova entrambi
        assertEquals(List.of("sistema_solare", "storia_di_roma").stream().sorted().toList(),
                nomi("nome roma solare").stream().sorted().toList());
    }

    @Test
    void nomeNonFaStemming() throws Exception {
        // sul nome non c'è stemming: "pizze" non trova pizza_napoletana
        assertTrue(nomi("nome pizze").isEmpty());
    }

    @Test
    void campoNonDistingueMaiuscole() throws Exception {
        assertEquals(nomi("nome roma"), nomi("NOME roma"));
    }

    // ---- campo contenuto ----

    @Test
    void contenutoStemming() throws Exception {
        assertTrue(nomi("contenuto pianeti").contains("sistema_solare"));
        assertEquals(nomi("contenuto pianeti"), nomi("contenuto pianeta"));
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
        assertEquals(List.of("lucene_introduzione"), nomi("contenuto lucene AND java"));
    }

    @Test
    void contenutoEsclusione() throws Exception {
        // "roman" compare in impero-romano, storia_di_roma e nella carbonara (pecorino romano)
        List<String> conRomano = nomi("contenuto romano");
        List<String> senzaPecorino = nomi("contenuto romano -pecorino");
        assertTrue(conRomano.contains("Ricetta Carbonara"));
        assertFalse(senzaPecorino.contains("Ricetta Carbonara"));
        assertEquals(conRomano.size() - 1, senzaPecorino.size());
    }

    @Test
    void contenutoRankingBM25() throws Exception {
        // impero-romano contiene entrambi i termini e deve stare sopra la carbonara, che ne ha uno solo
        List<String> r = nomi("contenuto impero romano");
        assertEquals("impero-romano", r.get(0));
        assertTrue(r.indexOf("impero-romano") < r.indexOf("Ricetta Carbonara"));
    }

    @Test
    void contenutoSoloStopword() throws Exception {
        assertTrue(nomi("contenuto il di la").isEmpty());
    }

    @Test
    void contenutoTermineAssente() throws Exception {
        assertTrue(nomi("contenuto xyzzy").isEmpty());
    }

    // ---- errori ----

    @Test
    void campoNonValido() {
        assertThrows(IllegalArgumentException.class, () -> Searcher.parse("titolo roma"));
    }

    @Test
    void mancanoITermini() {
        assertThrows(IllegalArgumentException.class, () -> Searcher.parse("contenuto"));
        assertThrows(IllegalArgumentException.class, () -> Searcher.parse("contenuto   "));
    }

    @Test
    void sintassiNonValida() {
        assertThrows(ParseException.class, () -> Searcher.parse("contenuto \"frase non chiusa"));
    }

    @Test
    void spaziMultipliTraCampoETermini() throws Exception {
        assertEquals(nomi("nome roma"), nomi("nome    roma"));
    }
}
