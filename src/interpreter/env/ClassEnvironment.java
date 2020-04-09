package interpreter.env;

import interpreter.Memory;
import interpreter.types.TypeValue;

import java.util.Map;

public class ClassEnvironment extends MainAbstractEnvironment {

    private ClassEnvironment superclassEnv;

    public ClassEnvironment(Environment outer, ClassEnvironment superclassEnv) {
        super(outer.getMemory(), outer);

        this.superclassEnv = superclassEnv;
    }

    public InstanceEnvironment createInstanceEnv() {
        InstanceEnvironment ie = new InstanceEnvironment(outer);
        ie.variables.putAll(variables);
        ie.constants.putAll(constants);
        ie.variables.replaceAll((k, v) -> v.copy());
        ie.variables.replaceAll((k, v) -> v.copy());
        return ie;
    }

    public void inherit(ClassEnvironment subclassEnv) {

    }
}
