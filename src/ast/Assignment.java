package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.Memory;
import interpreter.SplException;
import interpreter.env.Environment;
import util.LineFile;

public class Assignment extends BinaryExpr {
    public Assignment(LineFile lineFile) {
        super("=", lineFile);
    }

    @Override
    public Object evaluate(Environment env) {
        if (left instanceof VarNameNode) {
            Object rightRes = right.evaluate(env);

        } else if (left instanceof Declaration) {

        } else {
            throw new SplException();
        }
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        left = left.preprocess(env);
        right = right.preprocess(env);
        return this;
    }
}
