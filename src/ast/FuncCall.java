package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.splObjects.Function;
import interpreter.env.Environment;
import interpreter.env.TypeValue;
import interpreter.primitives.Pointer;
import interpreter.types.CallableType;
import interpreter.types.TypeError;
import util.LineFile;

public class FuncCall extends BinaryExpr {

    public FuncCall(LineFile lineFile) {
        super("call", lineFile);
    }

    @Override
    public TypeValue evaluate(Environment env) {
        TypeValue leftTv = (TypeValue) left.evaluate(env);
        if (!(leftTv.getType() instanceof CallableType)) {
            throw new TypeError("Type '" + leftTv.getType() + "' is not callable. ", getLineFile());
        }
        Function function = (Function) env.getMemory().get((Pointer) leftTv.getValue());

        TypeValue result = function.call((Arguments) right, env);

        return result;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }

}
