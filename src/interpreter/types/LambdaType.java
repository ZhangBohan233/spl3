package interpreter.types;

import java.util.List;

public class LambdaType extends CallableType {

    public LambdaType(List<Type> paramTypes) {
        super(paramTypes, null);
    }
}
