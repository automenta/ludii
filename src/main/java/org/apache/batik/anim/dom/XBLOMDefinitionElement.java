// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.dom.util.DOMUtilities;
import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;

public class XBLOMDefinitionElement extends XBLOMElement
{
    protected XBLOMDefinitionElement() {
    }
    
    public XBLOMDefinitionElement(final String prefix, final AbstractDocument owner) {
        super(prefix, owner);
    }
    
    @Override
    public String getLocalName() {
        return "definition";
    }
    
    @Override
    protected Node newNode() {
        return new XBLOMDefinitionElement();
    }
    
    public String getElementNamespaceURI() {
        final String qname = this.getAttributeNS(null, "element");
        final String prefix = DOMUtilities.getPrefix(qname);
        final String ns = this.lookupNamespaceURI(prefix);
        if (ns == null) {
            throw this.createDOMException((short)14, "prefix", new Object[] { this.getNodeType(), this.getNodeName(), prefix });
        }
        return ns;
    }
    
    public String getElementLocalName() {
        final String qname = this.getAttributeNS(null, "element");
        return DOMUtilities.getLocalName(qname);
    }
}
