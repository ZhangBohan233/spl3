package interpreter.env;

import util.LineFile;
import util.SplBaseException;

public class EnvironmentError extends SplBaseException {

    public EnvironmentError() {
        super();
    }

    public EnvironmentError(String msg) {
        super(msg);
    }

    public EnvironmentError(String msg, LineFile lineFile) {
        super(msg, lineFile);
    }
}
