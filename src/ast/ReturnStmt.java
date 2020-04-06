package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.env.TypeValue;
import util.LineFile;

public class ReturnStmt extends UnaryExpr {

    public ReturnStmt(LineFile lineFile) {
        super("return", true, lineFile);
    }

    @Override
    public TypeValue evaluate(Environment env) {
        env.setReturn((TypeValue) value.evaluate(env));
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
