package interpreter.types;

import interpreter.env.Environment;

public interface Type {

    boolean isPrimitive();

    boolean isSuperclassOfOrEquals(Type child, Environment env);
}
