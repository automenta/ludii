// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.IdContainer;
import org.apache.batik.dom.xbl.XBLShadowTreeElement;

public class XBLOMShadowTreeElement extends XBLOMElement implements XBLShadowTreeElement, IdContainer
{
    protected XBLOMShadowTreeElement() {
    }
    
    public XBLOMShadowTreeElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "shadowTree";
    }
    
    @Override
    protected Node newNode() {
        return new XBLOMShadowTreeElement();
    }
    
    @Override
    public Element getElementById(final String elementId) {
        return this.getElementById(elementId, this);
    }
    
    protected Element getElementById(final String elementId, final Node n) {
        if (n.getNodeType() == 1) {
            final Element e = (Element)n;
            if (e.getAttributeNS(null, "id").equals(elementId)) {
                return (Element)n;
            }
        }
        for (Node m = n.getFirstChild(); m != null; m = m.getNextSibling()) {
            final Element result = this.getElementById(elementId, m);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
    @Override
    public Node getCSSParentNode() {
        return this.ownerDocument.getXBLManager().getXblBoundElement(this);
    }
}
