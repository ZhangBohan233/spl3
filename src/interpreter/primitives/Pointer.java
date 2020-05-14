package interpreter.primitives;

import interpreter.types.TypeError;

public class Pointer extends Primitive {

    private final int ptr;

    public static final Pointer NULL_PTR = new Pointer(0);

    public Pointer(int ptr) {
        this.ptr = ptr;
    }

    @Override
    public int type() {
        return Primitive.POINTER;
    }

    @Override
    public long intValue() {
        return ptr;
    }

    @Override
    public double floatValue() {
        throw new TypeError("Cannot convert pointer to float. ");
    }

    @Override
    public String toString() {
        return "Ptr<" + ptr + ">";
    }

    public int getPtr() {
        return ptr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pointer pointer = (Pointer) o;

        return ptr == pointer.ptr;
    }
}
