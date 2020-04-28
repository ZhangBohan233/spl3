package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.env.Environment;
import interpreter.primitives.Char;
import interpreter.primitives.Int;
import interpreter.primitives.Pointer;
import interpreter.splObjects.Instance;
import interpreter.splObjects.SplArray;
import interpreter.types.ArrayType;
import interpreter.types.ClassType;
import interpreter.types.PrimitiveType;
import interpreter.types.TypeValue;
import util.LineFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringLiteral extends LiteralNode {

    private final char[] charArray;

    public StringLiteral(char[] charArray, LineFile lineFile) {
        super(lineFile);

        this.charArray = charArray;
    }

    @Override
    public TypeValue evaluate(Environment env) {
        // create spl char array
        ArrayType arrayType = new ArrayType(PrimitiveType.TYPE_CHAR);
        Pointer arrPtr = SplArray.createArray(arrayType, List.of(charArray.length), env.getMemory());
        for (int i = 0; i < charArray.length; ++i) {
            Char c = new Char(charArray[i]);
            SplArray.setItemAtIndex(
                    arrPtr,
                    i,
                    arrayType,
                    new TypeValue(PrimitiveType.TYPE_CHAR, c),
                    env,
                    getLineFile()
            );
        }
        TypeValue arrTv = new TypeValue(arrayType, arrPtr);

        // create String instance
        NameNode clazzNode = new NameNode("String", getLineFile());
        ClassType classType = (ClassType) clazzNode.evalType(env);
        Instance.InstanceTypeValue instanceTv = Instance.createInstanceAndAllocate(classType, env, getLineFile());

        Instance.callInit(instanceTv.instance, new TypeValue[]{arrTv}, env, getLineFile());

        return instanceTv.typeValue;
    }

    @Override
    public Node preprocess(FakeEnv env) {
        return this;
    }

    @Override
    public String toString() {
        return "StringLiteral{" + new String(charArray) + "}";
    }
}
