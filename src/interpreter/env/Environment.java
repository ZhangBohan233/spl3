package interpreter.env;

import interpreter.*;
import interpreter.types.Type;
import interpreter.types.TypeError;
import interpreter.types.TypeValue;
import util.LineFile;

import java.util.HashMap;
import java.util.Map;

public abstract class Environment {

    protected Environment outer;
    protected Memory memory;

    protected Map<String, TypeValue> variables = new HashMap<>();
    protected Map<String, TypeValue> constants = new HashMap<>();

    public Environment(Memory memory, Environment outer) {
        this.memory = memory;
        this.outer = outer;
    }

    public Memory getMemory() {
        return memory;
    }

    public abstract void defineFunction(String name, TypeValue funcTv);

    public abstract void setReturn(TypeValue typeValue);

    public void defineVar(String name, Type type, LineFile lineFile) {
        if (innerGet(name, true) != null)
            throw new EnvironmentError("Variable '" + name + "' already defined. ", lineFile);

        TypeValue typeValue = new TypeValue(type);
        variables.put(name, typeValue);
    }

    public void setVar(String name, TypeValue newTypeValue) {
        TypeValue typeValue = get(name);
        if (typeValue == null) throw new EnvironmentError("Variable '" + name + "' is not defined in this scope. ");

        if (typeCheck(typeValue.getType(), newTypeValue.getType())) {
            typeValue.setValue(newTypeValue.getValue());
        } else {
            throw new TypeError();
        }
    }

    public TypeValue get(String name) {
        TypeValue tv = innerGet(name, true);
        if (tv == null) {
            throw new EnvironmentError("Name '" + name + "' not found. ");
        }
        return tv;
    }

    protected TypeValue innerGet(String name, boolean isFirst) {
        TypeValue tv = constants.get(name);
        if (tv == null) tv = variables.get(name);
        if (tv == null) {
            if (outer != null) {
                tv = outer.innerGet(name, false);
            }
            if (isFirst && tv == null) {
                tv = searchInNamespaces(name);
            }
        }
        return tv;
    }

    public void printVars() {
        System.out.println(variables);
    }

    public Environment getOuter() {
        return outer;
    }

    private boolean typeCheck(Type bigger, Type smaller) {
        return true;
    }

    protected boolean alreadyDefined(String name) {
        return false;
    }

    public abstract void addNamespace(ModuleEnvironment moduleEnvironment);

    protected abstract TypeValue searchInNamespaces(String name);

    protected abstract void setInNamespaces(String name, TypeValue typeValue);
}
