// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;

public class BindableElement extends SVGGraphicsElement
{
    protected String namespaceURI;
    protected String localName;
    protected XBLOMShadowTreeElement xblShadowTree;
    
    protected BindableElement() {
    }
    
    public BindableElement(final String prefix, final AbstractDocument owner, final String ns, final String ln) {
        super(prefix, owner);
        this.namespaceURI = ns;
        this.localName = ln;
    }
    
    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }
    
    @Override
    public String getLocalName() {
        return this.localName;
    }
    
    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return null;
    }
    
    @Override
    protected Node newNode() {
        return new BindableElement(null, null, this.namespaceURI, this.localName);
    }
    
    public void setShadowTree(final XBLOMShadowTreeElement s) {
        this.xblShadowTree = s;
    }
    
    public XBLOMShadowTreeElement getShadowTree() {
        return this.xblShadowTree;
    }
    
    @Override
    public Node getCSSFirstChild() {
        if (this.xblShadowTree != null) {
            return this.xblShadowTree.getFirstChild();
        }
        return null;
    }
    
    @Override
    public Node getCSSLastChild() {
        return this.getCSSFirstChild();
    }
}
