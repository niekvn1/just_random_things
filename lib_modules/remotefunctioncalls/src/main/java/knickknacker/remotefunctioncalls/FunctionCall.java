package knickknacker.remotefunctioncalls;

import java.io.Serializable;

public class FunctionCall extends Message {
    private String func;
    private Arguments args;

    public FunctionCall(String func, Arguments args) {
        this.func = func;
        this.args = args;
    }

    public FunctionCall(String func, Serializable... args) {
        this.func = func;
        this.args = new Arguments(args);
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public Arguments getArgs() {
        return args;
    }

    public void setArgs(Arguments args) {
        this.args = args;
    }
}
