import sys

sys.path.insert(0, '/data/niekserver/lib')
from http import _POST
from http import _USER
from http import html
from http import java
from general import make_page
from general import redirect
import user as u
import userdb as db

name = "Login"

content_head = """
<h1>Login</h1>
"""

form = " \
<form method='POST' style='display: inline;'> \
    {} \
    <pre style='display: inline;'>Username:\t</pre> \
    <input type='text' name='username' value='{}'><br> \
    <pre style='display: inline;'>Password:\t\t</pre> \
    <input type='password' name='password'><br><br> \
    <input type=submit value='Login'> \
</form> \
"

invalid_login = "<pre class='error' style='display: inline;'>Username or password is incorrect.</pre><br>"

if "username" in _USER:
    html(redirect("Home", '/'))
    exit()

if "username" not in _POST and "password" not in _POST:
    html(make_page(name, content_head + form.format("<br>", "")))
    exit()

if "password" not in _POST:
    html(make_page(name, content_head + form.format(invalid_login, _POST["username"])))
    exit()

user = db.get_user_by_username(_POST["username"])
if user is None:
    html(make_page(name, content_head + form.format(invalid_login, _POST["username"])))
    exit()
elif not user.check_hash(_POST["password"]):
    html(make_page(name, content_head + form.format(invalid_login, _POST["username"])))
    exit()
else:
    html(redirect("Home", '/'))
    java("action=login")
    exit()
