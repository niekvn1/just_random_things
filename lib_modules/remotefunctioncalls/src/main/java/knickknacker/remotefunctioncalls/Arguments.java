package knickknacker.remotefunctioncalls;

import java.io.Serializable;

public class Arguments implements Serializable {
    private Serializable[] args;
    private int cursor = 0;

    public Arguments(Arguments args) {
        this.args = args.getArgs();
    }

    public Arguments(Serializable... args) {
        this.args = args;
    }

    public Serializable[] getArgs() {
        return args;
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
