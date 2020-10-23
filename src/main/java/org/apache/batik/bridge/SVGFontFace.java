// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.gvt.font.GVTFontFace;
import java.util.List;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.w3c.dom.Element;

public class SVGFontFace extends FontFace
{
    Element fontFaceElement;
    GVTFontFamily fontFamily;
    
    public SVGFontFace(final Element fontFaceElement, final List srcs, final String familyName, final float unitsPerEm, final String fontWeight, final String fontStyle, final String fontVariant, final String fontStretch, final float slope, final String panose1, final float ascent, final float descent, final float strikethroughPosition, final float strikethroughThickness, final float underlinePosition, final float underlineThickness, final float overlinePosition, final float overlineThickness) {
        super(srcs, familyName, unitsPerEm, fontWeight, fontStyle, fontVariant, fontStretch, slope, panose1, ascent, descent, strikethroughPosition, strikethroughThickness, underlinePosition, underlineThickness, overlinePosition, overlineThickness);
        this.fontFamily = null;
        this.fontFaceElement = fontFaceElement;
    }
    
    @Override
    public GVTFontFamily getFontFamily(final BridgeContext ctx) {
        if (this.fontFamily != null) {
            return this.fontFamily;
        }
        final Element fontElt = SVGUtilities.getParentElement(this.fontFaceElement);
        if (fontElt.getNamespaceURI().equals("http://www.w3.org/2000/svg") && fontElt.getLocalName().equals("font")) {
            return new SVGFontFamily(this, fontElt, ctx);
        }
        return this.fontFamily = super.getFontFamily(ctx);
    }
    
    public Element getFontFaceElement() {
        return this.fontFaceElement;
    }
    
    @Override
    protected Element getBaseElement(final BridgeContext ctx) {
        if (this.fontFaceElement != null) {
            return this.fontFaceElement;
        }
        return super.getBaseElement(ctx);
    }
}
