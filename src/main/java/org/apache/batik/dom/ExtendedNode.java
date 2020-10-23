// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Document;
import org.apache.batik.dom.events.NodeEventTarget;
import org.w3c.dom.Node;

public interface ExtendedNode extends Node, NodeEventTarget
{
    void setNodeName(final String p0);
    
    boolean isReadonly();
    
    void setReadonly(final boolean p0);
    
    void setOwnerDocument(final Document p0);
    
    void setParentNode(final Node p0);
    
    void setPreviousSibling(final Node p0);
    
    void setNextSibling(final Node p0);
    
    void setSpecified(final boolean p0);
}
