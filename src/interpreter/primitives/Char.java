package interpreter.primitives;

public class Char extends Primitive {

    public static final Char NULL_TERMINATOR = new Char('\0');

    public final char value;

    public Char(char value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int type() {
        return Primitive.CHAR;
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

        Char anInt = (Char) o;

        return value == anInt.value;
    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }
}
