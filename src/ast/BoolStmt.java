package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

public class BoolStmt extends LiteralNode {

    public static final BoolStmt BOOL_STMT_TRUE = new BoolStmt(true, LineFile.LF_INTERPRETER);
    public static final BoolStmt BOOL_STMT_FALSE = new BoolStmt(false, LineFile.LF_INTERPRETER);

    private final TypeValue typeValue;

    public BoolStmt(boolean val, LineFile lineFile) {
        super(lineFile);

        if (val) typeValue = TypeValue.BOOL_TRUE;
        else typeValue = TypeValue.BOOL_FALSE;
    }

    @Override
    public TypeValue evaluate(Environment env) {
        return typeValue;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
