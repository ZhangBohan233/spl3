package interpreter.env;

import interpreter.Memory;

public class InstanceEnvironment extends MainAbstractEnvironment {
    InstanceEnvironment(Environment outer) {
        super(outer.getMemory(), outer);
    }
}
