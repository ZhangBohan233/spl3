package interpreter.env;

import interpreter.Memory;

public class MainAbstractEnvironment extends Environment {
    public MainAbstractEnvironment(Memory memory, Environment outer) {
        super(memory, outer);
    }
}
