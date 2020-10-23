// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;

public class GenericEntityReference extends AbstractEntityReference
{
    protected boolean readonly;
    
    protected GenericEntityReference() {
    }
    
    public GenericEntityReference(final String name, final AbstractDocument owner) {
        super(name, owner);
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
        return new GenericEntityReference();
    }
}
