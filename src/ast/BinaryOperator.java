package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.primitives.Bool;
import interpreter.types.PrimitiveType;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import interpreter.primitives.Int;
import interpreter.primitives.Primitive;
import util.LineFile;

public class BinaryOperator extends BinaryExpr {

    public static final int NUMERIC = 1;
    public static final int LOGICAL = 2;
    private final int type;

    public BinaryOperator(String operator, int type, LineFile lineFile) {
        super(operator, lineFile);

        this.type = type;
    }

    @Override
    public TypeValue evaluate(Environment env) {
        if (env.interrupted()) return null;

        TypeValue leftTv = left.evaluate(env);
        TypeValue rightTv = right.evaluate(env);
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
            boolean result = false;
            if (leftTv.getType().equals(PrimitiveType.TYPE_INT)) {
                long leftV = leftTv.getValue().intValue();
                if (rightTv.getType().equals(PrimitiveType.TYPE_INT)) {
                    long rightV = rightTv.getValue().intValue();
                    switch (operator) {
                        case "==":
                            result = leftV == rightV;
                            break;
                        case ">":
                            result = leftV > rightV;
                            break;
                        case "<":
                            result = leftV < rightV;
                            break;
                        default:
                            throw new SplException("Unsupported binary operator '" + operator + "' between " +
                                    leftTv.getType() + " and " + rightTv.getType() + ". ", getLineFile());
                    }
                }
            }
            return Bool.boolTvValueOf(result);
        } else {
            throw new SplException("Unexpected error. ", getLineFile());
        }

        return null;
    }

    @Override
    public BinaryOperator preprocess(FakeEnv env) {
        return this;
    }
}
