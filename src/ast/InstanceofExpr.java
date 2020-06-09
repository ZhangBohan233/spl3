package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.primitives.Bool;
import interpreter.primitives.Pointer;
import interpreter.splObjects.Instance;
import interpreter.splObjects.SplObject;
import interpreter.types.PrimitiveType;
import interpreter.types.Type;
import interpreter.types.TypeError;
import interpreter.types.TypeValue;
import util.LineFile;

public class InstanceofExpr extends BinaryExpr {

    public InstanceofExpr(LineFile lineFile) {
        super("instanceof", lineFile);
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        if (!(right instanceof TypeRepresent))
            throw new SplException("Right side of 'instanceof' must be a type. ", getLineFile());

        Type expectedT = ((TypeRepresent) right).evalType(env);
        TypeValue leftTv = left.evaluate(env);

        if (leftTv.getType().isPrimitive()) {
            throw new TypeError("Primitive types do not support operation 'instanceof'. ", getLineFile());
        }

        SplObject obj = env.getMemory().get((Pointer) leftTv.getValue());
        if (!(obj instanceof Instance)) return TypeValue.BOOL_FALSE;

        Instance ins = (Instance) obj;
        return Bool.boolTvValueOf(expectedT.isSuperclassOfOrEquals(ins.getType(), env));
    }

    @Override
    protected Type inferredType(Environment env) {
        return PrimitiveType.TYPE_BOOLEAN;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
