package interpreter;

public abstract class MainAbstractEnvironment extends Environment {
    protected MainAbstractEnvironment(Environment outer) {
        super(outer);
    }
}
