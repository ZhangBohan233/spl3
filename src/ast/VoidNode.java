package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

public class VoidNode extends LeafNode {

    public static final VoidNode VOID_NODE = new VoidNode(LineFile.LF_PARSER);

    public VoidNode(LineFile lineFile) {
        super(lineFile);
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
