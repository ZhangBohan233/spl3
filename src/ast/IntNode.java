package ast;

import interpreter.Environment;
import interpreter.Memory;
import util.LineFile;

public class IntNode extends LeafNode {
    private long value;

    public IntNode(long value, LineFile lineFile) {
        super(lineFile);

        this.value = value;
    }

    @Override
    public Object evaluate(Memory memory) {
        return null;
    }

    @Override
    public IntNode preprocess(Environment env) {
        return this;
    }

    @Override
    public String toString() {
        return "Int(" + value + ')';
    }
}
