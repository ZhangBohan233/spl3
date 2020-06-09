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

    public final String name;
    private TypeRepresent rType;
    private Line parameters;
    private BlockStmt body;
    public final boolean isAbstract;

    public FuncDefinition(String name, boolean isAbstract, LineFile lineFile) {
        super(lineFile);

        this.name = name;
        this.isAbstract = isAbstract;
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

        List<Function.Parameter> params = new ArrayList<>();
        List<Type> paramTypes = new ArrayList<>();
        Function.evalParamTypes(parameters, params, paramTypes, env);

        Type rtype = rType.evalType(env);
        CallableType funcType = new CallableType(paramTypes, rtype);

        Function function;
        if (isAbstract) {
            function = new Function(params, funcType, env, name, getLineFile());
        } else {
            function = new Function(body, params, funcType, env, name, getLineFile());
        }
        Pointer funcPtr = env.getMemory().allocateFunction(function, env);

        TypeValue funcTv = new TypeValue(funcType, funcPtr);
        env.defineFunction(name, funcTv, getLineFile());
        return funcTv;
    }

    @Override
    public FuncDefinition preprocess(FakeEnv env) {
        return this;
    }

    @Override
    public String toString() {
        if (name == null)
            return String.format("fn(%s)->%s: %s", parameters, rType, body);
        else
            return String.format("fn %s(%s)->%s: %s", name, parameters, rType, body);
    }

    public boolean doesOverride(FuncDefinition superMethod, Environment env) {
        if (parameters.getChildren().size() != superMethod.parameters.getChildren().size()) return false;
        for (int i = 0 ; i < parameters.getChildren().size(); ++i) {
            Node thisParam = parameters.getChildren().get(i);
            Node superParam = superMethod.parameters.getChildren().get(i);
            // TODO: check this. Two cases: Declaration and Assignment
        }

        Type thisRType = rType.evalType(env);
        Type superRType = superMethod.rType.evalType(env);
        return superRType.isSuperclassOfOrEquals(thisRType, env);
    }
}
