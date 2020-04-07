package interpreter.env;

import interpreter.Memory;
import interpreter.types.TypeValue;

public abstract class MainAbstractEnvironment extends Environment {

    public MainAbstractEnvironment(Memory memory, Environment outer) {
        super(memory, outer);
    }

    @Override
    public void defineFunction(String name, TypeValue funcTv) {
        if (alreadyDefined(name)) throw new EnvironmentError();

        variables.put(name, funcTv);
    }

    @Override
    public void setReturn(TypeValue typeValue) {
        throw new EnvironmentError("Return outside function. ");
    }
}
