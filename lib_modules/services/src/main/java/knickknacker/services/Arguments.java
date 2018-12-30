package knickknacker.services;

import java.io.Serializable;

public class Arguments implements Serializable {
    private Serializable[] args;
    private int cursor = 0;
    private boolean notfull = true;

    public Arguments(int size) {
        args = new Serializable[size];
    }

    public void push(Serializable... objects) {
        for (Serializable object : objects) {
            if (notfull) {
                args[cursor] = object;
                cursor += 1;
                if (cursor == args.length) {
                    notfull = false;
                    cursor = 0;
                }
            }
        }
    }

    public <T> T pop(Class<T> cls) {
        if (cursor == args.length) {
            return null;
        }

        Serializable object = args[cursor];
        cursor += 1;
        return cast(object, cls);
    }

    public int length() {
        return args.length;
    }

    private <T> T cast(Object o, Class<T> clazz) {
        return clazz.isInstance(o) ? clazz.cast(o) : null;
    }
}
