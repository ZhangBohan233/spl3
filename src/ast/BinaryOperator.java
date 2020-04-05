package ast;

import interpreter.Environment;
import interpreter.Memory;
import util.LineFile;

public class BinaryOperator extends BinaryExpr {
    public BinaryOperator(String operator, LineFile lineFile) {
        super(operator, lineFile);
    }

    @Override
    public Object evaluate(Memory memory) {
        return null;
    }

    @Override
    public BinaryOperator preprocess(Environment env) {
        return this;
    }
}
