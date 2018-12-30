import java.util.*;

import tokens.*;

public class Lexer {
    private List<Character> inputCharacters;
    private ListIterator<Character> characterIterator;

    private int line = 1;
    private char peek = ' ';

    private Hashtable<String, WordToken> words = new Hashtable<>();

    public Lexer(String input) {
        reserve(new WordToken(Tag.TRUE, "true"));
        reserve(new WordToken(Tag.FALSE, "false"));

        saveInputAsCharacters(input);
    }

    private void reserve(WordToken word) {
        words.put(word.lexeme, word);
    }

    private void saveInputAsCharacters(String input) {
        for (int i = 0; i < input.length(); i++) {
            this.inputCharacters.add(input.charAt(i));
        }

        characterIterator = (ListIterator<Character>) inputCharacters.iterator();
    }

    public Token scanNext() {
        handleWhitespace();
        handleLineComment();
        handleMultilineComment();

        if (isDigit()) {
            return createIntegerToken();
        } else if (isLetter()) {
            return createWordToken();
        } else {
            return createUnknownToken();
        }
    }

    private void handleWhitespace() {
        while (characterIterator.hasNext()) {
            nextCharacter();

            if (isSpaceOrTab()) {
                continue;
            } else if (isNewline()) {
                line++;
            } else {
                break;
            }
        }
    }

    private void nextCharacter() {
        peek = characterIterator.next();
    }

    private boolean isSpaceOrTab() {
        return peek == ' ' || peek == '\t';
    }

    private boolean isNewline() {
        return peek == '\n';
    }

    private void handleLineComment() {
        while (characterIterator.hasNext()) {
            nextCharacter();

            if (beginsWithDoubleSlash()) {
                skipLine();
            } else {
                break;
            }
        }
    }

    private boolean beginsWithDoubleSlash() {
        char nextCharacter = peek;
        char nextNextCharacter = characterIterator.next();
        characterIterator.previous();

        return nextCharacter == '/' && nextNextCharacter == '/';
    }

    private void skipLine() {
        while (characterIterator.hasNext()) {
            nextCharacter();

            if (isNewline()) {
                break;
            }
        }
    }

    private void handleMultilineComment() {
        while (characterIterator.hasNext()) {
            nextCharacter();

            if (beginsWithSlashAndAsterisk()) {
                skipContentUntilAsteriskAndSlash();
            }
        }
    }

    private boolean beginsWithSlashAndAsterisk() {
        char nextCharacter = peek;
        char nextNextCharacter = characterIterator.next();
        characterIterator.previous();

        return nextCharacter == '/' && nextNextCharacter == '*';
    }

    private void skipContentUntilAsteriskAndSlash() {
        while (characterIterator.hasNext()) {
            nextCharacter();

            if (endsWithAsteriskAndSlash()) {
                break;
            }
        }
    }

    private boolean endsWithAsteriskAndSlash() {
        char nextCharacter = peek;
        char nextNextCharacter = characterIterator.next();
        characterIterator.previous();

        return nextCharacter == '*' && nextNextCharacter == '/';
    }

    private boolean isDigit() {
        return Character.isDigit(peek);
    }

    private Token createIntegerToken() {
        int value = 0;
        do {
            value = 10 * value + Character.digit(peek, 10);
            nextCharacter();
        } while (isDigit());

        return new IntegerToken(value);
    }

    private boolean isLetter() {
        return Character.isLetter(peek);
    }

    private Token createWordToken() {
        StringBuilder b = new StringBuilder();

        do {
            b.append(peek);
            nextCharacter();
        } while (isLetterOrDigit());

        String identifierName = b.toString();
        WordToken wordToken = (WordToken) words.get(identifierName);

        if (wordExists(wordToken)) {
            return wordToken;
        }

        wordToken = new WordToken(Tag.IDENTIFIER, identifierName);
        words.put(identifierName, wordToken);
        return wordToken;
    }

    private boolean isLetterOrDigit() {
        return isLetter() || isDigit();
    }

    private boolean wordExists(WordToken wordToken) {
        return wordToken != null;
    }

    private Token createUnknownToken() {
        Token token = new Token(peek);
        peek = ' ';
        return token;
    }
}
