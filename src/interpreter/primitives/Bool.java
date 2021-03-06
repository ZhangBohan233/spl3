package interpreter.primitives;

import ast.Node;
import interpreter.env.Environment;
import interpreter.types.PrimitiveType;
import interpreter.types.TypeError;
import interpreter.types.TypeValue;
import util.LineFile;

public class Bool extends Primitive {

    public static final Bool TRUE = new Bool(true);
    public static final Bool FALSE = new Bool(false);

    public final boolean value;

    public Bool(boolean value) {
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
        return value ? 1 : 0;
    }

    @Override
    public double floatValue() {
        return intValue();
    }

    public boolean booleanValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bool anInt = (Bool) o;

        return value == anInt.value;
    }

    public static TypeValue boolTvValueOf(boolean b) {
        return b ? TypeValue.BOOL_TRUE : TypeValue.BOOL_FALSE;
    }

    public static Bool evalBoolean(Node node, Environment env, LineFile lineFile) {
        TypeValue cond = node.evaluate(env);
        if (!cond.getType().equals(PrimitiveType.TYPE_BOOLEAN)) throw new TypeError("Statement takes " +
                "boolean value as condition. ", lineFile);
        return (Bool) cond.getValue();
    }
}
