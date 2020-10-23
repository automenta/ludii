// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGTextContentElement;

public class SVGOMFlowDivElement extends SVGOMTextContentElement implements SVGTextContentElement
{
    protected SVGOMFlowDivElement() {
    }
    
    public SVGOMFlowDivElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "flowDiv";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFlowDivElement();
    }
}
