package interpreter.types;

import interpreter.env.Environment;

import java.util.List;
import java.util.Objects;

public class CallableType extends PointerType {

    private final List<Type> paramTypes;

    private final Type rType;

    public CallableType(List<Type> paramTypes, Type rType) {
        this.paramTypes = paramTypes;
        this.rType = rType;
    }

    /**
     * Constructor for native function.
     *
     * @param rType the returning type
     */
    public CallableType(Type rType) {
        this.paramTypes = null;
        this.rType = rType;
    }

    public List<Type> getParamTypes() {
        return paramTypes;
    }

    public Type getRType() {
        return rType;
    }

    @Override
    public String toString() {
        return paramTypes + "->" + rType;
    }

    @Override
    public int getPointerType() {
        return PointerType.CALLABLE_TYPE;
    }

    @Override
    public boolean isSuperclassOfOrEqualsNotNull(Type child, Environment env) {
        if (child instanceof CallableType) {
            CallableType ct = (CallableType) child;

            if (ct.paramTypes == null || paramTypes == null) return equals(ct);  // may be native functions

            if (ct.paramTypes.size() == paramTypes.size()) {
                for (int i = 0; i < paramTypes.size(); ++i) {
                    Type ptThis = paramTypes.get(i);
                    Type ptChild = ct.paramTypes.get(i);
                    if (!ptThis.isSuperclassOfOrEquals(ptChild, env)) {
                        return false;
                    }
                }
                var b= rType.isSuperclassOfOrEquals(ct.rType, env);
                return b;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallableType that = (CallableType) o;

        if (!Objects.equals(paramTypes, that.paramTypes)) return false;
        return Objects.equals(rType, that.rType);
    }

}
