// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;

public class SVGOMFlowParaElement extends SVGOMTextPositioningElement
{
    protected SVGOMFlowParaElement() {
    }
    
    public SVGOMFlowParaElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "flowPara";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFlowParaElement();
    }
}
