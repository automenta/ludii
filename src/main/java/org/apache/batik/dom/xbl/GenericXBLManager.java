// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.xbl;

import org.apache.batik.dom.AbstractNode;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class GenericXBLManager implements XBLManager
{
    protected boolean isProcessing;
    
    @Override
    public void startProcessing() {
        this.isProcessing = true;
    }
    
    @Override
    public void stopProcessing() {
        this.isProcessing = false;
    }
    
    @Override
    public boolean isProcessing() {
        return this.isProcessing;
    }
    
    @Override
    public Node getXblParentNode(final Node n) {
        return n.getParentNode();
    }
    
    @Override
    public NodeList getXblChildNodes(final Node n) {
        return n.getChildNodes();
    }
    
    @Override
    public NodeList getXblScopedChildNodes(final Node n) {
        return n.getChildNodes();
    }
    
    @Override
    public Node getXblFirstChild(final Node n) {
        return n.getFirstChild();
    }
    
    @Override
    public Node getXblLastChild(final Node n) {
        return n.getLastChild();
    }
    
    @Override
    public Node getXblPreviousSibling(final Node n) {
        return n.getPreviousSibling();
    }
    
    @Override
    public Node getXblNextSibling(final Node n) {
        return n.getNextSibling();
    }
    
    @Override
    public Element getXblFirstElementChild(final Node n) {
        Node m;
        for (m = n.getFirstChild(); m != null && m.getNodeType() != 1; m = m.getNextSibling()) {}
        return (Element)m;
    }
    
    @Override
    public Element getXblLastElementChild(final Node n) {
        Node m;
        for (m = n.getLastChild(); m != null && m.getNodeType() != 1; m = m.getPreviousSibling()) {}
        return (Element)m;
    }
    
    @Override
    public Element getXblPreviousElementSibling(final Node n) {
        Node m = n;
        do {
            m = m.getPreviousSibling();
        } while (m != null && m.getNodeType() != 1);
        return (Element)m;
    }
    
    @Override
    public Element getXblNextElementSibling(final Node n) {
        Node m = n;
        do {
            m = m.getNextSibling();
        } while (m != null && m.getNodeType() != 1);
        return (Element)m;
    }
    
    @Override
    public Element getXblBoundElement(final Node n) {
        return null;
    }
    
    @Override
    public Element getXblShadowTree(final Node n) {
        return null;
    }
    
    @Override
    public NodeList getXblDefinitions(final Node n) {
        return AbstractNode.EMPTY_NODE_LIST;
    }
}
