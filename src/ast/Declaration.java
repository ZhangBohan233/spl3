package ast;

import interpreter.Environment;
import interpreter.EnvironmentError;
import interpreter.Memory;
import interpreter.Type;
import util.LineFile;

public class Declaration extends BinaryExpr {

    public static final int VAR = 1;
    public static final int CONST = 2;

    private int level;

    public Declaration(int level, LineFile lineFile) {
        super(":", lineFile);

        this.level = level;
    }

    @Override
    public Object evaluate(Memory memory) {
        return null;
    }

    @Override
    public Declaration preprocess(Environment env) {
//        System.out.println(left);
//        System.out.println(right);

        if (!(left instanceof NameNode)) throw new EnvironmentError();

        Type type = new Type(right);
        env.defineVar(((NameNode) left).getName(), type);

        return this;
    }
}
