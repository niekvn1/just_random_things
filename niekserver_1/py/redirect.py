def get_redirect(name, css, icon, redirect):
    return "<!DOCTYPE html> \
    <html lang='en'> \
        <head> \
            <title>{}</title> \
            <meta charset='UTF-8' /> \
            <meta name='viewport' content='width=device-width, initial-scale=1.0' /> \
            <meta http-equiv='refresh' content='0; url={}' /> \
            <link rel='stylesheet' type='text/css' href='{}' /> \
            <link rel='shortcut icon' href='{}' type='image/x-icon' /> \
            <link rel='icon' href='{}' type='image/x-icon' /> \
        </head> \
        \
        <body> \
            <p><a href='{}'>{}</a></p> \
        </body> \
    </html> \
    ".format(name, redirect, css, icon, icon, redirect, name)
