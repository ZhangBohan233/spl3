package interpreter.splObjects;

import interpreter.env.ModuleEnvironment;

public class Module extends SplObject {

    private ModuleEnvironment env;

    public Module(ModuleEnvironment env) {
        this.env = env;
    }

    public ModuleEnvironment getEnv() {
        return env;
    }

    @Override
    public String toString() {
        return "Module";
    }
}
