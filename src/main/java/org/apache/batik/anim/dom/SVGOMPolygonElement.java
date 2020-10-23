// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGPolygonElement;

public class SVGOMPolygonElement extends SVGPointShapeElement implements SVGPolygonElement
{
    protected SVGOMPolygonElement() {
    }
    
    public SVGOMPolygonElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "polygon";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMPolygonElement();
    }
}
