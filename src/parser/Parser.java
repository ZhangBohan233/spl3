package parser;

import ast.BinaryOperator;
import ast.BlockStmt;
import ast.Declaration;
import lexer.*;
import util.LineFile;

import java.util.List;
import java.util.Stack;

public class Parser {

    private List<Token> tokens;

    public Parser(TokenList tokenList) {
        tokens = tokenList.getTokens();
    }

    public BlockStmt parse() {

        AstBuilder builder = new AstBuilder();

        int bracketCount = 0;
        int braceCount = 0;
        int sqrBracketCount = 0;

        int varLevel = Declaration.VAR;

        boolean fnHeader = false;
        boolean fnRType = false;

        Stack<Integer> paramBrackets = new Stack<>();
        Stack<Integer> funcBodyBraces = new Stack<>();

        for (int i = 0; i < tokens.size(); ++i) {
            Token token = tokens.get(i);
            LineFile lineFile = token.getLineFile();

            if (token instanceof IdToken) {
                String identifier = ((IdToken) token).getIdentifier();

                if (Tokenizer.NUMERIC_BINARY.contains(identifier)) {
                    builder.addBinaryOperator(identifier, BinaryOperator.NUMERIC, lineFile);
                } else if (Tokenizer.LOGICAL_BINARY.contains(identifier)) {
                    builder.addBinaryOperator(identifier, BinaryOperator.LOGICAL, lineFile);
                } else {
                    switch (identifier) {
                        case "(":
                            bracketCount++;
                            if (fnHeader) {  // declaring function
                                fnHeader = false;
                                fnRType = true;
                                paramBrackets.push(bracketCount);
                                builder.addParameterBracket();

                            }
                            break;
                        case ")":
                            if (bracketCount == paramBrackets.peek()) {
                                paramBrackets.pop();
                                builder.buildParameterBracket();
                            }
                            bracketCount--;
                            break;
                        case "{":
                            braceCount++;
                            if (fnRType) {
                                fnRType = false;
                                builder.addFnRType();
                                builder.addBraceBlock();
                                funcBodyBraces.add(braceCount);
                            }
                            break;
                        case "}":
                            if (braceCount == funcBodyBraces.peek()) {
                                funcBodyBraces.pop();
                                builder.buildBraceBlock();
                                builder.finishFunction();
                            }

                            braceCount--;
                            break;
                        case "[":
                        case "]":
                        case ".":
                            builder.addDot(lineFile);
                            break;
                        case ":":
                            builder.addDeclaration(varLevel, lineFile);
                            break;
                        case "=":
                            builder.addAssignment(lineFile);
                            break;
                        case "fn":
                            Token fnNameTk = tokens.get(i + 1);
                            if (!(fnNameTk instanceof IdToken))
                                throw new ParseError("Function must have either a name or a header", lineFile);
                            String fnName = ((IdToken) fnNameTk).getIdentifier();
                            if (!fnName.equals("(")) {  // a function with name
                                i++;
                            } else {
                                fnName = null;
                            }
                            builder.addFunction(fnName, lineFile);
                            fnHeader = true;
                            break;
                        case "return":
                            break;
                        case ",":
                            builder.finishPart();
                            break;
                        case ";":
                            builder.finishPart();
                            if (fnRType) {
                                fnRType = false;  // TODO
                                builder.addFnRType();
                            }

                            builder.finishLine();

                            varLevel = Declaration.VAR;  // restore the var level
                            break;
                        default:  // name
                            builder.addName(identifier, lineFile);
                            break;
                    }
                }
            } else if (token instanceof IntToken) {
                builder.addInt(((IntToken) token).getValue(), lineFile);
            } else if (token instanceof FloatToken) {

            } else if (token instanceof StrToken) {

            } else if (token instanceof CharToken) {

            } else {
                throw new ParseError("Unexpected token type. ", lineFile);
            }
        }

        return builder.getBaseBlock();
    }
}
