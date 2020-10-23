// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.lang.ref.SoftReference;
import java.util.Map;
import org.apache.batik.gvt.font.GVTFont;
import org.w3c.dom.Element;
import org.apache.batik.gvt.font.GVTFontFace;
import java.text.AttributedCharacterIterator;
import org.apache.batik.gvt.font.GVTFontFamily;

public class SVGFontFamily implements GVTFontFamily
{
    public static final AttributedCharacterIterator.Attribute TEXT_COMPOUND_ID;
    protected GVTFontFace fontFace;
    protected Element fontElement;
    protected BridgeContext ctx;
    protected Boolean complex;
    
    public SVGFontFamily(final GVTFontFace fontFace, final Element fontElement, final BridgeContext ctx) {
        this.complex = null;
        this.fontFace = fontFace;
        this.fontElement = fontElement;
        this.ctx = ctx;
    }
    
    @Override
    public String getFamilyName() {
        return this.fontFace.getFamilyName();
    }
    
    @Override
    public GVTFontFace getFontFace() {
        return this.fontFace;
    }
    
    @Override
    public GVTFont deriveFont(final float size, final AttributedCharacterIterator aci) {
        return this.deriveFont(size, aci.getAttributes());
    }
    
    @Override
    public GVTFont deriveFont(final float size, final Map attrs) {
        final SVGFontElementBridge fontBridge = (SVGFontElementBridge)this.ctx.getBridge(this.fontElement);
        final SoftReference sr = attrs.get(SVGFontFamily.TEXT_COMPOUND_ID);
        final Element textElement = sr.get();
        return fontBridge.createFont(this.ctx, this.fontElement, textElement, size, this.fontFace);
    }
    
    @Override
    public boolean isComplex() {
        if (this.complex != null) {
            return this.complex;
        }
        final boolean ret = isComplex(this.fontElement, this.ctx);
        this.complex = (ret ? Boolean.TRUE : Boolean.FALSE);
        return ret;
    }
    
    public static boolean isComplex(final Element fontElement, final BridgeContext ctx) {
        final NodeList glyphElements = fontElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "glyph");
        for (int numGlyphs = glyphElements.getLength(), i = 0; i < numGlyphs; ++i) {
            final Element glyph = (Element)glyphElements.item(i);
            for (Node child = glyph.getFirstChild(); child != null; child = child.getNextSibling()) {
                if (child.getNodeType() == 1) {
                    final Element e = (Element)child;
                    final Bridge b = ctx.getBridge(e);
                    if (b != null && b instanceof GraphicsNodeBridge) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    static {
        TEXT_COMPOUND_ID = GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_ID;
    }
}
