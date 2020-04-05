package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.Memory;
import interpreter.env.Environment;
import util.LineFile;

public class BinaryOperator extends BinaryExpr {
    public BinaryOperator(String operator, LineFile lineFile) {
        super(operator, lineFile);
    }

    @Override
    public Object evaluate(Environment env) {
        return 0;
    }

    @Override
    public BinaryOperator preprocess(FakeEnv env) {
        return this;
    }
}
