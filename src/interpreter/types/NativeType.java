package interpreter.types;

import interpreter.env.Environment;
import interpreter.splObjects.NativeObject;

public class NativeType extends PointerType {

    public final Class<? extends NativeObject> clazzPtr;

    public NativeType(Class<? extends NativeObject> clazzPtr) {
        this.clazzPtr = clazzPtr;
    }

    @Override
    public int getPointerType() {
        return PointerType.NATIVE_TYPE;
    }

    @Override
    public boolean isSuperclassOfOrEqualsNotNull(Type child, Environment env) {
        return equals(child);
    }

    @Override
    public boolean equals(Object o) {
        return getClass() == o.getClass() && clazzPtr == ((NativeType) o).clazzPtr;
    }

    @Override
    public String toString() {
        return "NativeType{" + clazzPtr + '}';
    }
}
