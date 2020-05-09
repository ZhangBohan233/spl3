package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.CallableType;
import interpreter.types.Type;
import interpreter.types.TypeError;
import interpreter.types.TypeValue;
import util.LineFile;

import java.util.ArrayList;
import java.util.List;

public class LambdaOperator extends BinaryExpr implements TypeRepresent {

    public LambdaOperator(LineFile lineFile) {
        super("->", lineFile);
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
    public Type evalType(Environment environment) {
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
