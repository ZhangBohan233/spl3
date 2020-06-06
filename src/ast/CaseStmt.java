package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

public class CaseStmt extends ConditionalStmt {

    final boolean isDefault;
    private Line condition;

    public CaseStmt(LineFile lineFile, boolean isDefault) {
        super(lineFile);

        this.isDefault = isDefault;
    }

    public Line getCondition() {
        return condition;
    }

    public void setCondition(Line condition) {
        this.condition = condition;
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        return bodyBlock.evaluate(env);
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }

    @Override
    public String toString() {
        return "case" + condition + " then " + bodyBlock;
    }
}
