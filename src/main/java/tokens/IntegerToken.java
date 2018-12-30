package tokens;

public class IntegerToken extends Token {
    public final int value;

    public IntegerToken(int value) {
        super(Tag.INTEGER);
        this.value = value;
    }
}
