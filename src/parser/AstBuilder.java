package parser;

import ast.*;
import util.LineFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AstBuilder {

    private static final Map<String, Integer> PRECEDENCES = Map.of(
            ".", 500,
            "+", 50,
            "-", 50,

            "=", 1,
            ":", 3
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

    void addBinaryOperator(String op, LineFile lineFile) {
        if (inner == null) {
            stack.add(new BinaryOperator(op, lineFile));
        } else {
            inner.addBinaryOperator(op, lineFile);
        }
    }

    void addDeclaration(int level, LineFile lineFile) {
        if (inner == null) {
            stack.add(new Declaration(level, lineFile));
        } else {
            inner.addDeclaration(level, lineFile);
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

    void addFnRType() {
        if (inner == null) {
            finishPart();
            if (activeLine.getChildren().size() != 2)
                throw new ParseError("Function must have a return type. ", activeLine.getLineFile());
            FuncDefinition def = (FuncDefinition) activeLine.getChildren().get(0);
            Node rType = activeLine.getChildren().remove(1);
            def.setRType(rType);
        } else {
            inner.addFnRType();
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

    void finishFunction() {
        if (inner == null) {
            finishPart();
            if (activeLine.getChildren().size() != 2)
                throw new ParseError("Non-abstract Function must have a body. ", activeLine.getLineFile());
            FuncDefinition def = (FuncDefinition) activeLine.getChildren().get(0);
            BlockStmt body = (BlockStmt) activeLine.getChildren().remove(1);
            def.setBody(body);
            finishLine();
        } else {
            inner.finishFunction();
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
                int maxPre = 0;
                int index = 0;

                for (int i = 0; i < list.size(); ++i) {
                    Node node = list.get(i);
                    if (node instanceof BinaryExpr && ((BinaryExpr) node).notFulfilled()) {
                        int pre = PRECEDENCES.get(((BinaryExpr) node).getOperator());
                        if (pre > maxPre) {
                            maxPre = pre;
                            index = i;
                        }
                    }
                }

                if (maxPre == 0) break;  // no expr found

                Expr expr = (Expr) list.get(index);
                if (expr instanceof BinaryExpr) {
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
