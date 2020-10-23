// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.util.HashMap;
import java.util.Map;

public class EncodingUtilities
{
    protected static final Map ENCODINGS;
    
    protected EncodingUtilities() {
    }
    
    public static String javaEncoding(final String encoding) {
        return EncodingUtilities.ENCODINGS.get(encoding.toUpperCase());
    }
    
    static {
        (ENCODINGS = new HashMap()).put("UTF-8", "UTF8");
        EncodingUtilities.ENCODINGS.put("UTF-16", "Unicode");
        EncodingUtilities.ENCODINGS.put("US-ASCII", "ASCII");
        EncodingUtilities.ENCODINGS.put("ISO-8859-1", "8859_1");
        EncodingUtilities.ENCODINGS.put("ISO-8859-2", "8859_2");
        EncodingUtilities.ENCODINGS.put("ISO-8859-3", "8859_3");
        EncodingUtilities.ENCODINGS.put("ISO-8859-4", "8859_4");
        EncodingUtilities.ENCODINGS.put("ISO-8859-5", "8859_5");
        EncodingUtilities.ENCODINGS.put("ISO-8859-6", "8859_6");
        EncodingUtilities.ENCODINGS.put("ISO-8859-7", "8859_7");
        EncodingUtilities.ENCODINGS.put("ISO-8859-8", "8859_8");
        EncodingUtilities.ENCODINGS.put("ISO-8859-9", "8859_9");
        EncodingUtilities.ENCODINGS.put("ISO-2022-JP", "JIS");
        EncodingUtilities.ENCODINGS.put("WINDOWS-31J", "MS932");
        EncodingUtilities.ENCODINGS.put("EUC-JP", "EUCJIS");
        EncodingUtilities.ENCODINGS.put("GB2312", "GB2312");
        EncodingUtilities.ENCODINGS.put("BIG5", "Big5");
        EncodingUtilities.ENCODINGS.put("EUC-KR", "KSC5601");
        EncodingUtilities.ENCODINGS.put("ISO-2022-KR", "ISO2022KR");
        EncodingUtilities.ENCODINGS.put("KOI8-R", "KOI8_R");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-US", "Cp037");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-CA", "Cp037");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-NL", "Cp037");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-WT", "Cp037");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-DK", "Cp277");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-NO", "Cp277");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-FI", "Cp278");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-SE", "Cp278");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-IT", "Cp280");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-ES", "Cp284");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-GB", "Cp285");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-FR", "Cp297");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-AR1", "Cp420");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-HE", "Cp424");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-BE", "Cp500");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-CH", "Cp500");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-ROECE", "Cp870");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-YU", "Cp870");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-IS", "Cp871");
        EncodingUtilities.ENCODINGS.put("EBCDIC-CP-AR2", "Cp918");
        EncodingUtilities.ENCODINGS.put("CP1252", "Cp1252");
    }
}
