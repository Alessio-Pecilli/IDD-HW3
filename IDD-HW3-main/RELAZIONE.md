# Homework 3 – Indicizzazione e ricerca di file .txt con Lucene

Alessio Pecilli · matricola 589232 · ale.pecilli@stud.uniroma3.it

Relazione completa (versione finale consegnata): [`Relazione_HW3_Pecilli_589232.pdf`](Relazione_HW3_Pecilli_589232.pdf) (sorgente in `relazione/`).

## Struttura

| Percorso | Contenuto |
|---|---|
| `src/main/java/.../Indexer.java` | indicizza i `.txt` di una cartella: campi `nome` (CustomAnalyzer), `contenuto` (ItalianAnalyzer), `path` |
| `src/main/java/.../Searcher.java` | query da console: `nome <termini>` o `contenuto <termini>` (sintassi del QueryParser) |
| `src/main/java/.../Metriche.java` | P@k, R@k, Precision, Recall, F1, R-Precision, AP, RR, nDCG@k |
| `src/main/java/.../Valutazione.java` | esegue le query giudicate e confronta 3 analyzer per il contenuto |
| `src/main/java/.../Benchmark.java` | tempi di indicizzazione al crescere della collezione e tempo di risposta |
| `scripts/build_dataset.py` | costruisce `dataset-wiki/` da Wikipedia e scrive `eval/manifest.tsv` (ground truth) |
| `dataset/` | 12 file scritti a mano, usati dai test JUnit |
| `eval/queries.tsv` | 50 query di valutazione con gli argomenti rilevanti |
| `eval/risultati.md`, `eval/benchmark.md` | risultati generati |
| `eval/top10.tsv`, `eval/giudizi_manuali.tsv` | primi 10 risultati e giudizi manuali dei non giudicati (analisi della recall) |

## Dataset

- **Piccolo** (`dataset/`): 12 file di 2–3 frasi scritti a mano, ciascuno pensato per una funzionalità (separatori nei nomi, stemming, elisione, phrase query, ambiguità come *pecorino romano*).
- **Wikipedia** (`dataset-wiki/`): 1980 voci di it.wikipedia (12 MB di testo, file `.txt` inclusi nel repository). Sono 923 voci tematiche di 16 categorie (imperatori romani, satelliti di Giove/Saturno, canti della Commedia, motori di ricerca, linguaggi di programmazione, dogi, pittori del Rinascimento…) più 1057 voci casuali come rumore. Un documento è rilevante per una query se appartiene a una delle sue categorie.

```bash
python3 scripts/build_dataset.py --max-voci 2300   # rigenera dataset-wiki/ e eval/manifest.tsv
```

## Esecuzione (Java 21+, Maven, Lucene 10.1.0)

```bash
mvn compile exec:java -Dexec.mainClass=it.uniroma3.idd.Indexer -Dexec.args="dataset-wiki index"
mvn exec:java -Dexec.mainClass=it.uniroma3.idd.Searcher -Dexec.args="index"
mvn exec:java -Dexec.mainClass=it.uniroma3.idd.Benchmark
mvn exec:java -Dexec.mainClass=it.uniroma3.idd.Valutazione
mvn test && python3 -m unittest discover -s scripts
python3 scripts/grafici.py                          # grafici della relazione
```

## Risultati principali

**Indicizzazione** (mediana di 5 esecuzioni): 1980 documenti in 788 ms (~2500 doc/s, 15 MB/s), tempo lineare oltre i 1000 documenti; indice di 3,8 MB; tempo di risposta medio 0,18 ms.

**Valutazione** (50 query, media):

| Analyzer contenuto | MAP | P@10 | R@100 | Recall | R-Prec | MRR | nDCG@10 |
|---|---|---|---|---|---|---|---|
| StandardAnalyzer | 0.687 | 0.862 | 0.717 | 0.762 | 0.667 | 0.940 | 0.881 |
| Italiano senza stemming | 0.707 | 0.862 | 0.729 | 0.780 | 0.689 | 0.953 | 0.886 |
| **ItalianAnalyzer (scelto)** | **0.720** | **0.878** | **0.741** | **0.792** | **0.699** | 0.943 | **0.897** |

La ground truth basata sulle categorie è incompleta. Tra i primi 10 risultati, 6 dei 44 documenti giudicati non rilevanti sono in realtà pertinenti, per esempio linguaggi per database classificati in "Linguaggi di programmazione". La recall è quindi una stima, valida per confrontare le configurazioni.

**Test**: 52 (40 JUnit + 12 unittest), tutti superati.
