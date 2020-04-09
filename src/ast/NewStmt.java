package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.env.InstanceEnvironment;
import interpreter.primitives.Pointer;
import interpreter.splObjects.Function;
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

        InstanceTypeValue instanceTv = createInstanceAndAllocate(clazzType, env);
        Instance instance = instanceTv.instance;

        Arguments args = (Arguments) getValue().right;
        TypeValue constructorTv = instance.getEnv().get("init");
        Function constructor = (Function) env.getMemory().get((Pointer) constructorTv.getValue());
        constructor.call(args, env);

        return instanceTv.typeValue;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }

    private static InstanceTypeValue createInstanceAndAllocate(ClassType clazzType, Environment env) {
        SplObject obj = env.getMemory().get(clazzType.getClazzPointer());
        if (!(obj instanceof SplClass)) throw new TypeError();
        SplClass clazz = (SplClass) obj;
        InstanceEnvironment instanceEnv = new InstanceEnvironment(clazz.getDefinitionEnv());

        clazz.getBody().evaluate(instanceEnv);  // most important step

        if (!instanceEnv.hasName("init")) {
            // If class no constructor, put an empty default constructor
            // TODO: call super() in constructor
            FuncDefinition fd = new FuncDefinition("init", LineFile.LF_INTERPRETER);
            fd.setParameters(new Line());
            fd.setRType(new PrimitiveTypeNameNode("void", LineFile.LF_INTERPRETER));
            BlockStmt constBody = new BlockStmt(LineFile.LF_INTERPRETER);
            fd.setBody(constBody);

            fd.evaluate(instanceEnv);
        }

        Instance instance = new Instance(clazzType, instanceEnv);
        Pointer instancePtr = env.getMemory().allocate(1);
        env.getMemory().set(instancePtr, instance);

        TypeValue instanceTv = new TypeValue(clazzType, instancePtr);

        instance.getEnv().directDefineConstAndSet("this", instanceTv);

        ClassType scp = clazz.getSuperclassType();
        if (scp != null) {
            InstanceTypeValue scItv = createInstanceAndAllocate(scp, env);
            TypeValue scInstance = scItv.typeValue;
            instance.getEnv().directDefineConstAndSet("super", scInstance);
        }

        return new InstanceTypeValue(instance, instanceTv);
    }

    private FuncCall getValue() {
        if (value instanceof FuncCall) {
            return (FuncCall) value;
        } else {
            throw new SplException("Class instantiation must be a call. ", getLineFile());
        }
    }

    private static class InstanceTypeValue {
        private Instance instance;
        private TypeValue typeValue;

        InstanceTypeValue(Instance instance, TypeValue typeValue) {
            this.instance = instance;
            this.typeValue = typeValue;
        }
    }
}
