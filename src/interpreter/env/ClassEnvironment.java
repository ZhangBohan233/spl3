package interpreter.env;

import interpreter.Memory;
import interpreter.types.TypeValue;

import java.util.Map;

public class ClassEnvironment extends MainAbstractEnvironment {

    public ClassEnvironment(Environment outer) {
        super(outer.getMemory(), outer);
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
        for (Map.Entry<String, TypeValue> entry : constants.entrySet()) {
            subclassEnv.constants.put(entry.getKey(), entry.getValue().copy());
        }
        for (Map.Entry<String, TypeValue> entry : variables.entrySet()) {
            subclassEnv.variables.put(entry.getKey(), entry.getValue().copy());
        }
    }
}
