package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.EnvironmentError;
import interpreter.types.TypeValue;
import interpreter.types.Type;
import interpreter.env.Environment;
import util.LineFile;

public class Declaration extends BinaryExpr {

    public static final int VAR = 1;
    public static final int CONST = 2;
    public static final int USELESS = 3;

    private final int level;

    public Declaration(int level, LineFile lineFile) {
        super(":", lineFile);

        this.level = level;
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        Type rightEv = getRightTypeRep().evalType(env);
        if (level == VAR) {
            env.defineVar(getLeftName().getName(), rightEv, getLineFile());
        } else if (level == CONST) {
            env.defineConst(getLeftName().getName(), rightEv, getLineFile());
        } else {
            throw new SplException("Unknown declaration type. ", getLineFile());
        }
        return null;
    }

    @Override
    public Declaration preprocess(FakeEnv env) {
//        System.out.println(left);
//        System.out.println(right);

//        if (!(left instanceof NameNode)) throw new EnvironmentError();
//
//        Type type = new Type(right);
//        env.defineVar(((NameNode) left).getName(), type);

        return this;
    }

    public NameNode getLeftName() {
        if (!(left instanceof NameNode)) throw new EnvironmentError();
        return (NameNode) left;
    }

    public TypeRepresent getRightTypeRep() {
        if (!(right instanceof TypeRepresent)) throw new EnvironmentError("Not a type");
        return (TypeRepresent) right;
    }
}
