// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGFEFuncBElement;

public class SVGOMFEFuncBElement extends SVGOMComponentTransferFunctionElement implements SVGFEFuncBElement
{
    protected SVGOMFEFuncBElement() {
    }
    
    public SVGOMFEFuncBElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "feFuncB";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFEFuncBElement();
    }
}
