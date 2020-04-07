package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.TypeValue;
import interpreter.types.PrimitiveType;
import interpreter.env.Environment;
import interpreter.primitives.Primitive;
import interpreter.types.Type;
import parser.ParseError;
import util.LineFile;

public class PrimitiveTypeNameNode extends LeafNode implements TypeRepresent {

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
    public TypeValue evaluate(Environment env) {
        throw new SplException("Type self is not evaluate-able. ", getLineFile());
    }

    @Override
    public PrimitiveType evalType(Environment environment) {
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
