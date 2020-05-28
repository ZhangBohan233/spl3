package interpreter.splObjects;

import ast.*;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.env.InstanceEnvironment;
import interpreter.primitives.Pointer;
import interpreter.types.*;
import util.LineFile;

import java.util.*;

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
        return createInstanceAndAllocate(clazzType, outerEnv, lineFile, new TreeMap<>());
    }

    private static InstanceTypeValue createInstanceAndAllocate(ClassType clazzType,
                                                               Environment outerEnv,
                                                               LineFile lineFile,
                                                               Map<String, TypeValue> undeterminedTemplates) {
        SplObject obj = outerEnv.getMemory().get(clazzType.getClazzPointer());
        if (!(obj instanceof SplClass)) throw new TypeError();
        SplClass clazz = (SplClass) obj;
        InstanceEnvironment instanceEnv = new InstanceEnvironment(
                clazz.getClassName(),
                clazz.getDefinitionEnv(),
                outerEnv
        );

        Instance instance = new Instance(clazzType, instanceEnv);
        Pointer instancePtr = outerEnv.getMemory().allocate(1, instanceEnv);
        outerEnv.getMemory().set(instancePtr, instance);

//        System.out.println(clazzType);

        TypeValue instanceTv = new TypeValue(clazzType, instancePtr);
        instance.getEnv().directDefineConstAndSet("this", instanceTv);

        Map<String, TypeValue> newUndTemplates = new TreeMap<>();

//        System.out.println("===");
//        System.out.println(clazz.templates);
//        System.out.println(Arrays.toString(clazzType.getTemplates()));
        if (clazzType.getTemplates() != null) {
            // has templates
            for (int i = 0; i < clazz.templates.size(); ++i) {
                Node templateDef = clazz.templates.get(i);
                if (templateDef instanceof NameNode) {
                    String templateName = ((NameNode) templateDef).getName();
                    TypeValue actualT = clazzType.getTemplates()[i];
                    TypeValue put;
                    if (actualT.getType() instanceof UndTemplateType) {
                        put = undeterminedTemplates.get(((UndTemplateType) actualT.getType()).templateName);
                    } else {
                        put = actualT;
                    }
                    instanceEnv.defineConstAndSet(templateName, put, lineFile);
                    newUndTemplates.put(templateName, put);
                } else {
                    // TODO: maybe <T extends Clazz> ?
                }
            }
        } else if (clazz.templates.size() > 0) {
            // class declares template but instance does not give actual templates
            // Example:
            // class SomeClazz<T> {...}
            // new SomeClazz();
//            System.out.println(clazz.templates);
            for (Node templateDef : clazz.templates) {
                if (templateDef instanceof NameNode) {
                    String templateName = ((NameNode) templateDef).getName();
                    TypeValue objectTv = instanceEnv.get("Object", lineFile);
                    instanceEnv.defineConstAndSet(templateName,
                            objectTv,
                            lineFile);
                    newUndTemplates.put(templateName, objectTv);
                } else {
                    // TODO
                }
            }
        }

        ClassType scp = clazz.getSuperclassType();
        if (scp != null) {
            InstanceTypeValue scItv = createInstanceAndAllocate(scp, outerEnv, lineFile, newUndTemplates);
            TypeValue scInstance = scItv.typeValue;
            instance.getEnv().directDefineConstAndSet("super", scInstance);
        }

        clazz.getBody().evaluate(instanceEnv);  // most important step

        if (!instanceEnv.selfContains("init")) {
            // If class no constructor, put an empty default constructor
            FuncDefinition fd = new FuncDefinition("init", false, LineFile.LF_INTERPRETER);
            fd.setParameters(new Line());
            fd.setRType(new PrimitiveTypeNameNode("void", LineFile.LF_INTERPRETER));
            BlockStmt constBody = new BlockStmt(LineFile.LF_INTERPRETER);

            fd.setBody(constBody);

            fd.evaluate(instanceEnv);
        }

        return new InstanceTypeValue(instance, instanceTv);
    }

    public static void callInit(Instance instance, Arguments arguments, Environment callEnv, LineFile lineFile) {
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
//            List<Type> supParamTypes = supConst.getFuncType().getParamTypes();
            if (supConst.minArgCount() > 0) {
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
            supInit.setArguments(new Arguments(new Line(), LineFile.LF_INTERPRETER));
            dot.setRight(supInit);

            Line constLine = new Line();
            constLine.getChildren().add(dot);
            ((BlockStmt) body).getLines().add(0, constLine);
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
