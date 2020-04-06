package interpreter.primitives;

public class Pointer extends Primitive {

    private int ptr;

    public Pointer(int ptr) {
        this.ptr = ptr;
    }

    @Override
    public int type() {
        return Primitive.POINTER;
    }

    @Override
    public long intValue() {
        return 0;
    }

    @Override
    public double floatValue() {
        return 0;
    }

    @Override
    public String toString() {
        return "Ptr<" + ptr + ">";
    }

    public int getPtr() {
        return ptr;
    }
}
