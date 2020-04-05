package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.Memory;
import interpreter.env.Environment;
import util.LineFile;

public class NameNode extends LeafNode {
    private String name;

    public NameNode(String name, LineFile lineFile) {
        super(lineFile);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Name(" + name + ")";
    }

    @Override
    public Object evaluate(Environment env) {
        return null;
    }

    @Override
    public VarNameNode preprocess(FakeEnv env) {
        int[] locLay = env.get(name);
        return new VarNameNode(locLay[0], locLay[1], getLineFile());
    }
}
