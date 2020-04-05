package interpreter.primitives;

public class Int extends Primitive {

    public final long value;

    public Int(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int type() {
        return Primitive.INT;
    }

    @Override
    public long intValue() {
        return value;
    }

    @Override
    public double floatValue() {
        return value;
    }
}
