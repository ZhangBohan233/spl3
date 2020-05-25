package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.primitives.Bool;
import interpreter.types.PrimitiveType;
import interpreter.types.Type;
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
//        System.out.println(leftTv.getType());

        return Bool.boolTvValueOf(expectedT.isSuperclassOfOrEquals(leftTv.getType(), env));
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
