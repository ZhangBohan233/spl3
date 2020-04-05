package interpreter.env;

import interpreter.EnvironmentError;
import interpreter.Memory;
import interpreter.Type;
import interpreter.TypeError;
import interpreter.primitives.Primitive;

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

    public void defineVar(String name, Type type) {
//        if (variables.containsKey(name)) throw new EnvironmentError("Variable '" + name + "' already defined. ");
        // TODO:

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
        TypeValue tv = constants.get(name);
        if (tv == null) tv = variables.get(name);
        if (tv == null) {
            if (outer == null) throw new EnvironmentError("Name '" + name + "' not found. ");
            else return outer.get(name);
        } else {
            return tv;
        }
    }

    public void printVars() {
        System.out.println(variables);
    }

    private boolean typeCheck(Type bigger, Type smaller) {
        return true;
    }
}
