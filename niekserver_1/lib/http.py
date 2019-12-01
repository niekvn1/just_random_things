import sys
import json

_GET = {}
_POST = {}
_USER = {}


def html(string):
    print("@NiekServer:HTML\n" + string)


def java(string):
    print("@NiekServer:JAVA\n" + string)


if len(sys.argv) > 1:
    for args in sys.argv[1:]:
        arg = args.split("=")
        if len(arg) > 1:
            if arg[0] == "POST":
                _POST = json.loads(arg[1])
            elif arg[0] == "GET":
                _GET = json.loads(arg[1])
            elif arg[0] == "USER":
                _USER = json.loads(arg[1])
