package interpreter.types;

import interpreter.env.Environment;
import interpreter.primitives.Primitive;

public interface Type {

    /**
     * Return {@code true} iff this type is primitive.
     * <p>
     * Otherwise, it is a pointer type.
     *
     * @return {@code true} if this type is primitive, {@code false} if pointer type.
     */
    boolean isPrimitive();

    boolean isSuperclassOfOrEquals(Type child, Environment env);

    Primitive defaultValue();
}
