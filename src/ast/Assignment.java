package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.Memory;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.env.TypeValue;
import interpreter.primitives.Primitive;
import util.LineFile;

public class Assignment extends BinaryExpr {
    public Assignment(LineFile lineFile) {
        super("=", lineFile);
    }

    @Override
    public TypeValue evaluate(Environment env) {
        TypeValue rightRes = right.evaluate(env);

        if (left instanceof NameNode) {
            env.setVar(((NameNode) left).getName(), rightRes);
        } else if (left instanceof Declaration) {
            left.evaluate(env);

            env.setVar(((Declaration) left).getLeft().getName(), rightRes);
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
