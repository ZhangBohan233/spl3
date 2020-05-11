package ast;

import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import util.LineFile;

public abstract class NonEvaluate extends Node {

    public NonEvaluate(LineFile lineFile) {
        super(lineFile);
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        throw new SplException("Not evaluate-able. ", getLineFile());
    }
}
