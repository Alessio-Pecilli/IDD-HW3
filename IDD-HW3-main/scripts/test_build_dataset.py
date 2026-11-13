"""Test del convertitore wikitext -> testo usato per costruire il dataset.

Uso: python3 -m unittest discover -s scripts
"""
import unittest

from build_dataset import filename, wikitext_a_testo


class WikitextTest(unittest.TestCase):

    def test_link_interni(self):
        self.assertEqual("un imperatore di Roma", wikitext_a_testo("un [[imperatore romano|imperatore]] di [[Roma]]"))

    def test_grassetto_corsivo(self):
        self.assertEqual("Traiano fu imperatore", wikitext_a_testo("'''Traiano''' fu ''imperatore''"))

    def test_template_annidati(self):
        self.assertEqual("testo", wikitext_a_testo("{{Infobox|nome={{lang|la|Traianus}}}}testo"))

    def test_note(self):
        self.assertEqual("frase.", wikitext_a_testo('frase.<ref name="a">fonte</ref><ref name="b"/>'))

    def test_tabelle(self):
        self.assertEqual("prima\n\ndopo", wikitext_a_testo("prima\n{| class=\"wikitable\"\n| a || b\n|}\ndopo"))

    def test_immagini_e_categorie_tolte(self):
        w = "testo\n[[File:a.jpg|thumb|didascalia con [[link]]]]\n[[Categoria:Imperatori romani]]"
        self.assertEqual("testo", wikitext_a_testo(w))

    def test_titoli_e_elenchi(self):
        self.assertEqual("Biografia\nNato a Italica", wikitext_a_testo("== Biografia ==\n* Nato a [[Italica]]"))

    def test_sezioni_finali_troncate(self):
        w = "corpo\n== Note ==\n<references/>\n== Collegamenti esterni ==\n* [http://x.it sito]"
        self.assertEqual("corpo", wikitext_a_testo(w))

    def test_link_esterni(self):
        self.assertEqual("vedi il sito", wikitext_a_testo("vedi [https://example.org il sito][https://x.org]"))

    def test_redirect_vuoto(self):
        self.assertEqual("", wikitext_a_testo("#RINVIA [[Altra voce]]"))

    def test_commenti_html(self):
        self.assertEqual("visibile", wikitext_a_testo("<!-- nascosto -->visibile"))

    def test_nome_file(self):
        self.assertEqual("AC_DC.txt", filename("AC/DC"))
        self.assertEqual("Io (astronomia).txt", filename("Io (astronomia)"))


if __name__ == "__main__":
    unittest.main()
