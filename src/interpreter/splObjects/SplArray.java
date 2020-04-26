package interpreter.splObjects;

import ast.NameNode;
import ast.Node;
import interpreter.AttributeError;
import interpreter.Memory;
import interpreter.env.InstanceEnvironment;
import interpreter.primitives.Int;
import interpreter.primitives.Pointer;
import interpreter.primitives.Primitive;
import interpreter.types.*;
import util.LineFile;

import java.util.Arrays;
import java.util.List;

public class SplArray extends SplObject {

    public final int length;

    public SplArray(int length) {
        this.length = length;
    }

    public TypeValue getAttr(Node attrNode, LineFile lineFile) {
        if (attrNode instanceof NameNode && ((NameNode) attrNode).getName().equals("length")) {
            return new TypeValue(PrimitiveType.TYPE_INT, new Int(length));
        } else {
            throw new AttributeError("Array does not have attribute '" + attrNode + "'. ", lineFile);
        }
    }

    @Override
    public String toString() {
        return "SplArray{" + length + '}';
    }

    public static Pointer createArray(ArrayType arrayType, List<Integer> dimensions, Memory memory) {
        return createArray(arrayType.getEleType(), dimensions, memory, 0);
    }

    private static Pointer createArray(Type eleType, List<Integer> dimensions, Memory memory, int proceedingIndex) {
        int arrSize = dimensions.get(proceedingIndex);
        if (arrSize == -1) {
            return Pointer.NULL_PTR;
        } else {
            Pointer arrPtr = memory.allocate(arrSize + 1);
            SplArray arrIns = new SplArray(arrSize);
            memory.set(arrPtr, arrIns);
            if (proceedingIndex == dimensions.size() - 1) {
                fillInitValue(eleType, arrPtr, memory, arrSize);
            } else{
                int firstEleAddr = arrPtr.getPtr() + 1;
                for (int i = 0; i < arrSize; ++i) {
                    Pointer innerArrPtr = createArray(((ArrayType) eleType).getEleType(),
                            dimensions,
                            memory,
                            proceedingIndex + 1);
                    memory.set(firstEleAddr + i, new ReadOnlyPrimitiveWrapper(innerArrPtr));
                }
            }
            return arrPtr;
        }
    }

    public static void fillInitValue(Type elementType, Pointer arrayPtr, Memory memory, int arrayLength) {
        int firstEleAddr = arrayPtr.getPtr() + 1;
        ReadOnlyPrimitiveWrapper wrapper;
        if (elementType.isPrimitive()) {
            PrimitiveType primitiveType = (PrimitiveType) elementType;
            switch (primitiveType.type) {
                case Primitive.INT:
                    wrapper = ReadOnlyPrimitiveWrapper.INT_ZERO_WRAPPER;
                    break;
                case Primitive.FLOAT:
                    wrapper = ReadOnlyPrimitiveWrapper.FLOAT_ZERO_WRAPPER;
                    break;
                case Primitive.BOOLEAN:
                    wrapper = ReadOnlyPrimitiveWrapper.BOOLEAN_FALSE_WRAPPER;
                    break;
                case Primitive.CHAR:
                    wrapper = ReadOnlyPrimitiveWrapper.CHAR_ZERO_WRAPPER;
                    break;
                default:
                    throw new TypeError();
            }
        } else {
            wrapper = ReadOnlyPrimitiveWrapper.NULL_WRAPPER;
        }
        for (int i = 0; i < arrayLength; ++i) {
            memory.set(firstEleAddr + i, wrapper);
        }
    }
}
