package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.Function;
import interpreter.env.TypeValue;
import interpreter.types.CallableType;
import interpreter.types.Type;
import interpreter.env.Environment;
import interpreter.primitives.Pointer;
import parser.ParseError;
import util.LineFile;

import java.util.ArrayList;
import java.util.List;

public class FuncDefinition extends Node {

    private String name;
    private Node rType;
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
        this.rType = rType;
    }

    public void setBody(BlockStmt body) {
        this.body = body;
    }

    @Override
    public Object evaluate(Environment env) {
        List<Declaration> params = new ArrayList<>();
        List<Type> paramTypes = new ArrayList<>();
        for (int i = 0; i < parameters.getChildren().size(); ++i) {
            Node node = parameters.getChildren().get(i);
            if (node instanceof Declaration) {
                params.add((Declaration) node);
                paramTypes.add((Type) ((Declaration) node).right.evaluate(env));
            } else {
                throw new ParseError("Unexpected parameter syntax. ", node.getLineFile());
            }
        }

        Type rtype = (Type) rType.evaluate(env);
        CallableType funcType = new CallableType(paramTypes, rtype);

        Function function = new Function(body, params, funcType, env);
        Pointer funcPtr = env.getMemory().allocateFunction(function);

        TypeValue funcTv = new TypeValue(funcType, funcPtr);
        env.defineFunction(name, funcTv);
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
