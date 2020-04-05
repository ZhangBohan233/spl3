package interpreter;

import ast.NameNode;
import ast.Node;
import util.LineFile;

import java.util.Map;
import java.util.Set;

public class Type {

    private static final Map<String, Integer> PRIMITIVES = Map.of(
            "int", 8,
            "float", 8,
            "boolean", 1,
            "char", 1
    );

    private static final int POINTER_SIZE = 8;

    private Node typeNode;

    private static final LineFile LINE_FILE = new LineFile(0, "Interpreter");

    public static final Type TYPE_INT = new Type(new NameNode("int", LINE_FILE));

    public Type(Node typeNode) {
        this.typeNode = typeNode;
    }

    public int getStackSize() {
        if (typeNode instanceof NameNode) {
            Integer size = PRIMITIVES.get(((NameNode) typeNode).getName());
            if (size == null) return POINTER_SIZE;
        }
        return POINTER_SIZE;
    }

    private boolean isPrimitive() {
        if (typeNode instanceof NameNode) {
            return PRIMITIVES.containsKey(((NameNode) typeNode).getName());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(typeNode);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Type && ((Type) obj).typeNode.equals(typeNode);
    }
}
