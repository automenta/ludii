// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGFEFuncAElement;

public class SVGOMFEFuncAElement extends SVGOMComponentTransferFunctionElement implements SVGFEFuncAElement
{
    protected SVGOMFEFuncAElement() {
    }
    
    public SVGOMFEFuncAElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "feFuncA";
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMFEFuncAElement();
    }
}
