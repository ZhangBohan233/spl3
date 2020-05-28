package interpreter.env;

import interpreter.Memory;
import interpreter.primitives.Pointer;
import interpreter.splObjects.Instance;
import interpreter.types.TypeValue;
import util.LineFile;

public class InstanceEnvironment extends MainAbstractEnvironment {

    /**
     * Store the reference to the environment where this instance is created. Used only for gc.
     */
    public final Environment creationEnvironment;
    private final String className;

    public InstanceEnvironment(String className, Environment definitionEnv, Environment creationEnvironment) {
        super(definitionEnv.getMemory(), definitionEnv);

        this.className = className;
        this.creationEnvironment = creationEnvironment;
    }

    public void directDefineConstAndSet(String name, TypeValue typeValue) {
        constants.put(name, typeValue);
    }

    @Override
    public boolean interrupted() {
        return false;
    }

    @Override
    protected TypeValue innerGet(String name, boolean isFirst, boolean includeConst, LineFile lineFile) {
        TypeValue tv = searchSuper(name, includeConst, lineFile);
        if (tv == null)
            return super.innerGet(name, isFirst, includeConst, lineFile);
        else return tv;
    }

    private TypeValue searchSuper(String name, boolean includeConst, LineFile lineFile) {
        TypeValue tv = constants.get(name);
        if (!includeConst && tv != null && tv.getValue() != null) {
            throw new EnvironmentError("Constant '" + name + "' is not assignable. ", lineFile);
        }
        if (tv == null) tv = variables.get(name);
        if (tv == null) {
            TypeValue superTv = constants.get("super");
            if (superTv == null) return null;
            else {
                Instance instance = (Instance) getMemory().get((Pointer) superTv.getValue());
                return instance.getEnv().searchSuper(name, includeConst, lineFile);
            }
        } else return tv;
    }

    public boolean selfContains(String name) {
        return variables.containsKey(name) || constants.containsKey(name);
    }

    @Override
    public String toString() {
        return "InstanceEnv of '" + className + "'";
    }
}
