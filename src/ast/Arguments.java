package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import util.LineFile;

public class Arguments extends Node {

    private Line line;

    public Arguments(Line line) {
        super(line.getLineFile());

        this.line = line;
    }

    public Line getLine() {
        return line;
    }

    @Override
    public Object evaluate(Environment env) {
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }

    @Override
    public String toString() {
        return "Arg" + line;
    }
}
