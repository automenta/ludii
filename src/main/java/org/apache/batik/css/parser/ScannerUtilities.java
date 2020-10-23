// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

public class ScannerUtilities
{
    protected static final int[] IDENTIFIER_START;
    protected static final int[] NAME;
    protected static final int[] HEXADECIMAL;
    protected static final int[] STRING;
    protected static final int[] URI;
    
    protected ScannerUtilities() {
    }
    
    public static boolean isCSSSpace(final char c) {
        return c <= ' ' && (4294981120L >> c & 0x1L) != 0x0L;
    }
    
    public static boolean isCSSIdentifierStartCharacter(final char c) {
        return c >= '\u0080' || (ScannerUtilities.IDENTIFIER_START[c >> 5] & 1 << (c & '\u001f')) != 0x0;
    }
    
    public static boolean isCSSNameCharacter(final char c) {
        return c >= '\u0080' || (ScannerUtilities.NAME[c >> 5] & 1 << (c & '\u001f')) != 0x0;
    }
    
    public static boolean isCSSHexadecimalCharacter(final char c) {
        return c < '\u0080' && (ScannerUtilities.HEXADECIMAL[c >> 5] & 1 << (c & '\u001f')) != 0x0;
    }
    
    public static boolean isCSSStringCharacter(final char c) {
        return c >= '\u0080' || (ScannerUtilities.STRING[c >> 5] & 1 << (c & '\u001f')) != 0x0;
    }
    
    public static boolean isCSSURICharacter(final char c) {
        return c >= '\u0080' || (ScannerUtilities.URI[c >> 5] & 1 << (c & '\u001f')) != 0x0;
    }
    
    static {
        IDENTIFIER_START = new int[] { 0, 0, -2013265922, 134217726 };
        NAME = new int[] { 0, 67051520, -2013265922, 134217726 };
        HEXADECIMAL = new int[] { 0, 67043328, 126, 126 };
        STRING = new int[] { 512, -133, -1, Integer.MAX_VALUE };
        URI = new int[] { 0, -902, -1, Integer.MAX_VALUE };
    }
}
