package interpreter.types;

import interpreter.env.Environment;

public class ArrayType extends PointerType {

    private final Type eleType;

    public ArrayType(Type elementType) {
        this.eleType = elementType;
    }

    public Type getEleType() {
        return eleType;
    }

    @Override
    public int getPointerType() {
        return PointerType.ARRAY_TYPE;
    }

    @Override
    public boolean isSuperclassOfOrEquals(Type child, Environment env) {
        return child instanceof ArrayType && ((ArrayType) child).eleType.equals(eleType);
    }

    @Override
    public String toString() {
        return "ArrayType{" + eleType + "}";
    }

    @Override
    public boolean equals(Object o) {
        return getClass() == o.getClass() && getEleType().equals(((ArrayType) o).getEleType());
    }
}
