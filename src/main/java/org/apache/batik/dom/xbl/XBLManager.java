// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.xbl;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public interface XBLManager
{
    void startProcessing();
    
    void stopProcessing();
    
    boolean isProcessing();
    
    Node getXblParentNode(final Node p0);
    
    NodeList getXblChildNodes(final Node p0);
    
    NodeList getXblScopedChildNodes(final Node p0);
    
    Node getXblFirstChild(final Node p0);
    
    Node getXblLastChild(final Node p0);
    
    Node getXblPreviousSibling(final Node p0);
    
    Node getXblNextSibling(final Node p0);
    
    Element getXblFirstElementChild(final Node p0);
    
    Element getXblLastElementChild(final Node p0);
    
    Element getXblPreviousElementSibling(final Node p0);
    
    Element getXblNextElementSibling(final Node p0);
    
    Element getXblBoundElement(final Node p0);
    
    Element getXblShadowTree(final Node p0);
    
    NodeList getXblDefinitions(final Node p0);
}
