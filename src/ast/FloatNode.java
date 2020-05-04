package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.primitives.SplFloat;
import interpreter.types.PrimitiveType;
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
        if (env.interrupted()) return null;

        return new TypeValue(PrimitiveType.TYPE_FLOAT, new SplFloat(value));
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }
}
