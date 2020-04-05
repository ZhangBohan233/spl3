package interpreter.env;

import interpreter.Memory;
import interpreter.Type;

public class MainAbstractEnvironment extends Environment {
    public MainAbstractEnvironment(Memory memory, Environment outer) {
        super(memory, outer);
    }
}
