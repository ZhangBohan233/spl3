package interpreter.env;

import interpreter.types.TypeValue;

public class FunctionEnvironment extends MainAbstractEnvironment {

    public final Environment callingEnv;
    private TypeValue returnValue;

    public FunctionEnvironment(Environment definitionEnv, Environment callingEnv) {
        super(definitionEnv.memory, definitionEnv);

        this.callingEnv = callingEnv;
    }

    public TypeValue getReturnValue() {
        return returnValue;
    }

    @Override
    public void setReturn(TypeValue typeValue) {
        returnValue = typeValue;
    }

    @Override
    public boolean interrupted() {
        return returnValue != null;
    }
}
