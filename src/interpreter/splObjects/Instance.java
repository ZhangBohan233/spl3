package interpreter.splObjects;

import ast.*;
import interpreter.env.Environment;
import interpreter.env.InstanceEnvironment;
import interpreter.primitives.Pointer;
import interpreter.types.ClassType;
import interpreter.types.TypeError;
import interpreter.types.TypeValue;
import util.LineFile;

import java.util.List;

public class Instance extends SplObject {

    private final InstanceEnvironment env;
    private final ClassType type;

    private Instance(ClassType type, InstanceEnvironment env) {
        this.type = type;
        this.env = env;
    }

    public InstanceEnvironment getEnv() {
        return env;
    }

    public ClassType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Instance<" + type + ">";
    }

    public static InstanceTypeValue createInstanceAndAllocate(ClassType clazzType,
                                                              Environment outerEnv,
                                                              LineFile lineFile) {
        SplObject obj = outerEnv.getMemory().get(clazzType.getClazzPointer());
        if (!(obj instanceof SplClass)) throw new TypeError();
        SplClass clazz = (SplClass) obj;
        InstanceEnvironment instanceEnv = new InstanceEnvironment(clazz.getDefinitionEnv());

        clazz.getBody().evaluate(instanceEnv);  // most important step

        if (!instanceEnv.hasName("init", lineFile)) {
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
        Pointer instancePtr = outerEnv.getMemory().allocate(1, instanceEnv);
        outerEnv.getMemory().set(instancePtr, instance);

        TypeValue instanceTv = new TypeValue(clazzType, instancePtr);

        instance.getEnv().directDefineConstAndSet("this", instanceTv);

        ClassType scp = clazz.getSuperclassType();
        if (scp != null) {
            InstanceTypeValue scItv = createInstanceAndAllocate(scp, outerEnv, lineFile);
            TypeValue scInstance = scItv.typeValue;
            instance.getEnv().directDefineConstAndSet("super", scInstance);
        }

        return new InstanceTypeValue(instance, instanceTv);
    }

    public static void callInit(Instance instance, Arguments arguments, Environment callEnv, LineFile lineFile) {
        TypeValue constructorTv = instance.getEnv().get("init", lineFile);
        Function constructor = (Function) callEnv.getMemory().get((Pointer) constructorTv.getValue());
        constructor.call(arguments, callEnv);
    }

    public static void callInit(Instance instance,
                                TypeValue[] evaluatedArgs,
                                Environment callEnv,
                                LineFile lineFile) {
        TypeValue constructorTv = instance.getEnv().get("init", lineFile);
        Function constructor = (Function) callEnv.getMemory().get((Pointer) constructorTv.getValue());
        constructor.call(evaluatedArgs, callEnv, lineFile);
    }

    public static class InstanceTypeValue {
        public final Instance instance;
        public final TypeValue typeValue;

        InstanceTypeValue(Instance instance, TypeValue typeValue) {
            this.instance = instance;
            this.typeValue = typeValue;
        }
    }
}
