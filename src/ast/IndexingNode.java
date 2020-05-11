package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.primitives.Pointer;
import interpreter.primitives.Primitive;
import interpreter.splObjects.ReadOnlyPrimitiveWrapper;
import interpreter.splObjects.SplArray;
import interpreter.types.*;
import util.LineFile;

import java.util.List;

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
    protected TypeValue internalEval(Environment env) {

        TypeValue callRes = getCallObj().evaluate(env);
        List<Node> arguments = getArgs().getChildren();
        int index = getIndex(callRes, arguments, env, getLineFile());

        Type arrEleType = ((ArrayType) callRes.getType()).getEleType();

        Primitive value = SplArray.getItemAtIndex((Pointer) callRes.getValue(), index, env, getLineFile());
        return new TypeValue(arrEleType, value);
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

    public static int getIndex(TypeValue arrayTv, List<Node> arguments, Environment env, LineFile lineFile) {
        if (!(arrayTv.getType() instanceof ArrayType)) {
            throw new TypeError("Only array type supports indexing. ", lineFile);
        }
        if (arguments.size() != 1) {
            throw new TypeError("Indexing must have 1 index. ", lineFile);
        }
        TypeValue index = arguments.get(0).evaluate(env);
        if (!index.getType().equals(PrimitiveType.TYPE_INT)) {
            throw new TypeError("Indexing must be int. ", lineFile);
        }
        return (int) index.getValue().intValue();
    }
}
