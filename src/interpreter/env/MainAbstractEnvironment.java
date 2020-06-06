package interpreter.env;

import interpreter.Memory;
import interpreter.types.TypeError;
import interpreter.types.TypeValue;
import util.LineFile;
import util.Utilities;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class MainAbstractEnvironment extends Environment {

    public static final Set<String> NON_OVERRIDE_FUNCTIONS = Set.of(
            "init"
    );

    protected Set<ModuleEnvironment> namespaces = new HashSet<>();

    public MainAbstractEnvironment(Memory memory, Environment outer) {
        super(memory, outer);
    }

    @Override
    public void defineFunction(String name, TypeValue funcTv, LineFile lineFile) {
        if (!NON_OVERRIDE_FUNCTIONS.contains(name) && hasName(name, lineFile)) {
            TypeValue superFn = get(name, lineFile);
            if (!superFn.getType().equals(funcTv.getType())) {
                throw new TypeError("Function '" + name + "' does not overrides its super function, but has " +
                        "identical names. ", lineFile);
            }
        }

        constants.put(name, funcTv);
    }

    @Override
    public boolean isSub() {
        return false;
    }

    @Override
    public void setReturn(TypeValue typeValue) {
        throw new EnvironmentError("Return outside function. ");
    }

    @Override
    public void addNamespace(ModuleEnvironment moduleEnvironment) {
        namespaces.add(moduleEnvironment);
    }

    @Override
    protected TypeValue searchInNamespaces(String name) {
        for (ModuleEnvironment me : namespaces) {
            TypeValue tv = me.constants.get(name);
            if (tv == null) tv = me.variables.get(name);
            if (tv != null) return tv;
        }
        if (outer == null) return null;
        else return outer.searchInNamespaces(name);
    }

    @Override
    protected void setInNamespaces(String name, TypeValue typeValue) {

    }

    public void breakLoop() {
        throw new EnvironmentError("Break outside loop");
    }

    public void resumeLoop() {
        throw new EnvironmentError("Outside function");
    }

    public void pauseLoop() {
        throw new EnvironmentError("Continue outside function");
    }

    public void invalidate() {
        throw new EnvironmentError();
    }

    @Override
    public void fallthrough() {
        throw new EnvironmentError("'fallthrough' outside case statements. ");
    }

    @Override
    public boolean isFallingThrough() {
        throw new EnvironmentError("'fallthrough' outside case statements. ");
    }
}
