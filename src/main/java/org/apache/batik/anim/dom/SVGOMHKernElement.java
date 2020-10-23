// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGHKernElement;

public class SVGOMHKernElement extends SVGOMElement implements SVGHKernElement
{
    protected SVGOMHKernElement() {
    }
    
    public SVGOMHKernElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "hkern";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMHKernElement();
    }
}
