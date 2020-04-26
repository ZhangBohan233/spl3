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
    public TypeValue evaluate(Environment env) {
        if (value instanceof FuncCall) {
            return instanceCreation((FuncCall) value, env, getLineFile());
        } else if (value instanceof IndexingNode) {
            return arrayCreation((IndexingNode) value, env, getLineFile());
        } else {
            throw new SplException("Class instantiation must be a call. ", getLineFile());
        }
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }

    private static TypeValue instanceCreation(FuncCall call, Environment env, LineFile lineFile) {
        TypeRepresent clazzNode = (TypeRepresent) call.left;
        Type type = clazzNode.evalType(env);
        if (!(type instanceof ClassType)) throw new TypeError();
        ClassType clazzType = (ClassType) type;

        InstanceTypeValue instanceTv = createInstanceAndAllocate(clazzType, env, lineFile);
        Instance instance = instanceTv.instance;

        Arguments args = (Arguments) call.right;
        TypeValue constructorTv = instance.getEnv().get("init", lineFile);
        Function constructor = (Function) env.getMemory().get((Pointer) constructorTv.getValue());
        constructor.call(args, env);

        return instanceTv.typeValue;
    }

    private TypeValue arrayCreation(IndexingNode node, Environment env, LineFile lineFile) {
        ArrayType arrayType = (ArrayType) node.evalType(env);
        List<Integer> dimensions = new ArrayList<>();
        traverseArrayCreation(node, dimensions, env, lineFile);
        Pointer arrPtr = SplArray.createArray(arrayType, dimensions, env.getMemory());

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

    private static InstanceTypeValue createInstanceAndAllocate(ClassType clazzType, Environment env, LineFile lineFile) {
        SplObject obj = env.getMemory().get(clazzType.getClazzPointer());
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
        Pointer instancePtr = env.getMemory().allocate(1);
        env.getMemory().set(instancePtr, instance);

        TypeValue instanceTv = new TypeValue(clazzType, instancePtr);

        instance.getEnv().directDefineConstAndSet("this", instanceTv);

        ClassType scp = clazz.getSuperclassType();
        if (scp != null) {
            InstanceTypeValue scItv = createInstanceAndAllocate(scp, env, lineFile);
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
