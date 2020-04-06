package ast.fakeEnv;

import interpreter.types.Type;

public class FakeGlobalEnv extends FakeMainAbstractEnv {

    private int globalCounter = 0;

    public FakeGlobalEnv() {
        super(null);
    }

    @Override
    public int defineVar(String name, Type type) {
        int address = globalCounter;
        variables.put(name, address);
//        globalCounter += type.getStackSize();
        return address;
    }
}
