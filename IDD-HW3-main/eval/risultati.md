# Risultati della valutazione

1980 documenti, 50 query.

## Medie

| Analyzer contenuto | MAP | P@10 | R@100 | Recall | R-Prec | AP | RR | nDCG@10 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| StandardAnalyzer | 0.687 | 0.862 | 0.717 | 0.762 | 0.667 | 0.687 | 0.940 | 0.881 |
| Italiano senza stemming | 0.707 | 0.862 | 0.729 | 0.780 | 0.689 | 0.707 | 0.953 | 0.886 |
| ItalianAnalyzer (scelto) | 0.720 | 0.878 | 0.741 | 0.792 | 0.699 | 0.720 | 0.943 | 0.897 |

## Dettaglio per query

### StandardAnalyzer

| Id | Query | Rilevanti | Restituiti | P@10 | R@100 | Recall | R-Prec | AP | RR | nDCG@10 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| q01 | `contenuto imperatore romano` | 102 | 251 | 1.000 | 0.882 | 0.971 | 0.882 | 0.940 | 1.000 | 1.000 |
| q02 | `contenuto satellite Giove` | 72 | 182 | 1.000 | 0.986 | 1.000 | 0.958 | 0.992 | 1.000 | 1.000 |
| q03 | `contenuto satellite Saturno` | 61 | 150 | 1.000 | 1.000 | 1.000 | 0.951 | 0.994 | 1.000 | 1.000 |
| q04 | `contenuto satellite naturale pianeta gigante` | 133 | 303 | 1.000 | 0.744 | 0.955 | 0.925 | 0.945 | 1.000 | 1.000 |
| q05 | `contenuto canto Inferno Dante` | 34 | 172 | 0.300 | 1.000 | 1.000 | 0.441 | 0.400 | 0.333 | 0.250 |
| q06 | `contenuto canto "Divina Commedia"` | 100 | 156 | 1.000 | 0.930 | 1.000 | 0.930 | 0.961 | 1.000 | 1.000 |
| q07 | `contenuto +canto +Paradiso` | 33 | 68 | 0.400 | 1.000 | 1.000 | 0.636 | 0.603 | 1.000 | 0.479 |
| q08 | `contenuto motore di ricerca web` | 73 | 1950 | 1.000 | 0.986 | 1.000 | 0.945 | 0.974 | 1.000 | 1.000 |
| q09 | `contenuto linguaggio di programmazione` | 105 | 1950 | 1.000 | 0.867 | 1.000 | 0.895 | 0.968 | 1.000 | 1.000 |
| q10 | `contenuto algoritmo di ordinamento` | 27 | 1950 | 1.000 | 0.963 | 1.000 | 0.889 | 0.949 | 1.000 | 1.000 |
| q11 | `contenuto database` | 28 | 62 | 0.700 | 0.750 | 0.750 | 0.500 | 0.398 | 0.333 | 0.556 |
| q12 | `contenuto doge Venezia` | 127 | 235 | 1.000 | 0.780 | 0.976 | 0.921 | 0.960 | 1.000 | 1.000 |
| q13 | `contenuto pittore Rinascimento` | 133 | 140 | 1.000 | 0.617 | 0.759 | 0.752 | 0.681 | 1.000 | 1.000 |
| q14 | `contenuto battaglia Annibale Cartagine` | 27 | 236 | 1.000 | 1.000 | 1.000 | 0.926 | 0.980 | 1.000 | 1.000 |
| q15 | `contenuto elemento chimico tavola periodica` | 53 | 222 | 1.000 | 0.943 | 1.000 | 0.943 | 0.965 | 1.000 | 1.000 |
| q16 | `contenuto formaggio latte stagionatura` | 6 | 41 | 0.600 | 1.000 | 1.000 | 0.833 | 0.948 | 1.000 | 0.984 |
| q17 | `contenuto pasta` | 9 | 15 | 0.600 | 0.889 | 0.889 | 0.667 | 0.811 | 1.000 | 0.777 |
| q18 | `contenuto "imperatore romano"` | 102 | 16 | 0.900 | 0.147 | 0.147 | 0.147 | 0.134 | 1.000 | 0.905 |
| q19 | `nome inferno` | 34 | 34 | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 |
| q20 | `nome astronomia` | 133 | 94 | 1.000 | 0.707 | 0.707 | 0.707 | 0.707 | 1.000 | 1.000 |
| q21 | `nome doge` | 127 | 11 | 0.900 | 0.079 | 0.079 | 0.079 | 0.078 | 1.000 | 0.936 |
| q22 | `contenuto +imperatore +impero +augusto` | 102 | 24 | 1.000 | 0.235 | 0.235 | 0.235 | 0.235 | 1.000 | 1.000 |
| q23 | `contenuto cesare augusto senato legioni` | 102 | 235 | 1.000 | 0.794 | 0.941 | 0.804 | 0.868 | 1.000 | 1.000 |
| q24 | `contenuto "satellite di Giove"` | 72 | 10 | 0.900 | 0.125 | 0.125 | 0.125 | 0.105 | 1.000 | 0.861 |
| q25 | `contenuto luna gioviana orbita` | 72 | 158 | 0.700 | 0.528 | 0.778 | 0.278 | 0.362 | 1.000 | 0.782 |
| q26 | `contenuto "satellite naturale di Saturno"` | 61 | 34 | 1.000 | 0.557 | 0.557 | 0.557 | 0.557 | 1.000 | 1.000 |
| q27 | `contenuto Cassini Saturno anelli` | 61 | 99 | 1.000 | 1.000 | 1.000 | 0.885 | 0.898 | 1.000 | 1.000 |
| q28 | `contenuto +canto +Inferno -Purgatorio -Paradiso` | 34 | 10 | 0.800 | 0.235 | 0.235 | 0.235 | 0.219 | 1.000 | 0.851 |
| q29 | `contenuto canto Purgatorio` | 33 | 154 | 1.000 | 1.000 | 1.000 | 0.818 | 0.920 | 1.000 | 1.000 |
| q30 | `contenuto anime espiazione monte Purgatorio` | 33 | 218 | 0.900 | 1.000 | 1.000 | 0.818 | 0.884 | 1.000 | 0.931 |
| q31 | `contenuto canto Paradiso Beatrice` | 33 | 173 | 0.600 | 1.000 | 1.000 | 0.697 | 0.658 | 1.000 | 0.571 |
| q32 | `contenuto Dante Virgilio` | 100 | 132 | 1.000 | 0.970 | 1.000 | 0.970 | 0.992 | 1.000 | 1.000 |
| q33 | `contenuto "motore di ricerca"` | 73 | 58 | 1.000 | 0.781 | 0.781 | 0.781 | 0.769 | 1.000 | 1.000 |
| q34 | `contenuto indicizzazione pagine web ricerca` | 73 | 294 | 1.000 | 0.890 | 1.000 | 0.836 | 0.906 | 1.000 | 1.000 |
| q35 | `contenuto "linguaggio di programmazione"` | 105 | 83 | 1.000 | 0.762 | 0.762 | 0.762 | 0.762 | 1.000 | 1.000 |
| q36 | `contenuto compilatore sintassi programma` | 105 | 190 | 1.000 | 0.676 | 0.733 | 0.705 | 0.689 | 1.000 | 1.000 |
| q37 | `contenuto ordinamento array confronto` | 27 | 116 | 1.000 | 0.963 | 0.963 | 0.889 | 0.931 | 1.000 | 1.000 |
| q38 | `contenuto "algoritmo di ordinamento"` | 27 | 20 | 1.000 | 0.741 | 0.741 | 0.741 | 0.741 | 1.000 | 1.000 |
| q39 | `contenuto "base di dati"` | 28 | 10 | 0.900 | 0.321 | 0.321 | 0.321 | 0.318 | 1.000 | 0.934 |
| q40 | `contenuto DBMS SQL tabelle query` | 28 | 36 | 0.500 | 0.250 | 0.250 | 0.250 | 0.162 | 1.000 | 0.567 |
| q41 | `contenuto "doge di Venezia"` | 127 | 7 | 0.600 | 0.047 | 0.047 | 0.047 | 0.047 | 1.000 | 0.727 |
| q42 | `contenuto doge Serenissima dogado` | 127 | 146 | 1.000 | 0.756 | 0.969 | 0.929 | 0.955 | 1.000 | 1.000 |
| q43 | `contenuto pittore affreschi pala d'altare Quattrocento` | 133 | 184 | 1.000 | 0.707 | 0.917 | 0.857 | 0.869 | 1.000 | 1.000 |
| q44 | `contenuto "pittore italiano"` | 133 | 4 | 0.200 | 0.015 | 0.015 | 0.015 | 0.006 | 0.333 | 0.205 |
| q45 | `contenuto "seconda guerra punica"` | 27 | 20 | 0.900 | 0.630 | 0.630 | 0.630 | 0.585 | 1.000 | 0.922 |
| q46 | `contenuto Annibale Scipione assedio` | 27 | 84 | 1.000 | 1.000 | 1.000 | 0.889 | 0.981 | 1.000 | 1.000 |
| q47 | `contenuto isotopi numero atomico elementi` | 53 | 502 | 1.000 | 1.000 | 1.000 | 0.981 | 0.993 | 1.000 | 1.000 |
| q48 | `contenuto "formaggio a pasta"` | 6 | 0 | 0.000 | 0.000 | 0.000 | 0.000 | 0.000 | 0.000 | 0.000 |
| q49 | `contenuto pasta grano duro sugo` | 9 | 77 | 0.700 | 1.000 | 1.000 | 0.778 | 0.849 | 1.000 | 0.825 |
| q50 | `contenuto indice ricerca informazioni documenti` | 101 | 401 | 1.000 | 0.584 | 0.891 | 0.584 | 0.707 | 1.000 | 1.000 |
|  | **media** |  |  | 0.862 | 0.717 | 0.762 | 0.667 | 0.687 | 0.940 | 0.881 |

### Italiano senza stemming

| Id | Query | Rilevanti | Restituiti | P@10 | R@100 | Recall | R-Prec | AP | RR | nDCG@10 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| q01 | `contenuto imperatore romano` | 102 | 309 | 1.000 | 0.794 | 0.990 | 0.804 | 0.873 | 1.000 | 1.000 |
| q02 | `contenuto satellite Giove` | 72 | 182 | 1.000 | 0.986 | 1.000 | 0.958 | 0.992 | 1.000 | 1.000 |
| q03 | `contenuto satellite Saturno` | 61 | 150 | 1.000 | 1.000 | 1.000 | 0.951 | 0.993 | 1.000 | 1.000 |
| q04 | `contenuto satellite naturale pianeta gigante` | 133 | 303 | 1.000 | 0.744 | 0.955 | 0.917 | 0.945 | 1.000 | 1.000 |
| q05 | `contenuto canto Inferno Dante` | 34 | 178 | 0.600 | 1.000 | 1.000 | 0.588 | 0.646 | 1.000 | 0.691 |
| q06 | `contenuto canto "Divina Commedia"` | 100 | 156 | 1.000 | 0.940 | 1.000 | 0.940 | 0.963 | 1.000 | 1.000 |
| q07 | `contenuto +canto +Paradiso` | 33 | 68 | 0.400 | 1.000 | 1.000 | 0.636 | 0.600 | 1.000 | 0.466 |
| q08 | `contenuto motore di ricerca web` | 73 | 292 | 1.000 | 0.986 | 1.000 | 0.945 | 0.975 | 1.000 | 1.000 |
| q09 | `contenuto linguaggio di programmazione` | 105 | 221 | 1.000 | 0.867 | 0.990 | 0.895 | 0.965 | 1.000 | 1.000 |
| q10 | `contenuto algoritmo di ordinamento` | 27 | 64 | 1.000 | 0.963 | 0.963 | 0.963 | 0.948 | 1.000 | 1.000 |
| q11 | `contenuto database` | 28 | 62 | 0.700 | 0.750 | 0.750 | 0.500 | 0.395 | 0.333 | 0.556 |
| q12 | `contenuto doge Venezia` | 127 | 235 | 1.000 | 0.780 | 0.976 | 0.921 | 0.960 | 1.000 | 1.000 |
| q13 | `contenuto pittore Rinascimento` | 133 | 140 | 1.000 | 0.624 | 0.759 | 0.752 | 0.679 | 1.000 | 1.000 |
| q14 | `contenuto battaglia Annibale Cartagine` | 27 | 236 | 1.000 | 1.000 | 1.000 | 0.926 | 0.979 | 1.000 | 1.000 |
| q15 | `contenuto elemento chimico tavola periodica` | 53 | 239 | 1.000 | 0.981 | 1.000 | 0.962 | 0.985 | 1.000 | 1.000 |
| q16 | `contenuto formaggio latte stagionatura` | 6 | 41 | 0.600 | 1.000 | 1.000 | 0.833 | 0.948 | 1.000 | 0.984 |
| q17 | `contenuto pasta` | 9 | 15 | 0.600 | 0.889 | 0.889 | 0.667 | 0.811 | 1.000 | 0.777 |
| q18 | `contenuto "imperatore romano"` | 102 | 30 | 0.700 | 0.265 | 0.265 | 0.265 | 0.227 | 1.000 | 0.775 |
| q19 | `nome inferno` | 34 | 34 | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 |
| q20 | `nome astronomia` | 133 | 94 | 1.000 | 0.707 | 0.707 | 0.707 | 0.707 | 1.000 | 1.000 |
| q21 | `nome doge` | 127 | 11 | 0.900 | 0.079 | 0.079 | 0.079 | 0.078 | 1.000 | 0.936 |
| q22 | `contenuto +imperatore +impero +augusto` | 102 | 66 | 1.000 | 0.598 | 0.598 | 0.598 | 0.591 | 1.000 | 1.000 |
| q23 | `contenuto cesare augusto senato legioni` | 102 | 235 | 1.000 | 0.784 | 0.941 | 0.804 | 0.869 | 1.000 | 1.000 |
| q24 | `contenuto "satellite di Giove"` | 72 | 10 | 0.900 | 0.125 | 0.125 | 0.125 | 0.105 | 1.000 | 0.861 |
| q25 | `contenuto luna gioviana orbita` | 72 | 188 | 0.600 | 0.431 | 0.847 | 0.375 | 0.371 | 1.000 | 0.713 |
| q26 | `contenuto "satellite naturale di Saturno"` | 61 | 34 | 1.000 | 0.557 | 0.557 | 0.557 | 0.557 | 1.000 | 1.000 |
| q27 | `contenuto Cassini Saturno anelli` | 61 | 99 | 1.000 | 1.000 | 1.000 | 0.885 | 0.898 | 1.000 | 1.000 |
| q28 | `contenuto +canto +Inferno -Purgatorio -Paradiso` | 34 | 19 | 1.000 | 0.471 | 0.471 | 0.471 | 0.461 | 1.000 | 1.000 |
| q29 | `contenuto canto Purgatorio` | 33 | 154 | 1.000 | 1.000 | 1.000 | 0.818 | 0.920 | 1.000 | 1.000 |
| q30 | `contenuto anime espiazione monte Purgatorio` | 33 | 222 | 0.900 | 1.000 | 1.000 | 0.848 | 0.911 | 1.000 | 0.934 |
| q31 | `contenuto canto Paradiso Beatrice` | 33 | 173 | 0.500 | 1.000 | 1.000 | 0.667 | 0.655 | 1.000 | 0.508 |
| q32 | `contenuto Dante Virgilio` | 100 | 132 | 1.000 | 0.980 | 1.000 | 0.980 | 0.993 | 1.000 | 1.000 |
| q33 | `contenuto "motore di ricerca"` | 73 | 58 | 1.000 | 0.781 | 0.781 | 0.781 | 0.771 | 1.000 | 1.000 |
| q34 | `contenuto indicizzazione pagine web ricerca` | 73 | 294 | 0.900 | 0.890 | 1.000 | 0.836 | 0.884 | 1.000 | 0.934 |
| q35 | `contenuto "linguaggio di programmazione"` | 105 | 83 | 1.000 | 0.762 | 0.762 | 0.762 | 0.761 | 1.000 | 1.000 |
| q36 | `contenuto compilatore sintassi programma` | 105 | 190 | 1.000 | 0.667 | 0.733 | 0.695 | 0.689 | 1.000 | 1.000 |
| q37 | `contenuto ordinamento array confronto` | 27 | 123 | 1.000 | 0.963 | 0.963 | 0.926 | 0.929 | 1.000 | 1.000 |
| q38 | `contenuto "algoritmo di ordinamento"` | 27 | 22 | 1.000 | 0.815 | 0.815 | 0.815 | 0.815 | 1.000 | 1.000 |
| q39 | `contenuto "base di dati"` | 28 | 13 | 0.800 | 0.321 | 0.321 | 0.321 | 0.315 | 1.000 | 0.870 |
| q40 | `contenuto DBMS SQL tabelle query` | 28 | 36 | 0.500 | 0.250 | 0.250 | 0.250 | 0.152 | 1.000 | 0.551 |
| q41 | `contenuto "doge di Venezia"` | 127 | 8 | 0.700 | 0.055 | 0.055 | 0.055 | 0.055 | 1.000 | 0.801 |
| q42 | `contenuto doge Serenissima dogado` | 127 | 146 | 1.000 | 0.764 | 0.969 | 0.929 | 0.956 | 1.000 | 1.000 |
| q43 | `contenuto pittore affreschi pala d'altare Quattrocento` | 133 | 201 | 1.000 | 0.677 | 0.932 | 0.842 | 0.860 | 1.000 | 1.000 |
| q44 | `contenuto "pittore italiano"` | 133 | 4 | 0.200 | 0.015 | 0.015 | 0.015 | 0.006 | 0.333 | 0.205 |
| q45 | `contenuto "seconda guerra punica"` | 27 | 20 | 0.900 | 0.630 | 0.630 | 0.630 | 0.585 | 1.000 | 0.922 |
| q46 | `contenuto Annibale Scipione assedio` | 27 | 115 | 1.000 | 1.000 | 1.000 | 0.926 | 0.995 | 1.000 | 1.000 |
| q47 | `contenuto isotopi numero atomico elementi` | 53 | 502 | 1.000 | 1.000 | 1.000 | 0.981 | 0.993 | 1.000 | 1.000 |
| q48 | `contenuto "formaggio a pasta"` | 6 | 0 | 0.000 | 0.000 | 0.000 | 0.000 | 0.000 | 0.000 | 0.000 |
| q49 | `contenuto pasta grano duro sugo` | 9 | 77 | 0.700 | 1.000 | 1.000 | 0.778 | 0.849 | 1.000 | 0.825 |
| q50 | `contenuto indice ricerca informazioni documenti` | 101 | 406 | 1.000 | 0.564 | 0.891 | 0.564 | 0.711 | 1.000 | 1.000 |
|  | **media** |  |  | 0.862 | 0.729 | 0.780 | 0.689 | 0.707 | 0.953 | 0.886 |

### ItalianAnalyzer (scelto)

| Id | Query | Rilevanti | Restituiti | P@10 | R@100 | Recall | R-Prec | AP | RR | nDCG@10 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| q01 | `contenuto imperatore romano` | 102 | 390 | 1.000 | 0.833 | 0.990 | 0.853 | 0.923 | 1.000 | 1.000 |
| q02 | `contenuto satellite Giove` | 72 | 190 | 1.000 | 1.000 | 1.000 | 0.986 | 0.999 | 1.000 | 1.000 |
| q03 | `contenuto satellite Saturno` | 61 | 163 | 1.000 | 1.000 | 1.000 | 0.967 | 0.996 | 1.000 | 1.000 |
| q04 | `contenuto satellite naturale pianeta gigante` | 133 | 369 | 1.000 | 0.737 | 0.992 | 0.932 | 0.978 | 1.000 | 1.000 |
| q05 | `contenuto canto Inferno Dante` | 34 | 178 | 0.600 | 1.000 | 1.000 | 0.588 | 0.647 | 1.000 | 0.691 |
| q06 | `contenuto canto "Divina Commedia"` | 100 | 156 | 1.000 | 0.940 | 1.000 | 0.940 | 0.967 | 1.000 | 1.000 |
| q07 | `contenuto +canto +Paradiso` | 33 | 68 | 0.400 | 1.000 | 1.000 | 0.636 | 0.599 | 1.000 | 0.466 |
| q08 | `contenuto motore di ricerca web` | 73 | 339 | 1.000 | 1.000 | 1.000 | 0.932 | 0.983 | 1.000 | 1.000 |
| q09 | `contenuto linguaggio di programmazione` | 105 | 226 | 1.000 | 0.867 | 1.000 | 0.905 | 0.972 | 1.000 | 1.000 |
| q10 | `contenuto algoritmo di ordinamento` | 27 | 77 | 1.000 | 0.963 | 0.963 | 0.963 | 0.937 | 1.000 | 1.000 |
| q11 | `contenuto database` | 28 | 62 | 0.700 | 0.750 | 0.750 | 0.500 | 0.395 | 0.333 | 0.556 |
| q12 | `contenuto doge Venezia` | 127 | 235 | 1.000 | 0.780 | 0.976 | 0.921 | 0.960 | 1.000 | 1.000 |
| q13 | `contenuto pittore Rinascimento` | 133 | 151 | 1.000 | 0.654 | 0.820 | 0.789 | 0.758 | 1.000 | 1.000 |
| q14 | `contenuto battaglia Annibale Cartagine` | 27 | 248 | 1.000 | 1.000 | 1.000 | 0.926 | 0.977 | 1.000 | 1.000 |
| q15 | `contenuto elemento chimico tavola periodica` | 53 | 411 | 1.000 | 1.000 | 1.000 | 0.962 | 0.994 | 1.000 | 1.000 |
| q16 | `contenuto formaggio latte stagionatura` | 6 | 48 | 0.600 | 1.000 | 1.000 | 0.833 | 0.915 | 1.000 | 0.971 |
| q17 | `contenuto pasta` | 9 | 15 | 0.600 | 0.889 | 0.889 | 0.667 | 0.811 | 1.000 | 0.777 |
| q18 | `contenuto "imperatore romano"` | 102 | 37 | 0.800 | 0.324 | 0.324 | 0.324 | 0.276 | 1.000 | 0.842 |
| q19 | `nome inferno` | 34 | 34 | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 |
| q20 | `nome astronomia` | 133 | 94 | 1.000 | 0.707 | 0.707 | 0.707 | 0.707 | 1.000 | 1.000 |
| q21 | `nome doge` | 127 | 11 | 0.900 | 0.079 | 0.079 | 0.079 | 0.078 | 1.000 | 0.936 |
| q22 | `contenuto +imperatore +impero +augusto` | 102 | 71 | 1.000 | 0.627 | 0.627 | 0.627 | 0.621 | 1.000 | 1.000 |
| q23 | `contenuto cesare augusto senato legioni` | 102 | 256 | 1.000 | 0.794 | 0.961 | 0.794 | 0.882 | 1.000 | 1.000 |
| q24 | `contenuto "satellite di Giove"` | 72 | 14 | 0.900 | 0.181 | 0.181 | 0.181 | 0.164 | 1.000 | 0.905 |
| q25 | `contenuto luna gioviana orbita` | 72 | 198 | 1.000 | 0.472 | 0.847 | 0.417 | 0.475 | 1.000 | 1.000 |
| q26 | `contenuto "satellite naturale di Saturno"` | 61 | 36 | 1.000 | 0.590 | 0.590 | 0.590 | 0.590 | 1.000 | 1.000 |
| q27 | `contenuto Cassini Saturno anelli` | 61 | 128 | 1.000 | 1.000 | 1.000 | 0.803 | 0.886 | 1.000 | 1.000 |
| q28 | `contenuto +canto +Inferno -Purgatorio -Paradiso` | 34 | 19 | 1.000 | 0.471 | 0.471 | 0.471 | 0.461 | 1.000 | 1.000 |
| q29 | `contenuto canto Purgatorio` | 33 | 154 | 1.000 | 1.000 | 1.000 | 0.818 | 0.920 | 1.000 | 1.000 |
| q30 | `contenuto anime espiazione monte Purgatorio` | 33 | 222 | 0.900 | 1.000 | 1.000 | 0.848 | 0.911 | 1.000 | 0.934 |
| q31 | `contenuto canto Paradiso Beatrice` | 33 | 177 | 0.500 | 1.000 | 1.000 | 0.667 | 0.655 | 1.000 | 0.508 |
| q32 | `contenuto Dante Virgilio` | 100 | 135 | 1.000 | 0.980 | 1.000 | 0.980 | 0.994 | 1.000 | 1.000 |
| q33 | `contenuto "motore di ricerca"` | 73 | 68 | 1.000 | 0.904 | 0.904 | 0.904 | 0.897 | 1.000 | 1.000 |
| q34 | `contenuto indicizzazione pagine web ricerca` | 73 | 339 | 1.000 | 0.890 | 1.000 | 0.836 | 0.874 | 1.000 | 1.000 |
| q35 | `contenuto "linguaggio di programmazione"` | 105 | 87 | 1.000 | 0.781 | 0.781 | 0.781 | 0.778 | 1.000 | 1.000 |
| q36 | `contenuto compilatore sintassi programma` | 105 | 211 | 1.000 | 0.695 | 0.800 | 0.724 | 0.742 | 1.000 | 1.000 |
| q37 | `contenuto ordinamento array confronto` | 27 | 232 | 1.000 | 0.963 | 0.963 | 0.889 | 0.922 | 1.000 | 1.000 |
| q38 | `contenuto "algoritmo di ordinamento"` | 27 | 24 | 1.000 | 0.889 | 0.889 | 0.889 | 0.889 | 1.000 | 1.000 |
| q39 | `contenuto "base di dati"` | 28 | 13 | 0.800 | 0.321 | 0.321 | 0.321 | 0.315 | 1.000 | 0.870 |
| q40 | `contenuto DBMS SQL tabelle query` | 28 | 69 | 0.600 | 0.250 | 0.250 | 0.250 | 0.142 | 0.500 | 0.534 |
| q41 | `contenuto "doge di Venezia"` | 127 | 8 | 0.700 | 0.055 | 0.055 | 0.055 | 0.055 | 1.000 | 0.801 |
| q42 | `contenuto doge Serenissima dogado` | 127 | 147 | 1.000 | 0.764 | 0.969 | 0.929 | 0.956 | 1.000 | 1.000 |
| q43 | `contenuto pittore affreschi pala d'altare Quattrocento` | 133 | 214 | 1.000 | 0.677 | 0.970 | 0.857 | 0.889 | 1.000 | 1.000 |
| q44 | `contenuto "pittore italiano"` | 133 | 5 | 0.300 | 0.023 | 0.023 | 0.023 | 0.011 | 0.333 | 0.290 |
| q45 | `contenuto "seconda guerra punica"` | 27 | 20 | 0.900 | 0.630 | 0.630 | 0.630 | 0.585 | 1.000 | 0.922 |
| q46 | `contenuto Annibale Scipione assedio` | 27 | 123 | 1.000 | 1.000 | 1.000 | 0.926 | 0.990 | 1.000 | 1.000 |
| q47 | `contenuto isotopi numero atomico elementi` | 53 | 542 | 1.000 | 1.000 | 1.000 | 0.981 | 0.992 | 1.000 | 1.000 |
| q48 | `contenuto "formaggio a pasta"` | 6 | 0 | 0.000 | 0.000 | 0.000 | 0.000 | 0.000 | 0.000 | 0.000 |
| q49 | `contenuto pasta grano duro sugo` | 9 | 77 | 0.700 | 1.000 | 1.000 | 0.778 | 0.849 | 1.000 | 0.825 |
| q50 | `contenuto indice ricerca informazioni documenti` | 101 | 563 | 1.000 | 0.584 | 0.901 | 0.584 | 0.687 | 1.000 | 1.000 |
|  | **media** |  |  | 0.878 | 0.741 | 0.792 | 0.699 | 0.720 | 0.943 | 0.897 |
