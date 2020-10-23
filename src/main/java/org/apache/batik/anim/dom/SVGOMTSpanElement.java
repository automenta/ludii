// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGTSpanElement;

public class SVGOMTSpanElement extends SVGOMTextPositioningElement implements SVGTSpanElement
{
    protected SVGOMTSpanElement() {
    }
    
    public SVGOMTSpanElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "tspan";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMTSpanElement();
    }
}
