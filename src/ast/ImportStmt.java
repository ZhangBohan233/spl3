package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.env.ModuleEnvironment;
import interpreter.env.TypeValue;
import interpreter.primitives.Pointer;
import interpreter.splObjects.Module;
import interpreter.types.ModuleType;
import util.LineFile;

public class ImportStmt extends Node {

    private String importName;
    private BlockStmt content;

    public ImportStmt(String importName, LineFile lineFile) {
        super(lineFile);

        this.importName = importName;
    }

    public void setContent(BlockStmt content) {
        this.content = content;
    }

    @Override
    public TypeValue evaluate(Environment env) {

        if (importName == null) {  // importing whole namespace
            content.evaluate(env);
        } else {
            ModuleEnvironment moduleScope = new ModuleEnvironment(env);
            content.evaluate(moduleScope);
            Module module = new Module(moduleScope);

            Pointer ptr = env.getMemory().allocate(1);
            env.getMemory().set(ptr, module);

            ModuleType moduleType = new ModuleType();
            env.defineVar(importName, moduleType);
            env.setVar(importName, new TypeValue(moduleType, ptr));
        }
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
