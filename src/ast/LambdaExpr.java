package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

public class LambdaExpr extends Node {

    private Line parameters;

    public LambdaExpr(LineFile lineFile) {
        super(lineFile);
    }

    public void setParameters(Line parameters) {
        this.parameters = parameters;
    }

    public Line getParameters() {
        return parameters;
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
