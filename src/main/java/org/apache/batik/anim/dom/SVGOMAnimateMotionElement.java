// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGAnimateMotionElement;

public class SVGOMAnimateMotionElement extends SVGOMAnimationElement implements SVGAnimateMotionElement
{
    protected static final AttributeInitializer attributeInitializer;
    
    protected SVGOMAnimateMotionElement() {
    }
    
    public SVGOMAnimateMotionElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "animateMotion";
    }
    
    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return SVGOMAnimateMotionElement.attributeInitializer;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMAnimateMotionElement();
    }
    
    static {
        (attributeInitializer = new AttributeInitializer(1)).addAttribute(null, null, "calcMode", "paced");
    }
}
