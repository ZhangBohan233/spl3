package ast;

import ast.fakeEnv.FakeEnv;
import interpreter.Memory;
import interpreter.env.Environment;
import interpreter.primitives.Char;
import interpreter.primitives.Pointer;
import interpreter.splObjects.Instance;
import interpreter.splObjects.SplArray;
import interpreter.types.ArrayType;
import interpreter.types.ClassType;
import interpreter.types.PrimitiveType;
import interpreter.types.TypeValue;
import util.LineFile;

import java.util.List;

public class StringLiteral extends LiteralNode {

    private final char[] charArray;

    public StringLiteral(char[] charArray, LineFile lineFile) {
        super(lineFile);

        this.charArray = charArray;
    }

    @Override
    protected TypeValue internalEval(Environment env) {
        return createStringOneStep(charArray, env, getLineFile());
    }

    public static TypeValue createStringOneStep(char[] charArray, Environment env, LineFile lineFile) {
        // create spl char array
        TypeValue arrTv = createCharArrayAndAllocate(charArray, env, lineFile);

        // create String instance
        return createStringInstance(arrTv, env, lineFile);
    }

    public static TypeValue createCharArrayAndAllocate(char[] charArray, Environment env, LineFile lineFile) {
        ArrayType arrayType = new ArrayType(PrimitiveType.TYPE_CHAR);
        Pointer arrPtr = SplArray.createArray(arrayType, List.of(charArray.length), env);
        for (int i = 0; i < charArray.length; ++i) {
            Char c = new Char(charArray[i]);
            SplArray.setItemAtIndex(
                    arrPtr,
                    i,
                    arrayType,
                    new TypeValue(PrimitiveType.TYPE_CHAR, c),
                    env,
                    lineFile
            );
        }
        return new TypeValue(arrayType, arrPtr);
    }

    public static TypeValue createStringInstance(TypeValue charArrTv, Environment env, LineFile lineFile) {
        NameNode clazzNode = new NameNode("String", lineFile);
        ClassType classType = (ClassType) clazzNode.evalType(env);
        Instance.InstanceTypeValue instanceTv = Instance.createInstanceAndAllocate(classType, env, lineFile);

        Instance.callInit(instanceTv.instance, new TypeValue[]{charArrTv}, env, lineFile);

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
