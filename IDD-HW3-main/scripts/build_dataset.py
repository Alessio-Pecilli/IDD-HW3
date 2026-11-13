#!/usr/bin/env python3
"""Costruisce il dataset grande (dataset-wiki/) scaricando voci da it.wikipedia.org.

Il dataset ha due parti:
  1. voci "tematiche": tutti i membri diretti (namespace 0) di alcune categorie di Wikipedia;
     l'appartenenza alla categoria e' usata come giudizio di rilevanza (ground truth) per la valutazione;
  2. voci casuali (generator=random) che fanno da "rumore" e rendono la collezione piu' grande e realistica.

Il testo viene scaricato come wikitext a blocchi di 50 voci per richiesta (prop=revisions), per restare nei limiti
di richieste dell'API, e convertito in testo in chiaro (template, tabelle, note e markup tolti; troncato alle
sezioni finali come "Note" e "Collegamenti esterni"). Per ogni voce si salva il testo in un file <titolo>.txt e una riga
nel manifest eval/manifest.tsv: file, pageid, revid, numero di caratteri, argomenti (codici categoria).

Uso:  python3 scripts/build_dataset.py [--random 3000]
Solo libreria standard di Python, nessuna dipendenza.
"""
import argparse
import json
import os
import re
import sys
import time
import urllib.parse
import urllib.request
import urllib.error

API = "https://it.wikipedia.org/w/api.php"
UA = "IDD-HW3-coursework/1.0 (university homework, Lucene indexing)"

# codice argomento -> categoria di Wikipedia
TOPICS = {
    "IMP": "Imperatori romani",
    "GIO": "Satelliti di Giove",
    "SAT": "Satelliti di Saturno",
    "INF": "Canti dell'Inferno",
    "PUR": "Canti del Purgatorio",
    "PAR": "Canti del Paradiso",
    "MOT": "Motori di ricerca",
    "LIN": "Linguaggi di programmazione",
    "ALG": "Algoritmi di ordinamento",
    "DB": "Basi di dati",
    "DOG": "Dogi della Repubblica di Venezia",
    "PIT": "Pittori italiani del Rinascimento",
    "PUN": "Battaglie della seconda guerra punica",
    "ELE": "Elementi chimici",
    "FOR": "Formaggi italiani",
    "PAS": "Tipi di pasta",
}

MIN_CHARS = 300  # voci piu' corte (stub, liste vuote) vengono scartate
PAUSA = 0.3      # secondi tra una richiesta e l'altra: richieste seriali, come chiede la API etiquette di Wikimedia


def api(params, retries=8):
    params = dict(params, format="json", formatversion="2", maxlag="5")
    url = API + "?" + urllib.parse.urlencode(params)
    for attempt in range(retries):
        time.sleep(PAUSA)
        try:
            req = urllib.request.Request(url, headers={"User-Agent": UA})
            with urllib.request.urlopen(req, timeout=30) as r:
                data = json.load(r)
            if "error" in data:
                raise RuntimeError(data["error"])
            return data
        except urllib.error.HTTPError as e:
            if attempt == retries - 1:
                raise
            # 429 / 503: aspetto quanto chiede il server (Retry-After), altrimenti backoff esponenziale
            attesa = int(e.headers.get("Retry-After") or 0) or min(300, 15 * 2 ** attempt)
            print(f"  HTTP {e.code}, attendo {attesa}s", file=sys.stderr)
            time.sleep(attesa)
        except Exception:  # rete instabile o maxlag
            if attempt == retries - 1:
                raise
            time.sleep(5 * 2 ** attempt)


def category_members(cat):
    pages, cont = [], {}
    while True:
        d = api({"action": "query", "list": "categorymembers", "cmtitle": "Categoria:" + cat,
                 "cmnamespace": 0, "cmlimit": 500, **cont})
        pages += [(p["pageid"], p["title"]) for p in d["query"]["categorymembers"]]
        if "continue" not in d:
            return pages
        cont = d["continue"]


def random_pages(n):
    seen = {}
    while len(seen) < n:
        d = api({"action": "query", "list": "random", "rnnamespace": 0, "rnlimit": 500})
        for p in d["query"]["random"]:
            seen[p["id"]] = p["title"]
    return list(seen.items())


BLOCCO = 50  # voci per richiesta (massimo consentito dall'API per il contenuto delle revisioni)


def fetch_blocco(pageids):
    """Wikitext di un blocco di voci: {pageid: (titolo, revid, testo in chiaro)}."""
    d = api({"action": "query", "prop": "revisions", "rvprop": "ids|content", "rvslots": "main",
             "pageids": "|".join(map(str, pageids))})
    out = {}
    for p in d["query"]["pages"]:
        if "revisions" not in p:
            continue
        rev = p["revisions"][0]
        out[p["pageid"]] = (p["title"], rev["revid"], wikitext_a_testo(rev["slots"]["main"]["content"]))
    return out


# sezioni finali fatte quasi solo di link e riferimenti: il testo viene troncato alla prima di queste
SEZIONI_FINALI = ("note", "bibliografia", "voci correlate", "collegamenti esterni", "altri progetti",
                  "fonti", "riferimenti", "approfondimenti")
NAMESPACE_SCARTATI = ("file:", "immagine:", "image:", "categoria:", "category:", "media:")


def _link(m):
    destinazione, etichetta = m.group(1), m.group(2)
    if destinazione.strip().lower().startswith(NAMESPACE_SCARTATI):
        return ""
    return etichetta if etichetta is not None else destinazione


def wikitext_a_testo(w):
    """Conversione semplificata wikitext -> testo in chiaro (toglie template, tabelle, note, markup)."""
    if re.match(r"\s*#(RINVIA|RINVIO|REDIRECT)", w, re.I):
        return ""
    w = re.sub(r"<!--.*?-->", "", w, flags=re.S)
    w = re.sub(r"<ref[^>/]*/>", "", w)
    w = re.sub(r"<ref[^>]*>.*?</ref>", "", w, flags=re.S)
    w = re.sub(r"<(gallery|math|syntaxhighlight|source|timeline|imagemap)[^>]*>.*?</\1>", "", w, flags=re.S | re.I)
    # template e tabelle possono essere annidati: tolgo prima i piu' interni, finche' ce ne sono
    for schema in (r"\{\{[^{}]*\}\}", r"\{\|(?:(?!\{\|)(?!\|\}).)*\|\}"):
        prima = None
        while prima != w:
            prima = w
            w = re.sub(schema, "", w, flags=re.S)
    # link interni (anche annidati nelle didascalie delle immagini)
    prima = None
    while prima != w:
        prima = w
        w = re.sub(r"\[\[([^\[\]|]*)(?:\|([^\[\]]*))?\]\]", _link, w)
    w = re.sub(r"\[https?://\S+ ([^\]]*)\]", r"\1", w)  # link esterni con etichetta
    w = re.sub(r"\[https?://\S+\]", "", w)
    w = re.sub(r"<[^>]+>", "", w)  # tag html rimasti
    w = re.sub(r"'{2,}", "", w)    # grassetto e corsivo
    w = re.sub(r"__[A-Z]+__", "", w)
    righe = []
    for riga in w.splitlines():
        riga = riga.strip()
        h = re.match(r"^(=+)\s*(.*?)\s*\1$", riga)
        if h:
            if h.group(2).lower() in SEZIONI_FINALI:
                break
            riga = h.group(2)
        riga = re.sub(r"^[*#:;]+\s*", "", riga)
        righe.append(riga)
    testo = "\n".join(righe)
    testo = re.sub(r"[ \t]+", " ", testo)
    return re.sub(r"\n{3,}", "\n\n", testo).strip()


def filename(title):
    # i nomi file non possono contenere / ; tolgo anche caratteri problematici su Windows
    return re.sub(r'[/\\:*?"<>|]', "_", title) + ".txt"


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--random", type=int, default=3000, help="numero di voci casuali da scaricare")
    ap.add_argument("--out", default="dataset-wiki")
    ap.add_argument("--manifest", default="eval/manifest.tsv")
    ap.add_argument("--max-voci", type=int, default=None,
                    help="limite di voci da scaricare (in ordine di pageid); quelle gia' in cache contano")
    args = ap.parse_args()

    os.makedirs(args.out, exist_ok=True)
    os.makedirs(os.path.dirname(args.manifest), exist_ok=True)
    # lista delle pagine e testi gia' scaricati vengono salvati, cosi' se il download si interrompe riparte da li'
    file_pagine = os.path.join(args.out, ".pagine.json")
    file_cache = os.path.join(args.out, ".cache.jsonl")

    if os.path.exists(file_pagine):
        with open(file_pagine, encoding="utf-8") as f:
            salvato = json.load(f)
        titles = {int(k): v for k, v in salvato["titles"].items()}
        topics_of = {int(k): set(v) for k, v in salvato["topics"].items()}
    else:
        # 1. membri delle categorie: pageid -> insieme di codici argomento
        topics_of, titles = {}, {}
        for code, cat in TOPICS.items():
            members = category_members(cat)
            print(f"{code:4} {cat:45} {len(members):4} voci", file=sys.stderr)
            for pid, title in members:
                topics_of.setdefault(pid, set()).add(code)
                titles[pid] = title
        # 2. voci casuali (se una voce casuale e' anche in una categoria, resta etichettata con la categoria)
        for pid, title in random_pages(args.random):
            titles.setdefault(pid, title)
        with open(file_pagine, "w", encoding="utf-8") as f:
            json.dump({"titles": titles, "topics": {k: sorted(v) for k, v in topics_of.items()}}, f)

    testi = {}
    if os.path.exists(file_cache):
        with open(file_cache, encoding="utf-8") as f:
            for riga in f:
                r = json.loads(riga)
                testi[r["pageid"]] = r

    # 3. download del testo, BLOCCO voci per richiesta
    mancanti = [pid for pid in sorted(titles) if pid not in testi]
    if args.max_voci is not None:
        mancanti = mancanti[:max(0, args.max_voci - len(testi))]
    print(f"Voci totali {len(titles)}, gia' scaricate {len(testi)}, da scaricare {len(mancanti)}", file=sys.stderr)
    with open(file_cache, "a", encoding="utf-8") as cache:
        for i in range(0, len(mancanti), BLOCCO):
            blocco = mancanti[i:i + BLOCCO]
            scaricati = fetch_blocco(blocco)
            for pid in blocco:  # le voci cancellate nel frattempo restano con testo vuoto e vengono scartate
                title, revid, text = scaricati.get(pid, (titles[pid], None, ""))
                r = {"pageid": pid, "title": title, "revid": revid, "text": text}
                testi[pid] = r
                cache.write(json.dumps(r, ensure_ascii=False) + "\n")
            cache.flush()
            print(f"  {min(i + BLOCCO, len(mancanti))}/{len(mancanti)}", file=sys.stderr, flush=True)

    # 4. scrittura dei file .txt e del manifest
    rows = []
    for pid, r in testi.items():
        if not r["title"] or len(r["text"]) < MIN_CHARS:
            continue
        name = filename(r["title"])
        with open(os.path.join(args.out, name), "w", encoding="utf-8") as f:
            f.write(r["text"])
        rows.append((name, pid, r["revid"], len(r["text"]), ",".join(sorted(topics_of.get(pid, [])))))

    rows.sort()
    with open(args.manifest, "w", encoding="utf-8") as f:
        f.write("file\tpageid\trevid\tchars\ttopics\n")
        for r in rows:
            f.write("\t".join(map(str, r)) + "\n")
    print(f"Salvati {len(rows)} documenti in {args.out}/, manifest in {args.manifest}", file=sys.stderr)


if __name__ == "__main__":
    main()
