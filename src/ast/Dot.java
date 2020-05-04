package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.splObjects.Instance;
import interpreter.splObjects.NativeObject;
import interpreter.splObjects.SplArray;
import interpreter.types.*;
import interpreter.primitives.Pointer;
import interpreter.splObjects.SplModule;
import util.LineFile;

public class Dot extends BinaryExpr implements TypeRepresent {
    public Dot(LineFile lineFile) {
        super(".", lineFile);
    }

    @Override
    public TypeValue evaluate(Environment env) {
        if (env.interrupted()) return null;

        TypeValue leftTv = left.evaluate(env);
        if (!leftTv.getType().isPrimitive()) {
            PointerType type = (PointerType) leftTv.getType();
            switch (type.getPointerType()) {
                case PointerType.CLASS_TYPE:
                    Instance instance = (Instance) env.getMemory().get((Pointer) leftTv.getValue());
                    return right.evaluate(instance.getEnv());
                case PointerType.MODULE_TYPE:
                    SplModule module = (SplModule) env.getMemory().get((Pointer) leftTv.getValue());
                    return right.evaluate(module.getEnv());
                case PointerType.ARRAY_TYPE:
                    SplArray arr = (SplArray) env.getMemory().get((Pointer) leftTv.getValue());
                    return arr.getAttr(right, getLineFile());
                case PointerType.NATIVE_TYPE:
                    NativeObject nativeObject = (NativeObject) env.getMemory().get((Pointer) leftTv.getValue());
                    return nativeObject.invoke(right, env, getLineFile());
                default:
                    throw new TypeError("Type '" + type + "' does not support attributes operation. ",
                            getLineFile());
            }
        } else {
            throw new TypeError("Only pointer type supports attributes operation. ", getLineFile());
        }
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }

    @Override
    public PointerType evalType(Environment environment) {
        TypeValue leftTv = left.evaluate(environment);
        if (leftTv.getType() instanceof ModuleType) {
            SplModule module = (SplModule) environment.getMemory().get((Pointer) leftTv.getValue());
            if (right instanceof TypeRepresent) {
                return (PointerType) ((TypeRepresent) right).evalType(module.getEnv());
            }
        }
        throw new TypeError();
    }
}
