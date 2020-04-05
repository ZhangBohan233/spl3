package interpreter.primitives;

public abstract class Primitive {

    public static final int INT = 1;
    public static final int FLOAT = 2;
    public static final int CHAR = 3;
    public static final int BOOLEAN = 4;
    public static final int POINTER = 5;

    public abstract int type();

    public abstract long intValue();

    public abstract double floatValue();
}
