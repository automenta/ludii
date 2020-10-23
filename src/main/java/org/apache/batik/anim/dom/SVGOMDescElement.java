// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGDescElement;

public class SVGOMDescElement extends SVGDescriptiveElement implements SVGDescElement
{
    protected SVGOMDescElement() {
    }
    
    public SVGOMDescElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "desc";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMDescElement();
    }
}
