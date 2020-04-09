package interpreter.types;

import interpreter.env.Environment;

public class ModuleType extends PointerType {

    @Override
    public String toString() {
        return "ModuleType";
    }

    @Override
    public int getPointerType() {
        return PointerType.MODULE_TYPE;
    }

    @Override
    public boolean isSuperclassOfOrEquals(Type child, Environment env) {
        return child instanceof ModuleType;
    }
}
