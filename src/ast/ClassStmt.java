package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.Environment;
import interpreter.types.Type;
import interpreter.types.TypeValue;
import interpreter.primitives.Pointer;
import interpreter.splObjects.SplClass;
import interpreter.types.ClassType;
import parser.ParseError;
import util.LineFile;

import java.util.ArrayList;
import java.util.List;

public class ClassStmt extends Node {

    private final String className;
    private Implements implementations;
    private TypeRepresent superclass;
    private final boolean isInterface;
    private boolean isAbstract;
    private BlockStmt body;

    public ClassStmt(String className, boolean isInterface, LineFile lineFile) {
        super(lineFile);

        this.className = className;
        this.isInterface = isInterface;
    }

    public void setBody(BlockStmt body) {
        this.body = body;
    }

    public void setImplements(Implements implementations) {
        this.implementations = implementations;
    }

    public void setSuperclass(Node extendNode) {
        if (extendNode instanceof Extends) {
            superclass = ((Extends) extendNode).getValue();
        } else {
            throw new ParseError("Superclass must be a class. ", getLineFile());
        }
    }

    private void validateExtending() {
        if (superclass == null) {
            if (!className.equals("Object"))
                superclass = new NameNode("Object", getLineFile());
        }
        if (implementations == null) {
            implementations = new Implements(new Line());
        }
    }

    @Override
    protected TypeValue internalEval(Environment env) {

        validateExtending();

        ClassType superclassPointer;
        if (superclass == null) {
            superclassPointer = null;
        } else {
            superclassPointer = (ClassType) superclass.evalType(env);
        }

        List<ClassType> interfacePointers = new ArrayList<>();
        for (Node node : implementations.getExtending().getChildren()) {
            if (node instanceof TypeRepresent) {
                ClassType t = (ClassType) ((TypeRepresent) node).evalType(env);
                interfacePointers.add(t);
            } else {
                throw new SplException();
            }
        }

        // TODO: check implementations

        SplClass clazz = new SplClass(className, superclassPointer, interfacePointers, body, env);
        Pointer clazzPtr = env.getMemory().allocate(1, env);
        env.getMemory().set(clazzPtr, clazz);
        ClassType clazzType = new ClassType(clazzPtr);

        env.defineVar(className, clazzType, getLineFile());
        TypeValue typeValue = new TypeValue(clazzType, clazzPtr);

        env.setVar(className, typeValue, getLineFile());

        return typeValue;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }

    @Override
    public String toString() {
        String title = isInterface ? "Interface" : "Class";
        return String.format("%s %s extends %s implements %s", title, className, superclass, implementations);
    }
}
