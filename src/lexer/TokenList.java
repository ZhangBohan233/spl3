package lexer;

import java.util.List;

public class TokenList {

    private List<Token> tokens;

    TokenList(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
