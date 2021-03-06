package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.Memory;
import interpreter.SplException;
import interpreter.env.BlockEnvironment;
import interpreter.env.Environment;
import interpreter.env.LoopTitleEnvironment;
import interpreter.primitives.Bool;
import interpreter.primitives.Pointer;
import interpreter.primitives.Primitive;
import interpreter.splObjects.Function;
import interpreter.splObjects.Instance;
import interpreter.splObjects.SplArray;
import interpreter.types.*;
import util.LineFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ForLoopStmt extends ConditionalStmt {

    private BlockStmt condition;

    private final static String forEachSyntaxMsg = "Syntax of for-each loop: for ele: T; collection {...}";

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

        if (condition.getLines().size() == 2) {  // for each loop
            forEachLoop(
                    condition.getLines().get(0),
                    condition.getLines().get(1),
                    env,
                    titleEnv,
                    bodyEnv
            );
        } else if (condition.getLines().size() == 3) {  // regular for loop
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

    private void forEachLoop(Line loopInvariantPart, Line collectionPart, Environment parentEnv,
                             LoopTitleEnvironment titleEnv, BlockEnvironment bodyEnv) {
        TypeValue collectionTv = collectionPart.evaluate(parentEnv);

        if (loopInvariantPart.getChildren().size() != 1)
            throw new SplException(forEachSyntaxMsg, getLineFile());
        Node lin = loopInvariantPart.getChildren().get(0);
        if (!(lin instanceof Declaration))
            throw new SplException(forEachSyntaxMsg, getLineFile());
        String liName = ((Declaration) lin).getLeftName().getName();
        loopInvariantPart.evaluate(titleEnv);

        if (collectionTv.getType() instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) collectionTv.getType();
            Pointer arrPtr = (Pointer) collectionTv.getValue();
            SplArray array = (SplArray) parentEnv.getMemory().get(arrPtr);
            int arrLen = array.length;
            for (int i = 0; i < arrLen; ++i) {
                bodyEnv.invalidate();
                Primitive ele = SplArray.getItemAtIndex(arrPtr, i, parentEnv, getLineFile());
                titleEnv.setVar(liName, new TypeValue(arrayType.getEleType(), ele), getLineFile());

                bodyBlock.evaluate(bodyEnv);
                if (titleEnv.isBroken() || parentEnv.interrupted()) break;

                titleEnv.resumeLoop();
            }
        } else if (collectionTv.getType() instanceof ClassType) {

//            WhileStmt whileStmt = new WhileStmt(getLineFile());
//            whileStmt.setCondition();

            ClassType iterable = (ClassType) new NameNode("Iterable", getLineFile()).evalType(parentEnv);
            ClassType thisType = (ClassType) collectionTv.getType();
            if (iterable.isSuperclassOfOrEquals(thisType, parentEnv)) {
                Memory memory = parentEnv.getMemory();
                Instance instance = (Instance) memory.get((Pointer) collectionTv.getValue());
                TypeValue iteratorFnTv = instance.getEnv().get("iterator", getLineFile());
                Function iteratorFn = (Function) memory.get((Pointer) iteratorFnTv.getValue());

                TypeValue iteratorTv = iteratorFn.call(new TypeValue[0], titleEnv, getLineFile());
                Instance iterator = (Instance) memory.get((Pointer) iteratorTv.getValue());

//                TypeValue hasNextTv = iterator.getEnv().get("hasNext", getLineFile());
//                TypeValue nextTv = iterator.getEnv().get("next", getLineFile());
//                Function hasNext = (Function) memory.get((Pointer) hasNextTv.getValue());
//                Function next = (Function) memory.get((Pointer) nextTv.getValue());

                FuncCall hasNextCall = new FuncCall(getLineFile());
                hasNextCall.setCallObj(new NameNode("hasNext", getLineFile()));
                hasNextCall.setArguments(new Arguments(new Line(getLineFile()), getLineFile()));

                FuncCall nextCall = new FuncCall(getLineFile());
                nextCall.setCallObj(new NameNode("next", getLineFile()));
                nextCall.setArguments(new Arguments(new Line(getLineFile()), getLineFile()));

                Bool bool = Bool.evalBoolean(hasNextCall, iterator.getEnv(), getLineFile());
                while (bool.value) {
                    bodyEnv.invalidate();
                    TypeValue nextTv = nextCall.evaluate(iterator.getEnv());
                    titleEnv.setVar(liName, nextTv, getLineFile());

                    bodyBlock.evaluate(bodyEnv);
                    if (titleEnv.isBroken() || parentEnv.interrupted()) break;

                    titleEnv.resumeLoop();

                    bool = Bool.evalBoolean(hasNextCall, iterator.getEnv(), getLineFile());
                }

            } else {
                throw new SplException("Only array or classes implements 'Iterable' supports for each loop. ",
                        getLineFile());
            }
        } else {
            throw new SplException("Only array or classes implements 'Iterable' supports for each loop. ",
                    getLineFile());
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
