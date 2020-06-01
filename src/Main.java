import ast.*;
import interpreter.Memory;
import interpreter.env.Environment;
import interpreter.env.GlobalEnvironment;
import interpreter.invokes.SplSystem;
import interpreter.primitives.Int;
import interpreter.primitives.Pointer;
import interpreter.splObjects.Function;
import interpreter.splObjects.Instance;
import interpreter.splObjects.NativeFunction;
import interpreter.splObjects.SplArray;
import interpreter.types.*;
import lexer.TokenList;
import lexer.Tokenizer;
import parser.Parser;
import util.ArgumentParser;
import util.LineFile;

import java.util.Arrays;
import java.util.List;

public class Main {

    private static final LineFile LF_MAIN = new LineFile(0, "Main");

    public static void main(String[] args) throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(args);
        if (argumentParser.isAllValid()) {
            long parseBegin = System.currentTimeMillis();
            Tokenizer tokenizer = new Tokenizer(
                    argumentParser.getMainSrcFile(),
                    true,
                    argumentParser.importLang()
            );
            TokenList tokenList = tokenizer.tokenize();
            if (argumentParser.isPrintTokens()) {
                System.out.println(tokenList);
            }
            Parser parser = new Parser(tokenList);
            BlockStmt root = parser.parse();
            if (argumentParser.isPrintAst()) {
                System.out.println("===== Ast =====");
                System.out.println(root);
                System.out.println("===== End of ast =====");
            }
            long vmStartBegin = System.currentTimeMillis();
//            FakeGlobalEnv environment = new FakeGlobalEnv();
//            root.preprocess(environment);
            Memory memory = new Memory();
            GlobalEnvironment globalEnvironment = new GlobalEnvironment(memory);

            if (argumentParser.isGcInfo()) memory.debugs.setPrintGcRes(true);
            if (argumentParser.isGcTrigger()) memory.debugs.setPrintGcTrigger(true);

            initNatives(globalEnvironment);

            long runBegin = System.currentTimeMillis();
            root.evaluate(globalEnvironment);

            callMain(argumentParser.getSplArgs(), globalEnvironment);

//            globalEnvironment.printVars();

            long processEnd = System.currentTimeMillis();

            if (argumentParser.isPrintMem()) {
                memory.printMemory();
            }
            if (argumentParser.isTimer()) {
                System.out.println(String.format(
                        "Parse time: %d ms, VM startup time: %d ms, running time: %d ms.",
                        vmStartBegin - parseBegin,
                        runBegin - vmStartBegin,
                        processEnd - runBegin
                ));
            }
        } else {
            System.out.println(argumentParser.getMsg());
        }
    }

    private static void initNatives(GlobalEnvironment globalEnvironment) {
        initNativeFunctions(globalEnvironment);

        SplSystem system = new SplSystem();

        Memory memory = globalEnvironment.getMemory();
        Pointer sysPtr = memory.allocateObject(system, globalEnvironment);

        globalEnvironment.defineConstAndSet(
                "System",
                new TypeValue(new NativeType(SplSystem.class), sysPtr),
                LineFile.LF_INTERPRETER);
    }

    private static void initNativeFunctions(GlobalEnvironment ge) {
        NativeFunction toInt = new NativeFunction("int",
                new CallableType(PrimitiveType.TYPE_INT), 1) {
            @Override
            protected TypeValue callFunc(Arguments arguments, Environment callingEnv) {
                TypeValue arg = arguments.getLine().getChildren().get(0).evaluate(callingEnv);
                if (arg.getType().isPrimitive()) {
                    return new TypeValue(PrimitiveType.TYPE_INT, new Int(arg.getValue().intValue()));
                } else {
                    throw new TypeError("Cannot convert pointer type to int. ");
                }
            }
        };

        Memory memory = ge.getMemory();
        Pointer ptrInt = memory.allocateFunction(toInt, ge);

        ge.defineFunction("int", new TypeValue(toInt.getFuncType(), ptrInt), LineFile.LF_INTERPRETER);
    }

    private static void callMain(String[] args, GlobalEnvironment globalEnvironment) {
        if (globalEnvironment.hasName("main", LF_MAIN)) {
            TypeValue mainTv = globalEnvironment.get("main", LF_MAIN);
            TypeValue[] splArg =
                    args == null ? new TypeValue[0] : makeSplArgArray(args, globalEnvironment);

            if (!(mainTv.getType() instanceof CallableType)) {
                throw new TypeError("Main function must be callable. ");
            }
            CallableType mainType = (CallableType) mainTv.getType();
            if (!(mainType.getRType().equals(PrimitiveType.TYPE_INT) ||
                    mainType.getRType().equals(PrimitiveType.TYPE_VOID))) {
                throw new TypeError("Main function returns either 'int' or 'void'. ");
            }

            Function mainFunc = (Function) globalEnvironment.getMemory().get((Pointer) mainTv.getValue());
            TypeValue rtn = mainFunc.call(splArg, globalEnvironment, LF_MAIN);

            System.out.println("Process finished with exit value " + rtn.getValue());
        }
    }

    private static TypeValue[] makeSplArgArray(String[] args, GlobalEnvironment globalEnvironment) {
        TypeValue stringTv = globalEnvironment.get("String", LF_MAIN);
        ClassType stringType = (ClassType) stringTv.getType();
        ArrayType type = new ArrayType(stringType);
        Pointer argPtr = SplArray.createArray(type, List.of(args.length), globalEnvironment);
        TypeValue arrTv = new TypeValue(type, argPtr);
        for (int i = 0; i < args.length; ++i) {
//            // create spl char array
//            TypeValue charArrTv = StringLiteral.createCharArrayAndAllocate(
//                    args[i].toCharArray(),
//                    globalEnvironment,
//                    LineFile.LF_INTERPRETER);

            // create String instance
            TypeValue strIns = StringLiteral.createString(
                    args[i].toCharArray(), globalEnvironment, LineFile.LF_INTERPRETER
            );
            SplArray.setItemAtIndex(argPtr, i, type, strIns, globalEnvironment, LineFile.LF_INTERPRETER);
        }
        return new TypeValue[]{arrTv};
    }
}
