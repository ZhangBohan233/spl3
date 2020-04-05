package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.Memory;
import interpreter.env.Environment;
import util.LineFile;

public class VarNameNode extends LeafNode {

    private int localAddress;
    private int layer;

    public VarNameNode(int localAddress, int layer, LineFile lineFile) {
        super(lineFile);
    }

    @Override
    public Object evaluate(Environment env) {
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
