package interpreter.splObjects;

import ast.Arguments;
import ast.FuncCall;
import ast.NameNode;
import ast.Node;
import interpreter.AttributeError;
import interpreter.env.Environment;
import interpreter.types.TypeError;
import interpreter.types.TypeValue;
import util.LineFile;

import java.lang.reflect.Method;

public class NativeObject extends SplObject {

    public TypeValue invoke(Node node, Environment callEnv, LineFile lineFile) {
        if (node instanceof NameNode) {
            return null;
        } else if (node instanceof FuncCall) {
            if (((FuncCall) node).getCallObj() instanceof NameNode) {
                String name = ((NameNode) ((FuncCall) node).getCallObj()).getName();
                return nativeCall(this, name, ((FuncCall) node).getArguments(), callEnv, lineFile);
            }
        }
        throw new TypeError("Not a native invoke. ");
    }

    private static TypeValue nativeCall(NativeObject obj,
                                        String methodName,
                                        Arguments arguments,
                                        Environment callEnv,
                                        LineFile lineFile) {
        try {
            Method method = obj.getClass().getMethod(methodName, Arguments.class, Environment.class, LineFile.class);

            return (TypeValue) method.invoke(obj, arguments, callEnv, lineFile);
        } catch (Exception e) {
            throw new AttributeError("Native class '" + obj.getClass() + "' does not have method '" +
                    methodName + ". ");
        }
    }
}
