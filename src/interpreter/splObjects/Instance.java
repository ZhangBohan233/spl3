package interpreter.splObjects;

import ast.*;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.env.InstanceEnvironment;
import interpreter.primitives.Pointer;
import interpreter.types.*;
import parser.ParseError;
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
        return createInstanceAndAllocate(
                clazzType, outerEnv, lineFile, new TreeMap<>(), new HashMap<>(), true);
    }

    /**
     * Creates an instance and allocate it in memory.
     *
     * @param clazzType             class type
     * @param outerEnv              env where the new instance is created, not the class definition env
     * @param lineFile              error traceback info of code where instance creation
     * @param undeterminedTemplates undetermined templates, used to pass templates from subclass to superclass.
     *                              For example, in code snippet:
     *                              {@code
     *                              class A<T> {
     *                              ...
     *                              }
     *                              class B<T> extends A<T> {
     *                              ...
     *                              }
     *                              ...
     *                              x : B<String> = new B<String>;
     *                              }
     *                              {@code undeterminedTemplates} contains {T: {TypeValue(String)}}
     * @param methods               all method definition of the sub-most class
     * @param isFirstCall           whether the currently creating instance is the actual instance
     * @return the tuple of the newly created instance, and the {@code TypeValue} contains the pointer to this instance
     */
    private static InstanceTypeValue createInstanceAndAllocate(ClassType clazzType,
                                                               Environment outerEnv,
                                                               LineFile lineFile,
                                                               Map<String, TypeValue> undeterminedTemplates,
                                                               Map<String, FuncDefinition> methods,
                                                               boolean isFirstCall) {

        SplObject obj = outerEnv.getMemory().get(clazzType.getClazzPointer());
        if (!(obj instanceof SplClass)) throw new TypeError();
        SplClass clazz = (SplClass) obj;
        if (clazz.isAbstract && isFirstCall) {
            throw new SplException("Abstract class '" + clazz.getClassName() + "' is not instantiable. ", lineFile);
        }
        if (clazz.isInterface && isFirstCall) {
            throw new SplException("Interface '" + clazz.getClassName() + "' is not instantiable. ", lineFile);
        }
        InstanceEnvironment instanceEnv = new InstanceEnvironment(
                clazz.getClassName(),
                clazz.getDefinitionEnv(),
                outerEnv
        );
        outerEnv.getMemory().addTempEnv(instanceEnv);

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

        // deal with templates
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

        // evaluate superclasses
        ClassType scp = clazz.getSuperclassType();
        if (scp != null) {
            InstanceTypeValue scItv =
                    createInstanceAndAllocate(scp.copy(), outerEnv, lineFile, newUndTemplates, methods, false);
            TypeValue scInstance = scItv.typeValue;
            instance.getEnv().directDefineConstAndSet("super", scInstance);
        }

        // evaluate interfaces
        for (ClassType interfaceT: clazz.getInterfacePointers()) {
            ClassType thisInterfaceT = interfaceT.copy();
            createInstanceAndAllocate(thisInterfaceT, outerEnv, lineFile, newUndTemplates, methods, false);
        }

        Map<String, FuncDefinition> implementedMethods = new HashMap<>();
        // evaluate class body
        for (Line line : clazz.getBody().getLines()) {  // class body
            Node element = extractOnlyElementFromLine(line);
            if (element instanceof Declaration || element instanceof Assignment) {
                /*
                Attribute declaration.
                Since expr `x: Something` is build as a declaration in ast while `x: Something = value`
                is an assignment in ast.
                Note that quick assignment `:=` is not allowed in class body
                 */
                element.evaluate(instanceEnv);
            } else if (element instanceof FuncDefinition) {
                // method declaration
                String fnName = ((FuncDefinition) element).name;
                element.evaluate(instanceEnv);
                implementedMethods.put(fnName, (FuncDefinition) element);
            } else if (element != null) {
                throw new SplException("Only attribute or method declarations are allowed in class body, got '" +
                        element.getClass() + "'. ",
                        element.getLineFile());
            }
        }
//        clazz.getBody().evaluate(instanceEnv);  // most important step

        // check method implementations
        for (Map.Entry<String, FuncDefinition> methodInSc : methods.entrySet()) {
            if (!implementedMethods.containsKey(methodInSc.getKey())) {
                if (!(clazz.isAbstract || clazz.isInterface) && methodInSc.getValue().isAbstract) {
                    assert scp != null;  // if scp == null, `method` should be empty
                    List<SplObject> interfaces = new ArrayList<>();
                    for (ClassType itrT: clazz.getInterfacePointers()) {
                        interfaces.add(instanceEnv.getMemory().get(itrT.getClazzPointer()));
                    }
                    throw new SplException(
                            String.format("'%s' extends '%s' implements %s but not implements abstract method '%s'. ",
                                    clazz,
                                    instanceEnv.getMemory().get(scp.getClazzPointer()),
                                    interfaces,
                                    methodInSc.getValue().name
                            ), lineFile);
                }
                /*
                Do an identical override.
                This process is used to fix the inheritance bug specified in issues.md, ISSUE C01
                 */
                methodInSc.getValue().evaluate(instanceEnv);
            } else {
                FuncDefinition impMethod = implementedMethods.get(methodInSc.getKey());
                if (!impMethod.doesOverride(methodInSc.getValue(), instanceEnv)) {
                    throw new TypeError("Function '" + impMethod.name +
                            "' does not overrides its super function, but has " +
                            "identical names. ", impMethod.getLineFile());
                }
            }
        }
        methods.putAll(implementedMethods);

        if (!instanceEnv.selfContains("init")) {
            // If class no constructor, put an empty default constructor
            FuncDefinition fd = new FuncDefinition("init", false, LineFile.LF_INTERPRETER);
            fd.setParameters(new Line());
            fd.setRType(new PrimitiveTypeNameNode("void", LineFile.LF_INTERPRETER));
            BlockStmt constBody = new BlockStmt(LineFile.LF_INTERPRETER);

            fd.setBody(constBody);

            fd.evaluate(instanceEnv);
        }

        outerEnv.getMemory().removeTempEnv(instanceEnv);

        return new InstanceTypeValue(instance, instanceTv);
    }

//    private static void

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

    private static Node extractOnlyElementFromLine(Line line) {
        if (line.getChildren().size() == 0) return null;
        else if (line.getChildren().size() == 1) {
            Node node = line.getChildren().get(0);
            if (node instanceof Line) return extractOnlyElementFromLine((Line) node);
            else return node;
        } else {
            throw new SplException("Too many elements in one line. ", line.getChildren().get(0).getLineFile());
        }
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
