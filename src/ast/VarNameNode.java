package ast;

import interpreter.Environment;
import interpreter.Memory;
import util.LineFile;

public class VarNameNode extends LeafNode {

    private int localAddress;
    private int layer;

    public VarNameNode(int localAddress, int layer, LineFile lineFile) {
        super(lineFile);
    }

    @Override
    public Object evaluate(Memory memory) {
        return null;
    }

    @Override
    public Node preprocess(Environment env) {
        return this;
    }
}
