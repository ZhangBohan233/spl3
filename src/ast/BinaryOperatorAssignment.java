package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

public class BinaryOperatorAssignment extends BinaryExpr {

    private final String realOp;

    public BinaryOperatorAssignment(String operator, LineFile lineFile) {
        super(operator, lineFile);

        realOp = operator.substring(0, operator.length() - 1);
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        BinaryOperator binaryOperator = new BinaryOperator(realOp, BinaryOperator.NUMERIC, getLineFile());
        binaryOperator.setLeft(left);
        binaryOperator.setRight(right);
        Assignment assignment = new Assignment(getLineFile());
        assignment.setLeft(left);
        assignment.setRight(binaryOperator);
        return assignment.evaluate(env);
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }
}
