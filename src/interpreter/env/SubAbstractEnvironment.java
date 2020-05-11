package interpreter.env;

import interpreter.Memory;
import interpreter.types.TypeValue;
import util.LineFile;
import util.Utilities;

import java.util.Map;

public abstract class SubAbstractEnvironment extends Environment {

    public SubAbstractEnvironment(Environment outer) {
        super(outer.memory, outer);
    }

    @Override
    public void defineFunction(String name, TypeValue funcTv, LineFile lineFile) {
        throw new EnvironmentError();
    }

    @Override
    public void setReturn(TypeValue typeValue) {
        outer.setReturn(typeValue);
    }


    @Override
    public void addNamespace(ModuleEnvironment moduleEnvironment) {
        throw new EnvironmentError();
    }

    @Override
    protected TypeValue searchInNamespaces(String name) {
        return outer.searchInNamespaces(name);
    }

    @Override
    protected void setInNamespaces(String name, TypeValue typeValue) {

    }
}
