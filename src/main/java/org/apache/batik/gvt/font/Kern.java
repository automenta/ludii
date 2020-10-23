// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

import java.util.Arrays;

public class Kern
{
    private int[] firstGlyphCodes;
    private int[] secondGlyphCodes;
    private UnicodeRange[] firstUnicodeRanges;
    private UnicodeRange[] secondUnicodeRanges;
    private float kerningAdjust;
    
    public Kern(final int[] firstGlyphCodes, final int[] secondGlyphCodes, final UnicodeRange[] firstUnicodeRanges, final UnicodeRange[] secondUnicodeRanges, final float adjustValue) {
        this.firstGlyphCodes = firstGlyphCodes;
        this.secondGlyphCodes = secondGlyphCodes;
        this.firstUnicodeRanges = firstUnicodeRanges;
        this.secondUnicodeRanges = secondUnicodeRanges;
        this.kerningAdjust = adjustValue;
        if (firstGlyphCodes != null) {
            Arrays.sort(this.firstGlyphCodes);
        }
        if (secondGlyphCodes != null) {
            Arrays.sort(this.secondGlyphCodes);
        }
    }
    
    public boolean matchesFirstGlyph(final int glyphCode, final String glyphUnicode) {
        if (this.firstGlyphCodes != null) {
            final int pt = Arrays.binarySearch(this.firstGlyphCodes, glyphCode);
            if (pt >= 0) {
                return true;
            }
        }
        if (glyphUnicode.length() < 1) {
            return false;
        }
        final char glyphChar = glyphUnicode.charAt(0);
        for (final UnicodeRange firstUnicodeRange : this.firstUnicodeRanges) {
            if (firstUnicodeRange.contains(glyphChar)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean matchesFirstGlyph(final int glyphCode, final char glyphUnicode) {
        if (this.firstGlyphCodes != null) {
            final int pt = Arrays.binarySearch(this.firstGlyphCodes, glyphCode);
            if (pt >= 0) {
                return true;
            }
        }
        for (final UnicodeRange firstUnicodeRange : this.firstUnicodeRanges) {
            if (firstUnicodeRange.contains(glyphUnicode)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean matchesSecondGlyph(final int glyphCode, final String glyphUnicode) {
        if (this.secondGlyphCodes != null) {
            final int pt = Arrays.binarySearch(this.secondGlyphCodes, glyphCode);
            if (pt >= 0) {
                return true;
            }
        }
        if (glyphUnicode.length() < 1) {
            return false;
        }
        final char glyphChar = glyphUnicode.charAt(0);
        for (final UnicodeRange secondUnicodeRange : this.secondUnicodeRanges) {
            if (secondUnicodeRange.contains(glyphChar)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean matchesSecondGlyph(final int glyphCode, final char glyphUnicode) {
        if (this.secondGlyphCodes != null) {
            final int pt = Arrays.binarySearch(this.secondGlyphCodes, glyphCode);
            if (pt >= 0) {
                return true;
            }
        }
        for (final UnicodeRange secondUnicodeRange : this.secondUnicodeRanges) {
            if (secondUnicodeRange.contains(glyphUnicode)) {
                return true;
            }
        }
        return false;
    }
    
    public float getAdjustValue() {
        return this.kerningAdjust;
    }
}
