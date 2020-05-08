package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.primitives.Bool;
import interpreter.primitives.Pointer;
import interpreter.types.*;
import interpreter.SplException;
import interpreter.env.Environment;
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
            boolean result;
            if (leftTv.getType().isPrimitive()) {
                if (leftTv.getType().equals(PrimitiveType.TYPE_INT) ||
                        leftTv.getType().equals(PrimitiveType.TYPE_CHAR)) {
                    long leftV = leftTv.getValue().intValue();
                    if (rightTv.getType().equals(PrimitiveType.TYPE_INT) ||
                            rightTv.getType().equals(PrimitiveType.TYPE_CHAR)) {
                        long rightV = rightTv.getValue().intValue();
                        result = integerLogical(operator, leftV, rightV, getLineFile());
                    } else if (rightTv.getType().equals(PrimitiveType.TYPE_FLOAT)) {
                        double rightV = rightTv.getValue().floatValue();
                        result = otherLogical(operator, leftV, rightV, leftTv.getType(), rightTv.getType(),
                                getLineFile());
                    } else {
                        throw new TypeError();
                    }
                } else if (leftTv.getType().equals(PrimitiveType.TYPE_FLOAT)) {
                    double leftV = leftTv.getValue().floatValue();
                    if (rightTv.getType().equals(PrimitiveType.TYPE_FLOAT) ||
                            rightTv.getType().equals(PrimitiveType.TYPE_INT) ||
                            rightTv.getType().equals(PrimitiveType.TYPE_CHAR)) {
                        double rightV = rightTv.getValue().intValue();
                        result = otherLogical(operator, leftV, rightV, leftTv.getType(), rightTv.getType(),
                                getLineFile());
                    } else {
                        throw new TypeError();
                    }
                } else if (leftTv.getType().equals(PrimitiveType.TYPE_BOOLEAN)) {
                    boolean leftV = ((Bool) leftTv.getValue()).booleanValue();
                    if (rightTv.getType().equals(PrimitiveType.TYPE_BOOLEAN)) {
                        boolean rightV = ((Bool) rightTv.getValue()).booleanValue();
                        if (operator.equals("==")) {
                            result = leftV == rightV;
                        } else if (operator.equals("!=")) {
                            result = leftV != rightV;
                        } else {
                            throw new TypeError();
                        }
                    } else {
                        throw new TypeError();
                    }
                } else {
                    throw new TypeError();
                }
            } else {  // is pointer type
                Pointer ptr = (Pointer) leftTv.getValue();
                if (rightTv.getType().isPrimitive())
                    throw new TypeError("Cannot compare primitive type to pointer type. ", getLineFile());
                Pointer rightPtr = (Pointer) rightTv.getValue();
                result = integerLogical(operator, ptr.getPtr(), rightPtr.getPtr(), getLineFile());
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

    private static boolean integerLogical(String op, long l, long r, LineFile lineFile) {
        switch (op) {
            case "==":
                return l == r;
            case "!=":
                return l != r;
            case ">":
                return l > r;
            case "<":
                return l < r;
            case ">=":
                return l >= r;
            case "<=":
                return l <= r;
            default:
                throw new SyntaxError("Unsupported binary operator '" + op + "' between int and int. ",
                        lineFile);
        }
    }

    private static boolean otherLogical(String op, double l, double r, Type lt, Type rt, LineFile lineFile) {
        switch (op) {
            case "==":
                return l == r;
            case "!=":
                return l != r;
            case ">":
                return l > r;
            case "<":
                return l < r;
            case ">=":
                return l >= r;
            case "<=":
                return l <= r;
            default:
                throw new SyntaxError(
                        String.format("Unsupported binary operator '%s' between %s and %s. ",
                                op, lt, rt),
                        lineFile
                );
        }
    }

    @Override
    public BinaryOperator preprocess(FakeEnv env) {
        return this;
    }
}
