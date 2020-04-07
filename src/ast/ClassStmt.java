package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.SplException;
import interpreter.env.ClassEnvironment;
import interpreter.env.Environment;
import interpreter.types.TypeValue;
import interpreter.primitives.Pointer;
import interpreter.splObjects.SplClass;
import interpreter.types.ClassType;
import util.LineFile;

import java.util.ArrayList;
import java.util.List;

public class ClassStmt extends Node {

    private String className;
    private Extends superclasses;
    private BlockStmt body;

    public ClassStmt(String className, LineFile lineFile) {
        super(lineFile);

        this.className = className;
    }

    public void setBody(BlockStmt body) {
        this.body = body;
    }

    public void setSuperclasses(Extends superclasses) {
        this.superclasses = superclasses;
    }

    private void validateExtending() {
        if (superclasses == null) {
            Line line = new Line();  // TODO
//            if (!className.equals("Object"))
//                line.getChildren().add(new NameNode("Object", getLineFile()));
            superclasses = new Extends(line);
        }
    }

    @Override
    public TypeValue evaluate(Environment env) {
        validateExtending();
        List<ClassType> superclassPointers = new ArrayList<>();
        for (Node node : superclasses.getExtending().getChildren()) {
            if (node instanceof TypeRepresent) {
                ClassType t = (ClassType) ((TypeRepresent) node).evalType(env);
                superclassPointers.add(t);
            } else {
                throw new SplException();
            }
        }

        ClassEnvironment classEnvironment = new ClassEnvironment(env);
        // TODO: inheritance
        // attributes are evaluated when class created
        // During instance creation, copies the whole class environment to the instance environment
        body.evaluate(classEnvironment);

        SplClass clazz = new SplClass(className, superclassPointers, classEnvironment);
        Pointer clazzPtr = env.getMemory().allocate(1);
        env.getMemory().set(clazzPtr, clazz);
        ClassType clazzType = new ClassType(clazzPtr);

        env.defineVar(className, clazzType);
        TypeValue typeValue = new TypeValue(clazzType, clazzPtr);

        env.setVar(className, typeValue);

        return typeValue;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return null;
    }
}
