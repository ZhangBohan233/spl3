package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.env.ModuleEnvironment;
import interpreter.types.TypeValue;
import interpreter.primitives.Pointer;
import interpreter.splObjects.SplModule;
import interpreter.types.ModuleType;
import util.LineFile;

public class ImportStmt extends Node {

    private final String importName;
    private BlockStmt content;

    public ImportStmt(String importName, LineFile lineFile) {
        super(lineFile);

        this.importName = importName;
    }

    public void setContent(BlockStmt content) {
        this.content = content;
    }

    @Override
    protected TypeValue internalEval(Environment env) {

        ModuleEnvironment moduleScope = new ModuleEnvironment(env);
        content.evaluate(moduleScope);
        SplModule module = new SplModule(importName, moduleScope);

        Pointer ptr = env.getMemory().allocate(1, moduleScope);
        env.getMemory().set(ptr, module);

        ModuleType moduleType = new ModuleType();
        env.defineVar(importName, moduleType, getLineFile());
        env.setVar(importName, new TypeValue(moduleType, ptr), getLineFile());

        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }

    @Override
    public String toString() {
        return "Import as '" + importName + "'";
    }
}
