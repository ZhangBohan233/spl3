package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.primitives.Pointer;
import interpreter.splObjects.Instance;
import interpreter.splObjects.SplModule;
import interpreter.types.PointerType;
import interpreter.types.TypeError;
import interpreter.types.TypeValue;
import util.LineFile;

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
