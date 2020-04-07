package interpreter.env;

import interpreter.Memory;

public abstract class MainAbstractEnvironment extends Environment {

    public MainAbstractEnvironment(Memory memory, Environment outer) {
        super(memory, outer);
    }

    @Override
    public void defineFunction(String name, TypeValue funcTv) {
        if (alreadyDefined(name)) throw new EnvironmentError();

        variables.put(name, funcTv);
    }
}
