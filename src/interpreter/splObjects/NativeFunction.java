package interpreter.splObjects;

import ast.Arguments;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.types.CallableType;
import interpreter.types.TypeValue;
import util.LineFile;

public abstract class NativeFunction extends SplCallable {

    private final String name;
    private final int leastArg;
    private final int mostArg;

    public NativeFunction(String name, CallableType funcType, int leastArg, int mostArg) {
        super(funcType);
        this.name = name;
        this.leastArg = leastArg;
        this.mostArg = mostArg;
    }

    public NativeFunction(String name, CallableType funcType, int argCount) {
        super(funcType);
        this.name = name;
        leastArg = argCount;
        mostArg = argCount;
    }

    protected abstract TypeValue callFunc(Arguments arguments, Environment callingEnv);

    public TypeValue call(Arguments arguments, Environment callingEnv) {
        int argc = arguments.getLine().getChildren().size();
        if (argc < leastArg || argc > mostArg) {
            if (leastArg == mostArg) {
                throw new SplException(
                        String.format("Function '%s' expects %d argument(s), got %d. ",
                                name, leastArg, argc));
            } else {
                throw new SplException(
                        String.format("Function '%s' expects %d to %d arguments, got %d. ",
                                name, leastArg, mostArg, argc));
            }
        }

        return callFunc(arguments, callingEnv);
    }

    @Override
    public String toString() {
        return "NativeFunction{" + name + "}";
    }
}
