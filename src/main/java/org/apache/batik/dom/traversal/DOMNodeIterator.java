// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.traversal;

import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.traversal.NodeIterator;

public class DOMNodeIterator implements NodeIterator
{
    protected static final short INITIAL = 0;
    protected static final short INVALID = 1;
    protected static final short FORWARD = 2;
    protected static final short BACKWARD = 3;
    protected AbstractDocument document;
    protected Node root;
    protected int whatToShow;
    protected NodeFilter filter;
    protected boolean expandEntityReferences;
    protected short state;
    protected Node referenceNode;
    
    public DOMNodeIterator(final AbstractDocument doc, final Node n, final int what, final NodeFilter nf, final boolean exp) {
        this.document = doc;
        this.root = n;
        this.whatToShow = what;
        this.filter = nf;
        this.expandEntityReferences = exp;
        this.referenceNode = this.root;
    }
    
    @Override
    public Node getRoot() {
        return this.root;
    }
    
    @Override
    public int getWhatToShow() {
        return this.whatToShow;
    }
    
    @Override
    public NodeFilter getFilter() {
        return this.filter;
    }
    
    @Override
    public boolean getExpandEntityReferences() {
        return this.expandEntityReferences;
    }
    
    @Override
    public Node nextNode() {
        switch (this.state) {
            case 1: {
                throw this.document.createDOMException((short)11, "detached.iterator", null);
            }
            case 0:
            case 3: {
                this.state = 2;
                return this.referenceNode;
            }
            default: {
                do {
                    this.unfilteredNextNode();
                    if (this.referenceNode == null) {
                        return null;
                    }
                } while ((this.whatToShow & 1 << this.referenceNode.getNodeType() - 1) == 0x0 || (this.filter != null && this.filter.acceptNode(this.referenceNode) != 1));
                return this.referenceNode;
            }
        }
    }
    
    @Override
    public Node previousNode() {
        switch (this.state) {
            case 1: {
                throw this.document.createDOMException((short)11, "detached.iterator", null);
            }
            case 0:
            case 2: {
                this.state = 3;
                return this.referenceNode;
            }
            default: {
                do {
                    this.unfilteredPreviousNode();
                    if (this.referenceNode == null) {
                        return this.referenceNode;
                    }
                } while ((this.whatToShow & 1 << this.referenceNode.getNodeType() - 1) == 0x0 || (this.filter != null && this.filter.acceptNode(this.referenceNode) != 1));
                return this.referenceNode;
            }
        }
    }
    
    @Override
    public void detach() {
        this.state = 1;
        this.document.detachNodeIterator(this);
    }
    
    public void nodeToBeRemoved(final Node removedNode) {
        if (this.state == 1) {
            return;
        }
        Node node;
        for (node = this.referenceNode; node != null && node != this.root && node != removedNode; node = node.getParentNode()) {}
        if (node == null || node == this.root) {
            return;
        }
        if (this.state == 3) {
            if (node.getNodeType() != 5 || this.expandEntityReferences) {
                final Node n = node.getFirstChild();
                if (n != null) {
                    this.referenceNode = n;
                    return;
                }
            }
            Node n = node.getNextSibling();
            if (n != null) {
                this.referenceNode = n;
                return;
            }
            n = node;
            while ((n = n.getParentNode()) != null && n != this.root) {
                final Node t = n.getNextSibling();
                if (t != null) {
                    this.referenceNode = t;
                    return;
                }
            }
            this.referenceNode = null;
        }
        else {
            Node n = node.getPreviousSibling();
            if (n == null) {
                this.referenceNode = node.getParentNode();
                return;
            }
            if (n.getNodeType() != 5 || this.expandEntityReferences) {
                Node t;
                while ((t = n.getLastChild()) != null) {
                    n = t;
                }
            }
            this.referenceNode = n;
        }
    }
    
    protected void unfilteredNextNode() {
        if (this.referenceNode == null) {
            return;
        }
        if (this.referenceNode.getNodeType() != 5 || this.expandEntityReferences) {
            final Node n = this.referenceNode.getFirstChild();
            if (n != null) {
                this.referenceNode = n;
                return;
            }
        }
        Node n = this.referenceNode.getNextSibling();
        if (n != null) {
            this.referenceNode = n;
            return;
        }
        n = this.referenceNode;
        while ((n = n.getParentNode()) != null && n != this.root) {
            final Node t = n.getNextSibling();
            if (t != null) {
                this.referenceNode = t;
                return;
            }
        }
        this.referenceNode = null;
    }
    
    protected void unfilteredPreviousNode() {
        if (this.referenceNode == null) {
            return;
        }
        if (this.referenceNode == this.root) {
            this.referenceNode = null;
            return;
        }
        Node n = this.referenceNode.getPreviousSibling();
        if (n == null) {
            this.referenceNode = this.referenceNode.getParentNode();
            return;
        }
        if (n.getNodeType() != 5 || this.expandEntityReferences) {
            Node t;
            while ((t = n.getLastChild()) != null) {
                n = t;
            }
        }
        this.referenceNode = n;
    }
}
