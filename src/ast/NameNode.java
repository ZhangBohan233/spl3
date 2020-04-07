package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.Memory;
import interpreter.env.Environment;
import interpreter.env.TypeValue;
import interpreter.primitives.Pointer;
import interpreter.types.PointerType;
import interpreter.types.Type;
import util.LineFile;

public class NameNode extends LeafNode implements TypeRepresent {
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
    public TypeValue evaluate(Environment env) {
        return env.get(name);
    }

    @Override
    public NameNode preprocess(FakeEnv env) {
//        int[] locLay = env.get(name);
//        return new VarNameNode(locLay[0], locLay[1], getLineFile());
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NameNode && ((NameNode) obj).name.equals(name);
    }

    @Override
    public PointerType evalType(Environment environment) {
        return null;
    }
}
