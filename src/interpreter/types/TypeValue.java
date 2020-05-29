package interpreter.types;

import interpreter.primitives.Bool;
import interpreter.primitives.Pointer;
import interpreter.types.Type;
import interpreter.primitives.Primitive;

import java.util.Objects;

public class TypeValue {

    public static final TypeValue VOID = new TypeValue(PrimitiveType.TYPE_VOID);
    public static final TypeValue POINTER_NULL = new TypeValue(NullType.NULL_TYPE, Pointer.NULL_PTR);
    public static final TypeValue BOOL_TRUE = new TypeValue(PrimitiveType.TYPE_BOOLEAN, Bool.TRUE);
    public static final TypeValue BOOL_FALSE = new TypeValue(PrimitiveType.TYPE_BOOLEAN, Bool.FALSE);
    public static final TypeValue INTERRUPTED = new TypeValue(PrimitiveType.TYPE_VOID, Pointer.NULL_PTR);

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeValue typeValue = (TypeValue) o;

        if (!Objects.equals(type, typeValue.type)) return false;
        return Objects.equals(value, typeValue.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
