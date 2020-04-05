package ast;

import interpreter.Environment;
import interpreter.FunctionEnvironment;
import interpreter.Memory;
import parser.ParseError;
import util.LineFile;

public class FuncDefinition extends Node {

    private String name;
    private Node rType;
    private Line parameters;
    private BlockStmt body;

    public FuncDefinition(String name, LineFile lineFile) {
        super(lineFile);

        this.name = name;
    }

    public void setParameters(Line parameters) {
        this.parameters = parameters;
    }

    public void setRType(Node rType) {
        this.rType = rType;
    }

    public void setBody(BlockStmt body) {
        this.body = body;
    }

    @Override
    public Object evaluate(Memory memory) {
        return null;
    }

    @Override
    public FuncDefinition preprocess(Environment env) {
        FunctionEnvironment scope = new FunctionEnvironment(env);
        for (int i = 0; i < parameters.getChildren().size(); ++i) {
            Node node = parameters.getChildren().get(i);
            if (!(node instanceof Declaration)) {
                throw new ParseError("Unexpected parameter syntax. ", node.getLineFile());
            }
            parameters.getChildren().set(i, node.preprocess(scope));
        }
        body = body.preprocess(scope);

        return this;
    }

    @Override
    public String toString() {
        if (name == null)
            return String.format("fn(%s)->%s: %s", parameters, rType, body);
        else
            return String.format("fn %s(%s)->%s: %s", name, parameters, rType, body);
    }
}
