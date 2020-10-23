// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGFontFaceFormatElement;

public class SVGOMFontFaceFormatElement extends SVGOMElement implements SVGFontFaceFormatElement
{
    protected SVGOMFontFaceFormatElement() {
    }
    
    public SVGOMFontFaceFormatElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "font-face-format";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFontFaceFormatElement();
    }
}
