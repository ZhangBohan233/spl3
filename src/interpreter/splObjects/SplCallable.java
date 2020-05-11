package interpreter.splObjects;

import ast.Arguments;
import interpreter.env.Environment;
import interpreter.types.CallableType;
import interpreter.types.TypeValue;

public abstract class SplCallable extends SplObject {

    protected final CallableType funcType;

    public SplCallable(CallableType funcType) {
        this.funcType = funcType;
    }

    public abstract TypeValue call(Arguments arguments, Environment callingEnv);

    public CallableType getFuncType() {
        return funcType;
    }
}
