package interpreter.types;

public interface Type {

    boolean isPrimitive();

    boolean isSuperclassOfOrEquals(Type child);
}
