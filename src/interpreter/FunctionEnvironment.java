package interpreter;

public class FunctionEnvironment extends MainAbstractEnvironment {

    private int stackCounter = 0;

    public FunctionEnvironment(Environment outer) {
        super(outer);
    }

    @Override
    public int defineVar(String name, Type type) {
        int address = stackCounter;
        variables.put(name, address);
        stackCounter += type.getStackSize();
        return address;
    }
}
