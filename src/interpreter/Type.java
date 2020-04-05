package interpreter;

import ast.NameNode;
import ast.Node;

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
}
