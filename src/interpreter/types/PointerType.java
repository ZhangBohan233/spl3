package interpreter.types;

import interpreter.env.Environment;

public abstract class PointerType implements Type {

    public static final int NULL_TYPE = 0;
    public static final int CLASS_TYPE = 1;
    public static final int MODULE_TYPE = 2;
    public static final int CALLABLE_TYPE = 3;
    public static final int ARRAY_TYPE = 4;
    public static final int NATIVE_TYPE = 5;

    @Override
    public boolean isPrimitive() {
        return false;
    }

    public abstract int getPointerType();

    @Override
    public final boolean isSuperclassOfOrEquals(Type child, Environment env) {
        return child instanceof NullType || isSuperclassOfOrEqualsNotNull(child, env);
    }

    protected abstract boolean isSuperclassOfOrEqualsNotNull(Type child, Environment env);
}
