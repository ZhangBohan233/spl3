package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

import java.util.Arrays;

public class Arguments extends Node {

    private final Line line;

    public Arguments(Line line, LineFile lineFile) {
        super(lineFile);

        this.line = line;
    }

    public Line getLine() {
        return line;
    }

    public TypeValue[] evalArgs(Environment callingEnv) {
        TypeValue[] res = new TypeValue[getLine().getChildren().size()];

        for (int i = 0; i < res.length; ++i) {
            Node argNode = getLine().getChildren().get(i);
            res[i] = argNode.evaluate(callingEnv);
        }
        return res;
    }

    @Override
    protected TypeValue internalEval(Environment env) {
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
