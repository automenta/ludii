// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGFEFuncGElement;

public class SVGOMFEFuncGElement extends SVGOMComponentTransferFunctionElement implements SVGFEFuncGElement
{
    protected SVGOMFEFuncGElement() {
    }
    
    public SVGOMFEFuncGElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "feFuncG";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFEFuncGElement();
    }
}
