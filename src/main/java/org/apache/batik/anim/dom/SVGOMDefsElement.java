// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGDefsElement;

public class SVGOMDefsElement extends SVGGraphicsElement implements SVGDefsElement
{
    protected SVGOMDefsElement() {
    }
    
    public SVGOMDefsElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "defs";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMDefsElement();
    }
}
