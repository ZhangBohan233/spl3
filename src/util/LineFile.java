package util;

public class LineFile {

    public static final LineFile LF_TOKENIZER = new LineFile(0, "Tokenizer");
    public static final LineFile LF_INTERPRETER = new LineFile(0, "Interpreter");
    public static final LineFile LF_PARSER = new LineFile(0, "Parser");

    private final int line;
    private final String fileName;

    public LineFile(int line, String fileName) {
        this.line = line;
        this.fileName = fileName;
    }

    public int getLine() {
        return line;
    }

    public String getFileName() {
        return fileName;
    }

    public String toStringFileLine() {
        return String.format("In file '%s', at line %d.", fileName, line);
    }
}
