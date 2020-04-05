package interpreter;

import util.LineFile;

public class SplException extends RuntimeException {

    public SplException() {
        super();
    }

    public SplException(String msg, LineFile lineFile) {
        super(msg + lineFile.toStringFileLine());
    }
}
