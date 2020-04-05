import ast.fakeEnv.FakeGlobalEnv;
import interpreter.Memory;
import interpreter.env.GlobalEnvironment;
import lexer.TokenList;
import lexer.Tokenizer;
import ast.BlockStmt;
import parser.Parser;
import util.ArgumentParser;

public class Main {

    public static void main(String[] args) throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(args);
        if (argumentParser.isAllValid()) {
            Tokenizer tokenizer = new Tokenizer(argumentParser.getMainSrcFile());
            TokenList tokenList = tokenizer.tokenize();
            Parser parser = new Parser(tokenList);
            BlockStmt root = parser.parse();
            System.out.println(root);
//            FakeGlobalEnv environment = new FakeGlobalEnv();
//            root.preprocess(environment);
            Memory memory = new Memory();
            GlobalEnvironment globalEnvironment = new GlobalEnvironment(memory);
            root.evaluate(globalEnvironment);

            globalEnvironment.printVars();
            memory.printMemory();
        } else {
            System.out.println(argumentParser.getMsg());
        }
    }
}
