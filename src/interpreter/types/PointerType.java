package interpreter.types;

public abstract class PointerType implements Type {

    public static final int CLASS_TYPE = 1;
    public static final int MODULE_TYPE = 2;
    public static final int CALLABLE_TYPE = 3;
    public static final int ARRAY_TYPE = 4;

    @Override
    public boolean isPrimitive() {
        return false;
    }

    public abstract int getPointerType();
}
