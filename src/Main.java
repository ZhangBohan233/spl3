import ast.*;
import interpreter.Memory;
import interpreter.env.GlobalEnvironment;
import interpreter.invokes.SplSystem;
import interpreter.primitives.Pointer;
import interpreter.splObjects.Function;
import interpreter.splObjects.Instance;
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

    public static void main(String[] args) throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(args);
        if (argumentParser.isAllValid()) {
            Tokenizer tokenizer = new Tokenizer(
                    argumentParser.getMainSrcFile(),
                    true,
                    argumentParser.importLang()
            );
            TokenList tokenList = tokenizer.tokenize();
            Parser parser = new Parser(tokenList);
            BlockStmt root = parser.parse();
            if (argumentParser.isPrintAst()) {
                System.out.println("===== Ast =====");
                System.out.println(root);
                System.out.println("===== End of ast =====");
            }
//            FakeGlobalEnv environment = new FakeGlobalEnv();
//            root.preprocess(environment);
            Memory memory = new Memory();
            GlobalEnvironment globalEnvironment = new GlobalEnvironment(memory);

            initNatives(globalEnvironment);
            root.evaluate(globalEnvironment);

            callMain(argumentParser.getSplArgs(), globalEnvironment);

            globalEnvironment.printVars();
            memory.printMemory();
        } else {
            System.out.println(argumentParser.getMsg());
        }
    }

    private static void initNatives(GlobalEnvironment globalEnvironment) {
        SplSystem system = new SplSystem();

        Memory memory = globalEnvironment.getMemory();
        Pointer sysPtr = memory.allocateObject(system);

        globalEnvironment.defineConstAndSet(
                "System",
                new TypeValue(new NativeType(SplSystem.class), sysPtr),
                LineFile.LF_INTERPRETER);
    }

    private static void callMain(String[] args, GlobalEnvironment globalEnvironment) {
        TypeValue mainTv = globalEnvironment.get("main", new LineFile(0, "Main"));
        if (mainTv != null) {
            TypeValue[] splArg =
                    args == null ? new TypeValue[0] : makeSplArgArray(args, globalEnvironment);

            if (!(mainTv.getType() instanceof CallableType)) {
                throw new TypeError("Main function must be callable. ");
            }

            Function mainFunc = (Function) globalEnvironment.getMemory().get((Pointer) mainTv.getValue());
            TypeValue rtn = mainFunc.call(splArg, globalEnvironment, LineFile.LF_INTERPRETER);

//            FuncCall mainCall = new FuncCall(LineFile.LF_INTERPRETER);
//            mainCall.setCallObj(new NameNode("main", LineFile.LF_INTERPRETER));
//            mainCall.setArguments(splArg);
//            TypeValue rtn = mainCall.evaluate(globalEnvironment);

            System.out.println("Process finished with exit value " + rtn.getValue());
        }
    }

    private static TypeValue[] makeSplArgArray(String[] args, GlobalEnvironment globalEnvironment) {
        TypeValue stringTv = globalEnvironment.get("String", LineFile.LF_INTERPRETER);
        ClassType stringType = (ClassType) stringTv.getType();
        ArrayType type = new ArrayType(stringType);
        Pointer argPtr = SplArray.createArray(type, List.of(args.length), globalEnvironment.getMemory());
        TypeValue arrTv = new TypeValue(type, argPtr);
        for (int i = 0; i < args.length; ++i) {
            // create spl char array
            TypeValue charArrTv = StringLiteral.createCharArrayAndAllocate(
                    args[i].toCharArray(),
                    globalEnvironment,
                    LineFile.LF_INTERPRETER);

            // create String instance
            TypeValue strIns = StringLiteral.createStringInstance(
                    charArrTv, globalEnvironment, LineFile.LF_INTERPRETER
            );
            SplArray.setItemAtIndex(argPtr, i, type, strIns, globalEnvironment, LineFile.LF_INTERPRETER);
        }
        return new TypeValue[]{arrTv};
    }
}
