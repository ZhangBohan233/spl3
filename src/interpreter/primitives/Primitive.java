package interpreter.primitives;

import interpreter.types.TypeError;

public abstract class Primitive {

    public static final int VOID = 0;
    public static final int INT = 1;
    public static final int FLOAT = 2;
    public static final int CHAR = 3;
    public static final int BOOLEAN = 4;
    public static final int POINTER = 5;

    public abstract int type();

    public abstract long intValue();

    public abstract double floatValue();

    public char charValue() {
        return (char) intValue();
    }

    public static String typeToString(int type) {
        switch (type) {
            case INT:
                return "int";
            case FLOAT:
                return "float";
            case CHAR:
                return "char";
            case BOOLEAN:
                return "boolean";
            case VOID:
                return "void";
            default:
                throw new TypeError();
        }
    }
}
