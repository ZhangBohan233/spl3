package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.Memory;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.primitives.Pointer;
import interpreter.primitives.Primitive;
import interpreter.splObjects.*;
import interpreter.types.*;
import util.LineFile;

import java.util.List;

public class Assignment extends BinaryExpr {
    public Assignment(LineFile lineFile) {
        super("=", lineFile);
    }

    @Override
    public TypeValue evaluate(Environment env) {
        TypeValue rightRes = right.evaluate(env);

        if (left instanceof NameNode) {
            env.setVar(((NameNode) left).getName(), rightRes, getLineFile());
        } else if (left instanceof Declaration) {
            left.evaluate(env);

            env.setVar(((Declaration) left).getLeft().getName(), rightRes, getLineFile());
        } else if (left instanceof Dot) {
            TypeValue leftLeft = ((Dot) left).left.evaluate(env);
            PointerType leftLeftType = (PointerType) leftLeft.getType();
            Environment leftEnv;
            if (leftLeftType.getPointerType() == PointerType.MODULE_TYPE) {
                SplModule leftModule = (SplModule) env.getMemory().get((Pointer) leftLeft.getValue());
                leftEnv = leftModule.getEnv();
            } else if (leftLeftType.getPointerType() == PointerType.CLASS_TYPE) {
                Instance leftModule = (Instance) env.getMemory().get((Pointer) leftLeft.getValue());
                leftEnv = leftModule.getEnv();
            } else {
                throw new TypeError();
            }
            leftEnv.setVar(((NameNode) ((Dot) left).right).getName(), rightRes, getLineFile());
        } else if (left instanceof IndexingNode) {
            TypeValue leftCallRes = ((IndexingNode) left).getCallObj().evaluate(env);
            List<Node> arguments = ((IndexingNode) left).getArgs().getChildren();
            int index = IndexingNode.getIndex(leftCallRes, arguments, env, getLineFile());
            setItemAtIndex((Pointer) leftCallRes.getValue(),
                    index,
                    (ArrayType) leftCallRes.getType(),
                    rightRes,
                    env,
                    getLineFile());
        } else {
            throw new SplException();
        }
        return rightRes;
    }

    private void setItemAtIndex(Pointer arrPtr,
                                int index,
                                ArrayType arrayType,
                                TypeValue valueTv,
                                Environment env,
                                LineFile lineFile) {
        Type arrEleType = arrayType.getEleType();
        if (arrEleType.isSuperclassOfOrEquals(valueTv.getType(), env)) {
            SplArray array = (SplArray) env.getMemory().get(arrPtr);
            if (index < 0 || index >= array.length) {
                throw new SplException("Index " + index + " out of array length " + array.length + ". ", lineFile);
            }
            env.getMemory().set(arrPtr.getPtr() + index + 1, new ReadOnlyPrimitiveWrapper(valueTv.getValue()));
        } else {
            throw new TypeError(String.format("Array element type: %s, argument type: %s. ",
                    arrEleType, valueTv.getType()));
        }
    }

    @Override
    public Node preprocess(FakeEnv env) {
        left = left.preprocess(env);
        right = right.preprocess(env);
        return this;
    }
}
