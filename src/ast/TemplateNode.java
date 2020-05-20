package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

public class TemplateNode extends Node {

    public final Line value;

    public TemplateNode(Line value, LineFile lineFile) {
        super(lineFile);

        this.value = value;
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }

    @Override
    public String toString() {
        return "<" + value + ">";
    }
}
