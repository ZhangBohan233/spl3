package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

public abstract class Node {
    private LineFile lineFile;

    static int spaceCount = 0;  // used for printing ast

    public Node(LineFile lineFile) {
        this.lineFile = lineFile;
    }

    public abstract TypeValue evaluate(Environment env);

    /**
     * Preprocess this node and return the new node of this
     *
     * @param env the environment
     * @return this {@code Node} if no substitution,
     */
    public abstract Node preprocess(FakeEnv env);

    public LineFile getLineFile() {
        return lineFile;
    }
}
