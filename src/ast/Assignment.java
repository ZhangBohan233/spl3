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
        if (env.interrupted()) return null;

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
            SplArray.setItemAtIndex((Pointer) leftCallRes.getValue(),
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

    @Override
    public Node preprocess(FakeEnv env) {
        left = left.preprocess(env);
        right = right.preprocess(env);
        return this;
    }
}
