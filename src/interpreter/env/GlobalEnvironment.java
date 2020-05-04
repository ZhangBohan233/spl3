package interpreter.env;

import interpreter.Memory;

public class GlobalEnvironment extends MainAbstractEnvironment {
    public GlobalEnvironment(Memory memory) {
        super(memory, null);
    }

    @Override
    public boolean interrupted() {
        return false;
    }
}
