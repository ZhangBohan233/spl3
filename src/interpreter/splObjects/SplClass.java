package interpreter.splObjects;

import ast.BlockStmt;
import interpreter.env.ClassEnvironment;
import interpreter.types.ClassType;

import java.util.List;

public class SplClass extends SplObject {

    private ClassType superclassType;
    private List<ClassType> interfacePointers;
    private ClassEnvironment classBaseEnv;
    private String className;

    public SplClass(String className, ClassType superclassType, List<ClassType> interfacePointers,
                    ClassEnvironment classBaseEnv) {
        this.className = className;
        this.superclassType = superclassType;
        this.interfacePointers = interfacePointers;
        this.classBaseEnv = classBaseEnv;
    }

    public ClassEnvironment getClassBaseEnv() {
        return classBaseEnv;
    }

    public ClassType getSuperclassType() {
        return superclassType;
    }

    @Override
    public String toString() {
        return "Class <" + className + ">";
    }
}
