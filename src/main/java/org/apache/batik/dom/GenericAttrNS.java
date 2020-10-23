// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;

public class GenericAttrNS extends AbstractAttrNS
{
    protected boolean readonly;
    
    protected GenericAttrNS() {
    }
    
    public GenericAttrNS(final String nsURI, final String qname, final AbstractDocument owner) throws DOMException {
        super(nsURI, qname, owner);
        this.setNodeName(qname);
    }
    
    @Override
    public boolean isReadonly() {
        return this.readonly;
    }
    
    @Override
    public void setReadonly(final boolean v) {
        this.readonly = v;
    }
    
    @Override
    protected Node newNode() {
        return new GenericAttrNS();
    }
}
