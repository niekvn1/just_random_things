from html_format import get_page
from redirect import get_redirect

css = "/css/dark.css"
icon = "/icons/favicon.ico"

def make_page(name, body):
    body = "<div class='wrapper'> \
    <div class='header' id='header'><h3>My Header</h3></div> \
    <div class='content'>{}</div> \
    </div> \
    \
    <script> \
    window.onscroll = function() {{headerFunc()}}; \
    \
    var header = document.getElementById('header'); \
    \
    var sticky = header.offsetTop; \
    \
    function headerFunc() {{ \
        if (window.pageYOffset > sticky) {{ \
            header.classList.add('sticky'); \
        }} else {{ \
            header.classList.remove('sticky'); \
        }} \
    }} \
    </script> \
    \
    ".format(body)
    return get_page(name, css, icon, body)

def redirect(name, redirect):
    return get_redirect(name, css, icon, redirect)
