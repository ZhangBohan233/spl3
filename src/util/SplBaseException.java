package util;

public class SplBaseException extends RuntimeException {

    public SplBaseException() {
        super();
    }

    public SplBaseException(String msg) {
        super(msg);
    }

    public SplBaseException(String msg, LineFile lineFile) {
        super(msg + lineFile.toStringFileLine());
    }
}
