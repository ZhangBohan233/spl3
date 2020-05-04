package interpreter.types;

import interpreter.primitives.Bool;
import interpreter.types.Type;
import interpreter.primitives.Primitive;

public class TypeValue {

    public static final TypeValue VOID_NULL = new TypeValue(PrimitiveType.TYPE_VOID);
    public static final TypeValue BOOL_TRUE = new TypeValue(PrimitiveType.TYPE_BOOLEAN, Bool.TRUE);
    public static final TypeValue BOOL_FALSE = new TypeValue(PrimitiveType.TYPE_BOOLEAN, Bool.FALSE);

    private final Type type;
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

    public TypeValue copy() {
        return new TypeValue(type, value);
    }

    @Override
    public String toString() {
        return "{" + value + ": " + type + "}";
    }
}
