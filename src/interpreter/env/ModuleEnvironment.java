package interpreter.env;

import interpreter.Memory;

public class ModuleEnvironment extends MainAbstractEnvironment {

    public ModuleEnvironment(Environment outer) {
        super(outer.memory, outer);
    }

    @Override
    public void setReturn(TypeValue typeValue) {
        throw new EnvironmentError("Return outside function. ");
    }
}
