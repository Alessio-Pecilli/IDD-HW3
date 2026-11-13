#!/usr/bin/env python3
"""Genera i grafici della relazione a partire da eval/benchmark.md e eval/risultati.md.

Uso: python3 scripts/grafici.py   (richiede matplotlib)
"""
import re

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt

ACC = "#1f5f8b"
plt.rcParams.update({"font.family": "Liberation Sans", "font.size": 9, "axes.spines.top": False,
                     "axes.spines.right": False})


def tabella(md, intestazione):
    """Righe (liste di celle) della tabella markdown che inizia con la riga di intestazione data."""
    righe, dentro = [], False
    for r in md.splitlines():
        if r.startswith(intestazione):
            dentro = True
            continue
        if dentro:
            if not r.startswith("|"):
                break
            if not re.fullmatch(r"[|\s-]+", r):
                righe.append([c.strip() for c in r.strip("|").split("|")])
    return righe


# 1. tempi di indicizzazione
bench = open("eval/benchmark.md", encoding="utf-8").read()
righe = tabella(bench, "| Documenti")
n = [int(r[0]) for r in righe]
ms = [float(r[2]) for r in righe]
dps = [float(r[3]) for r in righe]

fig, (a, b) = plt.subplots(1, 2, figsize=(7.2, 2.6))
a.plot(n, [m / 1000 for m in ms], "o-", color=ACC)
a.set_xlabel("documenti indicizzati")
a.set_ylabel("tempo (s)")
a.set_title("Tempo di indicizzazione", fontsize=9)
a.set_ylim(bottom=0)
b.plot(n, dps, "o-", color=ACC)
b.set_xlabel("documenti indicizzati")
b.set_ylabel("documenti / s")
b.set_title("Throughput", fontsize=9)
b.set_ylim(bottom=0)
for ax in (a, b):
    ax.grid(alpha=.3)
fig.tight_layout()
fig.savefig("relazione/tempi.png", dpi=200)

# 2. P@10 / Recall / MAP per configurazione
ris = open("eval/risultati.md", encoding="utf-8").read()
righe = tabella(ris, "| Analyzer contenuto")
nomi = [re.sub(r" \(scelto\)", "", r[0]) for r in righe]
metriche = {"P@10": 2, "Recall": 4, "MAP": 1}
fig, ax = plt.subplots(figsize=(7.2, 2.5))
larg = 0.8 / len(nomi)
colori = ["#9db4c6", "#5b8db3", ACC]
for i, (nome, r) in enumerate(zip(nomi, righe)):
    xs = [j + i * larg for j in range(len(metriche))]
    vals = [float(r[k]) for k in metriche.values()]
    ax.bar(xs, vals, larg, label=nome, color=colori[i % len(colori)])
    for x, v in zip(xs, vals):
        ax.text(x, v + .01, f"{v:.2f}", ha="center", fontsize=7)
ax.set_xticks([j + larg * (len(nomi) - 1) / 2 for j in range(len(metriche))], list(metriche))
ax.set_ylim(0, 1.05)
ax.legend(frameon=False, fontsize=8, ncol=3, loc="upper center", bbox_to_anchor=(.5, 1.18))
ax.grid(axis="y", alpha=.3)
fig.tight_layout()
fig.savefig("relazione/metriche.png", dpi=200)
print("Grafici salvati in relazione/")
