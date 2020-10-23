// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;

public class GenericComment extends AbstractComment
{
    protected boolean readonly;
    
    public GenericComment() {
    }
    
    public GenericComment(final String value, final AbstractDocument owner) {
        this.ownerDocument = owner;
        this.setNodeValue(value);
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
        return new GenericComment();
    }
}
