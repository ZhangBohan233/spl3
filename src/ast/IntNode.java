package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.types.PrimitiveType;
import interpreter.types.TypeValue;
import interpreter.primitives.Int;
import interpreter.env.Environment;
import util.LineFile;

public class IntNode extends LiteralNode {
    private final long value;

    public IntNode(long value, LineFile lineFile) {
        super(lineFile);

        this.value = value;
    }

    @Override
    protected TypeValue internalEval(Environment env) {
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
