package interpreter.types;

import interpreter.Memory;
import interpreter.env.Environment;
import interpreter.primitives.Pointer;
import interpreter.primitives.Primitive;
import interpreter.splObjects.SplClass;
import interpreter.splObjects.SplObject;

import java.util.*;

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
        else if (equalsWithoutTemplates(child)) {
            return checkTemplateSuperOrEquals((ClassType) child, env);
        } else {
            ClassType childCt = (ClassType) child;
            SplClass childClazz = (SplClass) env.getMemory().get(childCt.clazzPointer);
            if (isSuperclassOfOrEquals(childClazz.getSuperclassType(), env)) return true;

            for (ClassType interfaceT: childClazz.getInterfacePointers()) {
                if (isSuperclassOfOrEquals(interfaceT, env)) return true;
            }
            return false;
        }
    }

    private boolean checkTemplateSuperOrEquals(ClassType child, Environment env) {
        if (templates == child.templates) return true;
        if (templates == null || child.templates == null) return false;
        if (templates.length != child.templates.length) return false;

        for (int i = 0; i < templates.length; ++i) {
            ClassType ct = (ClassType) templates[i].getType();
            ClassType childCt = (ClassType) child.templates[i].getType();
            if (!ct.isSuperclassOfOrEquals(childCt, env)) {
                return false;
            }
        }
        return true;
    }

    private boolean equalsWithoutTemplates(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassType classType = (ClassType) o;

        return Objects.equals(clazzPointer, classType.clazzPointer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        if (equalsWithoutTemplates(o)) {
            ClassType classType = (ClassType) o;
//        System.out.println(Arrays.toString(templates) + " t " + Arrays.toString(classType.templates));
            return Arrays.equals(templates, classType.templates);
        }
        return false;
    }

    public String toStringClass(Memory memory) {
        SplClass clazz = (SplClass) memory.get(clazzPointer);
        return clazz.toString();
    }

    public ClassType copy() {
        ClassType cpy = new ClassType(clazzPointer);
        if (templates != null)
            cpy.templates = Arrays.copyOf(templates, templates.length);
        return cpy;
    }
}
