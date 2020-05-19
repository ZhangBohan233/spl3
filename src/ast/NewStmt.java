package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.primitives.Pointer;
import interpreter.splObjects.*;
import interpreter.types.*;
import util.LineFile;

import java.util.ArrayList;
import java.util.List;

public class NewStmt extends UnaryExpr {

    public NewStmt(LineFile lineFile) {
        super("new", true, lineFile);
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        if (value instanceof AnonymousClassExpr) {
            AnonymousClassExpr ace = (AnonymousClassExpr) value;
            return initAnonymousClass(ace.left, ace.getContent(), env, env, getLineFile());
        } else {
            return directInitClass(value, env, env, getLineFile());
        }
    }

    @Override
    protected Type inferredType(Environment env) {
        return typeInference(value, env, getLineFile());
    }

    private static Type typeInference(Node node, Environment env, LineFile lineFile) {
        if (node instanceof FuncCall) {
            return  ((TypeRepresent) ((FuncCall) node).callObj).evalType(env);
        } else if (node instanceof IndexingNode) {
            return ((IndexingNode) node).evalType(env);
        } else if (node instanceof Dot) {
            Dot dot = (Dot) node;
            TypeValue dotLeft = dot.left.evaluate(env);
            if (!(dotLeft.getType() instanceof ModuleType)) throw new TypeError();
            SplModule module = (SplModule) env.getMemory().get((Pointer) dotLeft.getValue());
            return typeInference(dot.right, module.getEnv(), lineFile);
        } else {
            throw new SplException("Class type must be a call. Got " + node + " instead. ", lineFile);
        }
    }

    private static TypeValue directInitClass(Node node, Environment classDefEnv, Environment callEnv, LineFile lineFile) {
        if (node instanceof FuncCall) {
            return instanceCreation((FuncCall) node, classDefEnv, callEnv, lineFile);
        } else if (node instanceof IndexingNode) {
            return arrayCreation((IndexingNode) node, classDefEnv, callEnv, lineFile);
        } else if (node instanceof Dot) {
            Dot dot = (Dot) node;
            TypeValue dotLeft = dot.left.evaluate(classDefEnv);
            if (!(dotLeft.getType() instanceof ModuleType)) throw new TypeError();
            SplModule module = (SplModule) classDefEnv.getMemory().get((Pointer) dotLeft.getValue());
            return directInitClass(dot.right, module.getEnv(), callEnv, lineFile);
        } else {
            throw new SplException("Class instantiation must be a call. Got " + node + " instead. ", lineFile);
        }
    }

    private static TypeValue initAnonymousClass(Node node,
                                                BlockStmt classBody,
                                                Environment classDefEnv,
                                                Environment callEnv,
                                                LineFile lineFile) {
        if (node instanceof FuncCall) {
            return anonymousInstanceCreation((FuncCall) node, classBody, classDefEnv, callEnv, lineFile);
        } else if (node instanceof Dot) {
            Dot dot = (Dot) node;
            TypeValue dotLeft = dot.left.evaluate(classDefEnv);
            if (!(dotLeft.getType() instanceof ModuleType)) throw new TypeError();
            SplModule module = (SplModule) classDefEnv.getMemory().get((Pointer) dotLeft.getValue());
            return initAnonymousClass(dot.right, classBody, module.getEnv(), callEnv, lineFile);
        } else {
            throw new SplException("Anonymous class instantiation must have a call to its parent constructor. " +
                    "Got " + node + " instead. ", lineFile);
        }
    }

    private static TypeValue anonymousInstanceCreation(FuncCall call,
                                                    BlockStmt classBody,
                                                    Environment classDefEnv,
                                                    Environment callEnv,
                                                    LineFile lineFile) {

        TypeRepresent scClazzNode = (TypeRepresent) call.callObj;
        Type scType = scClazzNode.evalType(classDefEnv);
        if (!(scType instanceof ClassType)) throw new TypeError();
        ClassType scClazzType = (ClassType) scType;

        // define the anonymous class
        // Note that the definition env of the anonymous class is the current calling env
        SplClass anClazz = new SplClass(null, scClazzType, new ArrayList<>(), classBody, callEnv);
        Pointer anClazzPtr = callEnv.getMemory().allocateObject(anClazz, callEnv);
        ClassType anClazzType = new ClassType(anClazzPtr);

        Instance.InstanceTypeValue instanceTv = Instance.createInstanceAndAllocate(anClazzType, callEnv, lineFile);
        Instance instance = instanceTv.instance;

        TypeValue supTv = instance.getEnv().get("super", lineFile);
        Instance supIns = (Instance) instance.getEnv().getMemory().get((Pointer) supTv.getValue());

        Instance.callInit(supIns, call.arguments, callEnv, lineFile);
        return instanceTv.typeValue;
    }

    private static TypeValue instanceCreation(FuncCall call,
                                              Environment classDefEnv,
                                              Environment callEnv,
                                              LineFile lineFile) {
        TypeRepresent clazzNode = (TypeRepresent) call.callObj;
        Type type = clazzNode.evalType(classDefEnv);
        if (!(type instanceof ClassType)) throw new TypeError();
        ClassType clazzType = (ClassType) type;

        Instance.InstanceTypeValue instanceTv = Instance.createInstanceAndAllocate(clazzType, callEnv, lineFile);
        Instance instance = instanceTv.instance;

        Instance.callInit(instance, call.arguments, callEnv, lineFile);
        return instanceTv.typeValue;
    }

    private static TypeValue arrayCreation(IndexingNode node,
                                           Environment classDefEnv,
                                           Environment callEnv,
                                           LineFile lineFile) {
        ArrayType arrayType = (ArrayType) node.evalType(classDefEnv);
        List<Integer> dimensions = new ArrayList<>();
        traverseArrayCreation(node, dimensions, callEnv, lineFile);
        Pointer arrPtr = SplArray.createArray(arrayType, dimensions, callEnv);

        return new TypeValue(arrayType, arrPtr);
    }

    private static void traverseArrayCreation(IndexingNode node,
                                              List<Integer> dimensions,
                                              Environment env,
                                              LineFile lineFile) {
        List<Node> argsList = node.getArgs().getChildren();
        if (node.getCallObj() instanceof IndexingNode) {

            traverseArrayCreation((IndexingNode) node.getCallObj(),
                    dimensions,
                    env,
                    lineFile);

            if (argsList.size() == 0) {
                dimensions.add(-1);
            } else if (argsList.size() == 1) {
                TypeValue argument = argsList.get(0).evaluate(env);
                if (argument.getType().equals(PrimitiveType.TYPE_INT)) {
                    int arrSize = (int) argument.getValue().intValue();
                    dimensions.add(arrSize);
                } else {
                    throw new TypeError();
                }
            } else {
                throw new TypeError("Array creation must have a size argument", lineFile);
            }
        } else {
            if (argsList.size() != 1) {
                throw new TypeError("Array creation must have a size argument", lineFile);
            }
            TypeValue argument = argsList.get(0).evaluate(env);
            if (argument.getType().equals(PrimitiveType.TYPE_INT)) {
                int arrSize = (int) argument.getValue().intValue();
                dimensions.add(arrSize);
            } else {
                throw new TypeError();
            }
        }
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }
}
