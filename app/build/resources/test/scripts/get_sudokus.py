from bs4 import BeautifulSoup 
import os

import requests

BASE_URL = "http://lipas.uwasa.fi/~timan/sudoku/"
SCRIPT_PATH = os.path.dirname(os.path.abspath(__file__))

soup = BeautifulSoup(requests.get(BASE_URL).text, "html.parser")

for link in soup.find_all('a'):
    href = link.get('href')
    # quick and dirty
    if href.startswith("s") and "_" not in href and "zip" not in href:
        txt_url = BASE_URL + href
        with open(os.path.join(SCRIPT_PATH, "..", "sudokus", href), "w") as f:
            f.write(requests.get(txt_url).text)


