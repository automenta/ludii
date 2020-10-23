// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGGlyphElement;

public class SVGOMGlyphElement extends SVGStylableElement implements SVGGlyphElement
{
    protected SVGOMGlyphElement() {
    }
    
    public SVGOMGlyphElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "glyph";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMGlyphElement();
    }
}
