package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

import java.util.ArrayList;
import java.util.List;

public class BlockStmt extends Node {

    private final List<Line> children = new ArrayList<>();

    private boolean independence;

    public BlockStmt(LineFile lineFile) {
        super(lineFile);
    }

    public BlockStmt() {
        super(new LineFile(0, "Parser"));
    }

    public void setIndependence(boolean independence) {
        this.independence = independence;
    }

    public void addLine(Line line) {
        children.add(line);
    }

    public List<Line> getLines() {
        return children;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n").append(" ".repeat(Math.max(0, Node.spaceCount))).append("{");
        Node.spaceCount += 2;
        for (Line line : children) {
            builder.append("\n").append(" ".repeat(Math.max(0, Node.spaceCount))).append(line.toString());
        }
        Node.spaceCount -= 2;
        builder.append("\n").append(" ".repeat(Math.max(0, Node.spaceCount))).append("}");
        return builder.toString();
    }

    @Override
    protected TypeValue internalEval(Environment env) {

        for (Line line : children) {
            line.evaluate(env);
        }
        return null;
    }

    @Override
    public BlockStmt preprocess(FakeEnv env) {
        for (int i = 0; i < children.size(); ++i) {
            children.set(i, children.get(i).preprocess(env));
        }
        return this;
    }
}
