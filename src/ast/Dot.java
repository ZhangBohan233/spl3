package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.splObjects.*;
import interpreter.types.*;
import interpreter.primitives.Pointer;
import util.LineFile;

public class Dot extends BinaryExpr implements TypeRepresent {
    public Dot(LineFile lineFile) {
        super(".", lineFile);
    }

    @Override
    protected TypeValue internalEval(Environment env) {

        TypeValue leftTv = left.evaluate(env);
        if (!leftTv.getType().isPrimitive()) {
            PointerType type = (PointerType) leftTv.getType();
            Pointer ptr = (Pointer) leftTv.getValue();
            if (ptr.getPtr() == 0) {
                throw new SplException("Pointer to null does not support attributes operation. ",
                        getLineFile());
            }
            switch (type.getPointerType()) {
                case PointerType.CLASS_TYPE:
                    Instance instance = (Instance) env.getMemory().get(ptr);
                    return crossEnvEval(right, instance.getEnv(), env, getLineFile());
                case PointerType.MODULE_TYPE:
                    SplModule module = (SplModule) env.getMemory().get(ptr);
                    return crossEnvEval(right, module.getEnv(), env, getLineFile());
//                    return right.evaluate(module.getEnv());
                case PointerType.ARRAY_TYPE:
                    SplArray arr = (SplArray) env.getMemory().get(ptr);
                    return arr.getAttr(right, getLineFile());
                case PointerType.NATIVE_TYPE:
                    NativeObject nativeObject = (NativeObject) env.getMemory().get(ptr);
                    return nativeObject.invoke(right, env, getLineFile());
                default:
                    throw new TypeError("Type '" + type + "' does not support attributes operation. ",
                            getLineFile());
            }
        } else {
            throw new TypeError("Only pointer type supports attributes operation. ", getLineFile());
        }
    }

    private static TypeValue crossEnvEval(Node right, Environment objEnv, Environment oldEnv, LineFile lineFile) {
        if (right instanceof NameNode) {
            return right.evaluate(objEnv);
        } else if (right instanceof FuncCall) {
            TypeValue funcTv = ((FuncCall) right).getCallObj().evaluate(objEnv);
            if(!(funcTv.getType() instanceof CallableType))
                throw new SplException("Class attribute not callable. ", lineFile);
            SplCallable callable = (SplCallable) objEnv.getMemory().get((Pointer) funcTv.getValue());
            return callable.call(((FuncCall) right).getArguments(), oldEnv);
        } else {
            throw new SplException("Unexpected right side type of dot '" + right.getClass() + "' ", lineFile);
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
