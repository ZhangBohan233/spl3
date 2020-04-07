package interpreter.types;

public class ModuleType extends PointerType {

    @Override
    public String toString() {
        return "ModuleType";
    }

    @Override
    public int getPointerType() {
        return PointerType.MODULE_TYPE;
    }
}
