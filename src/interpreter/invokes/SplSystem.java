package interpreter.invokes;

import ast.Arguments;
import ast.StringLiteral;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.primitives.Int;
import interpreter.primitives.Pointer;
import interpreter.splObjects.*;
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
        checkArgCount(arguments, 0, "memoryView", lineFile);

        stdout.println("Memory: " + environment.getMemory().memoryView());
        stdout.println("Available: " + environment.getMemory().availableView());
        return TypeValue.VOID;
    }

    public TypeValue id(Arguments arguments, Environment environment, LineFile lineFile) {
        checkArgCount(arguments, 1, "id", lineFile);

        TypeValue typeValue = arguments.getLine().getChildren().get(0).evaluate(environment);
        if (typeValue.getType().isPrimitive())
            throw new TypeError("System.id() takes a pointer as argument. ", lineFile);

        return new TypeValue(PrimitiveType.TYPE_INT, new Int(typeValue.getValue().intValue()));
    }

    public TypeValue string(Arguments arguments, Environment environment, LineFile lineFile) {
        checkArgCount(arguments, 1, "id", lineFile);

        TypeValue typeValue = arguments.getLine().getChildren().get(0).evaluate(environment);

        String s = getString(typeValue, environment, lineFile);

        return StringLiteral.createStringOneStep(s.toCharArray(), environment, lineFile);
    }

    public TypeValue typeName(Arguments arguments, Environment environment, LineFile lineFile) {
        checkArgCount(arguments, 1, "typeName", lineFile);

        TypeValue typeValue = arguments.getLine().getChildren().get(0).evaluate(environment);

        String s = typeValue.getType().toString();
        return StringLiteral.createStringOneStep(s.toCharArray(), environment, lineFile);
    }

    private static void checkArgCount(Arguments arguments, int expectArgc, String fnName, LineFile lineFile) {
        if (arguments.getLine().getChildren().size() != expectArgc) {
            throw new SplException("System." + fnName + "() takes " + expectArgc + " arguments, " +
                    arguments.getLine().getChildren().size() + " given. ", lineFile);
        }
    }

    private static String getString(TypeValue typeValue, Environment environment, LineFile lineFile) {
        TypeValue stringTv = environment.get("String", lineFile);
        ClassType stringType = (ClassType) stringTv.getType();
        return getString(typeValue, environment, lineFile, stringType);
    }

    private static String getString(TypeValue typeValue, Environment environment, LineFile lineFile,
                                    ClassType stringType) {
        if (typeValue.getType().isPrimitive()) {
            return typeValue.getValue().toString();
        } else {
            PointerType ptrType = (PointerType) typeValue.getType();
            Pointer ptr = (Pointer) typeValue.getValue();
            return pointerToSting(ptrType, ptr, environment, lineFile, stringType);
        }
    }

    private static String getPrintString(Arguments arguments, Environment environment, LineFile lineFile) {
        TypeValue[] args = arguments.evalArgs(environment);

        TypeValue stringTv = environment.get("String", lineFile);
        ClassType stringType = (ClassType) stringTv.getType();

        String[] resArr = new String[args.length];
        for (int i = 0; i < args.length; ++i) {
            resArr[i] = getString(args[i], environment, lineFile, stringType);
        }
        return String.join(", ", resArr);
    }

    private static String pointerToSting(PointerType ptrType,
                                         Pointer ptr,
                                         Environment environment,
                                         LineFile lineFile) {

        TypeValue stringTv = environment.get("String", lineFile);
        ClassType stringType = (ClassType) stringTv.getType();
        return pointerToSting(ptrType, ptr, environment, lineFile, stringType);
    }

    private static String pointerToSting(PointerType ptrType,
                                         Pointer ptr,
                                         Environment environment,
                                         LineFile lineFile,
                                         ClassType stringType) {
        if (ptr.getPtr() == 0) {  // Pointed to null
            return  "null";
        } else if (ptrType.getPointerType() == PointerType.CLASS_TYPE) {
            Instance instance = (Instance) environment.getMemory().get(ptr);

            if (stringType.equals(instance.getType())) {  // is String itself
                return extractFromSplString(instance, environment, lineFile);
            } else {
                TypeValue toStrFtnTv = instance.getEnv().get("toString", lineFile);
                Function toStrFtn = (Function) environment.getMemory().get((Pointer) toStrFtnTv.getValue());
                TypeValue toStrRes = toStrFtn.call(new TypeValue[0], environment, lineFile);
                assert stringType.isSuperclassOfOrEquals(toStrRes.getType(), environment);

                Instance strIns = (Instance) environment.getMemory().get((Pointer) toStrRes.getValue());
                return extractFromSplString(strIns, environment, lineFile);
            }
        } else {
            return environment.getMemory().get(ptr).toString();
        }
    }

    private static String extractFromSplString(Instance stringInstance, Environment env, LineFile lineFile) {
        TypeValue chars = stringInstance.getEnv().get("chars", lineFile);

        char[] arr = SplArray.toJavaCharArray(chars, env.getMemory());
        return new String(arr);
    }
}
