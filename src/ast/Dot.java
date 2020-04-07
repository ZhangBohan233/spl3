package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.splObjects.Instance;
import interpreter.types.TypeValue;
import interpreter.primitives.Pointer;
import interpreter.splObjects.Module;
import interpreter.types.ClassType;
import interpreter.types.ModuleType;
import interpreter.types.Type;
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
            Module module = (Module) env.getMemory().get((Pointer) leftTv.getValue());
            return right.evaluate(module.getEnv());
        }
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }

    @Override
    public Type evalType(Environment environment) {
        return null;
    }
}
