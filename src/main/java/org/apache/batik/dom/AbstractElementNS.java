// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.apache.batik.dom.util.DOMUtilities;

public abstract class AbstractElementNS extends AbstractElement
{
    protected String namespaceURI;
    
    protected AbstractElementNS() {
    }
    
    protected AbstractElementNS(String nsURI, final String qname, final AbstractDocument owner) throws DOMException {
        super(qname, owner);
        if (nsURI != null && nsURI.length() == 0) {
            nsURI = null;
        }
        this.namespaceURI = nsURI;
        final String prefix = DOMUtilities.getPrefix(qname);
        if (prefix != null && (nsURI == null || ("xml".equals(prefix) && !"http://www.w3.org/XML/1998/namespace".equals(nsURI)))) {
            throw this.createDOMException((short)14, "namespace.uri", new Object[] { this.getNodeType(), this.getNodeName(), nsURI });
        }
    }
    
    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }
    
    @Override
    protected Node export(final Node n, final AbstractDocument d) {
        super.export(n, d);
        final AbstractElementNS ae = (AbstractElementNS)n;
        ae.namespaceURI = this.namespaceURI;
        return n;
    }
    
    @Override
    protected Node deepExport(final Node n, final AbstractDocument d) {
        super.deepExport(n, d);
        final AbstractElementNS ae = (AbstractElementNS)n;
        ae.namespaceURI = this.namespaceURI;
        return n;
    }
    
    @Override
    protected Node copyInto(final Node n) {
        super.copyInto(n);
        final AbstractElementNS ae = (AbstractElementNS)n;
        ae.namespaceURI = this.namespaceURI;
        return n;
    }
    
    @Override
    protected Node deepCopyInto(final Node n) {
        super.deepCopyInto(n);
        final AbstractElementNS ae = (AbstractElementNS)n;
        ae.namespaceURI = this.namespaceURI;
        return n;
    }
}
