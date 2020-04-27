package interpreter.splObjects;

import ast.Arguments;
import ast.BlockStmt;
import ast.Declaration;
import ast.Node;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.env.FunctionEnvironment;
import interpreter.types.TypeValue;
import interpreter.types.CallableType;
import interpreter.types.TypeError;
import util.LineFile;

import java.util.List;

public class Function extends SplObject {

    /**
     * The environment where this function is defined
     */
    private Environment definitionEnv;

    private List<Declaration> params;
    private CallableType funcType;
    private BlockStmt body;
    private LineFile lineFile;

    public Function(BlockStmt body, List<Declaration> params, CallableType funcType, Environment definitionEnv,
                    LineFile lineFile) {
        this.body = body;
        this.params = params;
        this.funcType = funcType;
        this.definitionEnv = definitionEnv;
        this.lineFile = lineFile;
    }

    @Override
    public String toString() {
        return "Function{" + funcType + "}";
    }

    public TypeValue call(Arguments arguments, Environment callingEnv) {
        FunctionEnvironment scope = new FunctionEnvironment(definitionEnv);

        if (arguments.getLine().getChildren().size() != params.size()) {
            throw new SplException("Arguments length does not match parameters. ", arguments.getLineFile());
        }

        for (int i = 0; i < params.size(); ++i) {
            Declaration param = params.get(i);
            String paramName = param.getLeft().getName();
            param.evaluate(scope);  // declare param
            Node argNode = arguments.getLine().getChildren().get(i);
            TypeValue arg = argNode.evaluate(callingEnv);
            scope.setVar(paramName, arg, lineFile);
        }

        body.evaluate(scope);
        TypeValue rtnVal = scope.getReturnValue();
        if (rtnVal != null && !funcType.getRType().isSuperclassOfOrEquals(rtnVal.getType(), callingEnv)) {
            throw new TypeError("Declared return type: " + funcType.getRType() + ", actual returning " +
                    "type: " + rtnVal.getType() + ". ");
        }

        return rtnVal;
    }
}
