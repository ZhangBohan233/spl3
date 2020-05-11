package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.primitives.Bool;
import interpreter.primitives.Int;
import interpreter.primitives.SplFloat;
import interpreter.types.PrimitiveType;
import interpreter.types.TypeValue;
import lexer.SyntaxError;
import util.LineFile;

public class RegularUnaryOperator extends UnaryExpr {

    public static final int NUMERIC = 1;
    public static final int LOGICAL = 2;
    private final int type;

    public RegularUnaryOperator(String op, int type, LineFile lineFile) {
        super(op, true, lineFile);

        this.type = type;
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        TypeValue valueTv = value.evaluate(env);
        if (type == NUMERIC) {
            if (valueTv.getType().equals(PrimitiveType.TYPE_INT)) {
                if (operator.equals("neg")) {
                    return new TypeValue(PrimitiveType.TYPE_INT,
                            new Int(-valueTv.getValue().intValue()));
                }
            } else if (valueTv.getType().equals(PrimitiveType.TYPE_FLOAT)) {
                if (operator.equals("neg")) {
                    return new TypeValue(PrimitiveType.TYPE_FLOAT,
                            new SplFloat(-valueTv.getValue().floatValue()));
                }
            }
        } else if (type == LOGICAL) {
            if (valueTv.getType().equals(PrimitiveType.TYPE_BOOLEAN)) {
                if (operator.equals("!")) {
                    return Bool.boolTvValueOf(!((Bool) valueTv.getValue()).value);
                }
            }
        }
        throw new SyntaxError("Operator error. ", getLineFile());
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
