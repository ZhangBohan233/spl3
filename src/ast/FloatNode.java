package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.primitives.SplFloat;
import interpreter.types.PrimitiveType;
import interpreter.types.Type;
import interpreter.types.TypeValue;
import util.LineFile;

public class FloatNode extends LiteralNode {

    public final double value;

    public FloatNode(double value, LineFile lineFile) {
        super(lineFile);

        this.value = value;
    }

    @Override
    protected TypeValue internalEval(Environment env) {

        return new TypeValue(PrimitiveType.TYPE_FLOAT, new SplFloat(value));
    }

    @Override
    protected Type inferredType(Environment env) {
        return PrimitiveType.TYPE_FLOAT;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }
}
