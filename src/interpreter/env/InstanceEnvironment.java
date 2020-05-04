package interpreter.env;

import interpreter.Memory;
import interpreter.primitives.Pointer;
import interpreter.splObjects.Instance;
import interpreter.types.TypeValue;
import util.LineFile;

public class InstanceEnvironment extends MainAbstractEnvironment {

    public InstanceEnvironment(Environment outer) {
        super(outer.getMemory(), outer);
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
        TypeValue tv = searchSuper(name);
        if (tv == null)
            return super.innerGet(name, isFirst, includeConst, lineFile);
        else return tv;
    }

    private TypeValue searchSuper(String name) {
        TypeValue tv = constants.get(name);
        if (tv == null) tv = variables.get(name);
        if (tv == null) {
            TypeValue superTv = constants.get("super");
            if (superTv == null) return null;
            else {
                Instance instance = (Instance) getMemory().get((Pointer) superTv.getValue());
                return instance.getEnv().searchSuper(name);
            }
        } else return tv;
    }
}
