def get_page(name="", css="", icon="", body=""):
    if name != "":
        name = "<title>{}</title>".format(name)

    if css != "":
        css = "<link rel='stylesheet' type='text/css' href='{}' />".format(css)

    if icon != "":
        icon = "<link rel='shortcut icon' href='{}' type='image/x-icon' /> \
            <link rel='icon' href='{}' type='image/x-icon' />".format(icon, icon)

    return " \
    <!DOCTYPE html> \
    <html lang='en'> \
        <head> \
            {} \
            <meta charset='UTF-8' /> \
            <meta name='viewport' content='width=device-width, initial-scale=1.0' /> \
            {} \
            {} \
        </head> \
    \
        <body> \
        {} \
        </body> \
    </html> \
    ".format(name, css, icon, body)
