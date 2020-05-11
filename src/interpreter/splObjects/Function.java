package interpreter.splObjects;

import ast.*;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.env.FunctionEnvironment;
import interpreter.types.TypeValue;
import interpreter.types.CallableType;
import interpreter.types.TypeError;
import util.LineFile;

import java.util.ArrayList;
import java.util.List;

public class Function extends SplCallable {

    /**
     * The environment where this function is defined
     */
    private final Environment definitionEnv;

    private final List<Declaration> params;
    private final BlockStmt body;
    private final LineFile lineFile;

    public Function(BlockStmt body, List<Declaration> params, CallableType funcType, Environment definitionEnv,
                    LineFile lineFile) {
        super(funcType);

        this.body = body;
        this.params = params;
        this.definitionEnv = definitionEnv;
        this.lineFile = lineFile;
    }

    @Override
    public String toString() {
        return "Function{" + funcType + "}";
    }

    public TypeValue call(Arguments arguments, Environment callingEnv) {
        TypeValue[] evaluatedArgs = arguments.evalArgs(callingEnv);

        return call(evaluatedArgs, callingEnv, arguments.getLineFile());
    }

    public TypeValue call(TypeValue[] evaluatedArgs, Environment callingEnv, LineFile argLineFile) {
        FunctionEnvironment scope = new FunctionEnvironment(definitionEnv);
        if (evaluatedArgs.length != params.size()) {
            throw new SplException("Arguments length does not match parameters. ", argLineFile);
        }

        for (int i = 0; i < params.size(); ++i) {
            Declaration param = params.get(i);
            String paramName = param.getLeft().getName();
            param.evaluate(scope);  // declare param
            scope.setVar(paramName, evaluatedArgs[i], lineFile);
        }

        body.evaluate(scope);
        TypeValue rtnVal = scope.getReturnValue();
        if (rtnVal != null && !funcType.getRType().isSuperclassOfOrEquals(rtnVal.getType(), callingEnv)) {
            throw new TypeError("Declared return type: " + funcType.getRType() + ", actual returning " +
                    "type: " + rtnVal.getType() + ". ", argLineFile);
        }

        return rtnVal;
    }
}
