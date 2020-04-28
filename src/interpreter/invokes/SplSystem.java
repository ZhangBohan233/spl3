package interpreter.invokes;

import ast.Arguments;
import ast.Node;
import interpreter.env.Environment;
import interpreter.primitives.Char;
import interpreter.primitives.Pointer;
import interpreter.splObjects.Function;
import interpreter.splObjects.Instance;
import interpreter.splObjects.NativeObject;
import interpreter.splObjects.SplArray;
import interpreter.types.ClassType;
import interpreter.types.PointerType;
import interpreter.types.PrimitiveType;
import interpreter.types.TypeValue;
import util.LineFile;

import java.io.InputStream;
import java.io.PrintStream;

public class SplSystem extends NativeObject {

    private PrintStream stdout = System.out;
    private PrintStream stderr = System.err;
    private InputStream stdin = System.in;

    @SuppressWarnings("unused")
    public TypeValue println(Arguments arguments, Environment environment, LineFile lineFile) {
        TypeValue[] args = arguments.evalArgs(environment);

        TypeValue stringTv = environment.get("String", lineFile);
        ClassType stringType = (ClassType) stringTv.getType();

        String[] resArr = new String[args.length];
        for (int i = 0; i < args.length; ++i) {
            TypeValue arg = args[i];
            if (arg.getType().isPrimitive()) {
                resArr[i] = arg.toString();
            } else {
                PointerType ptrType = (PointerType) arg.getType();
                Pointer ptr = (Pointer) arg.getValue();
                if (ptrType.getPointerType() == PointerType.CLASS_TYPE) {
                    Instance instance = (Instance) environment.getMemory().get(ptr);
                    TypeValue toStrFtnTv = instance.getEnv().get("toString", lineFile);
                    Function toStrFtn = (Function) environment.getMemory().get((Pointer) toStrFtnTv.getValue());
                    TypeValue toStrRes = toStrFtn.call(new TypeValue[0], environment, lineFile);
                    assert stringType.isSuperclassOfOrEquals(toStrRes.getType(), environment);

                    Instance strIns = (Instance) environment.getMemory().get((Pointer) toStrRes.getValue());
                    TypeValue chars = strIns.getEnv().get("chars", lineFile);

                    char[] arr = SplArray.toJavaCharArray(chars, environment.getMemory());
                    resArr[i] = new String(arr);
                } else {
                    resArr[i] = environment.getMemory().get(ptr).toString();
                }
            }
        }
        String result = String.join(", ", resArr);
        stdout.println(result);

        return TypeValue.VOID_NULL;
    }
}
