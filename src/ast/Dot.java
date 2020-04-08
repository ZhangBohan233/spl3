package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.splObjects.Instance;
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
        TypeValue leftTv = left.evaluate(env);
        if (leftTv.getType() instanceof ClassType) {
            Instance instance = (Instance) env.getMemory().get((Pointer) leftTv.getValue());
            return right.evaluate(instance.getEnv());
        } else if (leftTv.getType() instanceof ModuleType) {
            SplModule module = (SplModule) env.getMemory().get((Pointer) leftTv.getValue());
            return right.evaluate(module.getEnv());
        }
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }

    @Override
    public PointerType evalType(Environment environment) {
        TypeValue leftTv = left.evaluate(environment);
        if (leftTv.getType() instanceof ModuleType) {
            SplModule module = (SplModule) environment.getMemory().get((Pointer) leftTv.getValue());
            TypeValue tv = right.evaluate(module.getEnv());
            return (PointerType) tv.getType();
        } else {
            throw new TypeError();
        }
    }
}
