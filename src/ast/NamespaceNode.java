package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.primitives.Pointer;
import interpreter.splObjects.SplModule;
import interpreter.types.TypeValue;
import util.LineFile;

public class NamespaceNode extends UnaryExpr {

    public NamespaceNode(LineFile lineFile) {
        super("namespace", true, lineFile);
    }

    @Override
    public TypeValue evaluate(Environment env) {
        TypeValue moduleTv = value.evaluate(env);
        SplModule module = (SplModule) env.getMemory().get((Pointer) moduleTv.getValue());
        env.addNamespace(module.getEnv());
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }
}
