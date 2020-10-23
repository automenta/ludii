// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGSetElement;

public class SVGOMSetElement extends SVGOMAnimationElement implements SVGSetElement
{
    protected SVGOMSetElement() {
    }
    
    public SVGOMSetElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "set";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMSetElement();
    }
}
