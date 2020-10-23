// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;

public class GenericAttr extends AbstractAttr
{
    protected boolean readonly;
    
    protected GenericAttr() {
    }
    
    public GenericAttr(final String name, final AbstractDocument owner) throws DOMException {
        super(name, owner);
        this.setNodeName(name);
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
        return new GenericAttr();
    }
}
