package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

public class ContinueStmt extends LeafNode {

    public ContinueStmt(LineFile lineFile) {
        super(lineFile);
    }

    @Override
    public TypeValue evaluate(Environment env) {
        env.pauseLoop();
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
