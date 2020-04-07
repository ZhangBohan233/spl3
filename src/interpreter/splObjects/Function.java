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

import java.util.List;

public class Function extends SplObject {

    /**
     * The environment where this function is defined
     */
    private Environment definitionEnv;

    private List<Declaration> params;
    private CallableType funcType;
    private BlockStmt body;

    public Function(BlockStmt body, List<Declaration> params, CallableType funcType, Environment definitionEnv) {
        this.body = body;
        this.params = params;
        this.funcType = funcType;
        this.definitionEnv = definitionEnv;
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

        for (Declaration param : params) {
            param.evaluate(scope);  // declare params
        }

        for (int i = 0; i < params.size(); ++i) {
            Declaration param = params.get(i);
            String paramName = param.getLeft().getName();
            param.evaluate(scope);
            Node argNode = arguments.getLine().getChildren().get(i);
            TypeValue arg = argNode.evaluate(callingEnv);
            scope.setVar(paramName, arg);
        }

        body.evaluate(scope);
        TypeValue rtnVal = scope.getReturnValue();
        if (!funcType.getRType().isSuperclassOfOrEquals(rtnVal.getType())) {
            throw new TypeError();
        }

        return rtnVal;
    }
}
