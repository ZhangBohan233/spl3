package interpreter.types;

import interpreter.env.Environment;
import interpreter.primitives.Primitive;

public class PrimitiveType implements Type {

    public static final PrimitiveType TYPE_INT = new PrimitiveType(Primitive.INT);
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
}
