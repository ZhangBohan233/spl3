package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

import java.util.ArrayList;
import java.util.List;

public class Line extends Node {
    private final List<Node> children = new ArrayList<>();

    public Line(LineFile lineFile) {
        super(lineFile);
    }

    public Line() {
        super(new LineFile(0, "Parser"));
    }

    public List<Node> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return children.toString();
    }

    @Override
    protected TypeValue internalEval(Environment env) {

        TypeValue res = null;
        for (Node node : children) {
            res = node.evaluate(env);
        }
        return res;
    }

    @Override
    public Line preprocess(FakeEnv env) {
//        for (int i = 0 ; i < children.size(); ++i) {
//            children.set(i, children.get(i).preprocess(env));
//        }
        return this;
    }
}
