package interpreter.splObjects;

import ast.*;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.env.InstanceEnvironment;
import interpreter.primitives.Pointer;
import interpreter.types.ClassType;
import interpreter.types.Type;
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

//        System.out.println(clazz.getBody());
        clazz.getBody().evaluate(instanceEnv);  // most important step

        if (!instanceEnv.hasName("init", lineFile)) {
            // If class no constructor, put an empty default constructor
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
//        TypeValue constructorTv = instance.getEnv().get("init", lineFile);
//        Function constructor = (Function) callEnv.getMemory().get((Pointer) constructorTv.getValue());
        Function constructor = getConstructor(instance, lineFile);
        constructor.call(arguments, callEnv);
    }

    public static void callInit(Instance instance,
                                TypeValue[] evaluatedArgs,
                                Environment callEnv,
                                LineFile lineFile) {

        Function constructor = getConstructor(instance, lineFile);
        constructor.call(evaluatedArgs, callEnv, lineFile);
    }

    private static Function getConstructor(Instance instance, LineFile lineFile) {
        InstanceEnvironment env = instance.getEnv();
        TypeValue constructorTv = env.get("init", lineFile);
        Function constructor = (Function) env.getMemory().get((Pointer) constructorTv.getValue());

        if (env.hasName("super", lineFile)) {
            // All classes has superclass except class 'Object'
            TypeValue superTv = env.get("super", lineFile);
            Instance supIns = (Instance) env.getMemory().get((Pointer) superTv.getValue());
            InstanceEnvironment supEnv = supIns.getEnv();
            TypeValue supConstTv = supEnv.get("init", lineFile);
            Function supConst = (Function) env.getMemory().get((Pointer) supConstTv.getValue());
            List<Type> supParamTypes = supConst.getFuncType().getParamTypes();
            if (supParamTypes.size() > 0) {
                // superclass has a non-trivial constructor
                if (noLeadingSuperCall(constructor)) {
                    throw new SplException("Constructor of child class must first call super.init() with matching " +
                            "arguments. ", lineFile);
                }
            } else {
                // superclass constructor has no parameters
                if (noLeadingSuperCall(constructor)) {
                    addDefaultSuperCall(constructor);
                }
            }
        }

        return constructor;
    }

    private static void addDefaultSuperCall(Function constructor) {
        Node body = constructor.getBody();
        if (body instanceof BlockStmt) {
            // call super.init()
            // if super.init(...) has arguments, this causes an error intentionally
            Dot dot = new Dot(LineFile.LF_INTERPRETER);
            dot.setLeft(new NameNode("super", LineFile.LF_INTERPRETER));
            FuncCall supInit = new FuncCall(LineFile.LF_INTERPRETER);
            supInit.setCallObj(new NameNode("init", LineFile.LF_INTERPRETER));
            supInit.setArguments(new Arguments(new Line()));
            dot.setRight(supInit);

            Line constLine = new Line();
            constLine.getChildren().add(dot);
            ((BlockStmt) body).addLine(constLine);
        } else {
            throw new SplException("Unexpected syntax. ");
        }
    }

    private static boolean noLeadingSuperCall(Function constructor) {
        Node body = constructor.getBody();
        if (body instanceof BlockStmt) {
            if (((BlockStmt) body).getLines().size() > 0) {
                Line firstLine = ((BlockStmt) body).getLines().get(0);
                if (firstLine.getChildren().size() == 1) {
                    Node node = firstLine.getChildren().get(0);
                    if (node instanceof Dot) {
                        Node left = ((Dot) node).getLeft();
                        Node right = ((Dot) node).getRight();
                        if (left instanceof NameNode && right instanceof FuncCall) {
                            Node callObj = ((FuncCall) right).getCallObj();
                            if (callObj instanceof NameNode) {
                                return !((NameNode) left).getName().equals("super") ||
                                        !((NameNode) callObj).getName().equals("init");
                            }
                        }
                    }
                }
            }
        }
        return true;
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
