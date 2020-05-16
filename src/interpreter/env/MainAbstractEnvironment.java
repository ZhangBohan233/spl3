package interpreter.env;

import interpreter.Memory;
import interpreter.types.TypeValue;
import util.LineFile;
import util.Utilities;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class MainAbstractEnvironment extends Environment {

    protected Set<ModuleEnvironment> namespaces = new HashSet<>();

    public MainAbstractEnvironment(Memory memory, Environment outer) {
        super(memory, outer);
    }

    @Override
    public void defineFunction(String name, TypeValue funcTv, LineFile lineFile) {
        if (hasName(name, lineFile)) throw new EnvironmentError("Function '" + name + "' has already defined" +
                "in this scope. ", lineFile);

        constants.put(name, funcTv);
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
}
