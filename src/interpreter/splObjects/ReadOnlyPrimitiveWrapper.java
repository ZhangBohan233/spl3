package interpreter.splObjects;

import interpreter.primitives.*;

/**
 * This class is the wrapper class for primitive. Used to store primitive in heap memory (like in array).
 */
public class ReadOnlyPrimitiveWrapper extends SplObject {

    public static final ReadOnlyPrimitiveWrapper NULL_WRAPPER =
            new ReadOnlyPrimitiveWrapper(Pointer.NULL_PTR);
    public static final ReadOnlyPrimitiveWrapper INT_ZERO_WRAPPER =
            new ReadOnlyPrimitiveWrapper(new Int(0));
    public static final ReadOnlyPrimitiveWrapper CHAR_ZERO_WRAPPER =
            new ReadOnlyPrimitiveWrapper(new Char('\0'));
    public static final ReadOnlyPrimitiveWrapper FLOAT_ZERO_WRAPPER =
            new ReadOnlyPrimitiveWrapper(new SplFloat(0));
    public static final ReadOnlyPrimitiveWrapper BOOLEAN_FALSE_WRAPPER =
            new ReadOnlyPrimitiveWrapper(new Bool(false));

    public final Primitive value;

    public ReadOnlyPrimitiveWrapper(Primitive value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "W{" + value + "}";
    }
}
