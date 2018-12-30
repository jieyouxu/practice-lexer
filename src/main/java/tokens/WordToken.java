package tokens;

public class WordToken extends Token {
    public final String lexeme;

    public WordToken(int tag, String lexeme) {
        super(tag);
        this.lexeme = new String(lexeme);
    }
}
