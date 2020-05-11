package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.env.InstanceEnvironment;
import interpreter.primitives.Pointer;
import interpreter.splObjects.*;
import interpreter.types.*;
import util.LineFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewStmt extends UnaryExpr {

    public NewStmt(LineFile lineFile) {
        super("new", true, lineFile);
    }

    @Override
    protected TypeValue internalEval(Environment env) {

        return initClass(value, env, env, getLineFile());
    }

    private static TypeValue initClass(Node node, Environment classDefEnv, Environment callEnv, LineFile lineFile) {
        if (node instanceof FuncCall) {
            return instanceCreation((FuncCall) node, classDefEnv, callEnv, lineFile);
        } else if (node instanceof IndexingNode) {
            return arrayCreation((IndexingNode) node, classDefEnv, callEnv, lineFile);
        } else if (node instanceof Dot) {
            Dot dot = (Dot) node;
            TypeValue dotLeft = dot.left.evaluate(classDefEnv);
            if (!(dotLeft.getType() instanceof ModuleType)) throw new TypeError();
            SplModule module = (SplModule) classDefEnv.getMemory().get((Pointer) dotLeft.getValue());
            return initClass(dot.right, module.getEnv(), callEnv, lineFile);
        } else {
            throw new SplException("Class instantiation must be a call. Got " + node + " instead. ", lineFile);
        }
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
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
        Pointer arrPtr = SplArray.createArray(arrayType, dimensions, callEnv.getMemory());

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

    private FuncCall getValue() {
        if (value instanceof FuncCall) {
            return (FuncCall) value;
        } else {
            throw new SplException("Class instantiation must be a call. ", getLineFile());
        }
    }
}
