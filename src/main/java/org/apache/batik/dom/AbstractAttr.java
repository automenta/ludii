// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.apache.batik.dom.util.DOMUtilities;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.Attr;

public abstract class AbstractAttr extends AbstractParentNode implements Attr
{
    protected String nodeName;
    protected boolean unspecified;
    protected boolean isIdAttr;
    protected AbstractElement ownerElement;
    protected TypeInfo typeInfo;
    
    protected AbstractAttr() {
    }
    
    protected AbstractAttr(final String name, final AbstractDocument owner) throws DOMException {
        this.ownerDocument = owner;
        if (owner.getStrictErrorChecking() && !DOMUtilities.isValidName(name)) {
            throw this.createDOMException((short)5, "xml.name", new Object[] { name });
        }
    }
    
    @Override
    public void setNodeName(final String v) {
        this.nodeName = v;
        this.isIdAttr = this.ownerDocument.isId(this);
    }
    
    @Override
    public String getNodeName() {
        return this.nodeName;
    }
    
    @Override
    public short getNodeType() {
        return 2;
    }
    
    @Override
    public String getNodeValue() throws DOMException {
        final Node first = this.getFirstChild();
        if (first == null) {
            return "";
        }
        Node n = first.getNextSibling();
        if (n == null) {
            return first.getNodeValue();
        }
        final StringBuffer result = new StringBuffer(first.getNodeValue());
        do {
            result.append(n.getNodeValue());
            n = n.getNextSibling();
        } while (n != null);
        return result.toString();
    }
    
    @Override
    public void setNodeValue(final String nodeValue) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[] { this.getNodeType(), this.getNodeName() });
        }
        final String s = this.getNodeValue();
        Node n;
        while ((n = this.getFirstChild()) != null) {
            this.removeChild(n);
        }
        final String val = (nodeValue == null) ? "" : nodeValue;
        n = this.getOwnerDocument().createTextNode(val);
        this.appendChild(n);
        if (this.ownerElement != null) {
            this.ownerElement.fireDOMAttrModifiedEvent(this.nodeName, this, s, val, (short)1);
        }
    }
    
    @Override
    public String getName() {
        return this.getNodeName();
    }
    
    @Override
    public boolean getSpecified() {
        return !this.unspecified;
    }
    
    @Override
    public void setSpecified(final boolean v) {
        this.unspecified = !v;
    }
    
    @Override
    public String getValue() {
        return this.getNodeValue();
    }
    
    @Override
    public void setValue(final String value) throws DOMException {
        this.setNodeValue(value);
    }
    
    public void setOwnerElement(final AbstractElement v) {
        this.ownerElement = v;
    }
    
    @Override
    public Element getOwnerElement() {
        return this.ownerElement;
    }
    
    @Override
    public TypeInfo getSchemaTypeInfo() {
        if (this.typeInfo == null) {
            this.typeInfo = new AttrTypeInfo();
        }
        return this.typeInfo;
    }
    
    @Override
    public boolean isId() {
        return this.isIdAttr;
    }
    
    public void setIsId(final boolean isId) {
        this.isIdAttr = isId;
    }
    
    @Override
    protected void nodeAdded(final Node n) {
        this.setSpecified(true);
    }
    
    @Override
    protected void nodeToBeRemoved(final Node n) {
        this.setSpecified(true);
    }
    
    @Override
    protected Node export(final Node n, final AbstractDocument d) {
        super.export(n, d);
        final AbstractAttr aa = (AbstractAttr)n;
        aa.nodeName = this.nodeName;
        aa.unspecified = false;
        aa.isIdAttr = d.isId(aa);
        return n;
    }
    
    @Override
    protected Node deepExport(final Node n, final AbstractDocument d) {
        super.deepExport(n, d);
        final AbstractAttr aa = (AbstractAttr)n;
        aa.nodeName = this.nodeName;
        aa.unspecified = false;
        aa.isIdAttr = d.isId(aa);
        return n;
    }
    
    @Override
    protected Node copyInto(final Node n) {
        super.copyInto(n);
        final AbstractAttr aa = (AbstractAttr)n;
        aa.nodeName = this.nodeName;
        aa.unspecified = this.unspecified;
        aa.isIdAttr = this.isIdAttr;
        return n;
    }
    
    @Override
    protected Node deepCopyInto(final Node n) {
        super.deepCopyInto(n);
        final AbstractAttr aa = (AbstractAttr)n;
        aa.nodeName = this.nodeName;
        aa.unspecified = this.unspecified;
        aa.isIdAttr = this.isIdAttr;
        return n;
    }
    
    @Override
    protected void checkChildType(final Node n, final boolean replace) {
        switch (n.getNodeType()) {
            case 3:
            case 5:
            case 11: {}
            default: {
                throw this.createDOMException((short)3, "child.type", new Object[] { this.getNodeType(), this.getNodeName(), n.getNodeType(), n.getNodeName() });
            }
        }
    }
    
    @Override
    protected void fireDOMSubtreeModifiedEvent() {
        final AbstractDocument doc = this.getCurrentDocument();
        if (doc.getEventsEnabled()) {
            super.fireDOMSubtreeModifiedEvent();
            if (this.getOwnerElement() != null) {
                ((AbstractElement)this.getOwnerElement()).fireDOMSubtreeModifiedEvent();
            }
        }
    }
    
    public static class AttrTypeInfo implements TypeInfo
    {
        @Override
        public String getTypeNamespace() {
            return null;
        }
        
        @Override
        public String getTypeName() {
            return null;
        }
        
        @Override
        public boolean isDerivedFrom(final String ns, final String name, final int method) {
            return false;
        }
    }
}
