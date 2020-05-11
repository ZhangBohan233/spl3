package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.splObjects.Function;
import interpreter.types.TypeValue;
import interpreter.types.CallableType;
import interpreter.types.Type;
import interpreter.env.Environment;
import interpreter.primitives.Pointer;
import parser.ParseError;
import util.LineFile;

import java.util.ArrayList;
import java.util.List;

public class FuncDefinition extends Node {

    private final String name;
    private TypeRepresent rType;
    private Line parameters;
    private BlockStmt body;

    public FuncDefinition(String name, LineFile lineFile) {
        super(lineFile);

        this.name = name;
    }

    public void setParameters(Line parameters) {
        this.parameters = parameters;
    }

    public void setRType(Node rType) {
        if (rType instanceof TypeRepresent)
            this.rType = (TypeRepresent) rType;
        else throw new ParseError("'" + rType + "' is not a type. ", rType.getLineFile());
    }

    public void setBody(BlockStmt body) {
        this.body = body;
    }

    @Override
    protected TypeValue internalEval(Environment env) {

        List<Declaration> params = new ArrayList<>();
        List<Type> paramTypes = new ArrayList<>();
        for (int i = 0; i < parameters.getChildren().size(); ++i) {
            Node node = parameters.getChildren().get(i);
            if (node instanceof Declaration) {
                params.add((Declaration) node);
                paramTypes.add(((Declaration) node).getRight().evalType(env));
            } else {
                throw new ParseError("Unexpected parameter syntax. ", node.getLineFile());
            }
        }

        Type rtype = rType.evalType(env);
        CallableType funcType = new CallableType(paramTypes, rtype);

        Function function = new Function(body, params, funcType, env, getLineFile());
        Pointer funcPtr = env.getMemory().allocateFunction(function, env);

        TypeValue funcTv = new TypeValue(funcType, funcPtr);
        env.defineFunction(name, funcTv, getLineFile());
        return funcTv;
    }

    @Override
    public FuncDefinition preprocess(FakeEnv env) {
//        FakeFunctionEnv scope = new FakeFunctionEnv(env);
//        for (int i = 0; i < parameters.getChildren().size(); ++i) {
//            Node node = parameters.getChildren().get(i);
//            if (!(node instanceof Declaration)) {
//                throw new ParseError("Unexpected parameter syntax. ", node.getLineFile());
//            }
//            parameters.getChildren().set(i, node.preprocess(scope));
//        }
//        body = body.preprocess(scope);
//        stackUsage = scope.getStackCounter();

        return this;
    }

    @Override
    public String toString() {
        if (name == null)
            return String.format("fn(%s)->%s: %s", parameters, rType, body);
        else
            return String.format("fn %s(%s)->%s: %s", name, parameters, rType, body);
    }
}
