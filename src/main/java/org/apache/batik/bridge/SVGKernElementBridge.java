// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.List;
import org.apache.batik.gvt.font.UnicodeRange;
import java.util.StringTokenizer;
import java.util.ArrayList;
import org.apache.batik.gvt.font.Kern;
import org.w3c.dom.Element;

public abstract class SVGKernElementBridge extends AbstractSVGBridge
{
    public Kern createKern(final BridgeContext ctx, final Element kernElement, final SVGGVTFont font) {
        final String u1 = kernElement.getAttributeNS(null, "u1");
        final String u2 = kernElement.getAttributeNS(null, "u2");
        final String g1 = kernElement.getAttributeNS(null, "g1");
        final String g2 = kernElement.getAttributeNS(null, "g2");
        String k = kernElement.getAttributeNS(null, "k");
        if (k.length() == 0) {
            k = "0";
        }
        final float kernValue = Float.parseFloat(k);
        int firstGlyphLen = 0;
        int secondGlyphLen = 0;
        int[] firstGlyphSet = null;
        int[] secondGlyphSet = null;
        final List firstUnicodeRanges = new ArrayList();
        final List secondUnicodeRanges = new ArrayList();
        StringTokenizer st = new StringTokenizer(u1, ",");
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            if (token.startsWith("U+")) {
                firstUnicodeRanges.add(new UnicodeRange(token));
            }
            else {
                final int[] glyphCodes = font.getGlyphCodesForUnicode(token);
                if (firstGlyphSet == null) {
                    firstGlyphSet = glyphCodes;
                    firstGlyphLen = glyphCodes.length;
                }
                else {
                    if (firstGlyphLen + glyphCodes.length > firstGlyphSet.length) {
                        int sz = firstGlyphSet.length * 2;
                        if (sz < firstGlyphLen + glyphCodes.length) {
                            sz = firstGlyphLen + glyphCodes.length;
                        }
                        final int[] tmp = new int[sz];
                        System.arraycopy(firstGlyphSet, 0, tmp, 0, firstGlyphLen);
                        firstGlyphSet = tmp;
                    }
                    for (final int glyphCode : glyphCodes) {
                        firstGlyphSet[firstGlyphLen++] = glyphCode;
                    }
                }
            }
        }
        st = new StringTokenizer(u2, ",");
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            if (token.startsWith("U+")) {
                secondUnicodeRanges.add(new UnicodeRange(token));
            }
            else {
                final int[] glyphCodes = font.getGlyphCodesForUnicode(token);
                if (secondGlyphSet == null) {
                    secondGlyphSet = glyphCodes;
                    secondGlyphLen = glyphCodes.length;
                }
                else {
                    if (secondGlyphLen + glyphCodes.length > secondGlyphSet.length) {
                        int sz = secondGlyphSet.length * 2;
                        if (sz < secondGlyphLen + glyphCodes.length) {
                            sz = secondGlyphLen + glyphCodes.length;
                        }
                        final int[] tmp = new int[sz];
                        System.arraycopy(secondGlyphSet, 0, tmp, 0, secondGlyphLen);
                        secondGlyphSet = tmp;
                    }
                    for (final int glyphCode : glyphCodes) {
                        secondGlyphSet[secondGlyphLen++] = glyphCode;
                    }
                }
            }
        }
        st = new StringTokenizer(g1, ",");
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            final int[] glyphCodes = font.getGlyphCodesForName(token);
            if (firstGlyphSet == null) {
                firstGlyphSet = glyphCodes;
                firstGlyphLen = glyphCodes.length;
            }
            else {
                if (firstGlyphLen + glyphCodes.length > firstGlyphSet.length) {
                    int sz = firstGlyphSet.length * 2;
                    if (sz < firstGlyphLen + glyphCodes.length) {
                        sz = firstGlyphLen + glyphCodes.length;
                    }
                    final int[] tmp = new int[sz];
                    System.arraycopy(firstGlyphSet, 0, tmp, 0, firstGlyphLen);
                    firstGlyphSet = tmp;
                }
                for (final int glyphCode : glyphCodes) {
                    firstGlyphSet[firstGlyphLen++] = glyphCode;
                }
            }
        }
        st = new StringTokenizer(g2, ",");
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            final int[] glyphCodes = font.getGlyphCodesForName(token);
            if (secondGlyphSet == null) {
                secondGlyphSet = glyphCodes;
                secondGlyphLen = glyphCodes.length;
            }
            else {
                if (secondGlyphLen + glyphCodes.length > secondGlyphSet.length) {
                    int sz = secondGlyphSet.length * 2;
                    if (sz < secondGlyphLen + glyphCodes.length) {
                        sz = secondGlyphLen + glyphCodes.length;
                    }
                    final int[] tmp = new int[sz];
                    System.arraycopy(secondGlyphSet, 0, tmp, 0, secondGlyphLen);
                    secondGlyphSet = tmp;
                }
                for (final int glyphCode : glyphCodes) {
                    secondGlyphSet[secondGlyphLen++] = glyphCode;
                }
            }
        }
        int[] firstGlyphs;
        if (firstGlyphLen == 0 || firstGlyphLen == firstGlyphSet.length) {
            firstGlyphs = firstGlyphSet;
        }
        else {
            firstGlyphs = new int[firstGlyphLen];
            System.arraycopy(firstGlyphSet, 0, firstGlyphs, 0, firstGlyphLen);
        }
        int[] secondGlyphs;
        if (secondGlyphLen == 0 || secondGlyphLen == secondGlyphSet.length) {
            secondGlyphs = secondGlyphSet;
        }
        else {
            secondGlyphs = new int[secondGlyphLen];
            System.arraycopy(secondGlyphSet, 0, secondGlyphs, 0, secondGlyphLen);
        }
        final UnicodeRange[] firstRanges = new UnicodeRange[firstUnicodeRanges.size()];
        firstUnicodeRanges.toArray(firstRanges);
        final UnicodeRange[] secondRanges = new UnicodeRange[secondUnicodeRanges.size()];
        secondUnicodeRanges.toArray(secondRanges);
        return new Kern(firstGlyphs, secondGlyphs, firstRanges, secondRanges, kernValue);
    }
}
