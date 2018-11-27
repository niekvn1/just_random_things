import sys

sys.path.insert(0, '/data/niekserver/lib')
from http import html
from http import java
from general import redirect

html(redirect("Home", '/'))
java("action=logout")
