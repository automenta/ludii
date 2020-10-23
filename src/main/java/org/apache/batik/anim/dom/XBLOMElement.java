// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.XBLConstants;

public abstract class XBLOMElement extends SVGOMElement implements XBLConstants
{
    protected String prefix;
    
    protected XBLOMElement() {
    }
    
    protected XBLOMElement(final String prefix, final AbstractDocument owner) {
        this.ownerDocument = owner;
        this.setPrefix(prefix);
    }
    
    @Override
    public String getNodeName() {
        if (this.prefix == null || this.prefix.equals("")) {
            return this.getLocalName();
        }
        return this.prefix + ':' + this.getLocalName();
    }
    
    @Override
    public String getNamespaceURI() {
        return "http://www.w3.org/2004/xbl";
    }
    
    @Override
    public void setPrefix(final String prefix) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[] { this.getNodeType(), this.getNodeName() });
        }
        if (prefix != null && !prefix.equals("") && !DOMUtilities.isValidName(prefix)) {
            throw this.createDOMException((short)5, "prefix", new Object[] { this.getNodeType(), this.getNodeName(), prefix });
        }
        this.prefix = prefix;
    }
    
    @Override
    protected Node export(final Node n, final AbstractDocument d) {
        super.export(n, d);
        final XBLOMElement e = (XBLOMElement)n;
        e.prefix = this.prefix;
        return n;
    }
    
    @Override
    protected Node deepExport(final Node n, final AbstractDocument d) {
        super.deepExport(n, d);
        final XBLOMElement e = (XBLOMElement)n;
        e.prefix = this.prefix;
        return n;
    }
    
    @Override
    protected Node copyInto(final Node n) {
        super.copyInto(n);
        final XBLOMElement e = (XBLOMElement)n;
        e.prefix = this.prefix;
        return n;
    }
    
    @Override
    protected Node deepCopyInto(final Node n) {
        super.deepCopyInto(n);
        final XBLOMElement e = (XBLOMElement)n;
        e.prefix = this.prefix;
        return n;
    }
}
