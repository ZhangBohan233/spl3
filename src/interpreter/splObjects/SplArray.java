package interpreter.splObjects;

import ast.NameNode;
import ast.Node;
import interpreter.AttributeError;
import interpreter.Memory;
import interpreter.SplException;
import interpreter.env.Environment;
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

    public static Primitive getItemAtIndex(Pointer arrPtr, int index, Environment env, LineFile lineFile) {
        SplArray array = (SplArray) env.getMemory().get(arrPtr);
        if (index < 0 || index >= array.length) {
            throw new SplException("Index " + index + " out of array length " + array.length + ". ", lineFile);
        }
        ReadOnlyPrimitiveWrapper wrapper = (ReadOnlyPrimitiveWrapper) env.getMemory().get(arrPtr.getPtr() + index + 1);
        return wrapper.value;
    }

    public static void setItemAtIndex(Pointer arrPtr,
                                      int index,
                                      ArrayType arrayType,
                                      TypeValue valueTv,
                                      Environment env,
                                      LineFile lineFile) {
        Type arrEleType = arrayType.getEleType();
        if (arrEleType.isSuperclassOfOrEquals(valueTv.getType(), env)) {
            SplArray array = (SplArray) env.getMemory().get(arrPtr);
            if (index < 0 || index >= array.length) {
                throw new SplException("Index " + index + " out of array length " + array.length + ". ", lineFile);
            }
            env.getMemory().set(arrPtr.getPtr() + index + 1, new ReadOnlyPrimitiveWrapper(valueTv.getValue()));
        } else {
            throw new TypeError(String.format("Array element type: %s, argument type: %s. ",
                    arrEleType, valueTv.getType()));
        }
    }

    public static char[] toJavaCharArray(TypeValue arrayTv, Memory memory) {
        int[] lenPtr = toJavaArrayCommon(arrayTv, memory);

        char[] javaCharArray = new char[lenPtr[0]];

        for (int j = 0; j < javaCharArray.length; ++j) {
            ReadOnlyPrimitiveWrapper wrapper = (ReadOnlyPrimitiveWrapper) memory.get(lenPtr[1] + j);
            javaCharArray[j] = (char) wrapper.value.intValue();
        }

        return javaCharArray;
    }

    private static int[] toJavaArrayCommon(TypeValue arrayTv, Memory memory) {
        if (((PointerType) arrayTv.getType()).getPointerType() != PointerType.ARRAY_TYPE) {
            throw new TypeError("Cannot convert to java array. ");
        }
        ArrayType arrayType = (ArrayType) arrayTv.getType();
        if (!arrayType.getEleType().isPrimitive()) {
            throw new TypeError("Only primitive array can be converted to java array. ");
        }
        Pointer arrPtr = (Pointer) arrayTv.getValue();
        SplArray charArray = (SplArray) memory.get(arrPtr);

        int firstEleAddr = arrPtr.getPtr() + 1;

        // returns array length and addr of first element
        return new int[]{charArray.length, firstEleAddr};
    }
}
