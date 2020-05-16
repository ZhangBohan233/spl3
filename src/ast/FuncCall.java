package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.splObjects.Function;
import interpreter.env.Environment;
import interpreter.splObjects.SplCallable;
import interpreter.types.Type;
import interpreter.types.TypeValue;
import interpreter.primitives.Pointer;
import interpreter.types.CallableType;
import interpreter.types.TypeError;
import util.LineFile;

public class FuncCall extends Node {

    Node callObj;
    Arguments arguments;

    public FuncCall(LineFile lineFile) {
        super(lineFile);
    }

    public void setCallObj(Node callObj) {
        this.callObj = callObj;
    }

    public void setArguments(Arguments arguments) {
        this.arguments = arguments;
    }

    @Override
    protected TypeValue internalEval(Environment env) {

        TypeValue leftTv = callObj.evaluate(env);
        if (!(leftTv.getType() instanceof CallableType)) {
            throw new TypeError("Type '" + leftTv.getType() + "' is not callable. ", getLineFile());
        }
        SplCallable function = (SplCallable) env.getMemory().get((Pointer) leftTv.getValue());

        return function.call(arguments, env);
    }

    @Override
    protected Type inferredType(Environment env) {
        Type leftT = callObj.inferredType(env);
        if (leftT instanceof CallableType) {
            return ((CallableType) leftT).getRType();
        } else {
            throw new TypeError("Not callable. ", getLineFile());
        }
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }

    @Override
    public String toString() {
        return callObj + "(" + arguments + ")";
    }

    public Arguments getArguments() {
        return arguments;
    }

    public Node getCallObj() {
        return callObj;
    }
}
