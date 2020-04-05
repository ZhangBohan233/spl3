package interpreter.primitives;

public class Pointer extends Primitive {
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
}
