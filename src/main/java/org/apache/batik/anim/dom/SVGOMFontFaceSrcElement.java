// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGFontFaceSrcElement;

public class SVGOMFontFaceSrcElement extends SVGOMElement implements SVGFontFaceSrcElement
{
    protected SVGOMFontFaceSrcElement() {
    }
    
    public SVGOMFontFaceSrcElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "font-face-src";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFontFaceSrcElement();
    }
}
