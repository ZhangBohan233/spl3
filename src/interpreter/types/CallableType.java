package interpreter.types;

import java.util.List;

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
}
