package interpreter.splObjects;

import ast.NameNode;
import ast.Node;
import interpreter.AttributeError;
import interpreter.env.InstanceEnvironment;
import interpreter.primitives.Int;
import interpreter.types.ClassType;
import interpreter.types.PrimitiveType;
import interpreter.types.TypeValue;
import util.LineFile;

public class SplArray extends SplObject {

    public final int length;

    public SplArray(int length) {
        this.length = length;
    }

    public TypeValue getAttr(Node attrNode, LineFile lineFile) {
        if (attrNode instanceof NameNode && ((NameNode) attrNode).getName().equals("length")) {
            return new TypeValue(PrimitiveType.TYPE_INT, new Int(length));
        } else {
            throw new AttributeError("Array does not have attribute '" + attrNode + "'. ", lineFile);
        }
    }

    @Override
    public String toString() {
        return "SplArray{" + length + '}';
    }
}
