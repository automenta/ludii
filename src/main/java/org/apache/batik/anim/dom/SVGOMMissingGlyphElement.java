// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGMissingGlyphElement;

public class SVGOMMissingGlyphElement extends SVGStylableElement implements SVGMissingGlyphElement
{
    protected SVGOMMissingGlyphElement() {
    }
    
    public SVGOMMissingGlyphElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "missing-glyph";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMMissingGlyphElement();
    }
}
