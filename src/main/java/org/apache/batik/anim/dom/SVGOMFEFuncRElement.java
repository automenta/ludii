// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGFEFuncRElement;

public class SVGOMFEFuncRElement extends SVGOMComponentTransferFunctionElement implements SVGFEFuncRElement
{
    protected SVGOMFEFuncRElement() {
    }
    
    public SVGOMFEFuncRElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "feFuncR";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFEFuncRElement();
    }
}
