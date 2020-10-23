// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGTRefElement;

public class SVGOMTRefElement extends SVGURIReferenceTextPositioningElement implements SVGTRefElement
{
    protected SVGOMTRefElement() {
    }
    
    public SVGOMTRefElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "tref";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMTRefElement();
    }
}
