import ast.*;
import interpreter.Memory;
import interpreter.env.GlobalEnvironment;
import interpreter.types.TypeValue;
import lexer.TokenList;
import lexer.Tokenizer;
import parser.Parser;
import util.ArgumentParser;
import util.LineFile;

public class Main {

    public static void main(String[] args) throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(args);
        if (argumentParser.isAllValid()) {
            Tokenizer tokenizer = new Tokenizer(argumentParser.getMainSrcFile(), true, argumentParser.importLang());
            TokenList tokenList = tokenizer.tokenize();
            Parser parser = new Parser(tokenList);
            BlockStmt root = parser.parse();
            System.out.println(root);
            System.out.println("===== End of ast =====");
//            FakeGlobalEnv environment = new FakeGlobalEnv();
//            root.preprocess(environment);
            Memory memory = new Memory();
            GlobalEnvironment globalEnvironment = new GlobalEnvironment(memory);
            root.evaluate(globalEnvironment);

            callMain(globalEnvironment);

            globalEnvironment.printVars();
            memory.printMemory();
        } else {
            System.out.println(argumentParser.getMsg());
        }
    }

    private static void callMain(GlobalEnvironment globalEnvironment) {
        TypeValue mainTv = globalEnvironment.get("main", new LineFile(0, "Main"));
        if (mainTv != null) {
            FuncCall mainCall = new FuncCall(LineFile.LF_INTERPRETER);
            mainCall.setLeft(new NameNode("main", LineFile.LF_INTERPRETER));
            mainCall.setRight(new Arguments(new Line()));
            TypeValue rtn = mainCall.evaluate(globalEnvironment);
            System.out.println("Process finished with exit value " + rtn.getValue());
        }
    }
}
