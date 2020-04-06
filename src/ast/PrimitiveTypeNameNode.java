package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.types.PrimitiveType;
import interpreter.env.Environment;
import interpreter.primitives.Primitive;
import parser.ParseError;
import util.LineFile;

public class PrimitiveTypeNameNode extends LeafNode {

    private PrimitiveType type;

    public PrimitiveTypeNameNode(String typeName, LineFile lineFile) {
        super(lineFile);

        switch (typeName) {
            case "int":
                type = PrimitiveType.TYPE_INT;
                break;
            case "float":
                break;
            case "char":
                break;
            case "boolean":
                break;
            default:
                throw new ParseError("No primitive type named '" + typeName + "'. ", lineFile);
        }
    }

    @Override
    public PrimitiveType evaluate(Environment env) {

        return type;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }

    @Override
    public String toString() {
        return Primitive.typeToString(type.type);
    }
}
