package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.ClassEnvironment;
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

    private String className;
    private Implements implementations;
    private TypeRepresent superclass;
    private boolean isInterface;
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
    public TypeValue evaluate(Environment env) {
        validateExtending();

        ClassType superclassPointer;
        SplClass superclassObj;
        ClassEnvironment superEnv;
        if (superclass == null) {
            superclassPointer = null;
            superclassObj = null;
            superEnv = null;
        } else {
            superclassPointer = (ClassType) superclass.evalType(env);
            superclassObj = (SplClass) env.getMemory().get(superclassPointer.getClazzPointer());
            superEnv = superclassObj.getClassBaseEnv();
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

        ClassEnvironment classEnvironment = new ClassEnvironment(env, superEnv);
        // TODO: inheritance
//        for (ClassType ct : interfacePointers) {
//            SplClass superclass = (SplClass) env.getMemory().get(ct.getClazzPointer());
//            superclass.getClassBaseEnv().inherit(classEnvironment);
//        }

        // attributes are evaluated when class created
        // During instance creation, copies the whole class environment to the instance environment
        body.evaluate(classEnvironment);

        // TODO: check implementations

        SplClass clazz = new SplClass(className, superclassPointer, interfacePointers, classEnvironment);
        Pointer clazzPtr = env.getMemory().allocate(1);
        env.getMemory().set(clazzPtr, clazz);
        ClassType clazzType = new ClassType(clazzPtr);

        env.defineVar(className, clazzType, getLineFile());
        TypeValue typeValue = new TypeValue(clazzType, clazzPtr);

        env.setVar(className, typeValue);

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