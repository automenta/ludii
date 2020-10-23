// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.apache.batik.dom.events.NodeEventTarget;
import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Element;
import org.apache.batik.css.engine.CSSNavigableNode;
import org.apache.batik.dom.AbstractDocumentFragment;

public class SVGOMUseShadowRoot extends AbstractDocumentFragment implements CSSNavigableNode, IdContainer
{
    protected Element cssParentElement;
    protected boolean isLocal;
    
    protected SVGOMUseShadowRoot() {
    }
    
    public SVGOMUseShadowRoot(final AbstractDocument owner, final Element parent, final boolean isLocal) {
        this.ownerDocument = owner;
        this.cssParentElement = parent;
        this.isLocal = isLocal;
    }
    
    @Override
    public boolean isReadonly() {
        return false;
    }
    
    @Override
    public void setReadonly(final boolean v) {
    }
    
    @Override
    public Element getElementById(final String id) {
        return this.ownerDocument.getChildElementById(this, id);
    }
    
    @Override
    public Node getCSSParentNode() {
        return this.cssParentElement;
    }
    
    @Override
    public Node getCSSPreviousSibling() {
        return null;
    }
    
    @Override
    public Node getCSSNextSibling() {
        return null;
    }
    
    @Override
    public Node getCSSFirstChild() {
        return this.getFirstChild();
    }
    
    @Override
    public Node getCSSLastChild() {
        return this.getLastChild();
    }
    
    @Override
    public boolean isHiddenFromSelectors() {
        return false;
    }
    
    @Override
    public NodeEventTarget getParentNodeEventTarget() {
        return (NodeEventTarget)this.getCSSParentNode();
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMUseShadowRoot();
    }
}
