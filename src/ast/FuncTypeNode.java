package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.env.FunctionEnvironment;
import interpreter.primitives.Pointer;
import interpreter.splObjects.Function;
import interpreter.types.*;
import util.LineFile;

import java.util.ArrayList;
import java.util.List;

public class FuncTypeNode extends BinaryExpr implements TypeRepresent {

    public FuncTypeNode(LineFile lineFile) {
        super("->", lineFile);
    }

    private boolean isLambdaOperator() {
        return left instanceof LambdaExpr;
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        if (!isLambdaOperator()) throw new TypeError();

        Line parameters = ((LambdaExpr) left).getParameters();

        List<Function.Parameter> params = new ArrayList<>();
        List<Type> paramTypes = new ArrayList<>();
        Function.evalParamTypes(parameters, params, paramTypes, env);

        FunctionEnvironment fakeEnv = new FunctionEnvironment(env, env);  // TODO:??
        for (Function.Parameter p : params) p.declaration.evaluate(fakeEnv);
        Type rt = right.inferredType(fakeEnv);

        CallableType lambdaType = new CallableType(paramTypes, rt);

        Function function = new Function(right, params, lambdaType, env, getLineFile());
        Pointer funcPtr = env.getMemory().allocateFunction(function, env);

        TypeValue funcTv = new TypeValue(lambdaType, funcPtr);
//        env.defineFunction(name, funcTv, getLineFile());
        return funcTv;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }

    @Override
    public Type evalType(Environment environment) {
        if (isLambdaOperator()) throw new TypeError();
        if (!(right instanceof TypeRepresent)) throw new TypeError();
        Type rType = ((TypeRepresent) right).evalType(environment);
        List<Node> paramNodes = ((Line) left).getChildren();
        List<Type> paramTypes = new ArrayList<>();
        for (Node node : paramNodes) {
            if (node instanceof TypeRepresent) {
                paramTypes.add(((TypeRepresent) node).evalType(environment));
            } else throw new TypeError();
        }
        return new CallableType(paramTypes, rType);
    }
}
