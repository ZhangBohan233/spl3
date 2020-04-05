package interpreter.env;

import interpreter.Memory;

public class FunctionEnvironment extends MainAbstractEnvironment {

    public FunctionEnvironment(Memory memory, Environment outer, int begins, int size) {
        super(memory, outer);
    }
}
