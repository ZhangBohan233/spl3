package parser;

import ast.*;
import util.LineFile;
import util.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AstBuilder {

    private static final Map<String, Integer> PCD_BIN_NUMERIC = Map.of(
            "*", 100,
            "/", 100,
            "%", 100,
            "+", 50,
            "-", 50
    );

    private static final Map<String, Integer> PCD_BIN_LOGICAL = Map.of(
            ">", 25,
            "<", 25,
            ">=", 25,
            "<=", 25,
            "==", 20
    );

    private static final Map<String, Integer> PCD_BIN_SPECIAL = Map.of(
            ".", 500,
            "call", 400,
            ":", 3,
            "=", 1
    );

    private static final Map<String, Integer> PCD_UNARY_SPECIAL = Map.of(
            "new", 150,
            "return", 0
    );

    private static final Map<String, Integer> PRECEDENCES = Utilities.mergeMaps(
            PCD_BIN_NUMERIC, PCD_BIN_LOGICAL, PCD_BIN_SPECIAL,
            PCD_UNARY_SPECIAL
    );

    private BlockStmt baseBlock = new BlockStmt();
    private List<Node> stack = new ArrayList<>();
    private Line activeLine = new Line();
    private AstBuilder inner;

    void addName(String name, LineFile lineFile) {
        if (inner == null) {
            stack.add(new NameNode(name, lineFile));
        } else {
            inner.addName(name, lineFile);
        }
    }

    void addPrimitiveTypeName(String name, LineFile lineFile) {
        if (inner == null) {
            stack.add(new PrimitiveTypeNameNode(name, lineFile));
        } else {
            inner.addPrimitiveTypeName(name, lineFile);
        }
    }

    void addInt(long value, LineFile lineFile) {
        if (inner == null) {
            stack.add(new IntNode(value, lineFile));
        } else {
            inner.addInt(value, lineFile);
        }
    }

    void addFloat(double value, LineFile lineFile) {

    }

    void addAssignment(LineFile lineFile) {
        if (inner == null) {
            stack.add(new Assignment(lineFile));
        } else {
            inner.addAssignment(lineFile);
        }
    }

    void addBinaryOperator(String op, int type, LineFile lineFile) {
        if (inner == null) {
            stack.add(new BinaryOperator(op, type, lineFile));
        } else {
            inner.addBinaryOperator(op, type, lineFile);
        }
    }

    void addUnaryOperator(String op, LineFile lineFile) {
//        if (inner == null) {
//            stack.add(new )
//        }
    }

    void addReturnStmt(LineFile lineFile) {
        if (inner == null) {
            stack.add(new ReturnStmt(lineFile));
        } else {
            inner.addReturnStmt(lineFile);
        }
    }

    void addNewStmt(LineFile lineFile) {
        if (inner == null) {
            stack.add(new NewStmt(lineFile));
        } else {
            inner.addNewStmt(lineFile);
        }
    }

    void addDeclaration(int level, LineFile lineFile) {
        if (inner == null) {
            stack.add(new Declaration(level, lineFile));
        } else {
            inner.addDeclaration(level, lineFile);
        }
    }

    void addDot(LineFile lineFile) {
        if (inner == null) {
            stack.add(new Dot(lineFile));
        } else {
            inner.addDot(lineFile);
        }
    }

    void addFunction(String name, LineFile lineFile) {
        if (inner == null) {
            FuncDefinition funcDefinition = new FuncDefinition(name, lineFile);
            stack.add(funcDefinition);
        } else {
            inner.addFunction(name, lineFile);
        }
    }

    void addParameterBracket() {
        if (inner == null) {
            inner = new AstBuilder();
        } else {
            inner.addParameterBracket();
        }
    }

    void buildParameterBracket() {
        if (inner.inner == null) {
            Node lastNode = stack.get(stack.size() - 1);
            if (!(lastNode instanceof FuncDefinition))
                throw new ParseError("Unexpected syntax. ", lastNode.getLineFile());
            FuncDefinition defNode = (FuncDefinition) lastNode;
            inner.finishPart();
            Line line = inner.getLine();
            defNode.setParameters(line);
            inner = null;
        } else {
            inner.buildParameterBracket();
        }
    }

    void addFnRType(LineFile lineFile) {
        if (inner == null) {
            finishPart();
            if (activeLine.getChildren().size() != 2) {
                System.err.println(activeLine.getChildren());
                throw new ParseError("Function must have a return type. ", lineFile);
            }
            FuncDefinition def = (FuncDefinition) activeLine.getChildren().get(0);
            Node rType = activeLine.getChildren().remove(1);
            def.setRType(rType);
        } else {
            inner.addFnRType(lineFile);
        }
    }

    void addCall(LineFile lineFile) {
        if (inner == null) {
            stack.add(new FuncCall(lineFile));
            inner = new AstBuilder();
        } else {
            inner.addCall(lineFile);
        }
    }

    void buildCall() {
        if (inner.inner == null) {
            inner.finishPart();
            Line line = inner.getLine();
            inner = null;
            Arguments arguments = new Arguments(line);
            stack.add(arguments);
        } else {
            inner.buildCall();
        }
    }

    void addBraceBlock() {
        if (inner == null) {
            inner = new AstBuilder();
        } else {
            inner.addBraceBlock();
        }
    }

    void buildBraceBlock() {
        if (inner.inner == null) {
            BlockStmt innerBlock = inner.getBaseBlock();
            stack.add(innerBlock);

            inner = null;
        } else {
            inner.buildBraceBlock();
        }
    }

    void addImportModule(String importName, LineFile lineFile) {
        if (inner == null) {
            ImportStmt stmt = new ImportStmt(importName, lineFile);
            stack.add(stmt);
        } else {
            inner.addImportModule(importName, lineFile);
        }
    }

    void buildImportModule(LineFile lineFile) {
        if (inner == null) {
            finishPart();
            if (activeLine.getChildren().size() != 2) {
                throw new ParseError("Function must have a return type. ", lineFile);
            }
            ImportStmt importStmt = (ImportStmt) activeLine.getChildren().get(0);
            BlockStmt blockStmt = (BlockStmt) activeLine.getChildren().remove(1);
            importStmt.setContent(blockStmt);
            finishLine();
        } else {
            inner.buildImportModule(lineFile);
        }
    }

    void addClass(String className, LineFile lineFile) {
        if (inner == null) {
            stack.add(new ClassStmt(className, lineFile));
        } else {
            inner.addClass(className, lineFile);
        }
    }

    void buildClass(LineFile lineFile) {
        if (inner == null) {
            finishPart();
            if (activeLine.getChildren().size() != 2)
                throw new ParseError("Class must have a body. ", lineFile);
            ClassStmt cs = (ClassStmt) activeLine.getChildren().get(0);
            BlockStmt body = (BlockStmt) activeLine.getChildren().remove(1);
            cs.setBody(body);
            finishLine();
//            System.out.println(6666);
        } else {
            inner.buildClass(lineFile);
        }
    }

    void addExtends() {
        if (inner == null) {
            inner = new AstBuilder();
        } else {
            inner.addExtends();
        }
    }

    void finishExtends() {
        if (inner.inner == null) {
            inner.finishPart();
            Line line = inner.getLine();
            inner = null;
            Extends extending = new Extends(line);
            ClassStmt clazz = (ClassStmt) stack.get(stack.size() - 1);
            clazz.setSuperclasses(extending);
        } else {
            inner.finishExtends();
        }
    }

    void finishFunction(LineFile lineFile) {
        if (inner == null) {
            finishPart();
            if (activeLine.getChildren().size() != 2)
                throw new ParseError("Non-abstract Function must have a body. ", lineFile);
            FuncDefinition def = (FuncDefinition) activeLine.getChildren().get(0);
            BlockStmt body = (BlockStmt) activeLine.getChildren().remove(1);
            def.setBody(body);
            finishLine();
        } else {
            inner.finishFunction(lineFile);
        }
    }

    void finishLine() {
        if (inner == null) {
            baseBlock.addLine(activeLine);
            activeLine = new Line();
        } else {
            inner.finishLine();
        }
    }

    Line getLine() {
        return activeLine;
    }

    BlockStmt getBaseBlock() {
        return baseBlock;
    }

    void finishPart() {
        if (inner == null) {
            if (!stack.isEmpty()) {
                boolean hasExpr = false;
                for (Node node : stack) {
                    if (node instanceof Expr && ((Expr) node).notFulfilled()) hasExpr = true;
                }
                if (hasExpr) {
                    buildExpr(stack);
                }
                activeLine.getChildren().addAll(stack);
                stack.clear();
            }
        } else {
            inner.finishPart();
        }
    }

    void buildExpr(List<Node> list) {
        try {
            while (true) {
//                System.out.println(list);
                int maxPre = -1;
                int index = 0;

                for (int i = 0; i < list.size(); ++i) {
                    Node node = list.get(i);
                    if (node instanceof Expr && ((Expr) node).notFulfilled()) {
                        if (node instanceof UnaryExpr) {
                            int pre = PRECEDENCES.get(((UnaryExpr) node).getOperator());
                            if (pre > maxPre) {
                                maxPre = pre;
                                index = i;
                            }
                        } else if (node instanceof BinaryExpr) {
                            int pre = PRECEDENCES.get(((BinaryExpr) node).getOperator());
                            if (pre > maxPre) {
                                maxPre = pre;
                                index = i;
                            }
                        }
                    }
                }

                if (maxPre == -1) break;  // no expr found

                Expr expr = (Expr) list.get(index);
                if (expr instanceof UnaryExpr) {
                    if (((UnaryExpr) expr).atLeft) {
                        Node value = list.remove(index + 1);
                        ((UnaryExpr) expr).setValue(value);
                    } else {
                        Node value = list.remove(index - 1);
                        ((UnaryExpr) expr).setValue(value);
                    }
                } else if (expr instanceof BinaryExpr) {
                    Node right = list.remove(index + 1);
                    Node left = list.remove(index - 1);
                    ((BinaryExpr) expr).setLeft(left);
                    ((BinaryExpr) expr).setRight(right);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Error when " + list);
            throw e;
        }
    }
}
