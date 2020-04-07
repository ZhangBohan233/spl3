package interpreter.env;

public class EnvironmentError extends RuntimeException {

    public EnvironmentError() {
        super();
    }

    public EnvironmentError(String msg) {
        super(msg);
    }
}
