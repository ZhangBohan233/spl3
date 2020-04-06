package util;

public class LineFile {

    public static final LineFile LF_INTERPRETER = new LineFile(0, "Interpreter");

    private int line;
    private String fileName;

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
