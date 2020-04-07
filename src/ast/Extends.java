package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import parser.ParseError;

public class Extends extends Node {

    private Line extending;

    public Extends(Line extending) {
        super(extending.getLineFile());

        this.extending = extending;
    }

    public Line getExtending() {
        return extending;
    }

    @Override
    public TypeValue evaluate(Environment env) {
        throw new ParseError("Extends is not evaluate-able. ", getLineFile());
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }

    @Override
    public String toString() {
        return "Extends " + extending;
    }
}
