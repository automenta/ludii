// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.DOMException;
import org.apache.batik.dom.AbstractAttr;
import org.w3c.dom.NamedNodeMap;
import org.apache.batik.dom.svg.LiveAttributeValue;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.css.engine.CSSNavigableNode;
import org.apache.batik.dom.events.NodeEventTarget;

public abstract class AbstractElement extends org.apache.batik.dom.AbstractElement implements NodeEventTarget, CSSNavigableNode, SVGConstants
{
    protected transient DoublyIndexedTable liveAttributeValues;
    
    protected AbstractElement() {
        this.liveAttributeValues = new DoublyIndexedTable();
    }
    
    protected AbstractElement(final String prefix, final AbstractDocument owner) {
        this.liveAttributeValues = new DoublyIndexedTable();
        this.ownerDocument = owner;
        this.setPrefix(prefix);
        this.initializeAttributes();
    }
    
    @Override
    public Node getCSSParentNode() {
        return this.getXblParentNode();
    }
    
    @Override
    public Node getCSSPreviousSibling() {
        return this.getXblPreviousSibling();
    }
    
    @Override
    public Node getCSSNextSibling() {
        return this.getXblNextSibling();
    }
    
    @Override
    public Node getCSSFirstChild() {
        return this.getXblFirstChild();
    }
    
    @Override
    public Node getCSSLastChild() {
        return this.getXblLastChild();
    }
    
    @Override
    public boolean isHiddenFromSelectors() {
        return false;
    }
    
    @Override
    public void fireDOMAttrModifiedEvent(final String name, final Attr node, final String oldv, final String newv, final short change) {
        super.fireDOMAttrModifiedEvent(name, node, oldv, newv, change);
        if (((SVGOMDocument)this.ownerDocument).isSVG12 && (change == 2 || change == 1)) {
            if (node.getNamespaceURI() == null && node.getNodeName().equals("id")) {
                final Attr a = this.getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "id");
                if (a == null) {
                    this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:id", newv);
                }
                else if (!a.getNodeValue().equals(newv)) {
                    a.setNodeValue(newv);
                }
            }
            else if (node.getNodeName().equals("xml:id")) {
                final Attr a = this.getAttributeNodeNS(null, "id");
                if (a == null) {
                    this.setAttributeNS(null, "id", newv);
                }
                else if (!a.getNodeValue().equals(newv)) {
                    a.setNodeValue(newv);
                }
            }
        }
    }
    
    public LiveAttributeValue getLiveAttributeValue(final String ns, final String ln) {
        return (LiveAttributeValue)this.liveAttributeValues.get(ns, ln);
    }
    
    public void putLiveAttributeValue(final String ns, final String ln, final LiveAttributeValue val) {
        this.liveAttributeValues.put(ns, ln, val);
    }
    
    protected AttributeInitializer getAttributeInitializer() {
        return null;
    }
    
    protected void initializeAttributes() {
        final AttributeInitializer ai = this.getAttributeInitializer();
        if (ai != null) {
            ai.initializeAttributes(this);
        }
    }
    
    protected boolean resetAttribute(final String ns, final String prefix, final String ln) {
        final AttributeInitializer ai = this.getAttributeInitializer();
        return ai != null && ai.resetAttribute(this, ns, prefix, ln);
    }
    
    @Override
    protected NamedNodeMap createAttributes() {
        return new ExtendedNamedNodeHashMap();
    }
    
    public void setUnspecifiedAttribute(final String nsURI, final String name, final String value) {
        if (this.attributes == null) {
            this.attributes = this.createAttributes();
        }
        ((ExtendedNamedNodeHashMap)this.attributes).setUnspecifiedAttribute(nsURI, name, value);
    }
    
    @Override
    protected void attrAdded(final Attr node, final String newv) {
        final LiveAttributeValue lav = this.getLiveAttributeValue(node);
        if (lav != null) {
            lav.attrAdded(node, newv);
        }
    }
    
    @Override
    protected void attrModified(final Attr node, final String oldv, final String newv) {
        final LiveAttributeValue lav = this.getLiveAttributeValue(node);
        if (lav != null) {
            lav.attrModified(node, oldv, newv);
        }
    }
    
    @Override
    protected void attrRemoved(final Attr node, final String oldv) {
        final LiveAttributeValue lav = this.getLiveAttributeValue(node);
        if (lav != null) {
            lav.attrRemoved(node, oldv);
        }
    }
    
    private LiveAttributeValue getLiveAttributeValue(final Attr node) {
        final String ns = node.getNamespaceURI();
        return this.getLiveAttributeValue(ns, (ns == null) ? node.getNodeName() : node.getLocalName());
    }
    
    @Override
    protected Node export(final Node n, final AbstractDocument d) {
        super.export(n, d);
        ((AbstractElement)n).initializeAttributes();
        super.export(n, d);
        return n;
    }
    
    @Override
    protected Node deepExport(final Node n, final AbstractDocument d) {
        super.export(n, d);
        ((AbstractElement)n).initializeAttributes();
        super.deepExport(n, d);
        return n;
    }
    
    protected class ExtendedNamedNodeHashMap extends NamedNodeHashMap
    {
        public ExtendedNamedNodeHashMap() {
        }
        
        public void setUnspecifiedAttribute(final String nsURI, final String name, final String value) {
            final Attr attr = AbstractElement.this.getOwnerDocument().createAttributeNS(nsURI, name);
            attr.setValue(value);
            ((AbstractAttr)attr).setSpecified(false);
            this.setNamedItemNS(attr);
        }
        
        @Override
        public Node removeNamedItemNS(final String namespaceURI, final String localName) throws DOMException {
            if (AbstractElement.this.isReadonly()) {
                throw AbstractElement.this.createDOMException((short)7, "readonly.node.map", new Object[0]);
            }
            if (localName == null) {
                throw AbstractElement.this.createDOMException((short)8, "attribute.missing", new Object[] { "" });
            }
            final AbstractAttr n = (AbstractAttr)this.remove(namespaceURI, localName);
            if (n == null) {
                throw AbstractElement.this.createDOMException((short)8, "attribute.missing", new Object[] { localName });
            }
            n.setOwnerElement(null);
            final String prefix = n.getPrefix();
            if (!AbstractElement.this.resetAttribute(namespaceURI, prefix, localName)) {
                AbstractElement.this.fireDOMAttrModifiedEvent(n.getNodeName(), n, n.getNodeValue(), "", (short)3);
            }
            return n;
        }
    }
}
