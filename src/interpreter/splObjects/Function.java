package interpreter.splObjects;

import ast.*;
import interpreter.Memory;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.env.FunctionEnvironment;
import interpreter.types.*;
import parser.ParseError;
import util.LineFile;

import java.util.ArrayList;
import java.util.List;

public class Function extends SplCallable {

    /**
     * The environment where this function is defined
     */
    private final Environment definitionEnv;

    private final List<Declaration> params;
    private final Node body;
    private final LineFile lineFile;

    private final boolean isLambda;

    public Function(BlockStmt body, List<Declaration> params, CallableType funcType, Environment definitionEnv,
                    LineFile lineFile) {
        super(funcType);

        this.body = body;
        this.params = params;
        this.definitionEnv = definitionEnv;
        this.lineFile = lineFile;

        isLambda = false;
    }

    /**
     * Constructor for lambda expression
     *
     * @param body          one-line function body
     * @param params        parameters
     * @param lambdaType    function type
     * @param definitionEnv environment where this lambda is defined
     * @param lineFile      line and file
     */
    public Function(Node body, List<Declaration> params, CallableType lambdaType, Environment definitionEnv,
                    LineFile lineFile) {
        super(lambdaType);

        this.body = body;
        this.params = params;
        this.definitionEnv = definitionEnv;
        this.lineFile = lineFile;

        isLambda = true;
    }

    public Node getBody() {
        return body;
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
        callingEnv.getMemory().increaseStack();
        FunctionEnvironment scope = new FunctionEnvironment(definitionEnv);
        if (evaluatedArgs.length != params.size()) {
            throw new SplException("Arguments length does not match parameters. ", argLineFile);
        }

        for (int i = 0; i < params.size(); ++i) {
            Declaration param = params.get(i);
            String paramName = param.getLeftName().getName();
            param.evaluate(scope);  // declare param
            scope.setVar(paramName, evaluatedArgs[i], lineFile);
        }

        TypeValue evalResult = body.evaluate(scope);
        if (isLambda) return evalResult;

        TypeValue rtnVal = scope.getReturnValue();
        if (rtnVal != null && !funcType.getRType().isSuperclassOfOrEquals(rtnVal.getType(), callingEnv)) {
            throw new TypeError("Declared return type: " + funcType.getRType() + ", actual returning " +
                    "type: " + rtnVal.getType() + ". ", argLineFile);
        }
        callingEnv.getMemory().decreaseStack();

        return rtnVal;
    }

    public static void evalParamTypes(Line parameters, List<Declaration> params, List<Type> paramTypes,
                                      Environment env) {

        for (int i = 0; i < parameters.getChildren().size(); ++i) {
            Node node = parameters.getChildren().get(i);
            if (node instanceof Declaration) {
                params.add((Declaration) node);
                paramTypes.add(((Declaration) node).getRightTypeRep().evalType(env));
            } else {
                throw new ParseError("Unexpected parameter syntax. ", node.getLineFile());
            }
        }
    }
}
