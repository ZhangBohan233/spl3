package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

public class ForLoopStmt extends ConditionalStmt {

    public ForLoopStmt(LineFile lineFile) {
        super(lineFile);
    }

    @Override
    public TypeValue evaluate(Environment env) {
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }
}
