// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGVKernElement;

public class SVGOMVKernElement extends SVGOMElement implements SVGVKernElement
{
    protected SVGOMVKernElement() {
    }
    
    public SVGOMVKernElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "vkern";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMVKernElement();
    }
}
