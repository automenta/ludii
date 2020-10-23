// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGFontFaceNameElement;

public class SVGOMFontFaceNameElement extends SVGOMElement implements SVGFontFaceNameElement
{
    protected SVGOMFontFaceNameElement() {
    }
    
    public SVGOMFontFaceNameElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "font-face-name";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFontFaceNameElement();
    }
}
