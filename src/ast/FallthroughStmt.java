package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

public class FallthroughStmt extends LeafNode {

    public FallthroughStmt(LineFile lineFile) {
        super(lineFile);
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        env.fallthrough();
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
