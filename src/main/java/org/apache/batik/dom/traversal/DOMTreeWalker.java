// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.traversal;

import org.apache.batik.dom.AbstractNode;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.TreeWalker;

public class DOMTreeWalker implements TreeWalker
{
    protected Node root;
    protected int whatToShow;
    protected NodeFilter filter;
    protected boolean expandEntityReferences;
    protected Node currentNode;
    
    public DOMTreeWalker(final Node n, final int what, final NodeFilter nf, final boolean exp) {
        this.root = n;
        this.whatToShow = what;
        this.filter = nf;
        this.expandEntityReferences = exp;
        this.currentNode = this.root;
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
    public Node getCurrentNode() {
        return this.currentNode;
    }
    
    @Override
    public void setCurrentNode(final Node n) {
        if (n == null) {
            throw ((AbstractNode)this.root).createDOMException((short)9, "null.current.node", null);
        }
        this.currentNode = n;
    }
    
    @Override
    public Node parentNode() {
        final Node result = this.parentNode(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }
    
    @Override
    public Node firstChild() {
        final Node result = this.firstChild(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }
    
    @Override
    public Node lastChild() {
        final Node result = this.lastChild(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }
    
    @Override
    public Node previousSibling() {
        final Node result = this.previousSibling(this.currentNode, this.root);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }
    
    @Override
    public Node nextSibling() {
        final Node result = this.nextSibling(this.currentNode, this.root);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }
    
    @Override
    public Node previousNode() {
        Node result = this.previousSibling(this.currentNode, this.root);
        if (result == null) {
            result = this.parentNode(this.currentNode);
            if (result != null) {
                this.currentNode = result;
            }
            return result;
        }
        Node last;
        for (Node n = last = this.lastChild(result); n != null; n = this.lastChild(last)) {
            last = n;
        }
        return this.currentNode = ((last != null) ? last : result);
    }
    
    @Override
    public Node nextNode() {
        Node result;
        if ((result = this.firstChild(this.currentNode)) != null) {
            return this.currentNode = result;
        }
        if ((result = this.nextSibling(this.currentNode, this.root)) != null) {
            return this.currentNode = result;
        }
        Node parent = this.currentNode;
        do {
            parent = this.parentNode(parent);
            if (parent == null) {
                return null;
            }
        } while ((result = this.nextSibling(parent, this.root)) == null);
        return this.currentNode = result;
    }
    
    protected Node parentNode(final Node n) {
        if (n == this.root) {
            return null;
        }
        Node result = n;
        do {
            result = result.getParentNode();
            if (result == null) {
                return null;
            }
        } while ((this.whatToShow & 1 << result.getNodeType() - 1) == 0x0 || (this.filter != null && this.filter.acceptNode(result) != 1));
        return result;
    }
    
    protected Node firstChild(final Node n) {
        if (n.getNodeType() == 5 && !this.expandEntityReferences) {
            return null;
        }
        final Node result = n.getFirstChild();
        if (result == null) {
            return null;
        }
        switch (this.acceptNode(result)) {
            case 1: {
                return result;
            }
            case 3: {
                final Node t = this.firstChild(result);
                if (t != null) {
                    return t;
                }
                break;
            }
        }
        return this.nextSibling(result, n);
    }
    
    protected Node lastChild(final Node n) {
        if (n.getNodeType() == 5 && !this.expandEntityReferences) {
            return null;
        }
        final Node result = n.getLastChild();
        if (result == null) {
            return null;
        }
        switch (this.acceptNode(result)) {
            case 1: {
                return result;
            }
            case 3: {
                final Node t = this.lastChild(result);
                if (t != null) {
                    return t;
                }
                break;
            }
        }
        return this.previousSibling(result, n);
    }
    
    protected Node previousSibling(Node n, final Node root) {
        while (n != root) {
            Node result = n.getPreviousSibling();
            if (result == null) {
                result = n.getParentNode();
                if (result == null || result == root) {
                    return null;
                }
                if (this.acceptNode(result) != 3) {
                    return null;
                }
                n = result;
            }
            else {
                switch (this.acceptNode(result)) {
                    case 1: {
                        return result;
                    }
                    case 3: {
                        final Node t = this.lastChild(result);
                        if (t != null) {
                            return t;
                        }
                        break;
                    }
                }
                n = result;
            }
        }
        return null;
    }
    
    protected Node nextSibling(Node n, final Node root) {
        while (n != root) {
            Node result = n.getNextSibling();
            if (result == null) {
                result = n.getParentNode();
                if (result == null || result == root) {
                    return null;
                }
                if (this.acceptNode(result) != 3) {
                    return null;
                }
                n = result;
            }
            else {
                switch (this.acceptNode(result)) {
                    case 1: {
                        return result;
                    }
                    case 3: {
                        final Node t = this.firstChild(result);
                        if (t != null) {
                            return t;
                        }
                        break;
                    }
                }
                n = result;
            }
        }
        return null;
    }
    
    protected short acceptNode(final Node n) {
        if ((this.whatToShow & 1 << n.getNodeType() - 1) == 0x0) {
            return 3;
        }
        if (this.filter == null) {
            return 1;
        }
        return this.filter.acceptNode(n);
    }
}
