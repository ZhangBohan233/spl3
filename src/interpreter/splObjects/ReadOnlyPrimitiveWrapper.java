package interpreter.splObjects;

import interpreter.primitives.Primitive;

/**
 * This class is the wrapper class for primitive. Used to store primitive in heap memory (like in array).
 */
public class ReadOnlyPrimitiveWrapper extends SplObject {

    public final Primitive value;

    public ReadOnlyPrimitiveWrapper(Primitive value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "W{" + value + "}";
    }
}
