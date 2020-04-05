package ast;

import interpreter.Environment;
import interpreter.Memory;
import util.LineFile;

import java.util.ArrayList;
import java.util.List;

public class BlockStmt extends Node {

    private List<Line> children = new ArrayList<>();

    public BlockStmt(LineFile lineFile) {
        super(lineFile);
    }

    public BlockStmt() {
        super(new LineFile(0, "Parser"));
    }

    public void addLine(Line line) {
        children.add(line);
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
    public Object evaluate(Memory memory) {
        for (Line line : children) {
            line.evaluate(memory);
        }
        return null;
    }

    @Override
    public BlockStmt preprocess(Environment env) {
        for (int i = 0; i < children.size(); ++i) {
            children.set(i, children.get(i).preprocess(env));
        }
        return this;
    }
}
