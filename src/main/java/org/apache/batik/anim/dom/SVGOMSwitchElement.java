// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGSwitchElement;

public class SVGOMSwitchElement extends SVGGraphicsElement implements SVGSwitchElement
{
    protected SVGOMSwitchElement() {
    }
    
    public SVGOMSwitchElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "switch";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMSwitchElement();
    }
}
