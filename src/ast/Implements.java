package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import parser.ParseError;

public class Implements extends NonEvaluate {

    private final Line extending;

    public Implements(Line extending) {
        super(extending.getLineFile());

        this.extending = extending;
    }

    public Line getExtending() {
        return extending;
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
