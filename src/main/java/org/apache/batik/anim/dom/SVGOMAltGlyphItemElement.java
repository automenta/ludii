// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGAltGlyphItemElement;

public class SVGOMAltGlyphItemElement extends SVGOMElement implements SVGAltGlyphItemElement
{
    protected SVGOMAltGlyphItemElement() {
    }
    
    public SVGOMAltGlyphItemElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "altGlyphItem";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMAltGlyphItemElement();
    }
}
