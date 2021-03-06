package interpreter.types;

import ast.NameNode;
import interpreter.env.Environment;
import interpreter.primitives.Bool;
import interpreter.primitives.Pointer;
import interpreter.splObjects.Instance;
import interpreter.splObjects.SplClass;
import interpreter.types.Type;
import interpreter.primitives.Primitive;
import util.LineFile;

import java.util.Objects;

public class TypeValue {

    public static final TypeValue VOID = new TypeValue(PrimitiveType.TYPE_VOID);
    public static final TypeValue POINTER_NULL = new TypeValue(NullType.NULL_TYPE, Pointer.NULL_PTR);
    public static final TypeValue BOOL_TRUE = new TypeValue(PrimitiveType.TYPE_BOOLEAN, Bool.TRUE);
    public static final TypeValue BOOL_FALSE = new TypeValue(PrimitiveType.TYPE_BOOLEAN, Bool.FALSE);
    public static final TypeValue INTERRUPTED = new TypeValue(PrimitiveType.TYPE_VOID, Pointer.NULL_PTR);

    private final Type type;
    private Primitive value;

    public TypeValue(Type type) {
        this.type = type;
    }

    public TypeValue(Type type, Primitive value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public Primitive getValue() {
        return value;
    }

    public void setValue(Primitive value) {
        this.value = value;
    }

    public TypeValue copy() {
        return new TypeValue(type, value);
    }

    @Override
    public String toString() {
        return "{" + value + ": " + type + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeValue typeValue = (TypeValue) o;

        if (!Objects.equals(type, typeValue.type)) return false;
        return Objects.equals(value, typeValue.value);
    }

    @Override
    public int hashCode() {
        if (value == null) {
            return super.hashCode();
        } else {
            return value.hashCode();
        }
    }

    public static TypeValue convertPrimitiveToWrapper(TypeValue primitiveTv,
                                                      Environment env,
                                                      LineFile lineFile) {
        PrimitiveType pt = (PrimitiveType) primitiveTv.getType();
        TypeValue wrapperClazzTv;
        switch (pt.type) {
            case Primitive.INT:
                wrapperClazzTv = env.get("Integer", lineFile);
                break;
            case Primitive.CHAR:
                wrapperClazzTv = env.get("Character", lineFile);
                break;
            case Primitive.FLOAT:
                wrapperClazzTv = env.get("Float", lineFile);
                break;
            case Primitive.BOOLEAN:
                wrapperClazzTv = env.get("Boolean", lineFile);
                break;
            default:
                throw new TypeError("Unexpected primitive type. ", lineFile);
        }
        ClassType wrapperClazz = (ClassType) wrapperClazzTv.getType();
        Instance.InstanceTypeValue instanceTv = Instance.createInstanceAndAllocate(wrapperClazz, env, lineFile);

        Instance.callInit(instanceTv.instance, new TypeValue[]{primitiveTv}, env, lineFile);
        return instanceTv.typeValue;
    }

    public static TypeValue convertWrapperToPrimitive(TypeValue wrapperTv, Environment env, LineFile lineFile) {
        if (!(wrapperTv.getType() instanceof ClassType)) {
            throw new TypeError("Unexpected type. ", lineFile);
        }
        ClassType classType = (ClassType) wrapperTv.getType();
        Instance wrapperInst = (Instance) env.getMemory().get((Pointer) wrapperTv.getValue());


        ClassType integerType = (ClassType) new NameNode("Integer", lineFile).evalType(env);
        if (integerType.isSuperclassOfOrEquals(classType, env)) {
            return wrapperInst.getEnv().get("value", lineFile);
        }
        ClassType charType = (ClassType) new NameNode("Character", lineFile).evalType(env);
        if (charType.isSuperclassOfOrEquals(classType, env)) {
            return wrapperInst.getEnv().get("value", lineFile);
        }
        ClassType booleanType = (ClassType) new NameNode("Boolean", lineFile).evalType(env);
        if (booleanType.isSuperclassOfOrEquals(classType, env)) {
            return wrapperInst.getEnv().get("value", lineFile);
        }
        ClassType floatType = (ClassType) new NameNode("Float", lineFile).evalType(env);
        if (floatType.isSuperclassOfOrEquals(classType, env)) {
            return wrapperInst.getEnv().get("value", lineFile);
        }

        throw new TypeError("Cannot convert pointer type '" + classType.toStringClass(env.getMemory()) +
                "' to primitive. ", lineFile);
    }
}
