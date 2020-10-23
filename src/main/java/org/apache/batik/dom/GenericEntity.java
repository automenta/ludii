// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;

public class GenericEntity extends AbstractEntity
{
    protected boolean readonly;
    
    protected GenericEntity() {
    }
    
    public GenericEntity(final String name, final String pubId, final String sysId, final AbstractDocument owner) {
        this.ownerDocument = owner;
        this.setNodeName(name);
        this.setPublicId(pubId);
        this.setSystemId(sysId);
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
        return new GenericEntity();
    }
}
