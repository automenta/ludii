// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGFontFaceUriElement;

public class SVGOMFontFaceUriElement extends SVGOMElement implements SVGFontFaceUriElement
{
    protected SVGOMFontFaceUriElement() {
    }
    
    public SVGOMFontFaceUriElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "font-face-uri";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFontFaceUriElement();
    }
}
