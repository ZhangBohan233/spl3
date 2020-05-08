package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

public class NullStmt extends LeafNode {

    public NullStmt(LineFile lineFile) {
        super(lineFile);
    }

    @Override
    public TypeValue evaluate(Environment env) {
        return TypeValue.POINTER_NULL;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
