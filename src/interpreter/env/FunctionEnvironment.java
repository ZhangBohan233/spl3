package interpreter.env;

import interpreter.types.TypeValue;

public class FunctionEnvironment extends MainAbstractEnvironment {

    public final Environment callingEnv;
    public final String definedName;
    private TypeValue returnValue;

    public FunctionEnvironment(Environment definitionEnv, Environment callingEnv, String definedName) {
        super(definitionEnv.memory, definitionEnv);

        this.callingEnv = callingEnv;
        this.definedName = definedName;
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

    @Override
    public String toString() {
        return "FunctionEnv '" + definedName + "'";
    }
}
