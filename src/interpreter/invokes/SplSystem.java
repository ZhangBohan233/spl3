package interpreter.invokes;

import ast.Arguments;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.primitives.Int;
import interpreter.primitives.Pointer;
import interpreter.splObjects.Function;
import interpreter.splObjects.Instance;
import interpreter.splObjects.NativeObject;
import interpreter.splObjects.SplArray;
import interpreter.types.*;
import util.LineFile;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * Native calls.
 * <p>
 * All public methods can be called from spl, with arguments
 * {@code Arguments}, {@code Environment}, {@code LineFile}
 */
@SuppressWarnings("unused")
public class SplSystem extends NativeObject {

    private PrintStream stdout = System.out;
    private PrintStream stderr = System.err;
    private InputStream stdin = System.in;

    public void setErr(PrintStream stderr) {
        this.stderr = stderr;
    }

    public void setIn(InputStream stdin) {
        this.stdin = stdin;
    }

    public void setOut(PrintStream stdout) {
        this.stdout = stdout;
    }

    public TypeValue println(Arguments arguments, Environment environment, LineFile lineFile) {
        stdout.println(getPrintString(arguments, environment, lineFile));

        return TypeValue.VOID;
    }

    public TypeValue print(Arguments arguments, Environment environment, LineFile lineFile) {
        stdout.print(getPrintString(arguments, environment, lineFile));

        return TypeValue.VOID;
    }

    public TypeValue clock(Arguments arguments, Environment environment, LineFile lineFile) {
        if (arguments.getLine().getChildren().size() != 0) {
            throw new SplException("System.clock() takes 0 arguments, " +
                    arguments.getLine().getChildren().size() + " given. ", lineFile);
        }
        Int v = new Int(System.currentTimeMillis());
        return new TypeValue(PrimitiveType.TYPE_INT, v);
    }

    public TypeValue free(Arguments arguments, Environment environment, LineFile lineFile) {
        if (arguments.getLine().getChildren().size() != 1) {
            throw new SplException("System.free(ptr) takes 1 argument, " +
                    arguments.getLine().getChildren().size() + " given. ", lineFile);
        }
        TypeValue tv = arguments.getLine().getChildren().get(0).evaluate(environment);
        if (tv.getType().isPrimitive())
            throw new TypeError("System.free(ptr) takes object pointer as argument. ", lineFile);

        PointerType type = (PointerType) tv.getType();
        Pointer ptr = (Pointer) tv.getValue();
        int freeLength;
        if (type.getPointerType() == PointerType.ARRAY_TYPE) {
            SplArray array = (SplArray) environment.getMemory().get(ptr);
            freeLength = array.length + 1;
        } else {
            freeLength = 1;
        }
        environment.getMemory().free(ptr, freeLength);

        return TypeValue.VOID;
    }

    public TypeValue gc(Arguments arguments, Environment environment, LineFile lineFile) {
        if (arguments.getLine().getChildren().size() != 0) {
            throw new SplException("System.gc() takes 0 arguments, " +
                    arguments.getLine().getChildren().size() + " given. ", lineFile);
        }

        environment.getMemory().gc(environment);

        return TypeValue.VOID;
    }

    public TypeValue memoryView(Arguments arguments, Environment environment, LineFile lineFile) {
        if (arguments.getLine().getChildren().size() != 0) {
            throw new SplException("System.memoryView() takes 0 arguments, " +
                    arguments.getLine().getChildren().size() + " given. ", lineFile);
        }

        stdout.println("Memory: " + environment.getMemory().memoryView());
        stdout.println("Available: " + environment.getMemory().availableView());
        return TypeValue.VOID;
    }

    private String getPrintString(Arguments arguments, Environment environment, LineFile lineFile) {
        TypeValue[] args = arguments.evalArgs(environment);

        TypeValue stringTv = environment.get("String", lineFile);
        ClassType stringType = (ClassType) stringTv.getType();

        String[] resArr = new String[args.length];
        for (int i = 0; i < args.length; ++i) {
            TypeValue arg = args[i];
            if (arg.getType().isPrimitive()) {
                resArr[i] = arg.getValue().toString();
            } else {
                PointerType ptrType = (PointerType) arg.getType();
                Pointer ptr = (Pointer) arg.getValue();
                if (ptr.getPtr() == 0) {  // Pointed to null
                    resArr[i] = "null";
                } else if (ptrType.getPointerType() == PointerType.CLASS_TYPE) {
                    Instance instance = (Instance) environment.getMemory().get(ptr);

                    if (stringType.equals(instance.getType())) {  // is String itself
                        resArr[i] = extractFromSplString(instance, environment, lineFile);
                    } else {
                        TypeValue toStrFtnTv = instance.getEnv().get("toString", lineFile);
                        Function toStrFtn = (Function) environment.getMemory().get((Pointer) toStrFtnTv.getValue());
                        TypeValue toStrRes = toStrFtn.call(new TypeValue[0], environment, lineFile);
                        assert stringType.isSuperclassOfOrEquals(toStrRes.getType(), environment);

                        Instance strIns = (Instance) environment.getMemory().get((Pointer) toStrRes.getValue());
                        resArr[i] = extractFromSplString(strIns, environment, lineFile);
                    }
                } else {
                    resArr[i] = environment.getMemory().get(ptr).toString();
                }
            }
        }
        return String.join(", ", resArr);
    }

    private static String extractFromSplString(Instance stringInstance, Environment env, LineFile lineFile) {
        TypeValue chars = stringInstance.getEnv().get("chars", lineFile);

        char[] arr = SplArray.toJavaCharArray(chars, env.getMemory());
        return new String(arr);
    }
}
