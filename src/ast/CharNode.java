package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.primitives.Char;
import interpreter.types.PrimitiveType;
import interpreter.types.Type;
import interpreter.types.TypeValue;
import util.LineFile;

public class CharNode extends LiteralNode {

    public final char ch;

    public CharNode(char ch, LineFile lineFile) {
        super(lineFile);

        this.ch = ch;
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        return new TypeValue(PrimitiveType.TYPE_CHAR, new Char(ch));
    }

    @Override
    protected Type inferredType(Environment env) {
        return PrimitiveType.TYPE_CHAR;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }

    @Override
    public String toString() {
        return "Char(" + ch + ')';
    }
}
