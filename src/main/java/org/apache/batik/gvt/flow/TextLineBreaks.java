// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.flow;

import java.util.HashSet;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Set;
import java.text.AttributedCharacterIterator;

public class TextLineBreaks
{
    public static final AttributedCharacterIterator.Attribute WORD_LIMIT;
    public static final AttributedCharacterIterator.Attribute FLOW_PARAGRAPH;
    public static final AttributedCharacterIterator.Attribute FLOW_LINE_BREAK;
    static Set lineBrks;
    public static final char CHAR_ZERO_WIDTH_JOINER = '\u200d';
    protected static final int QUICK_LUT_SIZE = 256;
    public static final byte BREAK_ACTION_DIRECT = 0;
    public static final byte BREAK_ACTION_INDIRECT = 1;
    public static final byte BREAK_ACTION_PROHIBITED = 2;
    public static final String[] brkStrs;
    public static final byte CHAR_CLASS_OP = 0;
    public static final byte CHAR_CLASS_CL = 1;
    public static final byte CHAR_CLASS_QU = 2;
    public static final byte CHAR_CLASS_GL = 3;
    public static final byte CHAR_CLASS_NS = 4;
    public static final byte CHAR_CLASS_EX = 5;
    public static final byte CHAR_CLASS_SY = 6;
    public static final byte CHAR_CLASS_IS = 7;
    public static final byte CHAR_CLASS_PR = 8;
    public static final byte CHAR_CLASS_PO = 9;
    public static final byte CHAR_CLASS_NU = 10;
    public static final byte CHAR_CLASS_AL = 11;
    public static final byte CHAR_CLASS_ID = 12;
    public static final byte CHAR_CLASS_IN = 13;
    public static final byte CHAR_CLASS_HY = 14;
    public static final byte CHAR_CLASS_BA = 15;
    public static final byte CHAR_CLASS_BB = 16;
    public static final byte CHAR_CLASS_B2 = 17;
    public static final byte CHAR_CLASS_ZW = 18;
    public static final byte CHAR_CLASS_CM = 19;
    public static final byte CHAR_CLASS_SA = 20;
    public static final byte CHAR_CLASS_SP = 21;
    public static final byte CHAR_CLASS_BK = 22;
    public static final byte CHAR_CLASS_AI = 11;
    public static final byte CHAR_CLASS_CR = 24;
    public static final byte CHAR_CLASS_LF = 25;
    public static final byte CHAR_CLASS_SG = 11;
    public static final byte CHAR_CLASS_XX = 11;
    public static final byte CHAR_CLASS_CB = 28;
    public static final String[] clsStrs;
    static byte[][] brkPairs;
    static byte[] quickLut;
    static final char[] raw_data;
    static final byte[] raw_classes;
    
    static int findComplexBreak(final AttributedCharacterIterator aci) {
        int cnt = 0;
        for (char ch = aci.current(); ch == '\uffff' && getCharCharClass(ch) == 20; ch = aci.next(), ++cnt) {}
        return cnt;
    }
    
    public static void findLineBrk(final AttributedString as) {
        final AttributedCharacterIterator aci = as.getIterator();
        if (aci.getEndIndex() == 0) {
            return;
        }
        char ch = aci.current();
        char prevCh = '\uffff';
        byte cls = getCharCharClass(ch);
        if (cls == 25) {
            cls = 22;
        }
        byte curCls = cls;
        byte prevCls = cls;
        byte prevPrevCls = -1;
        int wordCnt = 0;
        int wordBegin = aci.getBeginIndex();
        int ich = wordBegin + 1;
        int lineEnd = aci.getRunLimit(TextLineBreaks.lineBrks);
        if (cls >= 19) {
            cls = 11;
        }
        for (ch = aci.next(); ch != '\uffff'; ch = aci.next(), prevPrevCls = prevCls, prevCls = curCls) {
            if (ich == lineEnd) {
                as.addAttribute(TextLineBreaks.WORD_LIMIT, wordCnt++, wordBegin, ich);
                wordBegin = ich;
                cls = (curCls = getCharCharClass(ch));
                if ((prevCls = cls) >= 19) {
                    cls = 11;
                }
                lineEnd = aci.getRunLimit(TextLineBreaks.lineBrks);
            }
            else {
                curCls = getCharCharClass(ch);
                if (curCls != 21) {
                    if (curCls == 20) {
                        ich += findComplexBreak(aci);
                        ch = aci.previous();
                        if (ch != '\uffff') {
                            prevCls = getCharCharClass(ch);
                        }
                        ch = aci.next();
                        if (ch != '\uffff') {
                            cls = (curCls = getCharCharClass(ch));
                        }
                    }
                    else if (ch != '\u200d') {
                        if (prevCh != '\u200d') {
                            if (curCls == 22 || curCls == 25) {
                                as.addAttribute(TextLineBreaks.WORD_LIMIT, wordCnt++, wordBegin, ich);
                                wordBegin = ich;
                                cls = 22;
                            }
                            else if (prevCls == 24) {
                                as.addAttribute(TextLineBreaks.WORD_LIMIT, wordCnt++, wordBegin, ich - 1);
                                wordBegin = ich - 1;
                                cls = 22;
                            }
                            else if (curCls != 24) {
                                if (curCls == 19) {
                                    if (prevCls == 21) {
                                        cls = 12;
                                        if (prevPrevCls != -1 && TextLineBreaks.brkPairs[prevPrevCls][12] == 0) {
                                            as.addAttribute(TextLineBreaks.WORD_LIMIT, wordCnt++, wordBegin, ich - 1);
                                            wordBegin = ich - 1;
                                        }
                                    }
                                }
                                else if (cls == 22) {
                                    cls = curCls;
                                }
                                else {
                                    final byte brk = TextLineBreaks.brkPairs[cls][curCls];
                                    if (brk == 0) {
                                        as.addAttribute(TextLineBreaks.WORD_LIMIT, wordCnt++, wordBegin, ich);
                                        wordBegin = ich;
                                    }
                                    else if (brk == 1 && prevCls == 21) {
                                        as.addAttribute(TextLineBreaks.WORD_LIMIT, wordCnt++, wordBegin, ich);
                                        wordBegin = ich;
                                    }
                                    cls = curCls;
                                }
                            }
                        }
                    }
                }
            }
            ++ich;
            prevCh = ch;
        }
        as.addAttribute(TextLineBreaks.WORD_LIMIT, wordCnt++, wordBegin, ich);
        wordBegin = ich;
    }
    
    public static byte[] stringToLineBreakClasses(final String s) {
        final int len = s.length();
        final byte[] ret = new byte[len];
        for (int i = 0; i < len; ++i) {
            ret[i] = getCharCharClass(s.charAt(i));
        }
        return ret;
    }
    
    public static byte getCharCharClass(final char ch) {
        if (ch < '\u0100') {
            if (TextLineBreaks.quickLut == null) {
                buildQuickLut();
            }
            return TextLineBreaks.quickLut[ch];
        }
        final int len = TextLineBreaks.raw_data.length;
        int l = 0;
        int r = len / 2 - 1;
        int entry = (l + r) / 2;
        while (l <= r) {
            final char min = TextLineBreaks.raw_data[2 * entry];
            final char max = TextLineBreaks.raw_data[2 * entry + 1];
            if (ch < min) {
                r = entry - 1;
            }
            else {
                if (ch <= max) {
                    break;
                }
                l = entry + 1;
            }
            entry = (l + r) / 2;
        }
        return TextLineBreaks.raw_classes[entry];
    }
    
    protected static void buildQuickLut() {
        int entry = 0;
        TextLineBreaks.quickLut = new byte[256];
        int i = 0;
        while (i < 256) {
            final int max = TextLineBreaks.raw_data[2 * entry + 1];
            final byte cls = TextLineBreaks.raw_classes[entry];
            while (i <= max) {
                TextLineBreaks.quickLut[i] = cls;
                if (++i >= 256) {
                    break;
                }
            }
            ++entry;
        }
    }
    
    static {
        WORD_LIMIT = new GVTAttributedCharacterIterator.TextAttribute("WORD_LIMIT");
        FLOW_PARAGRAPH = GVTAttributedCharacterIterator.TextAttribute.FLOW_PARAGRAPH;
        FLOW_LINE_BREAK = GVTAttributedCharacterIterator.TextAttribute.FLOW_LINE_BREAK;
        (TextLineBreaks.lineBrks = new HashSet()).add(TextLineBreaks.FLOW_PARAGRAPH);
        TextLineBreaks.lineBrks.add(TextLineBreaks.FLOW_LINE_BREAK);
        brkStrs = new String[] { "DB", "IB", "PB" };
        clsStrs = new String[] { "OP", "CL", "QU", "GL", "NS", "EX", "SY", "IS", "PR", "PO", "NU", "AL", "ID", "IN", "HY", "BA", "BB", "B2", "ZW", "CM", "SA", "SP", "BK", "AI", "CR", "LF", "SG", "XX", "CB" };
        TextLineBreaks.brkPairs = new byte[][] { { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1 }, { 0, 2, 1, 1, 2, 2, 2, 2, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 2, 1 }, { 2, 2, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1 }, { 1, 2, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1 }, { 0, 2, 1, 1, 1, 2, 2, 2, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 2, 1 }, { 0, 2, 1, 1, 1, 2, 2, 2, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 2, 1 }, { 0, 2, 1, 1, 1, 2, 2, 2, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 2, 1 }, { 0, 2, 1, 1, 1, 2, 2, 2, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 2, 1 }, { 1, 2, 1, 1, 1, 2, 2, 2, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 2, 1 }, { 0, 2, 1, 1, 1, 2, 2, 2, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 2, 1 }, { 0, 2, 1, 1, 1, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 2, 1 }, { 0, 2, 1, 1, 1, 2, 2, 2, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 2, 1 }, { 0, 2, 1, 1, 1, 2, 2, 2, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 2, 1 }, { 0, 2, 1, 1, 1, 2, 2, 2, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 2, 1 }, { 0, 2, 1, 1, 1, 2, 2, 2, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 2, 1 }, { 0, 2, 1, 1, 1, 2, 2, 2, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 2, 1 }, { 1, 2, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1 }, { 0, 2, 1, 1, 1, 2, 2, 2, 0, 0, 0, 0, 0, 0, 1, 1, 0, 2, 2, 1 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1 }, { 0, 2, 1, 1, 1, 2, 2, 2, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 2, 1 } };
        TextLineBreaks.quickLut = null;
        raw_data = new char[] { '\0', '\b', '\t', '\t', '\n', '\n', '\u000b', '\u000b', '\f', '\f', '\r', '\r', '\u000e', '\u001f', ' ', ' ', '!', '!', '\"', '\"', '#', '#', '$', '$', '%', '%', '&', '&', '\'', '\'', '(', '(', ')', ')', '*', '*', '+', '+', ',', ',', '-', '-', '.', '.', '/', '/', '0', '9', ':', ';', '<', '>', '?', '?', '@', 'Z', '[', '[', '\\', '\\', ']', ']', '^', 'z', '{', '{', '|', '|', '}', '}', '~', '~', '\u007f', '\u009f', ' ', ' ', '¡', '¡', '¢', '¢', '£', '¥', '¦', '¦', '§', '¨', '©', '©', 'ª', 'ª', '«', '«', '¬', '¬', '\u00ad', '\u00ad', '®', '¯', '°', '°', '±', '±', '²', '³', '´', '´', 'µ', 'µ', '¶', 'º', '»', '»', '¼', '¿', '\u00c0', '\u00c5', '\u00c6', '\u00c6', '\u00c7', '\u00cf', '\u00d0', '\u00d0', '\u00d1', '\u00d6', '\u00d7', '\u00d8', '\u00d9', '\u00dd', '\u00de', '\u00e1', '\u00e2', '\u00e5', '\u00e6', '\u00e6', '\u00e7', '\u00e7', '\u00e8', '\u00ea', '\u00eb', '\u00eb', '\u00ec', '\u00ed', '\u00ee', '\u00ef', '\u00f0', '\u00f0', '\u00f1', '\u00f1', '\u00f2', '\u00f3', '\u00f4', '\u00f6', '\u00f7', '\u00fa', '\u00fb', '\u00fb', '\u00fc', '\u00fc', '\u00fd', '\u00fd', '\u00fe', '\u00fe', '\u00ff', '\u0100', '\u0101', '\u0101', '\u0102', '\u0110', '\u0111', '\u0111', '\u0112', '\u0112', '\u0113', '\u0113', '\u0114', '\u011a', '\u011b', '\u011b', '\u011c', '\u0125', '\u0126', '\u0127', '\u0128', '\u012a', '\u012b', '\u012b', '\u012c', '\u0130', '\u0131', '\u0133', '\u0134', '\u0137', '\u0138', '\u0138', '\u0139', '\u013e', '\u013f', '\u0142', '\u0143', '\u0143', '\u0144', '\u0144', '\u0145', '\u0147', '\u0148', '\u014a', '\u014b', '\u014c', '\u014d', '\u014d', '\u014e', '\u0151', '\u0152', '\u0153', '\u0154', '\u0165', '\u0166', '\u0167', '\u0168', '\u016a', '\u016b', '\u016b', '\u016c', '\u01cd', '\u01ce', '\u01ce', '\u01cf', '\u01cf', '\u01d0', '\u01d0', '\u01d1', '\u01d1', '\u01d2', '\u01d2', '\u01d3', '\u01d3', '\u01d4', '\u01d4', '\u01d5', '\u01d5', '\u01d6', '\u01d6', '\u01d7', '\u01d7', '\u01d8', '\u01d8', '\u01d9', '\u01d9', '\u01da', '\u01da', '\u01db', '\u01db', '\u01dc', '\u01dc', '\u01dd', '\u0250', '\u0251', '\u0251', '\u0252', '\u0260', '\u0261', '\u0261', '\u0262', '\u02c6', '\u02c7', '\u02c7', '\u02c8', '\u02c8', '\u02c9', '\u02cb', '\u02cc', '\u02cc', '\u02cd', '\u02cd', '\u02ce', '\u02cf', '\u02d0', '\u02d0', '\u02d1', '\u02d7', '\u02d8', '\u02db', '\u02dc', '\u02dc', '\u02dd', '\u02dd', '\u02de', '\u02ee', '\u0300', '\u036f', '\u0374', '\u0390', '\u0391', '\u03a9', '\u03aa', '\u03b0', '\u03b1', '\u03c1', '\u03c2', '\u03c2', '\u03c3', '\u03c9', '\u03ca', '\u0400', '\u0401', '\u0401', '\u0402', '\u040f', '\u0410', '\u044f', '\u0450', '\u0450', '\u0451', '\u0451', '\u0452', '\u0482', '\u0483', '\u0489', '\u048a', '\u0587', '\u0589', '\u0589', '\u058a', '\u058a', '\u0591', '\u05bd', '\u05be', '\u05be', '\u05bf', '\u05bf', '\u05c0', '\u05c0', '\u05c1', '\u05c2', '\u05c3', '\u05c3', '\u05c4', '\u05c4', '\u05d0', '\u064a', '\u064b', '\u0655', '\u0660', '\u0669', '\u066a', '\u066f', '\u0670', '\u0670', '\u0671', '\u06d5', '\u06d6', '\u06e4', '\u06e5', '\u06e6', '\u06e7', '\u06e8', '\u06e9', '\u06e9', '\u06ea', '\u06ed', '\u06f0', '\u06f9', '\u06fa', '\u070d', '\u070f', '\u070f', '\u0710', '\u0710', '\u0711', '\u0711', '\u0712', '\u072c', '\u0730', '\u074a', '\u0780', '\u07a5', '\u07a6', '\u07b0', '\u07b1', '\u07b1', '\u0901', '\u0903', '\u0905', '\u0939', '\u093c', '\u093c', '\u093d', '\u093d', '\u093e', '\u094d', '\u0950', '\u0950', '\u0951', '\u0954', '\u0958', '\u0961', '\u0962', '\u0963', '\u0964', '\u0965', '\u0966', '\u096f', '\u0970', '\u0970', '\u0981', '\u0983', '\u0985', '\u09b9', '\u09bc', '\u09d7', '\u09dc', '\u09e1', '\u09e2', '\u09e3', '\u09e6', '\u09ef', '\u09f0', '\u09f1', '\u09f2', '\u09f3', '\u09f4', '\u09fa', '\u0a02', '\u0a02', '\u0a05', '\u0a39', '\u0a3c', '\u0a4d', '\u0a59', '\u0a5e', '\u0a66', '\u0a6f', '\u0a70', '\u0a71', '\u0a72', '\u0a74', '\u0a81', '\u0a83', '\u0a85', '\u0ab9', '\u0abc', '\u0abc', '\u0abd', '\u0abd', '\u0abe', '\u0acd', '\u0ad0', '\u0ae0', '\u0ae6', '\u0aef', '\u0b01', '\u0b03', '\u0b05', '\u0b39', '\u0b3c', '\u0b3c', '\u0b3d', '\u0b3d', '\u0b3e', '\u0b57', '\u0b5c', '\u0b61', '\u0b66', '\u0b6f', '\u0b70', '\u0b70', '\u0b82', '\u0b82', '\u0b83', '\u0bb9', '\u0bbe', '\u0bd7', '\u0be7', '\u0bef', '\u0bf0', '\u0bf2', '\u0c01', '\u0c03', '\u0c05', '\u0c39', '\u0c3e', '\u0c56', '\u0c60', '\u0c61', '\u0c66', '\u0c6f', '\u0c82', '\u0c83', '\u0c85', '\u0cb9', '\u0cbe', '\u0cd6', '\u0cde', '\u0ce1', '\u0ce6', '\u0cef', '\u0d02', '\u0d03', '\u0d05', '\u0d39', '\u0d3e', '\u0d57', '\u0d60', '\u0d61', '\u0d66', '\u0d6f', '\u0d82', '\u0d83', '\u0d85', '\u0dc6', '\u0dca', '\u0df3', '\u0df4', '\u0df4', '\u0e01', '\u0e30', '\u0e31', '\u0e31', '\u0e32', '\u0e33', '\u0e34', '\u0e3a', '\u0e3f', '\u0e3f', '\u0e40', '\u0e46', '\u0e47', '\u0e4e', '\u0e4f', '\u0e4f', '\u0e50', '\u0e59', '\u0e5a', '\u0e5b', '\u0e81', '\u0eb0', '\u0eb1', '\u0eb1', '\u0eb2', '\u0eb3', '\u0eb4', '\u0ebc', '\u0ebd', '\u0ec6', '\u0ec8', '\u0ecd', '\u0ed0', '\u0ed9', '\u0edc', '\u0edd', '\u0f00', '\u0f0a', '\u0f0b', '\u0f0b', '\u0f0c', '\u0f0c', '\u0f0d', '\u0f17', '\u0f18', '\u0f19', '\u0f1a', '\u0f1f', '\u0f20', '\u0f29', '\u0f2a', '\u0f34', '\u0f35', '\u0f35', '\u0f36', '\u0f36', '\u0f37', '\u0f37', '\u0f38', '\u0f38', '\u0f39', '\u0f39', '\u0f3a', '\u0f3a', '\u0f3b', '\u0f3b', '\u0f3c', '\u0f3c', '\u0f3d', '\u0f3d', '\u0f3e', '\u0f3f', '\u0f40', '\u0f6a', '\u0f71', '\u0f84', '\u0f85', '\u0f85', '\u0f86', '\u0f87', '\u0f88', '\u0f8b', '\u0f90', '\u0fbc', '\u0fbe', '\u0fc5', '\u0fc6', '\u0fc6', '\u0fc7', '\u0fcf', '\u1000', '\u102a', '\u102c', '\u1039', '\u1040', '\u1049', '\u104a', '\u104f', '\u1050', '\u1055', '\u1056', '\u1059', '\u10a0', '\u10fb', '\u1100', '\u115f', '\u1160', '\u11f9', '\u1200', '\u135a', '\u1361', '\u1361', '\u1362', '\u1368', '\u1369', '\u1371', '\u1372', '\u1676', '\u1680', '\u1680', '\u1681', '\u169a', '\u169b', '\u169b', '\u169c', '\u169c', '\u16a0', '\u1711', '\u1712', '\u1714', '\u1720', '\u1731', '\u1732', '\u1734', '\u1735', '\u1751', '\u1752', '\u1753', '\u1760', '\u1770', '\u1772', '\u1773', '\u1780', '\u17b3', '\u17b4', '\u17d3', '\u17d4', '\u17d4', '\u17d5', '\u17d5', '\u17d6', '\u17da', '\u17db', '\u17db', '\u17dc', '\u17dc', '\u17e0', '\u17e9', '\u1800', '\u1805', '\u1806', '\u1806', '\u1807', '\u180a', '\u180b', '\u180e', '\u1810', '\u1819', '\u1820', '\u18a8', '\u18a9', '\u18a9', '\u1e00', '\u1ffe', '\u2000', '\u2006', '\u2007', '\u2007', '\u2008', '\u200a', '\u200b', '\u200b', '\u200c', '\u200f', '\u2010', '\u2010', '\u2011', '\u2011', '\u2012', '\u2013', '\u2014', '\u2014', '\u2015', '\u2016', '\u2017', '\u2017', '\u2018', '\u2019', '\u201a', '\u201a', '\u201b', '\u201d', '\u201e', '\u201e', '\u201f', '\u201f', '\u2020', '\u2021', '\u2022', '\u2023', '\u2024', '\u2026', '\u2027', '\u2027', '\u2028', '\u2029', '\u202a', '\u202e', '\u202f', '\u202f', '\u2030', '\u2037', '\u2038', '\u2038', '\u2039', '\u203a', '\u203b', '\u203b', '\u203c', '\u203c', '\u203d', '\u2043', '\u2044', '\u2044', '\u2045', '\u2045', '\u2046', '\u2046', '\u2047', '\u2057', '\u205f', '\u205f', '\u2060', '\u2060', '\u2061', '\u2063', '\u206a', '\u206f', '\u2070', '\u2071', '\u2074', '\u2074', '\u2075', '\u207c', '\u207d', '\u207d', '\u207e', '\u207e', '\u207f', '\u207f', '\u2080', '\u2080', '\u2081', '\u2084', '\u2085', '\u208c', '\u208d', '\u208d', '\u208e', '\u208e', '\u20a0', '\u20a6', '\u20a7', '\u20a7', '\u20a8', '\u20b1', '\u20d0', '\u20ea', '\u2100', '\u2102', '\u2103', '\u2103', '\u2104', '\u2104', '\u2105', '\u2105', '\u2106', '\u2108', '\u2109', '\u2109', '\u210a', '\u2112', '\u2113', '\u2113', '\u2114', '\u2115', '\u2116', '\u2116', '\u2117', '\u2120', '\u2121', '\u2122', '\u2123', '\u2125', '\u2126', '\u2126', '\u2127', '\u212a', '\u212b', '\u212b', '\u212c', '\u213f', '\u2140', '\u2140', '\u2141', '\u2153', '\u2154', '\u2155', '\u2156', '\u215a', '\u215b', '\u215b', '\u215c', '\u215d', '\u215e', '\u215e', '\u215f', '\u215f', '\u2160', '\u216b', '\u216c', '\u216f', '\u2170', '\u2179', '\u217a', '\u2183', '\u2190', '\u2199', '\u219a', '\u21d1', '\u21d2', '\u21d2', '\u21d3', '\u21d3', '\u21d4', '\u21d4', '\u21d5', '\u21ff', '\u2200', '\u2200', '\u2201', '\u2201', '\u2202', '\u2203', '\u2204', '\u2206', '\u2207', '\u2208', '\u2209', '\u220a', '\u220b', '\u220b', '\u220c', '\u220e', '\u220f', '\u220f', '\u2210', '\u2210', '\u2211', '\u2211', '\u2212', '\u2213', '\u2214', '\u2214', '\u2215', '\u2215', '\u2216', '\u2219', '\u221a', '\u221a', '\u221b', '\u221c', '\u221d', '\u2220', '\u2221', '\u2222', '\u2223', '\u2223', '\u2224', '\u2224', '\u2225', '\u2225', '\u2226', '\u2226', '\u2227', '\u222c', '\u222d', '\u222d', '\u222e', '\u222e', '\u222f', '\u2233', '\u2234', '\u2237', '\u2238', '\u223b', '\u223c', '\u223d', '\u223e', '\u2247', '\u2248', '\u2248', '\u2249', '\u224b', '\u224c', '\u224c', '\u224d', '\u2251', '\u2252', '\u2252', '\u2253', '\u225f', '\u2260', '\u2261', '\u2262', '\u2263', '\u2264', '\u2267', '\u2268', '\u2269', '\u226a', '\u226b', '\u226c', '\u226d', '\u226e', '\u226f', '\u2270', '\u2281', '\u2282', '\u2283', '\u2284', '\u2285', '\u2286', '\u2287', '\u2288', '\u2294', '\u2295', '\u2295', '\u2296', '\u2298', '\u2299', '\u2299', '\u229a', '\u22a4', '\u22a5', '\u22a5', '\u22a6', '\u22be', '\u22bf', '\u22bf', '\u22c0', '\u2311', '\u2312', '\u2312', '\u2313', '\u2328', '\u2329', '\u2329', '\u232a', '\u232a', '\u232b', '\u23b3', '\u23b4', '\u23b4', '\u23b5', '\u23b5', '\u23b6', '\u23b6', '\u23b7', '\u244a', '\u2460', '\u24bf', '\u24c0', '\u24cf', '\u24d0', '\u24e9', '\u24ea', '\u24ea', '\u24eb', '\u254b', '\u254c', '\u254f', '\u2550', '\u2574', '\u2575', '\u257f', '\u2580', '\u258f', '\u2590', '\u2591', '\u2592', '\u2595', '\u2596', '\u259f', '\u25a0', '\u25a1', '\u25a2', '\u25a2', '\u25a3', '\u25a9', '\u25aa', '\u25b1', '\u25b2', '\u25b3', '\u25b4', '\u25b5', '\u25b6', '\u25b7', '\u25b8', '\u25bb', '\u25bc', '\u25bd', '\u25be', '\u25bf', '\u25c0', '\u25c1', '\u25c2', '\u25c5', '\u25c6', '\u25c8', '\u25c9', '\u25ca', '\u25cb', '\u25cb', '\u25cc', '\u25cd', '\u25ce', '\u25d1', '\u25d2', '\u25e1', '\u25e2', '\u25e5', '\u25e6', '\u25ee', '\u25ef', '\u25ef', '\u25f0', '\u2604', '\u2605', '\u2606', '\u2607', '\u2608', '\u2609', '\u2609', '\u260a', '\u260d', '\u260e', '\u260f', '\u2610', '\u2613', '\u2616', '\u2617', '\u2619', '\u261b', '\u261c', '\u261c', '\u261d', '\u261d', '\u261e', '\u261e', '\u261f', '\u263f', '\u2640', '\u2640', '\u2641', '\u2641', '\u2642', '\u2642', '\u2643', '\u265f', '\u2660', '\u2661', '\u2662', '\u2662', '\u2663', '\u2665', '\u2666', '\u2666', '\u2667', '\u266a', '\u266b', '\u266b', '\u266c', '\u266d', '\u266e', '\u266e', '\u266f', '\u266f', '\u2670', '\u275a', '\u275b', '\u275e', '\u2761', '\u2761', '\u2762', '\u2763', '\u2764', '\u2767', '\u2768', '\u2768', '\u2769', '\u2769', '\u276a', '\u276a', '\u276b', '\u276b', '\u276c', '\u276c', '\u276d', '\u276d', '\u276e', '\u276e', '\u276f', '\u276f', '\u2770', '\u2770', '\u2771', '\u2771', '\u2772', '\u2772', '\u2773', '\u2773', '\u2774', '\u2774', '\u2775', '\u2775', '\u2776', '\u27e5', '\u27e6', '\u27e6', '\u27e7', '\u27e7', '\u27e8', '\u27e8', '\u27e9', '\u27e9', '\u27ea', '\u27ea', '\u27eb', '\u27eb', '\u27f0', '\u2982', '\u2983', '\u2983', '\u2984', '\u2984', '\u2985', '\u2985', '\u2986', '\u2986', '\u2987', '\u2987', '\u2988', '\u2988', '\u2989', '\u2989', '\u298a', '\u298a', '\u298b', '\u298b', '\u298c', '\u298c', '\u298d', '\u298d', '\u298e', '\u298e', '\u298f', '\u298f', '\u2990', '\u2990', '\u2991', '\u2991', '\u2992', '\u2992', '\u2993', '\u2993', '\u2994', '\u2994', '\u2995', '\u2995', '\u2996', '\u2996', '\u2997', '\u2997', '\u2998', '\u2998', '\u2999', '\u29d7', '\u29d8', '\u29d8', '\u29d9', '\u29d9', '\u29da', '\u29da', '\u29db', '\u29db', '\u29dc', '\u29fb', '\u29fc', '\u29fc', '\u29fd', '\u29fd', '\u29fe', '\u2aff', '\u2e80', '\u3000', '\u3001', '\u3002', '\u3003', '\u3004', '\u3005', '\u3005', '\u3006', '\u3007', '\u3008', '\u3008', '\u3009', '\u3009', '\u300a', '\u300a', '\u300b', '\u300b', '\u300c', '\u300c', '\u300d', '\u300d', '\u300e', '\u300e', '\u300f', '\u300f', '\u3010', '\u3010', '\u3011', '\u3011', '\u3012', '\u3013', '\u3014', '\u3014', '\u3015', '\u3015', '\u3016', '\u3016', '\u3017', '\u3017', '\u3018', '\u3018', '\u3019', '\u3019', '\u301a', '\u301a', '\u301b', '\u301b', '\u301c', '\u301c', '\u301d', '\u301d', '\u301e', '\u301f', '\u3020', '\u3029', '\u302a', '\u302f', '\u3030', '\u303a', '\u303b', '\u303c', '\u303d', '\u303f', '\u3041', '\u3041', '\u3042', '\u3042', '\u3043', '\u3043', '\u3044', '\u3044', '\u3045', '\u3045', '\u3046', '\u3046', '\u3047', '\u3047', '\u3048', '\u3048', '\u3049', '\u3049', '\u304a', '\u3062', '\u3063', '\u3063', '\u3064', '\u3082', '\u3083', '\u3083', '\u3084', '\u3084', '\u3085', '\u3085', '\u3086', '\u3086', '\u3087', '\u3087', '\u3088', '\u308d', '\u308e', '\u308e', '\u308f', '\u3094', '\u3095', '\u3096', '\u3099', '\u309a', '\u309b', '\u309e', '\u309f', '\u309f', '\u30a0', '\u30a1', '\u30a2', '\u30a2', '\u30a3', '\u30a3', '\u30a4', '\u30a4', '\u30a5', '\u30a5', '\u30a6', '\u30a6', '\u30a7', '\u30a7', '\u30a8', '\u30a8', '\u30a9', '\u30a9', '\u30aa', '\u30c2', '\u30c3', '\u30c3', '\u30c4', '\u30e2', '\u30e3', '\u30e3', '\u30e4', '\u30e4', '\u30e5', '\u30e5', '\u30e6', '\u30e6', '\u30e7', '\u30e7', '\u30e8', '\u30ed', '\u30ee', '\u30ee', '\u30ef', '\u30f4', '\u30f5', '\u30f6', '\u30f7', '\u30fa', '\u30fb', '\u30fb', '\u30fc', '\u30fc', '\u30fd', '\u30fd', '\u30fe', '\u31b7', '\u31f0', '\u31ff', '\u3200', '\ud7a3', '\ud800', '\udfff', '\ue000', '\uf8ff', '\uf900', '\ufa6a', '\ufb00', '\ufb1d', '\ufb1e', '\ufb1e', '\ufb1f', '\ufd3d', '\ufd3e', '\ufd3e', '\ufd3f', '\ufd3f', '\ufd50', '\ufdfb', '\ufdfc', '\ufdfc', '\ufe00', '\ufe23', '\ufe30', '\ufe34', '\ufe35', '\ufe35', '\ufe36', '\ufe36', '\ufe37', '\ufe37', '\ufe38', '\ufe38', '\ufe39', '\ufe39', '\ufe3a', '\ufe3a', '\ufe3b', '\ufe3b', '\ufe3c', '\ufe3c', '\ufe3d', '\ufe3d', '\ufe3e', '\ufe3e', '\ufe3f', '\ufe3f', '\ufe40', '\ufe40', '\ufe41', '\ufe41', '\ufe42', '\ufe42', '\ufe43', '\ufe43', '\ufe44', '\ufe44', '\ufe45', '\ufe4f', '\ufe50', '\ufe50', '\ufe51', '\ufe51', '\ufe52', '\ufe52', '\ufe54', '\ufe55', '\ufe56', '\ufe57', '\ufe58', '\ufe58', '\ufe59', '\ufe59', '\ufe5a', '\ufe5a', '\ufe5b', '\ufe5b', '\ufe5c', '\ufe5c', '\ufe5d', '\ufe5d', '\ufe5e', '\ufe5e', '\ufe5f', '\ufe68', '\ufe69', '\ufe69', '\ufe6a', '\ufe6a', '\ufe6b', '\ufe6b', '\ufe70', '\ufefc', '\ufeff', '\ufeff', '\uff01', '\uff01', '\uff02', '\uff03', '\uff04', '\uff04', '\uff05', '\uff05', '\uff06', '\uff07', '\uff08', '\uff08', '\uff09', '\uff09', '\uff0a', '\uff0b', '\uff0c', '\uff0c', '\uff0d', '\uff0d', '\uff0e', '\uff0e', '\uff0f', '\uff19', '\uff1a', '\uff1b', '\uff1c', '\uff1e', '\uff1f', '\uff1f', '\uff20', '\uff3a', '\uff3b', '\uff3b', '\uff3c', '\uff3c', '\uff3d', '\uff3d', '\uff3e', '\uff5a', '\uff5b', '\uff5b', '\uff5c', '\uff5c', '\uff5d', '\uff5d', '\uff5e', '\uff5e', '\uff5f', '\uff5f', '\uff60', '\uff61', '\uff62', '\uff62', '\uff63', '\uff64', '\uff65', '\uff65', '\uff66', '\uff66', '\uff67', '\uff70', '\uff71', '\uff9d', '\uff9e', '\uff9f', '\uffa0', '\uffdc', '\uffe0', '\uffe0', '\uffe1', '\uffe1', '\uffe2', '\uffe4', '\uffe5', '\uffe6', '\uffe8', '\uffee', '\ufff9', '\ufffb', '\ufffc', '\ufffc', '\ufffd', '\uffff' };
        raw_classes = new byte[] { 19, 15, 25, 19, 22, 24, 19, 21, 5, 2, 11, 8, 9, 11, 2, 0, 1, 11, 8, 7, 14, 7, 6, 10, 7, 11, 5, 11, 0, 8, 1, 11, 0, 15, 1, 11, 19, 3, 11, 9, 8, 11, 11, 11, 11, 2, 11, 15, 11, 9, 8, 11, 16, 11, 11, 2, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 16, 11, 16, 11, 11, 11, 11, 11, 11, 11, 11, 19, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 19, 11, 7, 15, 19, 11, 19, 11, 19, 11, 19, 11, 19, 10, 11, 19, 11, 19, 11, 19, 11, 19, 10, 11, 19, 11, 19, 11, 19, 11, 19, 11, 19, 11, 19, 11, 19, 11, 19, 11, 19, 11, 10, 11, 19, 11, 19, 11, 19, 10, 11, 8, 11, 19, 11, 19, 11, 10, 19, 11, 19, 11, 19, 11, 19, 11, 10, 19, 11, 19, 11, 19, 11, 10, 11, 19, 11, 19, 10, 11, 19, 11, 19, 11, 10, 19, 11, 19, 11, 10, 19, 11, 19, 11, 10, 19, 11, 19, 11, 20, 19, 20, 19, 8, 20, 19, 11, 10, 4, 20, 19, 20, 19, 20, 19, 10, 20, 11, 15, 3, 11, 19, 11, 10, 11, 19, 11, 19, 11, 19, 0, 1, 0, 1, 19, 11, 19, 11, 19, 11, 19, 11, 19, 11, 20, 19, 10, 11, 20, 19, 11, 12, 19, 11, 15, 11, 10, 11, 15, 11, 0, 1, 11, 19, 11, 19, 11, 19, 11, 19, 20, 19, 4, 15, 4, 8, 11, 10, 11, 16, 11, 19, 10, 11, 19, 11, 15, 3, 15, 18, 19, 15, 3, 15, 17, 11, 11, 2, 0, 2, 0, 2, 11, 11, 13, 15, 22, 19, 3, 9, 11, 2, 11, 4, 11, 4, 0, 1, 11, 15, 3, 11, 19, 11, 11, 11, 0, 1, 11, 11, 11, 11, 0, 1, 8, 9, 8, 19, 11, 9, 11, 11, 11, 9, 11, 11, 11, 8, 11, 11, 11, 9, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 8, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 0, 1, 11, 0, 1, 2, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 2, 11, 5, 11, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 11, 0, 1, 0, 1, 0, 1, 11, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 11, 0, 1, 0, 1, 11, 0, 1, 11, 12, 1, 12, 4, 12, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 12, 0, 1, 0, 1, 0, 1, 0, 1, 4, 0, 1, 12, 19, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 19, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 4, 12, 11, 11, 12, 11, 19, 11, 0, 1, 11, 9, 19, 12, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 12, 1, 12, 1, 4, 5, 12, 0, 1, 0, 1, 0, 1, 12, 8, 9, 12, 11, 3, 5, 12, 8, 9, 12, 0, 1, 12, 1, 12, 1, 12, 4, 12, 5, 12, 0, 12, 1, 12, 0, 12, 1, 12, 0, 1, 0, 1, 4, 11, 4, 11, 4, 11, 9, 8, 12, 8, 11, 19, 28, 11 };
    }
}
