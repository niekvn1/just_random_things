import sys

sys.path.insert(0, '/data/niekserver/lib')
from http import _POST
from http import _USER
from http import html
from http import java
from general import redirect
from general import make_page
from urllib.parse import unquote

name = "Server Connect"

content_head = """
<h1>Server Connect</h1>
"""

form = " \
<form method='POST' style='display: inline;'> \
    <pre style='display: inline;'>Domain/IP:\t</pre> \
    <input type='text' name='url' value=''> \
    <pre style='display: inline;'>Port:</pre> \
    <input type='text' name='port' value=''> \
    <input type=submit value='Connect'> \
</form> \
"

result = " \
<pre>Domain:\t\t{}</pre> \
<pre>IP:\t\t\t{}</pre> \
<pre>Port:\t\t\t{}</pre> \
\
<ul class='terminal'> \
<li><form> \
<input class='terminal' type='text' name='command', value=''> \
</form></li> \
<li>Test</li> \
</ul> \
"

def connect(url, port):
    import socket
    import re

    if url[:7] == "http://":
        url = url[7:]

    pattern = re.compile(r'[a-zA-Z0-9.-]+')
    if pattern.match(url):
        ip = str(socket.gethostbyname(url))
        html(make_page(name, content_head + result.format(url, ip, port)))
    else:
        html("Invalid Domain/IP")

if "username" not in _USER:
    html(redirect("Login", '/py/login.py'))
elif "url" not in _POST or "port" not in _POST:
    html(make_page(name, content_head + form))
else:
    connect(unquote(_POST["url"]), _POST["port"])
