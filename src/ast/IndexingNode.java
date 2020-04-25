package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.ArrayType;
import interpreter.types.Type;
import interpreter.types.TypeValue;
import util.LineFile;

public class IndexingNode extends Node implements TypeRepresent {

    private Node callObj;
    private Line args;

    public IndexingNode(Node callObj, LineFile lineFile) {
        super(lineFile);

        this.callObj = callObj;
    }

    public void setArgs(Line args) {
        this.args = args;
    }

    public Line getArgs() {
        return args;
    }

    public Node getCallObj() {
        return callObj;
    }

    @Override
    public TypeValue evaluate(Environment env) {
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }

    @Override
    public String toString() {
        return callObj + " " + args;
    }

    @Override
    public Type evalType(Environment environment) {
        Type ofType = ((TypeRepresent) callObj).evalType(environment);
        return new ArrayType(ofType);
    }
}
