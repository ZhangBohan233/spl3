package interpreter.env;

import interpreter.types.TypeValue;

public class FunctionEnvironment extends MainAbstractEnvironment {

    private TypeValue returnValue;

    public FunctionEnvironment(Environment outer) {
        super(outer.memory, outer);
    }

    public TypeValue getReturnValue() {
        return returnValue;
    }

    @Override
    public void setReturn(TypeValue typeValue) {
        returnValue = typeValue;
    }
}
