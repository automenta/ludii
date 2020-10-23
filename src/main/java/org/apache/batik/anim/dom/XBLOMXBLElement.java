// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;

public class XBLOMXBLElement extends XBLOMElement
{
    protected XBLOMXBLElement() {
    }
    
    public XBLOMXBLElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "xbl";
    }
    
    @Override
    protected Node newNode() {
        return new XBLOMXBLElement();
    }
}
