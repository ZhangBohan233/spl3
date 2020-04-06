package ast.fakeEnv;

import interpreter.types.Type;

public class FakeFunctionEnv extends FakeMainAbstractEnv {

    private int stackCounter = 0;

    public FakeFunctionEnv(FakeEnv outer) {
        super(outer);
    }

    @Override
    public int defineVar(String name, Type type) {
        int address = stackCounter;
//        variables.put(name, address);
//        stackCounter += type.getStackSize();
        return address;
    }

    public int getStackCounter() {
        return stackCounter;
    }
}
