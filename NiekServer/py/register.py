import sys

sys.path.insert(0, '/data/niekserver/lib')
from http import _POST
from http import _USER
from http import html
from http import java
from general import redirect
from general import make_page
import user as u
import userdb as db

name = "Register"

content_head = """
<h1>Register</h1>
<p>Please fill in this form to sign up.</p>
"""

form = " \
<form method='POST' style='display: inline;'> \
    <pre style='display: inline;'>Username:\t</pre> \
    <input type='text' name='username' value='{}'>{}<br> \
    <pre style='display: inline;'>Password:\t\t</pre> \
    <input type='password' name='password'>{}<br><br> \
    <input type=submit value='Register'> \
</form> \
"

username_inuse = "<pre class='error' style='display: inline;'> Username already used</pre>"
invalid_pass = "<pre class='error' style='display: inline;'> Invalid password</pre>"

if "username" in _USER:
    html(redirect("Home", '/'))
    exit()

if "username" not in _POST and "password" not in _POST:
    html(make_page(name, content_head + form.format("", "", "")))
    exit()

if "password" not in _POST:
    html(make_page(name, content_head + form.format(_POST["username"], "", invalid_pass)))
    exit()

user = db.get_user_by_username(_POST["username"])
if user is None:
    salt, password = u.pass_hash(_POST["password"])
    user = u.User(-1, _POST["username"], salt, password)
    db.insert(user)
    html(redirect("Home", '/'))
    java("action=login")
    exit()
else:
    html(make_page(name, content_head + form.format(_POST["username"], username_inuse, "")))
    exit()
