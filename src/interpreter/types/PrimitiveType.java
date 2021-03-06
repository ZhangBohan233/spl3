package interpreter.types;

import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.primitives.*;

public class PrimitiveType implements Type {

    public static final PrimitiveType TYPE_INT = new PrimitiveType(Primitive.INT);
    public static final PrimitiveType TYPE_BOOLEAN = new PrimitiveType(Primitive.BOOLEAN);
    public static final PrimitiveType TYPE_FLOAT = new PrimitiveType(Primitive.FLOAT);
    public static final PrimitiveType TYPE_CHAR = new PrimitiveType(Primitive.CHAR);
    public static final PrimitiveType TYPE_VOID = new PrimitiveType(Primitive.VOID);

    public final int type;

    public PrimitiveType(int type) {
        this.type = type;
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public boolean isSuperclassOfOrEquals(Type child, Environment env) {
        return equals(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrimitiveType that = (PrimitiveType) o;

        return type == that.type;
    }

    @Override
    public String toString() {
        return Primitive.typeToString(type);
    }

    public boolean isIntLike() {
        return type == Primitive.INT || type == Primitive.CHAR;
    }

    @Override
    public Primitive defaultValue() {
        switch (type) {
            case Primitive.INT:
                return Int.ZERO;
            case Primitive.BOOLEAN:
                return Bool.FALSE;
            case Primitive.FLOAT:
                return SplFloat.ZERO;
            case Primitive.CHAR:
                return Char.NULL_TERMINATOR;
            default:
                throw new SplException("No other types of primitive. ");
        }
    }
}
