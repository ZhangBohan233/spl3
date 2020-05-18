package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.primitives.Int;
import interpreter.primitives.Primitive;
import interpreter.primitives.SplFloat;
import interpreter.types.PrimitiveType;
import interpreter.types.TypeError;
import interpreter.types.TypeValue;
import util.LineFile;

public class IncDecOperator extends Expr {

    /**
     * Is increment or not.
     *
     * If {@code true}, this node is `++`. Otherwise `--`
     */
    public final boolean isIncrement;

    /**
     * Is post increment/decrement or not.
     *
     * If {@code true}, this node is `x++` or `x--`. Otherwise, `++x` or `--x`
     */
    private boolean isPost;

    private Node value;

    public IncDecOperator(boolean isIncrement, LineFile lineFile) {
        super(lineFile);

        this.isIncrement = isIncrement;
    }

    public void setPost(boolean post) {
        isPost = post;
    }

    public void setValue(Node value) {
        this.value = value;
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        TypeValue vtv = value.evaluate(env);
        Primitive current = vtv.getValue();
        TypeValue result = null;
        if (vtv.getType().isPrimitive()) {
            PrimitiveType pt = (PrimitiveType) vtv.getType();
            if (pt.isIntLike()) {
                if (isIncrement) {
                    result = new TypeValue(pt, new Int(current.intValue() + 1));
                } else {
                    result = new TypeValue(pt, new Int(current.intValue() - 1));
                }
            } else if (pt.equals(PrimitiveType.TYPE_FLOAT)) {
                if (isIncrement) {
                    result = new TypeValue(pt, new SplFloat(current.floatValue() + 1));
                } else {
                    result = new TypeValue(pt, new SplFloat(current.floatValue() - 1));
                }
            }
        }
        if (result == null) {
            throw new SplException("Increment/decrement operator is not applicable to type " + vtv.getType(),
                    getLineFile());
        }

        Assignment.assignment(value, result, env, getLineFile());
        if (isPost) {
            return new TypeValue(vtv.getType(), current);
        } else {
            return result;
        }
    }

    @Override
    public Node preprocess(FakeEnv env) {
        value = value.preprocess(env);
        return this;
    }

    @Override
    public boolean notFulfilled() {
        return value == null;
    }

    @Override
    public String toString() {
        if (isIncrement) {
            return isPost ? value + "++" : "++" + value;
        } else {
            return isPost ? value + "--" : "--" + value;
        }
    }
}
