package parser;

import ast.*;
import lexer.*;
import util.LineFile;

import java.util.List;
import java.util.Stack;

public class Parser {

    private final List<Token> tokens;

    public Parser(TokenList tokenList) {
        tokens = tokenList.getTokens();
    }

    public BlockStmt parse() {

        AstBuilder builder = new AstBuilder();

        int bracketCount = 0;
        int braceCount = 0;
        int sqrBracketCount = 0;

        int varLevel = Declaration.VAR;

        boolean fnParams = false;
        boolean lambdaParams = false;
        boolean fnRType = false;
        boolean importingModule = false;
        boolean classHeader = false;
        boolean implementing = false;
        boolean extending = false;
        boolean conditioning = false;
        boolean isElse = false;
        boolean isAbstract = false;

        Stack<Integer> paramBrackets = new Stack<>();
        Stack<Integer> lambdaParamBrackets = new Stack<>();
        Stack<Integer> callBrackets = new Stack<>();
        Stack<Integer> funcBodyBraces = new Stack<>();
        Stack<Integer> moduleBraces = new Stack<>();
        Stack<Integer> classBraces = new Stack<>();
        Stack<Integer> condBraces = new Stack<>();
        Stack<Integer> funcTypeSqrBrackets = new Stack<>();

        for (int i = 0; i < tokens.size(); ++i) {
            Token token = tokens.get(i);
            LineFile lineFile = token.getLineFile();

            if (token instanceof IdToken) {
                String identifier = ((IdToken) token).getIdentifier();

                if (identifier.equals("-")) {
                    // special case, since "-" can both binary (subtraction) or unary (negation)
                    if (i > 0 && isUnary(tokens.get(i - 1))) {
                        // negation
                        builder.addUnaryOperator("neg", RegularUnaryOperator.NUMERIC, lineFile);
                    } else {
                        // subtraction
                        builder.addBinaryOperator("-", BinaryOperator.NUMERIC, lineFile);
                    }
                } else if (Tokenizer.LOGICAL_UNARY.contains(identifier)) {
                    builder.addUnaryOperator(identifier, RegularUnaryOperator.LOGICAL, lineFile);
                } else if (Tokenizer.NUMERIC_BINARY.contains(identifier)) {
                    builder.addBinaryOperator(identifier, BinaryOperator.NUMERIC, lineFile);
                } else if (Tokenizer.NUMERIC_BINARY_ASSIGN.contains(identifier)) {
                    builder.addBinaryOperatorAssign(identifier, lineFile);
                } else if (Tokenizer.LOGICAL_BINARY.contains(identifier)) {
                    builder.addBinaryOperator(identifier, BinaryOperator.LOGICAL, lineFile);
                } else if (Tokenizer.LAZY_BINARY.contains(identifier)) {
                    builder.addBinaryOperator(identifier, BinaryOperator.LAZY, lineFile);
                } else if (Tokenizer.FAKE_TERNARY.contains(identifier)) {
                    builder.addFakeTernary(identifier, lineFile);
                } else {
                    boolean isInterface = false;
                    switch (identifier) {
                        case "(":
                            bracketCount++;
                            if (fnParams) {  // declaring function
                                fnParams = false;
                                paramBrackets.push(bracketCount);
                                builder.addParameterBracket();
                            } else if (lambdaParams) {
                                lambdaParams = false;
                                lambdaParamBrackets.push(bracketCount);
                                builder.addParameterBracket();
                            } else if (isCall(tokens.get(i - 1))) {
                                builder.addCall(lineFile);
                                callBrackets.push(bracketCount);
                            } else {
                                builder.addParenthesis();
                            }
                            break;
                        case ")":
                            if (isThisStack(paramBrackets, bracketCount)) {
                                paramBrackets.pop();
                                builder.buildParameterBracket();
                                fnRType = true;
                            } else if (isThisStack(lambdaParamBrackets, bracketCount)) {
                                lambdaParamBrackets.pop();
                                builder.buildParameterBracket();
                            } else if (isThisStack(callBrackets, bracketCount)) {
                                callBrackets.pop();
                                builder.buildCall();
                            } else {
                                builder.buildParenthesis(lineFile);
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
                            } else if (conditioning) {
                                conditioning = false;
                                builder.buildConditionTitle();
                                builder.addBraceBlock();
                                condBraces.push(braceCount);
                            } else if (isElse) {
                                isElse = false;
                                builder.addBraceBlock();
                            } else {
                                builder.addIndependenceBraceBlock();
                            }
                            break;
                        case "}":
                            if (isThisStack(funcBodyBraces, braceCount)) {
                                funcBodyBraces.pop();
                                builder.buildBraceBlock();
                                builder.finishFunction(lineFile);
                                builder.finishFunctionOuterBlock();
                            } else if (isThisStack(moduleBraces, braceCount)) {
                                moduleBraces.pop();
                                builder.buildBraceBlock();
                                builder.buildImportModule(lineFile);
                            } else if (isThisStack(classBraces, braceCount)) {
                                classBraces.pop();
                                builder.buildBraceBlock();
                                builder.buildClass(lineFile);
                            } else if (isThisStack(condBraces, braceCount)) {
                                condBraces.pop();
                                builder.buildBraceBlock();
                                builder.buildConditionBody();
                            } else {
                                builder.buildBraceBlock();
                            }
                            if (i < tokens.size() - 1) {
                                Token nextTk = tokens.get(i + 1);
                                if (!(nextTk instanceof IdToken) ||
                                        !((IdToken) nextTk).getIdentifier().equals("else")) {
                                    builder.finishLine();  // fill the end line terminator
                                }
                            }

                            braceCount--;
                            break;
                        case "[":
                            sqrBracketCount++;

                            if (i > 0 && isFuncType(tokens.get(i - 1))) {
                                funcTypeSqrBrackets.push(sqrBracketCount);
                                builder.addSqrBracketBlock();
                            } else {
                                builder.addIndexing(lineFile);
                            }
                            break;
                        case "]":

                            if (isThisStack(funcTypeSqrBrackets, sqrBracketCount)) {
                                funcTypeSqrBrackets.pop();
                                builder.finishSqrBracketBlock();
                            } else {
                                builder.buildIndexing();
                            }
                            sqrBracketCount--;
                            break;
                        case ".":
                            builder.addDot(lineFile);
                            break;
                        case ":":
                            builder.addDeclaration(varLevel, lineFile);
                            break;
                        case "=":
                            builder.addAssignment(lineFile);
                            break;
                        case ":=":
                            builder.addQuickAssignment(lineFile);
                            break;
                        case "->":
                            builder.addFuncTypeNode(lineFile);
                            break;
                        case "<-":
                            builder.addAnonymousClass(lineFile);
                            break;
                        case "++":
                            builder.addIncDecOperator(true, lineFile);
                            break;
                        case "--":
                            builder.addIncDecOperator(false, lineFile);
                            break;
                        case "true":
                            builder.addBoolean(true, lineFile);
                            break;
                        case "false":
                            builder.addBoolean(false, lineFile);
                            break;
                        case "const":
                            varLevel = Declaration.CONST;
                            break;
                        case "if":
                            conditioning = true;
                            builder.addIf(lineFile);
                            break;
                        case "else":
                            builder.addElse(lineFile);
                            isElse = true;
//                            IdToken next = (IdToken) tokens.get(i + 1);
//                            i += 1;
//                            IfStmt ifStmt = builder.getActiveIfStmt();
//
//                            if (next.getIdentifier().equals("{")) {
//                                builder.addBraceBlock();
//                                BlockStmt innermost = builder.getInnermostBlock();
//                                ifStmt.setElseBlock(innermost);
//                                braceCount += 1;
////                            } else if (next.getIdentifier().equals("if")) {
////                                // This part should be identical with the 'if' case
////                                // #####
////                                conditioning = true;
////                                IfStmt ifs = builder.addIf(lineFile);
////                                // #####
////                                ifStmt.setElseBlock(ifs);
//                            } else {
//                                throw new ParseError("Else must follow '{' or 'if'. ", lineFile);
//                            }
                            break;
                        case "while":
                            conditioning = true;
                            builder.addWhile(lineFile);
                            break;
                        case "for":
                            conditioning = true;
                            builder.addFor(lineFile);
                            break;
                        case "break":
                            builder.addBreak(lineFile);
                            break;
                        case "continue":
                            builder.addContinue(lineFile);
                            break;
                        case "lambda":
                            builder.addLambdaHeader(lineFile);
                            lambdaParams = true;
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
                            builder.addBraceBlock();  // give the function a whole block
                            builder.addFunction(fnName, isAbstract, lineFile);
                            isAbstract = false;
                            fnParams = true;
                            break;
                        case "interface":
                            isInterface = true;
                            if (isAbstract) throw new SyntaxError("Illegal combination 'abstract interface'. ",
                                    lineFile);
                        case "class":
                            Token classNameToken = tokens.get(i + 1);
                            i += 1;
                            if (!(classNameToken instanceof IdToken)) {
                                throw new ParseError("Class must have a name. ", lineFile);
                            }
                            String className = ((IdToken) classNameToken).getIdentifier();
                            classHeader = true;
                            builder.addClass(className, isInterface, isAbstract, lineFile);
                            isAbstract = false;
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
                        case "abstract":
                            isAbstract = true;
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
                            if (isAbstract) {
                                throw new SyntaxError("Unexpected token 'abstract'. ", lineFile);
                            }

                            builder.finishPart();
                            if (fnRType) {
                                fnRType = false;
                                builder.addFnRType(lineFile);
                                builder.finishAbstractFunction(lineFile);
                                builder.finishFunctionOuterBlock();
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
                        case "null":
                            builder.addNull(lineFile);
                            break;
                        default:  // name
                            builder.addName(identifier, lineFile);
                            break;
                    }
                }
            } else if (token instanceof IntToken) {
                builder.addInt(((IntToken) token).getValue(), lineFile);
            } else if (token instanceof FloatToken) {
                builder.addFloat(((FloatToken) token).getValue(), lineFile);
            } else if (token instanceof StrToken) {
                builder.addString(((StrToken) token).getLiteral().toCharArray(), lineFile);
            } else if (token instanceof CharToken) {
                builder.addChar(((CharToken) token).getValue(), lineFile);
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
            if (Tokenizer.StringTypes.isIdentifier(identifier) &&
                    !Tokenizer.RESERVED.contains(identifier))
                return true;
            else return identifier.equals(")") || identifier.equals("]");
        }
        return false;
    }

    private static boolean isUnary(Token token) {
        if (token instanceof IdToken) {
            String identifier = ((IdToken) token).getIdentifier();
            switch (identifier) {
                case ";":
                case "=":
                case "->":
                case "(":
                case "[":
                case "{":
                case "}":
                case ".":
                case ",":
                    return true;
                default:
                    return Tokenizer.ALL_BINARY.contains(identifier) ||
                            Tokenizer.RESERVED.contains(identifier);
            }
        } else return !(token instanceof IntToken) && !(token instanceof FloatToken);
    }

    private static boolean isFuncType(Token token) {
        if (token instanceof IdToken) {
            String identifier = ((IdToken) token).getIdentifier();
            if (identifier.equals(")")) return true;
            else if (identifier.equals(":")) return true;
            else return false;
        } else {
            return false;
        }
    }
}
