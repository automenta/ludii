// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.editor;

import java.util.ArrayList;
import java.util.List;

public class LudiiTokeniser
{
    private static final char OPEN_PARENTHESES = '(';
    private static final char CLOSE_PARENTHESES = ')';
    private static final char OPEN_CURLY = '{';
    private static final char CLOSE_CURLY = '}';
    private static final char OPEN_SQUARE = '[';
    private static final char CLOSE_SQUARE = ']';
    private static final char OPEN_ANGLE = '<';
    private static final char CLOSE_ANGLE = '>';
    private static final char STRING_DELIMITER = '\"';
    private static final char LABEL_DELIMITER = ':';
    private final List<String> tokens;
    private final StringBuilder token;
    
    public LudiiTokeniser(final String gameDescription) {
        this.tokens = new ArrayList<>();
        this.token = new StringBuilder();
        boolean inString = false;
        boolean inNumber = false;
        boolean whitespaceLast = false;
        for (final char ch : gameDescription.toCharArray()) {
            Label_0310: {
                if (inString) {
                    this.token.append(ch);
                    if (ch == '\"') {
                        this.startNewToken();
                        inString = false;
                    }
                }
                else {
                    if (inNumber) {
                        if (isNumber(ch)) {
                            this.token.append(ch);
                            break Label_0310;
                        }
                        this.startNewToken();
                        inNumber = false;
                    }
                    final boolean isWhitespace = Character.isWhitespace(ch);
                    if (whitespaceLast != isWhitespace) {
                        this.startNewToken();
                    }
                    whitespaceLast = isWhitespace;
                    switch (ch) {
                        case '(':
                        case ')':
                        case '<':
                        case '>':
                        case '[':
                        case ']':
                        case '{':
                        case '}': {
                            this.startNewToken();
                            this.addCompleteToken(ch);
                            break;
                        }
                        case '\"': {
                            this.startNewToken();
                            inString = true;
                            this.token.append(ch);
                            break;
                        }
                        case ':': {
                            this.token.append(ch);
                            this.startNewToken();
                            break;
                        }
                        default: {
                            if (isNumber(ch)) {
                                this.startNewToken();
                                inNumber = true;
                            }
                            this.token.append(ch);
                            break;
                        }
                    }
                }
            }
        }
        this.startNewToken();
    }
    
    private static boolean isNumber(final char ch) {
        final boolean isDigit = ch == '+' || ch == '-' || ch == '.' || Character.isDigit(ch);
        return isDigit;
    }
    
    private boolean addCompleteToken(final char ch) {
        return this.tokens.add(String.valueOf(ch));
    }
    
    private void startNewToken() {
        if (this.token.length() != 0) {
            this.tokens.add(this.token.toString());
            this.token.setLength(0);
        }
    }
    
    public String[] getTokens() {
        return this.tokens.toArray(new String[0]);
    }
    
    public static EditorTokenType typeForToken(final String token, final boolean inAngle, final EditorTokenType lastToken) {
        if (token == null || token.length() == 0) {
            return EditorTokenType.OTHER;
        }
        if (token.length() == 1) {
            switch (token.charAt(0)) {
                case '(': {
                    return EditorTokenType.OPEN_ROUND;
                }
                case '{': {
                    return EditorTokenType.OPEN_CURLY;
                }
                case '[': {
                    return EditorTokenType.OPEN_SQUARE;
                }
                case '<': {
                    return EditorTokenType.OPEN_ANGLE;
                }
                case ')': {
                    return EditorTokenType.CLOSE_ROUND;
                }
                case '}': {
                    return EditorTokenType.CLOSE_CURLY;
                }
                case ']': {
                    return EditorTokenType.CLOSE_SQUARE;
                }
                case '>': {
                    return EditorTokenType.CLOSE_ANGLE;
                }
            }
        }
        if (token.charAt(0) == '\"') {
            return EditorTokenType.STRING;
        }
        if (isFloat(token)) {
            return EditorTokenType.FLOAT;
        }
        if (isInteger(token)) {
            return EditorTokenType.INT;
        }
        if (token.endsWith(Character.toString(':'))) {
            return EditorTokenType.LABEL;
        }
        if (token.trim().isEmpty()) {
            return EditorTokenType.WHITESPACE;
        }
        if (inAngle) {
            return EditorTokenType.RULE;
        }
        if (lastToken != null && lastToken == EditorTokenType.OPEN_ROUND && Character.isLowerCase(token.charAt(0))) {
            return EditorTokenType.CLASS;
        }
        if (Character.isUpperCase(token.charAt(0))) {
            return EditorTokenType.ENUM;
        }
        return EditorTokenType.OTHER;
    }
    
    private static boolean isInteger(final String token) {
        try {
            Long.parseLong(token);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public static boolean isFloat(final String token) {
        if (!token.contains(".")) {
            return false;
        }
        try {
            Double.parseDouble(token);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
