package interpreter.splObjects;

import ast.BlockStmt;
import interpreter.env.Environment;
import interpreter.types.ClassType;

import java.util.List;

public class SplClass extends SplObject {

    private ClassType superclassType;
    private List<ClassType> interfacePointers;
    private BlockStmt body;
    private String className;
    private Environment definitionEnv;

    public SplClass(String className, ClassType superclassType, List<ClassType> interfacePointers,
                    BlockStmt body, Environment definitionEnv) {
        this.className = className;
        this.superclassType = superclassType;
        this.interfacePointers = interfacePointers;
        this.body = body;
        this.definitionEnv = definitionEnv;
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

    @Override
    public String toString() {
        return "Class <" + className + ">";
    }
}
