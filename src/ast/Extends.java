package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import parser.ParseError;
import util.LineFile;

public class Extends extends UnaryExpr {

    public Extends(LineFile lineFile) {
        super("extends", true, lineFile);
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        throw new ParseError("Extend node not evaluate-able. ", getLineFile());
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }

    public TypeRepresent getValue() {
        if (value instanceof TypeRepresent) return (TypeRepresent) value;
        else throw new ParseError("Superclass must be a class. ", getLineFile());
    }
}
