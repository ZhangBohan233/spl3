package interpreter.splObjects;

import interpreter.env.InstanceEnvironment;
import interpreter.types.ClassType;

public class Instance extends SplObject {

    private InstanceEnvironment env;
    private ClassType type;

    public Instance(ClassType type, InstanceEnvironment env) {
        this.type = type;
        this.env = env;
    }

    public InstanceEnvironment getEnv() {
        return env;
    }

    public ClassType getType() {
        return type;
    }
}
