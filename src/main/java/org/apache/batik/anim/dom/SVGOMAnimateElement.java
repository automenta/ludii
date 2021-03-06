// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGAnimateElement;

public class SVGOMAnimateElement extends SVGOMAnimationElement implements SVGAnimateElement
{
    protected SVGOMAnimateElement() {
    }
    
    public SVGOMAnimateElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "animate";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMAnimateElement();
    }
}
