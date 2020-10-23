// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGTitleElement;

public class SVGOMTitleElement extends SVGDescriptiveElement implements SVGTitleElement
{
    protected SVGOMTitleElement() {
    }
    
    public SVGOMTitleElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "title";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMTitleElement();
    }
}
