package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.primitives.Char;
import interpreter.primitives.Int;
import interpreter.primitives.Primitive;
import interpreter.primitives.SplFloat;
import interpreter.types.PrimitiveType;
import interpreter.types.Type;
import interpreter.types.TypeError;
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

        if (srcTv.getType().isPrimitive() && dstType.isPrimitive()) {
            // primitive casts
            Primitive value = srcTv.getValue();
            PrimitiveType priDstT = (PrimitiveType) dstType;
            switch (priDstT.type) {
                case Primitive.INT:
                    return new TypeValue(dstType, new Int(value.intValue()));
                case Primitive.CHAR:
                    return new TypeValue(dstType, new Char(value.charValue()));
                case Primitive.FLOAT:
                    return new TypeValue(dstType, new SplFloat(value.floatValue()));
                // note that 'boolean' is not a case since boolean is not castable
                default:
                    throw new TypeError("Cannot cast primitive type '" + srcTv.getType() + "' to ' " +
                            dstType + "'. ", getLineFile());
            }

        } else if (!srcTv.getType().isSuperclassOfOrEquals(dstType, env)) {
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
