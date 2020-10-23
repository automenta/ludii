// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;

public class XBLOMHandlerGroupElement extends XBLOMElement
{
    protected XBLOMHandlerGroupElement() {
    }
    
    public XBLOMHandlerGroupElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "handlerGroup";
    }
    
    @Override
    protected Node newNode() {
        return new XBLOMHandlerGroupElement();
    }
}
