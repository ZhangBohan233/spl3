package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.primitives.Bool;
import interpreter.types.TypeValue;
import lexer.SyntaxError;
import util.LineFile;

public class FakeTernaryOperator extends BinaryExpr {

    public FakeTernaryOperator(String operator, LineFile lineFile) {
        super(operator, lineFile);
    }

    @Override
    public TypeValue evaluate(Environment env) {
        if (operator.equals("?")) {
            if (!(right instanceof Declaration)) throw new SyntaxError(
                    "Usage: 'expr ? if true : if false'. ", getLineFile()
            );
            Declaration rd = (Declaration) right;
            Bool bool = Bool.evalBoolean(left, env, getLineFile());
            if (bool.value) {
                return rd.left.evaluate(env);
            } else {
                return rd.right.evaluate(env);
            }

        } else {
            throw new SyntaxError("Unsupported ternary operator '" + operator + "'. ", getLineFile());
        }
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
