// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.gvt.font.Glyph;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.SVGGVTGlyphVector;
import org.apache.batik.gvt.font.GVTGlyphVector;
import java.text.AttributedCharacterIterator;
import java.awt.font.FontRenderContext;
import org.w3c.dom.Element;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.gvt.font.AltGlyphHandler;

public class SVGAltGlyphHandler implements AltGlyphHandler, SVGConstants
{
    private BridgeContext ctx;
    private Element textElement;
    
    public SVGAltGlyphHandler(final BridgeContext ctx, final Element textElement) {
        this.ctx = ctx;
        this.textElement = textElement;
    }
    
    @Override
    public GVTGlyphVector createGlyphVector(final FontRenderContext frc, final float fontSize, final AttributedCharacterIterator aci) {
        try {
            if ("http://www.w3.org/2000/svg".equals(this.textElement.getNamespaceURI()) && "altGlyph".equals(this.textElement.getLocalName())) {
                final SVGAltGlyphElementBridge altGlyphBridge = (SVGAltGlyphElementBridge)this.ctx.getBridge(this.textElement);
                final Glyph[] glyphArray = altGlyphBridge.createAltGlyphArray(this.ctx, this.textElement, fontSize, aci);
                if (glyphArray != null) {
                    return new SVGGVTGlyphVector(null, glyphArray, frc);
                }
            }
        }
        catch (SecurityException e) {
            this.ctx.getUserAgent().displayError(e);
            throw e;
        }
        return null;
    }
}
