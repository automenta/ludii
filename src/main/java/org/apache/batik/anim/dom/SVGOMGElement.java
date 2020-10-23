// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGGElement;

public class SVGOMGElement extends SVGGraphicsElement implements SVGGElement
{
    protected SVGOMGElement() {
    }
    
    public SVGOMGElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "g";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMGElement();
    }
}
