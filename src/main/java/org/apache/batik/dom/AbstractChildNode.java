// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;

public abstract class AbstractChildNode extends AbstractNode
{
    protected Node parentNode;
    protected Node previousSibling;
    protected Node nextSibling;
    
    @Override
    public Node getParentNode() {
        return this.parentNode;
    }
    
    @Override
    public void setParentNode(final Node v) {
        this.parentNode = v;
    }
    
    @Override
    public void setPreviousSibling(final Node v) {
        this.previousSibling = v;
    }
    
    @Override
    public Node getPreviousSibling() {
        return this.previousSibling;
    }
    
    @Override
    public void setNextSibling(final Node v) {
        this.nextSibling = v;
    }
    
    @Override
    public Node getNextSibling() {
        return this.nextSibling;
    }
}
