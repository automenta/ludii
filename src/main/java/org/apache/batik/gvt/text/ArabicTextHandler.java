// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.text;

import java.util.Map;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator;

public final class ArabicTextHandler
{
    private static final int arabicStart = 1536;
    private static final int arabicEnd = 1791;
    private static final AttributedCharacterIterator.Attribute ARABIC_FORM;
    private static final Integer ARABIC_NONE;
    private static final Integer ARABIC_ISOLATED;
    private static final Integer ARABIC_TERMINAL;
    private static final Integer ARABIC_INITIAL;
    private static final Integer ARABIC_MEDIAL;
    static int singleCharFirst;
    static int singleCharLast;
    static int[][] singleCharRemappings;
    static int doubleCharFirst;
    static int doubleCharLast;
    static int[][][] doubleCharRemappings;
    
    private ArabicTextHandler() {
    }
    
    public static AttributedString assignArabicForms(AttributedString as) {
        if (!containsArabic(as)) {
            return as;
        }
        AttributedCharacterIterator aci = as.getIterator();
        final int numChars = aci.getEndIndex() - aci.getBeginIndex();
        int[] charOrder = null;
        if (numChars >= 3) {
            char prevChar = aci.first();
            char c = aci.next();
            int i = 1;
            for (char nextChar = aci.next(); nextChar != '\uffff'; nextChar = aci.next(), ++i) {
                if (arabicCharTransparent(c) && hasSubstitute(prevChar, nextChar)) {
                    if (charOrder == null) {
                        charOrder = new int[numChars];
                        for (int j = 0; j < numChars; ++j) {
                            charOrder[j] = j + aci.getBeginIndex();
                        }
                    }
                    final int temp = charOrder[i];
                    charOrder[i] = charOrder[i - 1];
                    charOrder[i - 1] = temp;
                }
                prevChar = c;
                c = nextChar;
            }
        }
        if (charOrder != null) {
            final StringBuffer reorderedString = new StringBuffer(numChars);
            for (int i = 0; i < numChars; ++i) {
                final char c = aci.setIndex(charOrder[i]);
                reorderedString.append(c);
            }
            final AttributedString reorderedAS = new AttributedString(reorderedString.toString());
            for (int k = 0; k < numChars; ++k) {
                aci.setIndex(charOrder[k]);
                final Map attributes = aci.getAttributes();
                reorderedAS.addAttributes(attributes, k, k + 1);
            }
            if (charOrder[0] != aci.getBeginIndex()) {
                aci.setIndex(charOrder[0]);
                final Float x = (Float)aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.X);
                final Float y = (Float)aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.Y);
                if (x != null && !x.isNaN()) {
                    reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.X, Float.NaN, charOrder[0], charOrder[0] + 1);
                    reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.X, x, 0, 1);
                }
                if (y != null && !y.isNaN()) {
                    reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.Y, Float.NaN, charOrder[0], charOrder[0] + 1);
                    reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.Y, y, 0, 1);
                }
            }
            as = reorderedAS;
        }
        aci = as.getIterator();
        int runStart = -1;
        int idx = aci.getBeginIndex();
        for (int c2 = aci.first(); c2 != 65535; c2 = aci.next(), ++idx) {
            if (c2 >= 1536 && c2 <= 1791) {
                if (runStart == -1) {
                    runStart = idx;
                }
            }
            else if (runStart != -1) {
                as.addAttribute(ArabicTextHandler.ARABIC_FORM, ArabicTextHandler.ARABIC_NONE, runStart, idx);
                runStart = -1;
            }
        }
        if (runStart != -1) {
            as.addAttribute(ArabicTextHandler.ARABIC_FORM, ArabicTextHandler.ARABIC_NONE, runStart, idx);
        }
        aci = as.getIterator();
        int end = aci.getBeginIndex();
        Integer currentForm = ArabicTextHandler.ARABIC_NONE;
        while (aci.setIndex(end) != '\uffff') {
            final int start = aci.getRunStart(ArabicTextHandler.ARABIC_FORM);
            end = aci.getRunLimit(ArabicTextHandler.ARABIC_FORM);
            char currentChar = aci.setIndex(start);
            currentForm = (Integer)aci.getAttribute(ArabicTextHandler.ARABIC_FORM);
            if (currentForm == null) {
                continue;
            }
            int currentIndex = start;
            int prevCharIndex = start - 1;
            while (currentIndex < end) {
                final char prevChar2 = currentChar;
                for (currentChar = aci.setIndex(currentIndex); arabicCharTransparent(currentChar) && currentIndex < end; ++currentIndex, currentChar = aci.setIndex(currentIndex)) {}
                if (currentIndex >= end) {
                    break;
                }
                Integer prevForm = currentForm;
                currentForm = ArabicTextHandler.ARABIC_NONE;
                if (prevCharIndex >= start) {
                    if (arabicCharShapesRight(prevChar2) && arabicCharShapesLeft(currentChar)) {
                        ++prevForm;
                        as.addAttribute(ArabicTextHandler.ARABIC_FORM, prevForm, prevCharIndex, prevCharIndex + 1);
                        currentForm = ArabicTextHandler.ARABIC_INITIAL;
                    }
                    else if (arabicCharShaped(currentChar)) {
                        currentForm = ArabicTextHandler.ARABIC_ISOLATED;
                    }
                }
                else if (arabicCharShaped(currentChar)) {
                    currentForm = ArabicTextHandler.ARABIC_ISOLATED;
                }
                if (currentForm != ArabicTextHandler.ARABIC_NONE) {
                    as.addAttribute(ArabicTextHandler.ARABIC_FORM, currentForm, currentIndex, currentIndex + 1);
                }
                prevCharIndex = currentIndex;
                ++currentIndex;
            }
        }
        return as;
    }
    
    public static boolean arabicChar(final char c) {
        return c >= '\u0600' && c <= '\u06ff';
    }
    
    public static boolean containsArabic(final AttributedString as) {
        return containsArabic(as.getIterator());
    }
    
    public static boolean containsArabic(final AttributedCharacterIterator aci) {
        for (char c = aci.first(); c != '\uffff'; c = aci.next()) {
            if (arabicChar(c)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean arabicCharTransparent(final char c) {
        final int charVal = c;
        return charVal >= 1611 && charVal <= 1773 && (charVal <= 1621 || charVal == 1648 || (charVal >= 1750 && charVal <= 1764) || (charVal >= 1767 && charVal <= 1768) || charVal >= 1770);
    }
    
    private static boolean arabicCharShapesRight(final char c) {
        final int charVal = c;
        return (charVal >= 1570 && charVal <= 1573) || charVal == 1575 || charVal == 1577 || (charVal >= 1583 && charVal <= 1586) || charVal == 1608 || (charVal >= 1649 && charVal <= 1651) || (charVal >= 1653 && charVal <= 1655) || (charVal >= 1672 && charVal <= 1689) || charVal == 1728 || (charVal >= 1730 && charVal <= 1739) || charVal == 1741 || charVal == 1743 || (charVal >= 1746 && charVal <= 1747) || arabicCharShapesDuel(c);
    }
    
    private static boolean arabicCharShapesDuel(final char c) {
        final int charVal = c;
        return charVal == 1574 || charVal == 1576 || (charVal >= 1578 && charVal <= 1582) || (charVal >= 1587 && charVal <= 1594) || (charVal >= 1601 && charVal <= 1607) || (charVal >= 1609 && charVal <= 1610) || (charVal >= 1656 && charVal <= 1671) || (charVal >= 1690 && charVal <= 1727) || charVal == 1729 || charVal == 1740 || charVal == 1742 || (charVal >= 1744 && charVal <= 1745) || (charVal >= 1786 && charVal <= 1788);
    }
    
    private static boolean arabicCharShapesLeft(final char c) {
        return arabicCharShapesDuel(c);
    }
    
    private static boolean arabicCharShaped(final char c) {
        return arabicCharShapesRight(c);
    }
    
    public static boolean hasSubstitute(final char ch1, final char ch2) {
        if (ch1 < ArabicTextHandler.doubleCharFirst || ch1 > ArabicTextHandler.doubleCharLast) {
            return false;
        }
        final int[][] remaps = ArabicTextHandler.doubleCharRemappings[ch1 - ArabicTextHandler.doubleCharFirst];
        if (remaps == null) {
            return false;
        }
        for (final int[] remap : remaps) {
            if (remap[0] == ch2) {
                return true;
            }
        }
        return false;
    }
    
    public static int getSubstituteChar(final char ch1, final char ch2, final int form) {
        if (form == 0) {
            return -1;
        }
        if (ch1 < ArabicTextHandler.doubleCharFirst || ch1 > ArabicTextHandler.doubleCharLast) {
            return -1;
        }
        final int[][] remaps = ArabicTextHandler.doubleCharRemappings[ch1 - ArabicTextHandler.doubleCharFirst];
        if (remaps == null) {
            return -1;
        }
        for (final int[] remap : remaps) {
            if (remap[0] == ch2) {
                return remap[form];
            }
        }
        return -1;
    }
    
    public static int getSubstituteChar(final char ch, final int form) {
        if (form == 0) {
            return -1;
        }
        if (ch < ArabicTextHandler.singleCharFirst || ch > ArabicTextHandler.singleCharLast) {
            return -1;
        }
        final int[] chars = ArabicTextHandler.singleCharRemappings[ch - ArabicTextHandler.singleCharFirst];
        if (chars == null) {
            return -1;
        }
        return chars[form - 1];
    }
    
    public static String createSubstituteString(final AttributedCharacterIterator aci) {
        final int start = aci.getBeginIndex();
        final int end = aci.getEndIndex();
        final int numChar = end - start;
        final StringBuffer substString = new StringBuffer(numChar);
        for (int i = start; i < end; ++i) {
            char c = aci.setIndex(i);
            if (!arabicChar(c)) {
                substString.append(c);
            }
            else {
                final Integer form = (Integer)aci.getAttribute(ArabicTextHandler.ARABIC_FORM);
                if (charStartsLigature(c) && i + 1 < end) {
                    final char nextChar = aci.setIndex(i + 1);
                    final Integer nextForm = (Integer)aci.getAttribute(ArabicTextHandler.ARABIC_FORM);
                    if (form != null && nextForm != null) {
                        if (form.equals(ArabicTextHandler.ARABIC_TERMINAL) && nextForm.equals(ArabicTextHandler.ARABIC_INITIAL)) {
                            final int substChar = getSubstituteChar(c, nextChar, ArabicTextHandler.ARABIC_ISOLATED);
                            if (substChar > -1) {
                                substString.append((char)substChar);
                                ++i;
                                continue;
                            }
                        }
                        else if (form.equals(ArabicTextHandler.ARABIC_TERMINAL)) {
                            final int substChar = getSubstituteChar(c, nextChar, ArabicTextHandler.ARABIC_TERMINAL);
                            if (substChar > -1) {
                                substString.append((char)substChar);
                                ++i;
                                continue;
                            }
                        }
                        else if (form.equals(ArabicTextHandler.ARABIC_MEDIAL) && nextForm.equals(ArabicTextHandler.ARABIC_MEDIAL)) {
                            final int substChar = getSubstituteChar(c, nextChar, ArabicTextHandler.ARABIC_MEDIAL);
                            if (substChar > -1) {
                                substString.append((char)substChar);
                                ++i;
                                continue;
                            }
                        }
                    }
                }
                if (form != null && form > 0) {
                    final int substChar2 = getSubstituteChar(c, form);
                    if (substChar2 > -1) {
                        c = (char)substChar2;
                    }
                }
                substString.append(c);
            }
        }
        return substString.toString();
    }
    
    public static boolean charStartsLigature(final char c) {
        final int charVal = c;
        return charVal == 1611 || charVal == 1612 || charVal == 1613 || charVal == 1614 || charVal == 1615 || charVal == 1616 || charVal == 1617 || charVal == 1618 || charVal == 1570 || charVal == 1571 || charVal == 1573 || charVal == 1575;
    }
    
    public static int getNumChars(final char c) {
        if (isLigature(c)) {
            return 2;
        }
        return 1;
    }
    
    public static boolean isLigature(final char c) {
        final int charVal = c;
        return charVal >= 65136 && charVal <= 65276 && (charVal <= 65138 || charVal == 65140 || (charVal >= 65142 && charVal <= 65151) || charVal >= 65269);
    }
    
    static {
        ARABIC_FORM = GVTAttributedCharacterIterator.TextAttribute.ARABIC_FORM;
        ARABIC_NONE = GVTAttributedCharacterIterator.TextAttribute.ARABIC_NONE;
        ARABIC_ISOLATED = GVTAttributedCharacterIterator.TextAttribute.ARABIC_ISOLATED;
        ARABIC_TERMINAL = GVTAttributedCharacterIterator.TextAttribute.ARABIC_TERMINAL;
        ARABIC_INITIAL = GVTAttributedCharacterIterator.TextAttribute.ARABIC_INITIAL;
        ARABIC_MEDIAL = GVTAttributedCharacterIterator.TextAttribute.ARABIC_MEDIAL;
        ArabicTextHandler.singleCharFirst = 1569;
        ArabicTextHandler.singleCharLast = 1610;
        ArabicTextHandler.singleCharRemappings = new int[][] { { 65152, -1, -1, -1 }, { 65153, 65154, -1, -1 }, { 65155, 65156, -1, -1 }, { 65157, 65158, -1, -1 }, { 65159, 65160, -1, -1 }, { 65161, 65162, 65163, 65164 }, { 65165, 65166, -1, -1 }, { 65167, 65168, 65169, 65170 }, { 65171, 65172, -1, -1 }, { 65173, 65174, 65175, 65176 }, { 65177, 65178, 65179, 65180 }, { 65181, 65182, 65183, 65184 }, { 65185, 65186, 65187, 65188 }, { 65189, 65190, 65191, 65192 }, { 65193, 65194, -1, -1 }, { 65195, 65196, -1, -1 }, { 65197, 65198, -1, -1 }, { 65199, 65200, -1, -1 }, { 65201, 65202, 65203, 65204 }, { 65205, 65206, 65207, 65208 }, { 65209, 65210, 65211, 65212 }, { 65213, 65214, 65215, 65216 }, { 65217, 65218, 65219, 65220 }, { 65221, 65222, 65223, 65224 }, { 65225, 65226, 65227, 65228 }, { 65229, 65230, 65231, 65232 }, null, null, null, null, null, null, { 65233, 65234, 65235, 65236 }, { 65237, 65238, 65239, 65240 }, { 65241, 65242, 65243, 65244 }, { 65245, 65246, 65247, 65248 }, { 65249, 65250, 65251, 65252 }, { 65253, 65254, 65255, 65256 }, { 65257, 65258, 65259, 65260 }, { 65261, 65262, -1, -1 }, { 65263, 65264, -1, -1 }, { 65265, 65266, 65267, 65268 } };
        ArabicTextHandler.doubleCharFirst = 1570;
        ArabicTextHandler.doubleCharLast = 1618;
        ArabicTextHandler.doubleCharRemappings = new int[][][] { { { 1604, 65269, 65270, -1, -1 } }, { { 1604, 65271, 65272, -1, -1 } }, null, { { 1604, 65273, 65274, -1, -1 } }, null, { { 1604, 65275, 65276, -1, -1 } }, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, { { 32, 65136, -1, -1, -1 }, { 1600, -1, -1, -1, 65137 } }, { { 32, 65138, -1, -1, -1 } }, { { 32, 65140, -1, -1, -1 } }, { { 32, 65142, -1, -1, -1 }, { 1600, -1, -1, -1, 65143 } }, { { 32, 65144, -1, -1, -1 }, { 1600, -1, -1, -1, 65145 } }, { { 32, 65146, -1, -1, -1 }, { 1600, -1, -1, -1, 65147 } }, { { 32, 65148, -1, -1, -1 }, { 1600, -1, -1, -1, 65149 } }, { { 32, 65150, -1, -1, -1 }, { 1600, -1, -1, -1, 65151 } } };
    }
}
