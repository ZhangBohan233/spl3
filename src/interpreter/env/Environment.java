package interpreter.env;

import interpreter.Memory;

public abstract class Environment {

    protected Environment outer;
    protected Memory memory;
    protected int stackLocation;

    public Environment(Memory memory, Environment outer, int stackLocation) {
        this.memory = memory;
        this.outer = outer;
    }

    public void set(int localAddr, int layer, byte[] value) {
        if (layer == 0) {

        }
    }
}
