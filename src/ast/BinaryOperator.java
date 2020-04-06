package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.types.PrimitiveType;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.env.TypeValue;
import interpreter.primitives.Int;
import interpreter.primitives.Primitive;
import util.LineFile;

public class BinaryOperator extends BinaryExpr {

    public static final int NUMERIC = 1;
    public static final int LOGICAL = 2;
    private int type;

    public BinaryOperator(String operator, int type, LineFile lineFile) {
        super(operator, lineFile);

        this.type = type;
    }

    @Override
    public Object evaluate(Environment env) {
        TypeValue leftTv = (TypeValue) left.evaluate(env);
        TypeValue rightTv = (TypeValue) right.evaluate(env);
        if (type == NUMERIC) {
            if (leftTv.getType().equals(PrimitiveType.TYPE_INT)) {
                Primitive result;
                switch (operator) {
                    case "+":
                        result = new Int(leftTv.getValue().intValue() + rightTv.getValue().intValue());
                        break;
                    case "-":
                        result = new Int(leftTv.getValue().intValue() - rightTv.getValue().intValue());
                        break;
                    case "*":
                        result = new Int(leftTv.getValue().intValue() * rightTv.getValue().intValue());
                        break;
                    case "/":
                        result = new Int(leftTv.getValue().intValue() / rightTv.getValue().intValue());
                        break;
                    case "%":
                        result = new Int(leftTv.getValue().intValue() % rightTv.getValue().intValue());
                        break;
                    default:
                        throw new SplException("Unsupported binary operator '" + operator + "' between " +
                                leftTv.getType() + " and " + rightTv.getType() + ". ", getLineFile());
                }
                return new TypeValue(PrimitiveType.TYPE_INT, result);
            }
        } else if (type == LOGICAL) {

        } else {
            throw new SplException("Unexpected error. ", getLineFile());
        }

        return 0;
    }

    @Override
    public BinaryOperator preprocess(FakeEnv env) {
        return this;
    }
}
