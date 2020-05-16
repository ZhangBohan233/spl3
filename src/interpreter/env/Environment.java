package interpreter.env;

import interpreter.*;
import interpreter.types.ClassType;
import interpreter.types.Type;
import interpreter.types.TypeError;
import interpreter.types.TypeValue;
import util.LineFile;
import util.Utilities;

import java.util.*;

public abstract class Environment {

    public final Environment outer;
    protected Memory memory;

    protected Map<String, TypeValue> variables = new HashMap<>();
    protected Map<String, TypeValue> constants = new HashMap<>();

    public Environment(Memory memory, Environment outer) {
        this.memory = memory;
        this.outer = outer;
    }

    public Memory getMemory() {
        return memory;
    }

    public abstract void defineFunction(String name, TypeValue funcTv, LineFile lineFile);

    public abstract boolean isSub();

    public abstract void setReturn(TypeValue typeValue);

    public abstract boolean interrupted();

    public abstract void breakLoop();

    public abstract void resumeLoop();

    public abstract void pauseLoop();

    public abstract void invalidate();

    public Set<TypeValue> attributes() {
        Set<TypeValue> set = new HashSet<>();
        set.addAll(constants.values());
        set.addAll(variables.values());

        return set;
    }

    public void defineVar(String name, Type type, LineFile lineFile) {
        if (localHasName(name, lineFile))
            throw new EnvironmentError("Variable '" + name + "' already defined. ", lineFile);

        TypeValue typeValue = new TypeValue(type);
        variables.put(name, typeValue);
    }

    public void defineVarAndSet(String name, TypeValue typeValue, LineFile lineFile) {
        if (localHasName(name, lineFile))
            throw new EnvironmentError("Variable '" + name + "' already defined. ", lineFile);

        variables.put(name, typeValue);
    }

    public void defineConst(String name, Type type, LineFile lineFile) {
        if (localHasName(name, lineFile))
            throw new EnvironmentError("Constant '" + name + "' already defined. ", lineFile);

        TypeValue typeValue = new TypeValue(type);
        constants.put(name, typeValue);
    }

    public void defineConstAndSet(String name, TypeValue typeValue, LineFile lineFile) {
        if (localHasName(name, lineFile))
            throw new EnvironmentError("Constant '" + name + "' already defined. ", lineFile);

        constants.put(name, typeValue);
    }

    public void setVar(String name, TypeValue newTypeValue, LineFile lineFile) {
        TypeValue typeValue = innerGet(name, true, false, lineFile);
        if (typeValue == null)
            throw new EnvironmentError("Variable '" + name + "' is not defined in this scope. ", lineFile);

        if (typeCheck(typeValue.getType(), newTypeValue.getType())) {
            typeValue.setValue(newTypeValue.getValue());
        } else {
            String ntvStr = newTypeValue.getType() instanceof ClassType ?
                    ((ClassType) newTypeValue.getType()).toStringClass(memory) : newTypeValue.toString();
            String otvStr = typeValue.getType() instanceof ClassType ?
                    ((ClassType) typeValue.getType()).toStringClass(memory) : typeValue.toString();
            throw new TypeError("Cannot convert type '" + ntvStr +
                    "' to type '" + otvStr + "'. ", lineFile);
        }
    }

    public TypeValue get(String name, LineFile lineFile) {
        TypeValue tv = innerGet(name, true, true, lineFile);
        if (tv == null) {
            throw new EnvironmentError("Name '" + name + "' not found. ", lineFile);
        }
        return tv;
    }

    public boolean hasName(String name, LineFile lineFile) {
        return innerGet(name, true, true, lineFile) != null;
    }

    /**
     * Get a type value pair by name.
     * <p>
     * Note that this method is overridden in {@code InstanceEnvironment}
     *
     * @param name         the name
     * @param isFirst      whether this is called by another function. {@code false} if this call is self recursion
     * @param includeConst whether allowing to set the uninitialized constants
     * @param lineFile     line and file for error information
     * @return the type value
     */
    protected TypeValue innerGet(String name, boolean isFirst, boolean includeConst, LineFile lineFile) {
        TypeValue tv = constants.get(name);
        if (!includeConst && tv != null && tv.getValue() != null) {
            throw new EnvironmentError("Constant '" + name + "' is not assignable. ", lineFile);
        }
        if (tv == null) tv = variables.get(name);
        if (tv == null) {
            if (outer != null) {
                tv = outer.innerGet(name, false, includeConst, lineFile);
            }
            if (isFirst && tv == null) {
                tv = searchInNamespaces(name);
            }
        }
        return tv;
    }

    protected TypeValue localInnerGet(String name, LineFile lineFile) {
        TypeValue tv = constants.get(name);
        if (tv == null) tv = variables.get(name);
        if (tv == null) {
            if (outer != null && outer.isSub()) {
                tv = outer.localInnerGet(name, lineFile);
            }
        }
        return tv;
    }

    protected final boolean localHasName(String name, LineFile lineFile) {
        return localInnerGet(name, lineFile) != null;
    }

    public void printVars() {
        System.out.println(variables);
    }

    private boolean typeCheck(Type inStock, Type assignment) {
        return inStock.isSuperclassOfOrEquals(assignment, this);
    }

    public abstract void addNamespace(ModuleEnvironment moduleEnvironment);

    protected abstract TypeValue searchInNamespaces(String name);

    protected abstract void setInNamespaces(String name, TypeValue typeValue);
}
