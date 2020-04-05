package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.Memory;
import interpreter.env.Environment;
import util.LineFile;

public class IntNode extends LeafNode {
    private long value;

    public IntNode(long value, LineFile lineFile) {
        super(lineFile);

        this.value = value;
    }

    @Override
    public Object evaluate(Environment env) {
        return 0;
    }

    @Override
    public IntNode preprocess(FakeEnv env) {
        return this;
    }

    @Override
    public String toString() {
        return "Int(" + value + ')';
    }
}
