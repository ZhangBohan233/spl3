package interpreter.types;

public class PointerType implements Type {
    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isSuperclassOfOrEquals(Type child) {
        // TODO
        return equals(child);
    }
}
