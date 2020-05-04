package ast;

import ast.fakeEnv.FakeEnv;
import util.LineFile;

public class ElseStmt extends NonEvaluate {

    public ElseStmt(LineFile lineFile) {
        super(lineFile);
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
