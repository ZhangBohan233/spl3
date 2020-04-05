package interpreter.env;

import interpreter.Type;
import interpreter.primitives.Primitive;

public class TypeValue {

    private Type type;
    private Primitive value;

    public TypeValue(Type type) {
        this.type = type;
    }

    public TypeValue(Type type, Primitive value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public Primitive getValue() {
        return value;
    }

    public void setValue(Primitive value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" + value + ": " + type + "}";
    }
}
