// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.NodeList;
import org.apache.batik.gvt.text.ArabicTextHandler;
import org.apache.batik.gvt.font.GVTFontFace;
import org.w3c.dom.Element;

public class SVGFontElementBridge extends AbstractSVGBridge
{
    @Override
    public String getLocalName() {
        return "font";
    }
    
    public SVGGVTFont createFont(final BridgeContext ctx, final Element fontElement, final Element textElement, final float size, final GVTFontFace fontFace) {
        final NodeList glyphElements = fontElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "glyph");
        final int numGlyphs = glyphElements.getLength();
        final String[] glyphCodes = new String[numGlyphs];
        final String[] glyphNames = new String[numGlyphs];
        final String[] glyphLangs = new String[numGlyphs];
        final String[] glyphOrientations = new String[numGlyphs];
        final String[] glyphForms = new String[numGlyphs];
        final Element[] glyphElementArray = new Element[numGlyphs];
        for (int i = 0; i < numGlyphs; ++i) {
            final Element glyphElement = (Element)glyphElements.item(i);
            glyphCodes[i] = glyphElement.getAttributeNS(null, "unicode");
            if (glyphCodes[i].length() > 1 && ArabicTextHandler.arabicChar(glyphCodes[i].charAt(0))) {
                glyphCodes[i] = new StringBuffer(glyphCodes[i]).reverse().toString();
            }
            glyphNames[i] = glyphElement.getAttributeNS(null, "glyph-name");
            glyphLangs[i] = glyphElement.getAttributeNS(null, "lang");
            glyphOrientations[i] = glyphElement.getAttributeNS(null, "orientation");
            glyphForms[i] = glyphElement.getAttributeNS(null, "arabic-form");
            glyphElementArray[i] = glyphElement;
        }
        final NodeList missingGlyphElements = fontElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "missing-glyph");
        Element missingGlyphElement = null;
        if (missingGlyphElements.getLength() > 0) {
            missingGlyphElement = (Element)missingGlyphElements.item(0);
        }
        final NodeList hkernElements = fontElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "hkern");
        final Element[] hkernElementArray = new Element[hkernElements.getLength()];
        for (int j = 0; j < hkernElementArray.length; ++j) {
            final Element hkernElement = (Element)hkernElements.item(j);
            hkernElementArray[j] = hkernElement;
        }
        final NodeList vkernElements = fontElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "vkern");
        final Element[] vkernElementArray = new Element[vkernElements.getLength()];
        for (int k = 0; k < vkernElementArray.length; ++k) {
            final Element vkernElement = (Element)vkernElements.item(k);
            vkernElementArray[k] = vkernElement;
        }
        return new SVGGVTFont(size, fontFace, glyphCodes, glyphNames, glyphLangs, glyphOrientations, glyphForms, ctx, glyphElementArray, missingGlyphElement, hkernElementArray, vkernElementArray, textElement);
    }
}
