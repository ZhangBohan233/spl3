package interpreter;

public class GlobalEnvironment extends MainAbstractEnvironment {

    private int globalCounter = 0;

    public GlobalEnvironment() {
        super(null);
    }

    @Override
    public int defineVar(String name, Type type) {
        int address = globalCounter;
        variables.put(name, address);
        globalCounter += type.getStackSize();
        return address;
    }
}
