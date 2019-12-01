package knickknacker.remotefunctioncalls;

import java.io.Serializable;

/** This class is a data structure to fill with function arguments for
 * remote functions. (Functions on the other side of a TCP connection
 * for example). */
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

    /** Get the next argument and cast it to cls. */
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
