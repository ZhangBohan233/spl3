package interpreter.primitives;

public class Int extends Primitive {

    public static final Int ZERO = new Int(0);

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Int anInt = (Int) o;

        return value == anInt.value;
    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }
}
