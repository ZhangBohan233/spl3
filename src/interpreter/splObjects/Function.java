package interpreter.splObjects;

import ast.*;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.env.FunctionEnvironment;
import interpreter.primitives.Pointer;
import interpreter.types.*;
import parser.ParseError;
import util.LineFile;

import java.util.List;

public class Function extends SplCallable {

    /**
     * The environment where this function is defined
     */
    private final Environment definitionEnv;

    private final List<Parameter> params;  // only Declaration and Assignment
    private final Node body;
    private final LineFile lineFile;
    private final String definedName;

    private final boolean isLambda;
    private final boolean isAbstract;

    /**
     * Constructor for regular function.
     */
    public Function(BlockStmt body, List<Parameter> params, CallableType funcType, Environment definitionEnv,
                    String definedName, LineFile lineFile) {
        super(funcType);

        this.body = body;
        this.params = params;
        this.definitionEnv = definitionEnv;
        this.lineFile = lineFile;
        this.definedName = definedName;

        isLambda = false;
        isAbstract = false;
    }

    /**
     * Constructor for abstract function.
     */
    public Function(List<Parameter> params, CallableType funcType, Environment definitionEnv,
                    String definedName, LineFile lineFile) {
        super(funcType);

        this.body = null;
        this.params = params;
        this.definitionEnv = definitionEnv;
        this.lineFile = lineFile;
        this.definedName = definedName;

        isLambda = false;
        isAbstract = true;
    }

    /**
     * Constructor for lambda expression.
     *
     * @param body          one-line function body
     * @param params        parameters
     * @param lambdaType    function type
     * @param definitionEnv environment where this lambda is defined
     * @param lineFile      line and file
     */
    public Function(Node body, List<Parameter> params, CallableType lambdaType, Environment definitionEnv,
                    LineFile lineFile) {
        super(lambdaType);

        this.body = body;
        this.params = params;
        this.definitionEnv = definitionEnv;
        this.lineFile = lineFile;
        this.definedName = "";

        isLambda = true;
        isAbstract = false;
    }

    public Node getBody() {
        return body;
    }

    public Environment getDefinitionEnv() {
        return definitionEnv;
    }

    @Override
    public String toString() {
        if (definedName.isEmpty()) {
            return "Anonymous function: {" + funcType + "}";
        } else {
            return "Function " + definedName + ": {" + funcType + "}";
        }
    }

    public TypeValue call(Arguments arguments, Environment callingEnv) {
        TypeValue[] evaluatedArgs = arguments.evalArgs(callingEnv);

        return call(evaluatedArgs, callingEnv, arguments.getLineFile());
    }

    public TypeValue call(TypeValue[] evaluatedArgs, Environment callingEnv, LineFile argLineFile) {
        if (isAbstract) {
            throw new SplException("Function is not implemented. ", lineFile);
        }
        assert body != null;

//        System.out.println(Arrays.toString(evaluatedArgs));

        FunctionEnvironment scope = new FunctionEnvironment(definitionEnv, callingEnv, definedName);
        if (evaluatedArgs.length < minArgCount() || evaluatedArgs.length > maxArgCount()) {
            throw new SplException("Arguments length does not match parameters. Expect " +
                    minArgCount() + ", got " + evaluatedArgs.length + ". ", argLineFile);
        }

        for (int i = 0; i < params.size(); ++i) {
            Parameter param = params.get(i);
            String paramName = param.declaration.getLeftName().getName();
            param.declaration.evaluate(scope);  // declare param
            if (i < evaluatedArgs.length) {
                // arg from call
                try {
                    scope.setVar(paramName, evaluatedArgs[i], lineFile);
                } catch (TypeError e) {
                    System.out.println(evaluatedArgs[i]);
                    System.out.println(callingEnv.getMemory().get((Pointer) evaluatedArgs[i].getValue()));
                    throw e;
                }
            } else if (param.hasDefaultTv()) {
                // default arg
                scope.setVar(paramName, param.defaultTv, lineFile);
            } else {
                throw new SplException("Unexpect argument error. ", lineFile);
            }
        }

        scope.getMemory().pushStack(scope);
        TypeValue evalResult = body.evaluate(scope);
        scope.getMemory().decreaseStack();

        if (isLambda) return evalResult;

        TypeValue rtnVal = scope.getReturnValue();

        if (funcType.getRType().equals(PrimitiveType.TYPE_VOID)) {
            if (rtnVal == null) {
                return TypeValue.VOID;
            } else {
                throw new TypeError("Function with void return type returns non-void value. ", lineFile);
            }
        } else {
            if (rtnVal == null) {
                throw new TypeError("Function with non-void return type returns nothing. ", lineFile);
            } else if (!funcType.getRType().isSuperclassOfOrEquals(rtnVal.getType(), callingEnv)) {
                throw new TypeError("Declared return type: " + funcType.getRType() + ", actual returning " +
                        "type: " + rtnVal.getType() + ". ", argLineFile);
            }
            return rtnVal;
        }
    }

    public int minArgCount() {
        int c = 0;
        for (Parameter param : params) {
            if (!param.hasDefaultTv()) c++;
        }
        return c;
    }

    public int maxArgCount() {
        return params.size();  // TODO: check unpack
    }

    public static void evalParamTypes(Line parameters, List<Parameter> params, List<Type> paramTypes,
                                      Environment env) {

        boolean hasDefault = false;
        for (int i = 0; i < parameters.getChildren().size(); ++i) {
            Node node = parameters.getChildren().get(i);
            if (node instanceof Declaration) {
                if (hasDefault) {
                    throw new ParseError("Positional parameter cannot occur behind optional parameter. ",
                            node.getLineFile());
                }
                params.add(new Parameter((Declaration) node));
                paramTypes.add(((Declaration) node).getRightTypeRep().evalType(env));
                continue;
            } else if (node instanceof Assignment) {
                hasDefault = true;
                Assignment assignment = (Assignment) node;
                if (assignment.getLeft() instanceof Declaration) {
                    Parameter p = new Parameter((Declaration) assignment.getLeft(),
                            assignment.getRight().evaluate(env));
                    params.add(p);
                    paramTypes.add(((Declaration) assignment.getLeft()).getRightTypeRep().evalType(env));
                    continue;
                }
            }
            throw new ParseError("Unexpected parameter syntax. ", node.getLineFile());
        }
    }

    public static class Parameter {
        public final Declaration declaration;
        public final TypeValue defaultTv;

        Parameter(Declaration declaration, TypeValue typeValue) {
            this.declaration = declaration;
            this.defaultTv = typeValue;
        }

        Parameter(Declaration declaration) {
            this.declaration = declaration;
            this.defaultTv = null;
        }

        public boolean hasDefaultTv() {
            return defaultTv != null;
        }
    }
}
