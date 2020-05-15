package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import lexer.SyntaxError;
import util.LineFile;

public class QuickAssignment extends BinaryExpr {

    public QuickAssignment(LineFile lineFile) {
        super(":=", lineFile);
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        TypeValue rtv = right.evaluate(env);
        if (left instanceof NameNode) {
            env.defineVarAndSet(((NameNode) left).getName(), rtv, getLineFile());
            return rtv;
        } else {
            throw new SyntaxError("Left side of ':=' must be a local name. ", getLineFile());
        }
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
