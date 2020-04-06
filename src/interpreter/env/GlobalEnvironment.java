package interpreter.env;

import interpreter.EnvironmentError;
import interpreter.Memory;

public class GlobalEnvironment extends MainAbstractEnvironment {
    public GlobalEnvironment(Memory memory) {
        super(memory, null);
    }

    @Override
    public void setReturn(TypeValue typeValue) {
        throw new EnvironmentError("Return outside function. ");
    }
}
