package interpreter.types;

import interpreter.env.Environment;

public class NullType extends PointerType {

    public static final NullType NULL_TYPE = new NullType();

    @Override
    public int getPointerType() {
        return PointerType.NULL_TYPE;
    }

    @Override
    public boolean isSuperclassOfOrEqualsNotNull(Type child, Environment env) {
        return child instanceof PointerType;
    }

    @Override
    public String toString() {
        return "NullType";
    }
}
