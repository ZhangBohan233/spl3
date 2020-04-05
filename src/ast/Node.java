package ast;

import interpreter.Environment;
import interpreter.Memory;
import util.LineFile;

public abstract class Node {
    private LineFile lineFile;

    static int spaceCount = 0;  // used for printing ast

    public Node(LineFile lineFile) {
        this.lineFile = lineFile;
    }

    public abstract Object evaluate(Memory memory);

    /**
     * Preprocess this node and return the new node of this
     *
     * @param env the environment
     * @return this {@code Node} if no substitution,
     */
    public abstract Node preprocess(Environment env);

    public LineFile getLineFile() {
        return lineFile;
    }
}
