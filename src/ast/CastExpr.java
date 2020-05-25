package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.types.Type;
import interpreter.types.TypeValue;
import util.LineFile;

public class CastExpr extends BinaryExpr {

    public CastExpr(LineFile lineFile) {
        super("as", lineFile);
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        if (!(right instanceof TypeRepresent)) throw new SplException("Cast must be a type. ", getLineFile());
        Type dstType = ((TypeRepresent) right).evalType(env);
        TypeValue srcTv = left.evaluate(env);
        if (!srcTv.getType().isSuperclassOfOrEquals(dstType, env)) {
            throw new SplException("Cannot cast type '" + srcTv.getType() + "' to' " + dstType + "'. ",
                    getLineFile());
        }
        return new TypeValue(dstType, srcTv.getValue());
    }

    @Override
    protected Type inferredType(Environment env) {
        if (!(right instanceof TypeRepresent)) throw new SplException("Cast must be a type. ", getLineFile());
        return ((TypeRepresent) right).evalType(env);
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
