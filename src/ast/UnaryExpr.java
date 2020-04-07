package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import util.LineFile;

public abstract class UnaryExpr extends Expr {

    protected final String op;
    protected Node value;
    public final boolean atLeft;

    public UnaryExpr(String op, boolean operatorAtLeft, LineFile lineFile) {
        super(lineFile);

        this.op = op;
        this.atLeft = operatorAtLeft;
    }

    @Override
    public boolean notFulfilled() {
        return value == null;
    }

    @Override
    public String toString() {
        if (atLeft) {
            return String.format("UE(%s %s)", op, value);
        } else {
            return String.format("UE(%s %s)", value, op);
        }
    }

    public String getOperator() {
        return op;
    }

    public void setValue(Node value) {
        this.value = value;
    }
}
