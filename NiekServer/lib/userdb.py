import sqlite3
import user

USER_DATABASE_NAME = '/data/niekserver/databases/user.db'


def insert(user):
    db = sqlite3.connect(USER_DATABASE_NAME)
    c = db.cursor()
    c.execute("INSERT INTO users VALUES (:id, :username, :salt, :password)", {'id': None, 'username': user.username, 'salt': user.salt, 'password': user.password})
    db.commit()
    db.close()


def delete(user):
    if user.id >= 0:
        db = sqlite3.connect(USER_DATABASE_NAME)
        c = db.cursor()
        c.execute("DELETE FROM users WHERE id = :id", {'id': user.id})
        db.commit()
        db.close()


def update(user):
    if user.id >= 0:
        db = sqlite3.connect(USER_DATABASE_NAME)
        c = db.cursor()
        c.execute("UPDATE users SET username = :username WHERE id = :id", {'id': user.id, 'username': user.username})
        c.execute("UPDATE users SET password = :password WHERE id = :id", {'id': user.id, 'password': user.password})
        db.commit()
        db.close()


def get_user_by_username(username):
    db = sqlite3.connect(USER_DATABASE_NAME)
    c = db.cursor()
    c.execute("SELECT * FROM users WHERE username = :username", {'username': username})
    values = c.fetchone()
    db.close()
    if values is None:
        return None

    return user.User(*values)


def get_user_by_id(id):
    db = sqlite3.connect(USER_DATABASE_NAME)
    c = db.cursor()
    c.execute("SELECT * FROM users WHERE id = :id", {'id': id})
    values = c.fetchone()
    db.close()
    if values is None:
        return None

    return user.User(*values)


def get_all_users():
    db = sqlite3.connect(USER_DATABASE_NAME)
    c = db.cursor()
    c.execute("SELECT * FROM users")
    user_values = c.fetchall()
    users = []
    for values in user_values:
        users.append(user.User(*values))

    return users


if __name__ == "__main__":
    db = sqlite3.connect(USER_DATABASE_NAME)
    c = db.cursor()

    c.execute("CREATE TABLE users (id INTEGER PRIMARY KEY, username TEXT, salt TEXT, password TEXT)")

    db.commit()
    db.close()
