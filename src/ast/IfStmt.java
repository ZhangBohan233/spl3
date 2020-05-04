package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.BlockEnvironment;
import interpreter.env.Environment;
import interpreter.primitives.Bool;
import interpreter.types.PrimitiveType;
import interpreter.types.TypeError;
import interpreter.types.TypeValue;
import util.LineFile;

public class IfStmt extends ConditionalStmt {

    private Line condition;
    private Node elseBlock;
    private boolean hasElse;

    public IfStmt(LineFile lineFile) {
        super(lineFile);
    }

    public void setCondition(Line condition) {
        this.condition = condition;
    }

    public void setElseBlock(Node elseBlock) {
        this.elseBlock = elseBlock;
    }

    public boolean hasElse() {
        return hasElse;
    }

    public void setHasElse(boolean hasElse) {
        this.hasElse = hasElse;
    }

    public Node getElseBlock() {
        return elseBlock;
    }

    @Override
    public TypeValue evaluate(Environment env) {
        TypeValue cond = condition.evaluate(env);
        if (!cond.getType().equals(PrimitiveType.TYPE_BOOLEAN)) throw new TypeError("If statement takes " +
                "boolean value as condition. ", getLineFile());
        Bool bool = (Bool) cond.getValue();
        BlockEnvironment blockEnvironment;
        if (bool.booleanValue()) {
            blockEnvironment = new BlockEnvironment(env);
            bodyBlock.evaluate(blockEnvironment);
        } else if (elseBlock != null) {
            blockEnvironment = new BlockEnvironment(env);
            elseBlock.evaluate(blockEnvironment);
        }
        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }

    @Override
    public String toString() {
        return String.format("If %s then %s else %s\n", condition, bodyBlock, elseBlock);
    }
}
