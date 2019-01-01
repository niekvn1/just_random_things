package knickknacker.remotefunctioncalls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FunctionCaller {
    protected Object executor;

    public FunctionCaller() {
        this.executor = this;
    }

    public FunctionCaller(Object executor) {
        this.executor = executor;
    }

    public void execute(FunctionCall call) {
        String func = call.getFunc();
        Arguments args = call.getArgs();
        try {
            Class<?> cls = executor.getClass();
            Method method = cls.getDeclaredMethod(func, Arguments.class);
            method.invoke(executor, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
