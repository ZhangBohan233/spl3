package ast.fakeEnv;

import interpreter.EnvironmentError;
import interpreter.types.Type;

import java.util.HashMap;
import java.util.Map;

public abstract class FakeEnv {

    protected FakeEnv outer;

    protected Map<String, Integer> variables = new HashMap<>();
    protected Map<String, Integer> constants = new HashMap<>();

    protected FakeEnv(FakeEnv outer) {
        this.outer = outer;
    }

    public abstract int defineVar(String name, Type type);

    /**
     * Returns (localAddress, layer).
     *
     * @param name
     * @return
     */
    public int[] get(String name) {
        Integer addr = constants.get(name);
        if (addr == null) addr = variables.get(name);
        if (addr == null) {
            if (outer == null) throw new EnvironmentError("Name '" + name + "' not found. ");
            else {
                int[] outRes = outer.get(name);
                outRes[1]++;
                return outRes;
            }
        } else {
            return new int[]{addr, 0};
        }
    }
}
