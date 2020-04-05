package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.Memory;
import interpreter.env.Environment;
import util.LineFile;

import java.util.ArrayList;
import java.util.List;

public class Line extends Node {
    private List<Node> children = new ArrayList<>();

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
    public Object evaluate(Environment env) {
        return null;
    }

    @Override
    public Line preprocess(FakeEnv env) {
        for (int i = 0 ; i < children.size(); ++i) {
            children.set(i, children.get(i).preprocess(env));
        }
        return this;
    }
}
