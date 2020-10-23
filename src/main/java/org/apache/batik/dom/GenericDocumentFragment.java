// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;

public class GenericDocumentFragment extends AbstractDocumentFragment
{
    protected boolean readonly;
    
    protected GenericDocumentFragment() {
    }
    
    public GenericDocumentFragment(final AbstractDocument owner) {
        this.ownerDocument = owner;
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
        return new GenericDocumentFragment();
    }
}
