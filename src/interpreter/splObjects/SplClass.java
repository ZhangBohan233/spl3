package interpreter.splObjects;

import ast.BlockStmt;
import ast.Node;
import interpreter.env.Environment;
import interpreter.types.ClassType;

import java.util.List;

public class SplClass extends SplObject {

    private final ClassType superclassType;
    private final List<ClassType> interfacePointers;
    public final List<Node> templates;
    private final BlockStmt body;
    private final String className;
    private final Environment definitionEnv;
    public final boolean isAbstract;
    public final boolean isInterface;

    public SplClass(String className, ClassType superclassType, List<ClassType> interfacePointers,
                    List<Node> templates, BlockStmt body, Environment definitionEnv,
                    boolean isAbstract, boolean isInterface) {
        this.className = className;
        this.superclassType = superclassType;
        this.interfacePointers = interfacePointers;
        this.templates = templates;
        this.body = body;
        this.definitionEnv = definitionEnv;
        this.isAbstract = isAbstract;
        this.isInterface = isInterface;
    }

    public List<ClassType> getInterfacePointers() {
        return interfacePointers;
    }

    public BlockStmt getBody() {
        return body;
    }

    public Environment getDefinitionEnv() {
        return definitionEnv;
    }

    public ClassType getSuperclassType() {
        return superclassType;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return "Class <" + className + ">";
    }
}
