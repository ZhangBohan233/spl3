package interpreter.types;

import interpreter.Memory;
import interpreter.env.Environment;
import interpreter.primitives.Pointer;
import interpreter.splObjects.SplClass;

public class ClassType extends PointerType {

    private final Pointer clazzPointer;

    public ClassType(Pointer clazzPointer) {
        this.clazzPointer = clazzPointer;
    }

    public Pointer getClazzPointer() {
        return clazzPointer;
    }

    @Override
    public String toString() {
        return "ClassType @" + clazzPointer.getPtr();
    }

    @Override
    public int getPointerType() {
        return PointerType.CLASS_TYPE;
    }

    @Override
    public boolean isSuperclassOfOrEquals(Type child, Environment env) {
        if (!(child instanceof ClassType)) return false;
        else if (equals(child)) return true;
        else {
            ClassType childCt = (ClassType) child;
            SplClass childClazz = (SplClass) env.getMemory().get(childCt.clazzPointer);
            return isSuperclassOfOrEquals(childClazz.getSuperclassType(), env);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ClassType && ((ClassType) o).clazzPointer.equals(clazzPointer);
    }

    public String toStringClass(Memory memory) {
        SplClass clazz = (SplClass) memory.get(clazzPointer);
        return clazz.toString();
    }
}
