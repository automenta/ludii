// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGAltGlyphDefElement;

public class SVGOMAltGlyphDefElement extends SVGOMElement implements SVGAltGlyphDefElement
{
    protected SVGOMAltGlyphDefElement() {
    }
    
    public SVGOMAltGlyphDefElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "altGlyphDef";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMAltGlyphDefElement();
    }
}
