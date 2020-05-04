package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

public class BreakStmt extends LeafNode {

    public BreakStmt(LineFile lineFile) {
        super(lineFile);
    }

    @Override
    public TypeValue evaluate(Environment env) {
        env.breakLoop();
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
