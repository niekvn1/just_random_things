import hashlib
import random
import sys

class User:
    def __init__(self, id, username, salt, password):
        self.id = id
        self.username = username
        self.password = password
        self.salt = salt

    def check_hash(self, password):
        m = hashlib.sha512()
        m.update((self.salt + password).encode())
        return m.digest() == self.password


    def printv(self):
        print("id: {}, username: {}, salt: {}, password: {}".format(self.id, self.username, self.salt, self.password))


def pass_hash(password):
    salt = hex(random.randint(0, sys.maxsize))
    m = hashlib.sha512()
    m.update((salt + password).encode())
    return salt, m.digest()


if __name__ == "__main__":
    salt, password = pass_hash('Dodo')
    user = User(-1, 'John', salt, password)
    print(user.check_hash('Dodo'))
    print(user.check_hash('Odod'))
