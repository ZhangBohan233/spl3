package interpreter.types;

import interpreter.primitives.Pointer;

public abstract class PointerType implements Type {

    public static final int CLASS_TYPE = 1;
    public static final int MODULE_TYPE = 2;
    public static final int CALLABLE_TYPE = 3;

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isSuperclassOfOrEquals(Type child) {
        // TODO
        return equals(child);
    }

    public abstract int getPointerType();
}