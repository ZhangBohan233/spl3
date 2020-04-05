package lexer;

import util.LineFile;

public class SyntaxError extends RuntimeException {

    public SyntaxError(String msg, LineFile location) {
        super(msg + location.toStringFileLine());
    }
}
