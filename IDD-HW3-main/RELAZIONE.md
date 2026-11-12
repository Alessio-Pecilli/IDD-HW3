# Homework 3 – Indicizzazione e ricerca di file .txt con Lucene

Repository: https://github.com/Alessio-Pecilli/IDD-HW3

## Struttura

- `src/main/java/it/uniroma3/idd/Indexer.java` – indicizza i file `.txt` della cartella `dataset/` in un indice Lucene (cartella `index/`) con due campi: `nome` e `contenuto`.
- `src/main/java/it/uniroma3/idd/Searcher.java` – legge le query da console, interroga l'indice e stampa i risultati (score e path).
- `src/test/java/it/uniroma3/idd/SearcherTest.java` – test JUnit automatici sulle query.
- `dataset/` – 12 file `.txt` in italiano usati per i test.

Esecuzione (Java 21+, Maven, Lucene 10.1.0):

```bash
mvn compile exec:java -Dexec.mainClass=it.uniroma3.idd.Indexer
mvn exec:java -Dexec.mainClass=it.uniroma3.idd.Searcher
mvn test
```

## Sintassi delle query

```
nome <termini>          es.  nome roma
contenuto <termini>     es.  contenuto "selva oscura"
```

La prima parola indica il campo (`nome` o `contenuto`), il resto è passato al `QueryParser` di Lucene con lo stesso analyzer usato in indicizzazione: i termini sono in OR, il testo tra virgolette diventa una phrase query, e sono disponibili anche `+termine` (obbligatorio) e `AND`/`OR`/`NOT`. `exit` termina il programma.

## Analyzer scelti

Si usa un `PerFieldAnalyzerWrapper`, condiviso da Indexer e Searcher, così query e documenti vengono analizzati allo stesso modo.

**Campo `nome`** – `CustomAnalyzer` con:
- `WhitespaceTokenizer`: spezza sugli spazi (`Ricetta Carbonara`).
- `LowerCaseFilter`: `Ricetta` e `ricetta` devono coincidere.
- `WordDelimiterGraphFilter`: i nomi dei file usano `_`, `-` e `.` come separatori (`storia_di_roma`, `motori-di-ricerca`); questo filtro li spezza in parole singole. Lo `StandardTokenizer` non spezzerebbe su `_`, quindi `storia_di_roma` diventerebbe un unico token.
- Niente stopword e niente stemming: un nome di file è breve e ogni parola conta (serve poter cercare la frase `"motori di ricerca"`); lo stemming renderebbe la ricerca sul nome troppo permissiva.
- L'estensione `.txt` viene tolta prima dell'indicizzazione, dato che è presente in tutti i file.

**Campo `contenuto`** – `ItalianAnalyzer`, perché i testi sono in italiano:
- rimuove le stopword italiane (`il`, `di`, `la`, …), che non portano informazione;
- gestisce l'elisione (`l'impero` → `impero`, `dell'informazione` → `informazione`);
- applica uno stemming leggero (`pianeti` → `pianet`, `pianeta` → `pianet`), così si trovano anche singolari/plurali e forme maschili/femminili.

## Indicizzazione

| File indicizzati | Tempo di indicizzazione |
|---|---|
| 12 | ~450 ms (misurato con `System.currentTimeMillis`, include l'avvio di Lucene) |

Il tempo varia di qualche decina di ms tra un'esecuzione e l'altra.

## Query di test

| # | Query | Query Lucene | Risultati |
|---|---|---|---|
| 1 | `nome roma` | `nome:roma` | storia_di_roma.txt |
| 2 | `nome carbonara` | `nome:carbonara` | Ricetta Carbonara.txt (nome con maiuscola e spazio) |
| 3 | `nome "motori di ricerca"` | `nome:"motori di ricerca"` | motori-di-ricerca.txt (phrase query sul nome) |
| 4 | `nome intelligenza artificiale` | `nome:intelligenza nome:artificiale` | intelligenza_artificiale.txt |
| 5 | `contenuto pianeti` | `contenuto:pianet` | sistema_solare.txt (stemming: il testo contiene "pianeta"/"pianeti") |
| 6 | `contenuto "selva oscura"` | `contenuto:"selva oscur"` | divina_commedia.txt (phrase query) |
| 7 | `contenuto +guanciale +pecorino` | `+contenuto:guancial +contenuto:pecorin` | Ricetta Carbonara.txt (AND) |
| 8 | `contenuto impero romano` | `contenuto:imper contenuto:roman` | impero-romano.txt, storia_di_roma.txt, Ricetta Carbonara.txt (ranking BM25) |
| 9 | `contenuto "indice invertito"` | `contenuto:"indic invertit"` | motori-di-ricerca.txt |
| 10 | `contenuto lucene AND java` | `+contenuto:lucen +contenuto:java` | lucene_introduzione.txt |
| 11 | `contenuto il di la` | (vuota, solo stopword) | nessun risultato |
| 12 | `titolo roma` | – | errore: campo non valido |

Osservazioni:
- `nome roma` non restituisce `impero-romano`: sul nome non c'è stemming, quindi `roma` ≠ `romano` (scelta voluta).
- Nella query 8 la carbonara compare per "pecorino romano": lo stemming porta "romano"/"romana" alla stessa radice `roman`, ma con score più basso.
- La phrase query `nome "ricerca motori"` (ordine invertito) non dà risultati, come verificato nei test.

Le stesse query sono verificate automaticamente da `mvn test` (11 test, tutti superati).
