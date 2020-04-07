package interpreter.types;

import interpreter.primitives.Pointer;

public class ClassType extends PointerType {

    private Pointer clazzPointer;

    public ClassType(Pointer clazzPointer) {
        this.clazzPointer = clazzPointer;
    }

    public Pointer getClazzPointer() {
        return clazzPointer;
    }

    @Override
    public String toString() {
        return "ClassType <at " + clazzPointer.getPtr() + ">";
    }

    @Override
    public int getPointerType() {
        return PointerType.CLASS_TYPE;
    }
}
