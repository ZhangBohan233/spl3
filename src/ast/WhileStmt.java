package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.BlockEnvironment;
import interpreter.env.Environment;
import interpreter.env.LoopTitleEnvironment;
import interpreter.primitives.Bool;
import interpreter.types.PrimitiveType;
import interpreter.types.TypeError;
import interpreter.types.TypeValue;
import util.LineFile;

public class WhileStmt extends ConditionalStmt {

    private Line condition;

    public WhileStmt(LineFile lineFile) {
        super(lineFile);
    }

    @Override
    public TypeValue evaluate(Environment env) {

        LoopTitleEnvironment titleEnv = new LoopTitleEnvironment(env);
        BlockEnvironment bodyEnv = new BlockEnvironment(titleEnv);

        TypeValue cond = condition.evaluate(titleEnv);
        if (!cond.getType().equals(PrimitiveType.TYPE_BOOLEAN)) throw new TypeError("If statement takes " +
                "boolean value as condition. ", getLineFile());
        Bool bool = (Bool) cond.getValue();
        while (bool.value) {
            bodyEnv.invalidate();
            bodyBlock.evaluate(bodyEnv);
            if (titleEnv.isBroken() || env.interrupted()) break;

            titleEnv.resumeLoop();
            bool = (Bool) condition.evaluate(titleEnv).getValue();
        }

        return null;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }

    public void setCondition(Line condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "while " + condition + " do " + bodyBlock;
    }
}
