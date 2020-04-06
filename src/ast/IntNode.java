package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.types.PrimitiveType;
import interpreter.env.TypeValue;
import interpreter.primitives.Int;
import interpreter.env.Environment;
import util.LineFile;

public class IntNode extends LeafNode {
    private long value;

    public IntNode(long value, LineFile lineFile) {
        super(lineFile);

        this.value = value;
    }

    @Override
    public TypeValue evaluate(Environment env) {
        return new TypeValue(PrimitiveType.TYPE_INT, new Int(value));
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
