package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.env.InstanceEnvironment;
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
        Type type = clazzNode.evalType(env);
        if (!(type instanceof ClassType)) throw new TypeError();
        ClassType clazzType = (ClassType) type;

        TypeValue instanceTv = createInstanceAndAllocate(clazzType, env);

        Arguments args = (Arguments) getValue().right;
//        CallableType callableType = instance.getEnv().get();

        return instanceTv;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }

    private static TypeValue createInstanceAndAllocate(ClassType clazzType, Environment env) {
        SplObject obj = env.getMemory().get(clazzType.getClazzPointer());
        if (!(obj instanceof SplClass)) throw new TypeError();
        SplClass clazz = (SplClass) obj;
        InstanceEnvironment instanceEnv = new InstanceEnvironment(clazz.getDefinitionEnv());
        clazz.getBody().evaluate(instanceEnv);
        Instance instance = new Instance(clazzType, instanceEnv);
        Pointer instancePtr = env.getMemory().allocate(1);
        env.getMemory().set(instancePtr, instance);

        TypeValue instanceTv = new TypeValue(clazzType, instancePtr);

        instance.getEnv().directDefineConstAndSet("this", instanceTv);

        ClassType scp = clazz.getSuperclassType();
        if (scp != null) {
            TypeValue scInstance = createInstanceAndAllocate(scp, env);
            instance.getEnv().directDefineConstAndSet("super", scInstance);
        }

        return instanceTv;
    }

    private FuncCall getValue() {
        if (value instanceof FuncCall) {
            return (FuncCall) value;
        } else {
            throw new SplException("Class instantiation must be a call. ", getLineFile());
        }
    }
}
