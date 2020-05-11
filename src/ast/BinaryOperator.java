package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.primitives.*;
import interpreter.types.*;
import interpreter.SplException;
import interpreter.env.Environment;
import lexer.SyntaxError;
import util.LineFile;

import java.util.Set;

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
            if (!leftTv.getType().isPrimitive() || !rightTv.getType().isPrimitive())
                throw new TypeError("Pointer type arithmetic is not supported. ",
                        getLineFile());
            PrimitiveType lt = (PrimitiveType) leftTv.getType();
            PrimitiveType rt = (PrimitiveType) rightTv.getType();

            if (lt.isIntLike()) {
                Primitive result = new Int(integerArithmetic(
                        operator,
                        leftTv.getValue().intValue(),
                        rightTv.getValue().intValue(),
                        rt.isIntLike(),
                        getLineFile()
                ));
                return new TypeValue(PrimitiveType.TYPE_INT, result);
            } else if (lt.equals(PrimitiveType.TYPE_FLOAT)) {
                Primitive result = new SplFloat(floatArithmetic(
                        operator,
                        leftTv.getValue().floatValue(),
                        rightTv.getValue().floatValue(),
                        getLineFile()
                ));
                return new TypeValue(PrimitiveType.TYPE_FLOAT, result);
            } else {
                throw new TypeError();
            }
        } else if (type == LOGICAL) {
            TypeValue leftTv = left.evaluate(env);
            TypeValue rightTv = right.evaluate(env);
            boolean result;
            if (leftTv.getType().isPrimitive()) {
                if (!rightTv.getType().isPrimitive())
                    throw new TypeError("Primitive type cannot compare to pointer type. ",
                            getLineFile());
                PrimitiveType lt = (PrimitiveType) leftTv.getType();
                PrimitiveType rt = (PrimitiveType) rightTv.getType();
                if (lt.isIntLike()) {
                    long leftV = leftTv.getValue().intValue();
                    if (rt.isIntLike()) {
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

    private static long integerArithmetic(String op, long l, long r, boolean rIsInt, LineFile lineFile) {
        switch (op) {
            case "+":
                return l + r;
            case "-":
                return l - r;
            case "*":
                return l * r;
            case "/":
                return l / r;
            case "%":
                return l % r;
            case "<<":
                if (rIsInt) return l << r;
            case ">>":
                if (rIsInt) return l >> r;
            case ">>>":
                if (rIsInt) return l >>> r;
            case "&":
                if (rIsInt) return l & r;
            case "|":
                if (rIsInt) return l | r;
            case "^":
                if (rIsInt) return l ^ r;
            default:
                throw new TypeError("Unsupported operation '" + op + "'. ", lineFile);
        }
    }

    private static double floatArithmetic(String op, double l, double r,  LineFile lineFile) {
        switch (op) {
            case "+":
                return l + r;
            case "-":
                return l - r;
            case "*":
                return l * r;
            case "/":
                return l / r;
            case "%":
                return l % r;
            default:
                throw new TypeError("Unsupported operation '" + op + "'. ", lineFile);
        }
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
