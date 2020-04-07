package interpreter.splObjects;

import ast.BlockStmt;
import interpreter.env.ClassEnvironment;
import interpreter.env.Environment;
import interpreter.primitives.Pointer;
import interpreter.types.ClassType;
import interpreter.types.PointerType;

import java.util.List;

public class SplClass extends SplObject {

    private List<ClassType> superclassPointers;
    private ClassEnvironment classBaseEnv;
    private BlockStmt body;
    private String className;

    public SplClass(String className, List<ClassType> superclassPointers, ClassEnvironment classBaseEnv) {
        this.className = className;
        this.superclassPointers = superclassPointers;
        this.classBaseEnv = classBaseEnv;
    }

    public ClassEnvironment getClassBaseEnv() {
        return classBaseEnv;
    }

    @Override
    public String toString() {
        return "Class <" + className + ">";
    }
}
