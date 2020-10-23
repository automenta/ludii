// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGAnimateTransformElement;

public class SVGOMAnimateTransformElement extends SVGOMAnimationElement implements SVGAnimateTransformElement
{
    protected static final AttributeInitializer attributeInitializer;
    
    protected SVGOMAnimateTransformElement() {
    }
    
    public SVGOMAnimateTransformElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "animateTransform";
    }
    
    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return SVGOMAnimateTransformElement.attributeInitializer;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMAnimateTransformElement();
    }
    
    static {
        (attributeInitializer = new AttributeInitializer(1)).addAttribute(null, null, "type", "translate");
    }
}
