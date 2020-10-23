// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class PostTable implements Table
{
    private static final String[] macGlyphName;
    private int version;
    private int italicAngle;
    private short underlinePosition;
    private short underlineThickness;
    private int isFixedPitch;
    private int minMemType42;
    private int maxMemType42;
    private int minMemType1;
    private int maxMemType1;
    private int numGlyphs;
    private int[] glyphNameIndex;
    private String[] psGlyphName;
    
    protected PostTable(final DirectoryEntry de, final RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        this.version = raf.readInt();
        this.italicAngle = raf.readInt();
        this.underlinePosition = raf.readShort();
        this.underlineThickness = raf.readShort();
        this.isFixedPitch = raf.readInt();
        this.minMemType42 = raf.readInt();
        this.maxMemType42 = raf.readInt();
        this.minMemType1 = raf.readInt();
        this.maxMemType1 = raf.readInt();
        if (this.version == 131072) {
            this.numGlyphs = raf.readUnsignedShort();
            this.glyphNameIndex = new int[this.numGlyphs];
            for (int i = 0; i < this.numGlyphs; ++i) {
                this.glyphNameIndex[i] = raf.readUnsignedShort();
            }
            int h = this.highestGlyphNameIndex();
            if (h > 257) {
                h -= 257;
                this.psGlyphName = new String[h];
                for (int j = 0; j < h; ++j) {
                    final int len = raf.readUnsignedByte();
                    final byte[] buf = new byte[len];
                    raf.readFully(buf);
                    this.psGlyphName[j] = new String(buf);
                }
            }
        }
        else if (this.version == 131077) {}
    }
    
    private int highestGlyphNameIndex() {
        int high = 0;
        for (int i = 0; i < this.numGlyphs; ++i) {
            if (high < this.glyphNameIndex[i]) {
                high = this.glyphNameIndex[i];
            }
        }
        return high;
    }
    
    public String getGlyphName(final int i) {
        if (this.version == 131072) {
            return (this.glyphNameIndex[i] > 257) ? this.psGlyphName[this.glyphNameIndex[i] - 258] : PostTable.macGlyphName[this.glyphNameIndex[i]];
        }
        return null;
    }
    
    @Override
    public int getType() {
        return 1886352244;
    }
    
    static {
        macGlyphName = new String[] { ".notdef", "null", "CR", "space", "exclam", "quotedbl", "numbersign", "dollar", "percent", "ampersand", "quotesingle", "parenleft", "parenright", "asterisk", "plus", "comma", "hyphen", "period", "slash", "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "colon", "semicolon", "less", "equal", "greater", "question", "at", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "bracketleft", "backslash", "bracketright", "asciicircum", "underscore", "grave", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "braceleft", "bar", "braceright", "asciitilde", "Adieresis", "Aring", "Ccedilla", "Eacute", "Ntilde", "Odieresis", "Udieresis", "aacute", "agrave", "acircumflex", "adieresis", "atilde", "aring", "ccedilla", "eacute", "egrave", "ecircumflex", "edieresis", "iacute", "igrave", "icircumflex", "idieresis", "ntilde", "oacute", "ograve", "ocircumflex", "odieresis", "otilde", "uacute", "ugrave", "ucircumflex", "udieresis", "dagger", "degree", "cent", "sterling", "section", "bullet", "paragraph", "germandbls", "registered", "copyright", "trademark", "acute", "dieresis", "notequal", "AE", "Oslash", "infinity", "plusminus", "lessequal", "greaterequal", "yen", "mu", "partialdiff", "summation", "product", "pi", "integral'", "ordfeminine", "ordmasculine", "Omega", "ae", "oslash", "questiondown", "exclamdown", "logicalnot", "radical", "florin", "approxequal", "increment", "guillemotleft", "guillemotright", "ellipsis", "nbspace", "Agrave", "Atilde", "Otilde", "OE", "oe", "endash", "emdash", "quotedblleft", "quotedblright", "quoteleft", "quoteright", "divide", "lozenge", "ydieresis", "Ydieresis", "fraction", "currency", "guilsinglleft", "guilsinglright", "fi", "fl", "daggerdbl", "middot", "quotesinglbase", "quotedblbase", "perthousand", "Acircumflex", "Ecircumflex", "Aacute", "Edieresis", "Egrave", "Iacute", "Icircumflex", "Idieresis", "Igrave", "Oacute", "Ocircumflex", "", "Ograve", "Uacute", "Ucircumflex", "Ugrave", "dotlessi", "circumflex", "tilde", "overscore", "breve", "dotaccent", "ring", "cedilla", "hungarumlaut", "ogonek", "caron", "Lslash", "lslash", "Scaron", "scaron", "Zcaron", "zcaron", "brokenbar", "Eth", "eth", "Yacute", "yacute", "Thorn", "thorn", "minus", "multiply", "onesuperior", "twosuperior", "threesuperior", "onehalf", "onequarter", "threequarters", "franc", "Gbreve", "gbreve", "Idot", "Scedilla", "scedilla", "Cacute", "cacute", "Ccaron", "ccaron", "" };
    }
}
