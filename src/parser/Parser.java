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
        boolean importingModule = false;
        boolean classHeader = false;
        boolean implementing = false;
        boolean extending = false;

        Stack<Integer> paramBrackets = new Stack<>();
        Stack<Integer> callBrackets = new Stack<>();
        Stack<Integer> funcBodyBraces = new Stack<>();
        Stack<Integer> moduleBraces = new Stack<>();
        Stack<Integer> classBraces = new Stack<>();

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
                    boolean isInterface = false;
                    switch (identifier) {
                        case "(":
                            bracketCount++;
                            if (fnHeader) {  // declaring function
                                fnHeader = false;
                                fnRType = true;
                                paramBrackets.push(bracketCount);
                                builder.addParameterBracket();
                            } else if (isCall(tokens.get(i - 1))) {
                                builder.addCall(lineFile);
                                callBrackets.push(bracketCount);
                            }
                            break;
                        case ")":
                            if (isThisStack(paramBrackets, bracketCount)) {
                                paramBrackets.pop();
                                builder.buildParameterBracket();
                            } else if (isThisStack(callBrackets, bracketCount)) {
                                callBrackets.pop();
                                builder.buildCall();
                            }
                            bracketCount--;
                            break;
                        case "{":
                            braceCount++;
                            if (fnRType) {
                                fnRType = false;
                                builder.addFnRType(lineFile);
                                builder.addBraceBlock();
                                funcBodyBraces.push(braceCount);
                            } else if (importingModule) {
                                importingModule = false;
                                builder.addBraceBlock();
                                moduleBraces.push(braceCount);
                            } else if (classHeader) {
                                classHeader = false;
                                if (extending) {
                                    assert !implementing;  // keyword "implements" should already finished extends
                                    extending = false;
                                    builder.finishExtend(lineFile);
                                }
                                if (implementing) {
                                    implementing = false;
                                    builder.finishImplements();
                                }
                                builder.addBraceBlock();
                                classBraces.push(braceCount);
                            }
                            break;
                        case "}":
                            if (isThisStack(funcBodyBraces, braceCount)) {
                                funcBodyBraces.pop();
                                builder.buildBraceBlock();
                                builder.finishFunction(lineFile);
                            } else if (isThisStack(moduleBraces, braceCount)) {
                                moduleBraces.pop();
                                builder.buildBraceBlock();
                                builder.buildImportModule(lineFile);
                            } else if (isThisStack(classBraces, braceCount)) {
                                classBraces.pop();
                                builder.buildBraceBlock();
                                builder.buildClass(lineFile);
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
                        case "interface":
                            isInterface = true;
                        case "class":
                            Token classNameToken = tokens.get(i + 1);
                            i += 1;
                            if (!(classNameToken instanceof IdToken)) {
                                throw new ParseError("Class must have a name. ", lineFile);
                            }
                            String className = ((IdToken) classNameToken).getIdentifier();
                            classHeader = true;
                            builder.addClass(className, isInterface, lineFile);
                            break;
                        case "extends":
                            builder.addExtend(lineFile);
                            extending = true;
                            break;
                        case "implements":
                            if (extending) {
                                extending = false;
                                builder.finishExtend(lineFile);
                            }
                            builder.addImplements();
                            implementing = true;
                            break;
                        case "import":
                            String importName = ((IdToken) tokens.get(i + 1)).getIdentifier();
                            builder.addImportModule(importName, lineFile);
                            importingModule = true;
                            i += 1;
                            break;
                        case "return":
                            builder.addReturnStmt(lineFile);
                            break;
                        case "new":
                            builder.addNewStmt(lineFile);
                            break;
                        case ",":
                            builder.finishPart();
                            break;
                        case ";":
                            builder.finishPart();
                            if (fnRType) {
                                fnRType = false;  // TODO
                                builder.addFnRType(lineFile);
                            }

                            builder.finishLine();

                            varLevel = Declaration.VAR;  // restore the var level
                            break;
                        case "int":
                        case "float":
                        case "char":
                        case "boolean":
                        case "void":
                            builder.addPrimitiveTypeName(identifier, lineFile);
                            break;
                        case "namespace":
                            builder.addNamespace(lineFile);
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

    private static boolean isThisStack(Stack<Integer> stack, int value) {
        if (stack.empty()) return false;
        else return stack.peek() == value;
    }

    private static boolean isCall(Token token) {
        if (token instanceof IdToken) {
            String identifier = ((IdToken) token).getIdentifier();
            return Tokenizer.StringTypes.isIdentifier(identifier) && !Tokenizer.RESERVED.contains(identifier);
        }
        return false;
    }
}
