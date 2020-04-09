package interpreter.types;

import interpreter.env.Environment;

import java.util.List;
import java.util.Objects;

public class CallableType extends PointerType {

    private List<Type> paramTypes;

    private Type rType;

    public CallableType(List<Type> paramTypes, Type rType) {
        this.paramTypes = paramTypes;
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
    public boolean isSuperclassOfOrEquals(Type child, Environment env) {
        return equals(child);
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
