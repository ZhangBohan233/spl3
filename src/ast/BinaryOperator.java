package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.primitives.Bool;
import interpreter.types.PrimitiveType;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import interpreter.primitives.Int;
import interpreter.primitives.Primitive;
import lexer.SyntaxError;
import util.LineFile;

public class BinaryOperator extends BinaryExpr {

    public static final int NUMERIC = 1;
    public static final int LOGICAL = 2;
    public static final int LAZY = 3;
    private final int type;

    public BinaryOperator(String operator, int type, LineFile lineFile) {
        super(operator, lineFile);

        this.type = type;
    }

    @Override
    public TypeValue evaluate(Environment env) {
        if (env.interrupted()) return null;

        if (type == NUMERIC) {
            TypeValue leftTv = left.evaluate(env);
            TypeValue rightTv = right.evaluate(env);
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
                        throw new SyntaxError("Unsupported binary operator '" + operator + "' between " +
                                leftTv.getType() + " and " + rightTv.getType() + ". ", getLineFile());
                }
                return new TypeValue(PrimitiveType.TYPE_INT, result);
            }
        } else if (type == LOGICAL) {
            TypeValue leftTv = left.evaluate(env);
            TypeValue rightTv = right.evaluate(env);
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
                            throw new SyntaxError("Unsupported binary operator '" + operator + "' between " +
                                    leftTv.getType() + " and " + rightTv.getType() + ". ", getLineFile());
                    }
                }
            }
            return Bool.boolTvValueOf(result);
        } else if (type == LAZY) {
            // a && b = a ? b : false
            // a || b = a ? true : b
            FakeTernaryOperator fto = new FakeTernaryOperator("?", getLineFile());
            fto.setLeft(left);

            Declaration d = new Declaration(Declaration.USELESS, getLineFile());
            if (operator.equals("&&")) {
                d.setLeft(right);
                d.setRight(BoolStmt.BOOL_STMT_FALSE);
            } else if (operator.equals("||")) {
                d.setLeft(BoolStmt.BOOL_STMT_TRUE);
                d.setRight(right);
            } else {
                throw new SyntaxError("Unsupported lazy binary operator '" + operator +
                        ". ", getLineFile());
            }
            fto.setRight(d);
            return fto.evaluate(env);
        }
        throw new SyntaxError("Unexpected error. ", getLineFile());
    }

    @Override
    public BinaryOperator preprocess(FakeEnv env) {
        return this;
    }
}
