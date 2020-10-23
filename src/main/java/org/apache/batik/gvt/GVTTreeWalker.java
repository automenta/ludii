// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import java.util.List;

public class GVTTreeWalker
{
    protected GraphicsNode gvtRoot;
    protected GraphicsNode treeRoot;
    protected GraphicsNode currentNode;
    
    public GVTTreeWalker(final GraphicsNode treeRoot) {
        this.gvtRoot = treeRoot.getRoot();
        this.treeRoot = treeRoot;
        this.currentNode = treeRoot;
    }
    
    public GraphicsNode getRoot() {
        return this.treeRoot;
    }
    
    public GraphicsNode getGVTRoot() {
        return this.gvtRoot;
    }
    
    public void setCurrentGraphicsNode(final GraphicsNode node) {
        if (node.getRoot() != this.gvtRoot) {
            throw new IllegalArgumentException("The node " + node + " is not part of the document " + this.gvtRoot);
        }
        this.currentNode = node;
    }
    
    public GraphicsNode getCurrentGraphicsNode() {
        return this.currentNode;
    }
    
    public GraphicsNode previousGraphicsNode() {
        final GraphicsNode result = this.getPreviousGraphicsNode(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }
    
    public GraphicsNode nextGraphicsNode() {
        final GraphicsNode result = this.getNextGraphicsNode(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }
    
    public GraphicsNode parentGraphicsNode() {
        if (this.currentNode == this.treeRoot) {
            return null;
        }
        final GraphicsNode result = this.currentNode.getParent();
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }
    
    public GraphicsNode getNextSibling() {
        final GraphicsNode result = getNextSibling(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }
    
    public GraphicsNode getPreviousSibling() {
        final GraphicsNode result = getPreviousSibling(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }
    
    public GraphicsNode firstChild() {
        final GraphicsNode result = getFirstChild(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }
    
    public GraphicsNode lastChild() {
        final GraphicsNode result = getLastChild(this.currentNode);
        if (result != null) {
            this.currentNode = result;
        }
        return result;
    }
    
    protected GraphicsNode getNextGraphicsNode(final GraphicsNode node) {
        if (node == null) {
            return null;
        }
        GraphicsNode n = getFirstChild(node);
        if (n != null) {
            return n;
        }
        n = getNextSibling(node);
        if (n != null) {
            return n;
        }
        n = node;
        while ((n = n.getParent()) != null && n != this.treeRoot) {
            final GraphicsNode t = getNextSibling(n);
            if (t != null) {
                return t;
            }
        }
        return null;
    }
    
    protected GraphicsNode getPreviousGraphicsNode(final GraphicsNode node) {
        if (node == null) {
            return null;
        }
        if (node == this.treeRoot) {
            return null;
        }
        GraphicsNode n = getPreviousSibling(node);
        if (n == null) {
            return node.getParent();
        }
        GraphicsNode t;
        while ((t = getLastChild(n)) != null) {
            n = t;
        }
        return n;
    }
    
    protected static GraphicsNode getLastChild(final GraphicsNode node) {
        if (!(node instanceof CompositeGraphicsNode)) {
            return null;
        }
        final CompositeGraphicsNode parent = (CompositeGraphicsNode)node;
        final List children = parent.getChildren();
        if (children == null) {
            return null;
        }
        if (children.size() >= 1) {
            return children.get(children.size() - 1);
        }
        return null;
    }
    
    protected static GraphicsNode getPreviousSibling(final GraphicsNode node) {
        final CompositeGraphicsNode parent = node.getParent();
        if (parent == null) {
            return null;
        }
        final List children = parent.getChildren();
        if (children == null) {
            return null;
        }
        final int index = children.indexOf(node);
        if (index - 1 >= 0) {
            return children.get(index - 1);
        }
        return null;
    }
    
    protected static GraphicsNode getFirstChild(final GraphicsNode node) {
        if (!(node instanceof CompositeGraphicsNode)) {
            return null;
        }
        final CompositeGraphicsNode parent = (CompositeGraphicsNode)node;
        final List children = parent.getChildren();
        if (children == null) {
            return null;
        }
        if (children.size() >= 1) {
            return children.get(0);
        }
        return null;
    }
    
    protected static GraphicsNode getNextSibling(final GraphicsNode node) {
        final CompositeGraphicsNode parent = node.getParent();
        if (parent == null) {
            return null;
        }
        final List children = parent.getChildren();
        if (children == null) {
            return null;
        }
        final int index = children.indexOf(node);
        if (index + 1 < children.size()) {
            return children.get(index + 1);
        }
        return null;
    }
}
