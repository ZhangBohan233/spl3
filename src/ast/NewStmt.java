package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.primitives.Pointer;
import interpreter.splObjects.Instance;
import interpreter.splObjects.SplClass;
import interpreter.splObjects.SplObject;
import interpreter.types.*;
import util.LineFile;

public class NewStmt extends UnaryExpr {

    public NewStmt(LineFile lineFile) {
        super("new", true, lineFile);
    }

    @Override
    public TypeValue evaluate(Environment env) {
        // TODO: Array creation

        TypeRepresent clazzNode = (TypeRepresent) getValue().left;
        Arguments args = (Arguments) getValue().right;
        Type type = clazzNode.evalType(env);
        if (!(type instanceof ClassType)) throw new TypeError();
        ClassType clazzType = (ClassType) type;
        SplObject obj = env.getMemory().get(clazzType.getClazzPointer());
        if (!(obj instanceof SplClass)) throw new TypeError();
        SplClass clazz = (SplClass) obj;

        Instance instance = new Instance(clazzType, clazz.getClassBaseEnv().createInstanceEnv());

        Pointer instancePtr = env.getMemory().allocate(1);
        env.getMemory().set(instancePtr, instance);
        return new TypeValue(clazzType, instancePtr);
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }

    private FuncCall getValue() {
        if (value instanceof FuncCall) {
            return (FuncCall) value;
        } else {
            throw new SplException("Class instantiation must be a call. ", getLineFile());
        }
    }
}
