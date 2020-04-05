package ast;

import interpreter.Environment;
import interpreter.Memory;
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
    public Object evaluate(Memory memory) {
        return null;
    }

    @Override
    public VarNameNode preprocess(Environment env) {
        int[] locLay = env.get(name);
        return new VarNameNode(locLay[0], locLay[1], getLineFile());
    }
}
