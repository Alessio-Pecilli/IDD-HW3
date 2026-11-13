# Benchmark

Java 25.0.4.1, 8 core, mediana di 5 esecuzioni dopo un riscaldamento.

| Documenti | Testo (MB) | Tempo (ms) | Documenti/s | MB/s | Indice (MB) |
|---|---|---|---|---|---|
| 100 | 0.5 | 70 | 1423 | 7.60 | 0.2 |
| 250 | 1.3 | 143 | 1754 | 9.22 | 0.5 |
| 500 | 2.9 | 224 | 2228 | 12.86 | 1.1 |
| 1000 | 5.9 | 389 | 2573 | 15.16 | 2.0 |
| 1500 | 9.4 | 606 | 2477 | 15.56 | 3.0 |
| 1980 | 12.2 | 788 | 2513 | 15.50 | 3.8 |

Tempo di risposta (top 10, 50 query x 50 ripetizioni, indice di 1980 documenti): media 0.182 ms, mediana 0.146 ms.
