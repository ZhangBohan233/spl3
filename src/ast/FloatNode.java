package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

public class FloatNode extends LiteralNode {

    public final double value;

    public FloatNode(double value, LineFile lineFile) {
        super(lineFile);

        this.value = value;
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
