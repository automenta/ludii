// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGPolylineElement;

public class SVGOMPolylineElement extends SVGPointShapeElement implements SVGPolylineElement
{
    protected SVGOMPolylineElement() {
    }
    
    public SVGOMPolylineElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "polyline";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMPolylineElement();
    }
}
