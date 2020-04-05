package ast;

import interpreter.Environment;
import interpreter.Memory;
import util.LineFile;

public class Assignment extends BinaryExpr {
    public Assignment(LineFile lineFile) {
        super("=", lineFile);
    }

    @Override
    public Object evaluate(Memory memory) {
        return null;
    }

    @Override
    public Node preprocess(Environment env) {
        left = left.preprocess(env);
        right = right.preprocess(env);
        return this;
    }
}
