// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGAnimateColorElement;

public class SVGOMAnimateColorElement extends SVGOMAnimationElement implements SVGAnimateColorElement
{
    protected SVGOMAnimateColorElement() {
    }
    
    public SVGOMAnimateColorElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "animateColor";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMAnimateColorElement();
    }
}
