package knickknacker.remotefunctioncalls;

import java.io.Serializable;

/** This class forms a Remote Function Call, the name
 * of the function that will be called is stored in here
 * together with the arguments for that function. */
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
