package lexer;

import util.LineFile;

public class StrToken extends Token {
    private String literal;

    public StrToken(String literal, LineFile lineFile) {
        super(lineFile);
        this.literal = literal;
    }

    @Override
    public String toString() {
        return "StrToken{" +
                literal +
                '}';
    }
}
