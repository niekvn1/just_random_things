import userdb
import user

salt1, pass1 = user.pass_hash('niek is cool')
salt2, pass2 = user.pass_hash('niek is hot')
user1 = user.User(-1, 'niekvn1', salt1, pass1)
user2 = user.User(-1, 'niekvn2', salt2, pass2)

userdb.insert(user1)
userdb.insert(user2)

users = userdb.get_all_users()
for user in users:
    user.printv()
    print(user.check_hash('niek is cool'))

userdb.delete(users[0])

users = userdb.get_all_users()
for user in users:
    user.printv()
    print(user.check_hash('niek is cool'))
