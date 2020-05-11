package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.BlockEnvironment;
import interpreter.env.Environment;
import interpreter.env.LoopTitleEnvironment;
import interpreter.primitives.Bool;
import interpreter.types.PrimitiveType;
import interpreter.types.TypeError;
import interpreter.types.TypeValue;
import util.LineFile;

public class ForLoopStmt extends ConditionalStmt {

    private BlockStmt condition;

    public ForLoopStmt(LineFile lineFile) {
        super(lineFile);
    }

    public void setCondition(BlockStmt condition) {
        this.condition = condition;
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        LoopTitleEnvironment titleEnv = new LoopTitleEnvironment(env);
        BlockEnvironment bodyEnv = new BlockEnvironment(titleEnv);

        if (condition.getLines().size() == 3) {
            forLoop3Parts(
                    condition.getLines().get(0),
                    condition.getLines().get(1),
                    condition.getLines().get(2),
                    env,
                    titleEnv,
                    bodyEnv
            );
        } else {
            throw new SplException("For loop takes 2 or 3 condition parts. ", getLineFile());
        }

        return null;
    }

    private void forLoop3Parts(Line init, Line end, Line step, Environment parentEnv,
                               LoopTitleEnvironment titleEnv, BlockEnvironment bodyEnv) {
        init.evaluate(titleEnv);
        Bool bool = Bool.evalBoolean(end, titleEnv, getLineFile());
        while (bool.value) {
            bodyEnv.invalidate();
            bodyBlock.evaluate(bodyEnv);
            if (titleEnv.isBroken() || parentEnv.interrupted()) break;

            titleEnv.resumeLoop();
            step.evaluate(titleEnv);
            bool = (Bool) end.evaluate(titleEnv).getValue();
        }
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }

    @Override
    public String toString() {
        return "for " + condition + " do " + bodyBlock;
    }
}
