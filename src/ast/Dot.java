package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import util.LineFile;

public class Dot extends BinaryExpr {
    public Dot(LineFile lineFile) {
        super(".", lineFile);
    }

    @Override
    public Object evaluate(Environment env) {
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }
}
