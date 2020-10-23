// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.apache.batik.dom.util.DOMUtilities;
import org.w3c.dom.EntityReference;

public abstract class AbstractEntityReference extends AbstractParentChildNode implements EntityReference
{
    protected String nodeName;
    
    protected AbstractEntityReference() {
    }
    
    protected AbstractEntityReference(final String name, final AbstractDocument owner) throws DOMException {
        this.ownerDocument = owner;
        if (owner.getStrictErrorChecking() && !DOMUtilities.isValidName(name)) {
            throw this.createDOMException((short)5, "xml.name", new Object[] { name });
        }
        this.nodeName = name;
    }
    
    @Override
    public short getNodeType() {
        return 5;
    }
    
    @Override
    public void setNodeName(final String v) {
        this.nodeName = v;
    }
    
    @Override
    public String getNodeName() {
        return this.nodeName;
    }
    
    @Override
    protected Node export(final Node n, final AbstractDocument d) {
        super.export(n, d);
        final AbstractEntityReference ae = (AbstractEntityReference)n;
        ae.nodeName = this.nodeName;
        return n;
    }
    
    @Override
    protected Node deepExport(final Node n, final AbstractDocument d) {
        super.deepExport(n, d);
        final AbstractEntityReference ae = (AbstractEntityReference)n;
        ae.nodeName = this.nodeName;
        return n;
    }
    
    @Override
    protected Node copyInto(final Node n) {
        super.copyInto(n);
        final AbstractEntityReference ae = (AbstractEntityReference)n;
        ae.nodeName = this.nodeName;
        return n;
    }
    
    @Override
    protected Node deepCopyInto(final Node n) {
        super.deepCopyInto(n);
        final AbstractEntityReference ae = (AbstractEntityReference)n;
        ae.nodeName = this.nodeName;
        return n;
    }
    
    @Override
    protected void checkChildType(final Node n, final boolean replace) {
        switch (n.getNodeType()) {
            case 1:
            case 3:
            case 4:
            case 5:
            case 7:
            case 8:
            case 11: {}
            default: {
                throw this.createDOMException((short)3, "child.type", new Object[] { this.getNodeType(), this.getNodeName(), n.getNodeType(), n.getNodeName() });
            }
        }
    }
}
