// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.activation.registries;

public class MailcapTokenizer
{
    public static final int UNKNOWN_TOKEN = 0;
    public static final int START_TOKEN = 1;
    public static final int STRING_TOKEN = 2;
    public static final int EOI_TOKEN = 5;
    public static final int SLASH_TOKEN = 47;
    public static final int SEMICOLON_TOKEN = 59;
    public static final int EQUALS_TOKEN = 61;
    private final String data;
    private int dataIndex;
    private final int dataLength;
    private int currentToken;
    private String currentTokenValue;
    private boolean isAutoquoting;
    private final char autoquoteChar;
    
    public MailcapTokenizer(final String inputString) {
        this.data = inputString;
        this.dataIndex = 0;
        this.dataLength = inputString.length();
        this.currentToken = 1;
        this.currentTokenValue = "";
        this.isAutoquoting = false;
        this.autoquoteChar = ';';
    }
    
    public void setIsAutoquoting(final boolean value) {
        this.isAutoquoting = value;
    }
    
    public int getCurrentToken() {
        return this.currentToken;
    }
    
    public static String nameForToken(final int token) {
        String name = "really unknown";
        switch (token) {
            case 0: {
                name = "unknown";
                break;
            }
            case 1: {
                name = "start";
                break;
            }
            case 2: {
                name = "string";
                break;
            }
            case 5: {
                name = "EOI";
                break;
            }
            case 47: {
                name = "'/'";
                break;
            }
            case 59: {
                name = "';'";
                break;
            }
            case 61: {
                name = "'='";
                break;
            }
        }
        return name;
    }
    
    public String getCurrentTokenValue() {
        return this.currentTokenValue;
    }
    
    public int nextToken() {
        if (this.dataIndex < this.dataLength) {
            while (this.dataIndex < this.dataLength && isWhiteSpaceChar(this.data.charAt(this.dataIndex))) {
                ++this.dataIndex;
            }
            if (this.dataIndex < this.dataLength) {
                final char c = this.data.charAt(this.dataIndex);
                if (this.isAutoquoting) {
                    if (c == ';' || c == '=') {
                        this.currentToken = c;
                        this.currentTokenValue = new Character(c).toString();
                        ++this.dataIndex;
                    }
                    else {
                        this.processAutoquoteToken();
                    }
                }
                else if (isStringTokenChar(c)) {
                    this.processStringToken();
                }
                else if (c == '/' || c == ';' || c == '=') {
                    this.currentToken = c;
                    this.currentTokenValue = new Character(c).toString();
                    ++this.dataIndex;
                }
                else {
                    this.currentToken = 0;
                    this.currentTokenValue = new Character(c).toString();
                    ++this.dataIndex;
                }
            }
            else {
                this.currentToken = 5;
                this.currentTokenValue = null;
            }
        }
        else {
            this.currentToken = 5;
            this.currentTokenValue = null;
        }
        return this.currentToken;
    }
    
    private void processStringToken() {
        final int initialIndex = this.dataIndex;
        while (this.dataIndex < this.dataLength && isStringTokenChar(this.data.charAt(this.dataIndex))) {
            ++this.dataIndex;
        }
        this.currentToken = 2;
        this.currentTokenValue = this.data.substring(initialIndex, this.dataIndex);
    }
    
    private void processAutoquoteToken() {
        final int initialIndex = this.dataIndex;
        boolean foundTerminator = false;
        while (this.dataIndex < this.dataLength && !foundTerminator) {
            final char c = this.data.charAt(this.dataIndex);
            if (c != this.autoquoteChar) {
                ++this.dataIndex;
            }
            else {
                foundTerminator = true;
            }
        }
        this.currentToken = 2;
        this.currentTokenValue = fixEscapeSequences(this.data.substring(initialIndex, this.dataIndex));
    }
    
    private static boolean isSpecialChar(final char c) {
        boolean lAnswer = false;
        switch (c) {
            case '\"':
            case '(':
            case ')':
            case ',':
            case '/':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case '[':
            case '\\':
            case ']': {
                lAnswer = true;
                break;
            }
        }
        return lAnswer;
    }
    
    private static boolean isControlChar(final char c) {
        return Character.isISOControl(c);
    }
    
    private static boolean isWhiteSpaceChar(final char c) {
        return Character.isWhitespace(c);
    }
    
    private static boolean isStringTokenChar(final char c) {
        return !isSpecialChar(c) && !isControlChar(c) && !isWhiteSpaceChar(c);
    }
    
    private static String fixEscapeSequences(final String inputString) {
        final int inputLength = inputString.length();
        final StringBuffer buffer = new StringBuffer();
        buffer.ensureCapacity(inputLength);
        for (int i = 0; i < inputLength; ++i) {
            final char currentChar = inputString.charAt(i);
            if (currentChar != '\\') {
                buffer.append(currentChar);
            }
            else if (i < inputLength - 1) {
                final char nextChar = inputString.charAt(i + 1);
                buffer.append(nextChar);
                ++i;
            }
            else {
                buffer.append(currentChar);
            }
        }
        return buffer.toString();
    }
}
