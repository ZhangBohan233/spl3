package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.primitives.Bool;
import interpreter.types.PrimitiveType;
import interpreter.types.Type;
import interpreter.types.TypeError;
import interpreter.types.TypeValue;
import util.LineFile;

public abstract class Node {
    private final LineFile lineFile;

    static int spaceCount = 0;  // used for printing ast

    public Node(LineFile lineFile) {
        this.lineFile = lineFile;
    }

    public final TypeValue evaluate(Environment env) {
        // pre
        if (env.interrupted()) return TypeValue.INTERRUPTED;

        // essential
        TypeValue typeValue = internalEval(env);

        // post

        return typeValue;
    }

    protected abstract TypeValue internalEval(Environment env);

    protected Type inferredType(Environment env) {
        return PrimitiveType.TYPE_VOID;
    }

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
