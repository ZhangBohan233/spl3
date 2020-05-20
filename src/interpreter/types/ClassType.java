package interpreter.types;

import interpreter.Memory;
import interpreter.env.Environment;
import interpreter.primitives.Pointer;
import interpreter.primitives.Primitive;
import interpreter.splObjects.SplClass;
import interpreter.splObjects.SplObject;

import java.util.Arrays;
import java.util.List;

public class ClassType extends PointerType {

    private final Pointer clazzPointer;
    private TypeValue[] templates;

    public ClassType(Pointer clazzPointer) {
        this.clazzPointer = clazzPointer;
    }

    public void setTemplates(TypeValue[] templates) {
        this.templates = templates;
    }

    public TypeValue[] getTemplates() {
        return templates;
    }

    public Pointer getClazzPointer() {
        return clazzPointer;
    }

    @Override
    public String toString() {
        if (templates == null) {
            return "ClassType @" + clazzPointer.getPtr();
        } else {
            return "ClassType @" + clazzPointer.getPtr() + "<" + Arrays.toString(templates) + ">";
        }
    }

    @Override
    public int getPointerType() {
        return PointerType.CLASS_TYPE;
    }

    @Override
    public boolean isSuperclassOfOrEqualsNotNull(Type child, Environment env) {
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
