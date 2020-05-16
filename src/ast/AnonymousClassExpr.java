package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

public class AnonymousClassExpr extends BinaryExpr {

    public AnonymousClassExpr(LineFile lineFile) {
        super("<-", lineFile);
    }

    BlockStmt getContent() {
        return (BlockStmt) right;
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        throw new SplException("Not evaluate-able. ", getLineFile());
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
