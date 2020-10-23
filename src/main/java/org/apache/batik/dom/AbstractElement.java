// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import java.io.Serializable;
import org.w3c.dom.events.Event;
import org.apache.batik.dom.events.DOMMutationEvent;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.apache.batik.dom.util.DOMUtilities;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.NamedNodeMap;
import org.apache.batik.w3c.dom.ElementTraversal;
import org.w3c.dom.Element;

public abstract class AbstractElement extends AbstractParentChildNode implements Element, ElementTraversal
{
    protected NamedNodeMap attributes;
    protected TypeInfo typeInfo;
    
    protected AbstractElement() {
    }
    
    protected AbstractElement(final String name, final AbstractDocument owner) {
        this.ownerDocument = owner;
        if (owner.getStrictErrorChecking() && !DOMUtilities.isValidName(name)) {
            throw this.createDOMException((short)5, "xml.name", new Object[] { name });
        }
    }
    
    @Override
    public short getNodeType() {
        return 1;
    }
    
    @Override
    public boolean hasAttributes() {
        return this.attributes != null && this.attributes.getLength() != 0;
    }
    
    @Override
    public NamedNodeMap getAttributes() {
        return (this.attributes == null) ? (this.attributes = this.createAttributes()) : this.attributes;
    }
    
    @Override
    public String getTagName() {
        return this.getNodeName();
    }
    
    @Override
    public boolean hasAttribute(final String name) {
        return this.attributes != null && this.attributes.getNamedItem(name) != null;
    }
    
    @Override
    public String getAttribute(final String name) {
        if (this.attributes == null) {
            return "";
        }
        final Attr attr = (Attr)this.attributes.getNamedItem(name);
        return (attr == null) ? "" : attr.getValue();
    }
    
    @Override
    public void setAttribute(final String name, final String value) throws DOMException {
        if (this.attributes == null) {
            this.attributes = this.createAttributes();
        }
        Attr attr = this.getAttributeNode(name);
        if (attr == null) {
            attr = this.getOwnerDocument().createAttribute(name);
            attr.setValue(value);
            this.attributes.setNamedItem(attr);
        }
        else {
            attr.setValue(value);
        }
    }
    
    @Override
    public void removeAttribute(final String name) throws DOMException {
        if (!this.hasAttribute(name)) {
            return;
        }
        this.attributes.removeNamedItem(name);
    }
    
    @Override
    public Attr getAttributeNode(final String name) {
        if (this.attributes == null) {
            return null;
        }
        return (Attr)this.attributes.getNamedItem(name);
    }
    
    @Override
    public Attr setAttributeNode(final Attr newAttr) throws DOMException {
        if (newAttr == null) {
            return null;
        }
        if (this.attributes == null) {
            this.attributes = this.createAttributes();
        }
        return (Attr)this.attributes.setNamedItemNS(newAttr);
    }
    
    @Override
    public Attr removeAttributeNode(final Attr oldAttr) throws DOMException {
        if (oldAttr == null) {
            return null;
        }
        if (this.attributes == null) {
            throw this.createDOMException((short)8, "attribute.missing", new Object[] { oldAttr.getName() });
        }
        final String nsURI = oldAttr.getNamespaceURI();
        return (Attr)this.attributes.removeNamedItemNS(nsURI, (nsURI == null) ? oldAttr.getNodeName() : oldAttr.getLocalName());
    }
    
    @Override
    public void normalize() {
        super.normalize();
        if (this.attributes != null) {
            final NamedNodeMap map = this.getAttributes();
            for (int i = map.getLength() - 1; i >= 0; --i) {
                map.item(i).normalize();
            }
        }
    }
    
    @Override
    public boolean hasAttributeNS(String namespaceURI, final String localName) {
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        return this.attributes != null && this.attributes.getNamedItemNS(namespaceURI, localName) != null;
    }
    
    @Override
    public String getAttributeNS(String namespaceURI, final String localName) {
        if (this.attributes == null) {
            return "";
        }
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        final Attr attr = (Attr)this.attributes.getNamedItemNS(namespaceURI, localName);
        return (attr == null) ? "" : attr.getValue();
    }
    
    @Override
    public void setAttributeNS(String namespaceURI, final String qualifiedName, final String value) throws DOMException {
        if (this.attributes == null) {
            this.attributes = this.createAttributes();
        }
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        Attr attr = this.getAttributeNodeNS(namespaceURI, qualifiedName);
        if (attr == null) {
            attr = this.getOwnerDocument().createAttributeNS(namespaceURI, qualifiedName);
            attr.setValue(value);
            this.attributes.setNamedItemNS(attr);
        }
        else {
            attr.setValue(value);
        }
    }
    
    @Override
    public void removeAttributeNS(String namespaceURI, final String localName) throws DOMException {
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        if (!this.hasAttributeNS(namespaceURI, localName)) {
            return;
        }
        this.attributes.removeNamedItemNS(namespaceURI, localName);
    }
    
    @Override
    public Attr getAttributeNodeNS(String namespaceURI, final String localName) {
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        if (this.attributes == null) {
            return null;
        }
        return (Attr)this.attributes.getNamedItemNS(namespaceURI, localName);
    }
    
    @Override
    public Attr setAttributeNodeNS(final Attr newAttr) throws DOMException {
        if (newAttr == null) {
            return null;
        }
        if (this.attributes == null) {
            this.attributes = this.createAttributes();
        }
        return (Attr)this.attributes.setNamedItemNS(newAttr);
    }
    
    @Override
    public TypeInfo getSchemaTypeInfo() {
        if (this.typeInfo == null) {
            this.typeInfo = new ElementTypeInfo();
        }
        return this.typeInfo;
    }
    
    @Override
    public void setIdAttribute(final String name, final boolean isId) throws DOMException {
        final AbstractAttr a = (AbstractAttr)this.getAttributeNode(name);
        if (a == null) {
            throw this.createDOMException((short)8, "attribute.missing", new Object[] { name });
        }
        if (a.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[] { name });
        }
        this.updateIdEntry(a, isId);
        a.isIdAttr = isId;
    }
    
    @Override
    public void setIdAttributeNS(String ns, final String ln, final boolean isId) throws DOMException {
        if (ns != null && ns.length() == 0) {
            ns = null;
        }
        final AbstractAttr a = (AbstractAttr)this.getAttributeNodeNS(ns, ln);
        if (a == null) {
            throw this.createDOMException((short)8, "attribute.missing", new Object[] { ns, ln });
        }
        if (a.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[] { a.getNodeName() });
        }
        this.updateIdEntry(a, isId);
        a.isIdAttr = isId;
    }
    
    @Override
    public void setIdAttributeNode(final Attr attr, final boolean isId) throws DOMException {
        final AbstractAttr a = (AbstractAttr)attr;
        if (a.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[] { a.getNodeName() });
        }
        this.updateIdEntry(a, isId);
        a.isIdAttr = isId;
    }
    
    private void updateIdEntry(final AbstractAttr a, final boolean isId) {
        if (a.isIdAttr) {
            if (!isId) {
                this.ownerDocument.removeIdEntry(this, a.getValue());
            }
        }
        else if (isId) {
            this.ownerDocument.addIdEntry(this, a.getValue());
        }
    }
    
    protected Attr getIdAttribute() {
        final NamedNodeMap nnm = this.getAttributes();
        if (nnm == null) {
            return null;
        }
        for (int len = nnm.getLength(), i = 0; i < len; ++i) {
            final AbstractAttr a = (AbstractAttr)nnm.item(i);
            if (a.isId()) {
                return a;
            }
        }
        return null;
    }
    
    protected String getId() {
        final Attr a = this.getIdAttribute();
        if (a != null) {
            final String id = a.getNodeValue();
            if (id.length() > 0) {
                return id;
            }
        }
        return null;
    }
    
    @Override
    protected void nodeAdded(final Node node) {
        this.invalidateElementsByTagName(node);
    }
    
    @Override
    protected void nodeToBeRemoved(final Node node) {
        this.invalidateElementsByTagName(node);
    }
    
    private void invalidateElementsByTagName(final Node node) {
        if (node.getNodeType() != 1) {
            return;
        }
        final AbstractDocument ad = this.getCurrentDocument();
        final String ns = node.getNamespaceURI();
        final String nm = node.getNodeName();
        final String ln = (ns == null) ? node.getNodeName() : node.getLocalName();
        for (Node n = this; n != null; n = n.getParentNode()) {
            switch (n.getNodeType()) {
                case 1:
                case 9: {
                    ElementsByTagName l = ad.getElementsByTagName(n, nm);
                    if (l != null) {
                        l.invalidate();
                    }
                    l = ad.getElementsByTagName(n, "*");
                    if (l != null) {
                        l.invalidate();
                    }
                    ElementsByTagNameNS lns = ad.getElementsByTagNameNS(n, ns, ln);
                    if (lns != null) {
                        lns.invalidate();
                    }
                    lns = ad.getElementsByTagNameNS(n, "*", ln);
                    if (lns != null) {
                        lns.invalidate();
                    }
                    lns = ad.getElementsByTagNameNS(n, ns, "*");
                    if (lns != null) {
                        lns.invalidate();
                    }
                    lns = ad.getElementsByTagNameNS(n, "*", "*");
                    if (lns != null) {
                        lns.invalidate();
                        break;
                    }
                    break;
                }
            }
        }
        for (Node c = node.getFirstChild(); c != null; c = c.getNextSibling()) {
            this.invalidateElementsByTagName(c);
        }
    }
    
    protected NamedNodeMap createAttributes() {
        return new NamedNodeHashMap();
    }
    
    @Override
    protected Node export(final Node n, final AbstractDocument d) {
        super.export(n, d);
        final AbstractElement ae = (AbstractElement)n;
        if (this.attributes != null) {
            final NamedNodeMap map = this.attributes;
            for (int i = map.getLength() - 1; i >= 0; --i) {
                final AbstractAttr aa = (AbstractAttr)map.item(i);
                if (aa.getSpecified()) {
                    final Attr attr = (Attr)aa.deepExport(aa.cloneNode(false), d);
                    if (aa instanceof AbstractAttrNS) {
                        ae.setAttributeNodeNS(attr);
                    }
                    else {
                        ae.setAttributeNode(attr);
                    }
                }
            }
        }
        return n;
    }
    
    @Override
    protected Node deepExport(final Node n, final AbstractDocument d) {
        super.deepExport(n, d);
        final AbstractElement ae = (AbstractElement)n;
        if (this.attributes != null) {
            final NamedNodeMap map = this.attributes;
            for (int i = map.getLength() - 1; i >= 0; --i) {
                final AbstractAttr aa = (AbstractAttr)map.item(i);
                if (aa.getSpecified()) {
                    final Attr attr = (Attr)aa.deepExport(aa.cloneNode(false), d);
                    if (aa instanceof AbstractAttrNS) {
                        ae.setAttributeNodeNS(attr);
                    }
                    else {
                        ae.setAttributeNode(attr);
                    }
                }
            }
        }
        return n;
    }
    
    @Override
    protected Node copyInto(final Node n) {
        super.copyInto(n);
        final AbstractElement ae = (AbstractElement)n;
        if (this.attributes != null) {
            final NamedNodeMap map = this.attributes;
            for (int i = map.getLength() - 1; i >= 0; --i) {
                final AbstractAttr aa = (AbstractAttr)map.item(i).cloneNode(true);
                if (aa instanceof AbstractAttrNS) {
                    ae.setAttributeNodeNS(aa);
                }
                else {
                    ae.setAttributeNode(aa);
                }
            }
        }
        return n;
    }
    
    @Override
    protected Node deepCopyInto(final Node n) {
        super.deepCopyInto(n);
        final AbstractElement ae = (AbstractElement)n;
        if (this.attributes != null) {
            final NamedNodeMap map = this.attributes;
            for (int i = map.getLength() - 1; i >= 0; --i) {
                final AbstractAttr aa = (AbstractAttr)map.item(i).cloneNode(true);
                if (aa instanceof AbstractAttrNS) {
                    ae.setAttributeNodeNS(aa);
                }
                else {
                    ae.setAttributeNode(aa);
                }
            }
        }
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
    
    public void fireDOMAttrModifiedEvent(final String name, final Attr node, final String oldv, final String newv, final short change) {
        switch (change) {
            case 2: {
                if (node.isId()) {
                    this.ownerDocument.addIdEntry(this, newv);
                }
                this.attrAdded(node, newv);
                break;
            }
            case 1: {
                if (node.isId()) {
                    this.ownerDocument.updateIdEntry(this, oldv, newv);
                }
                this.attrModified(node, oldv, newv);
                break;
            }
            default: {
                if (node.isId()) {
                    this.ownerDocument.removeIdEntry(this, oldv);
                }
                this.attrRemoved(node, oldv);
                break;
            }
        }
        final AbstractDocument doc = this.getCurrentDocument();
        if (doc.getEventsEnabled() && !oldv.equals(newv)) {
            final DOMMutationEvent ev = (DOMMutationEvent)doc.createEvent("MutationEvents");
            ev.initMutationEventNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", true, false, node, oldv, newv, name, change);
            this.dispatchEvent(ev);
        }
    }
    
    protected void attrAdded(final Attr node, final String newv) {
    }
    
    protected void attrModified(final Attr node, final String oldv, final String newv) {
    }
    
    protected void attrRemoved(final Attr node, final String oldv) {
    }
    
    @Override
    public Element getFirstElementChild() {
        for (Node n = this.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                return (Element)n;
            }
        }
        return null;
    }
    
    @Override
    public Element getLastElementChild() {
        for (Node n = this.getLastChild(); n != null; n = n.getPreviousSibling()) {
            if (n.getNodeType() == 1) {
                return (Element)n;
            }
        }
        return null;
    }
    
    @Override
    public Element getNextElementSibling() {
        for (Node n = this.getNextSibling(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                return (Element)n;
            }
        }
        return null;
    }
    
    @Override
    public Element getPreviousElementSibling() {
        Node n;
        for (n = this.getPreviousSibling(); n != null; n = n.getPreviousSibling()) {
            if (n.getNodeType() == 1) {
                return (Element)n;
            }
        }
        return (Element)n;
    }
    
    @Override
    public int getChildElementCount() {
        this.getChildNodes();
        return this.childNodes.elementChildren;
    }
    
    public class NamedNodeHashMap implements NamedNodeMap, Serializable
    {
        protected static final int INITIAL_CAPACITY = 3;
        protected Entry[] table;
        protected int count;
        
        public NamedNodeHashMap() {
            this.table = new Entry[3];
        }
        
        @Override
        public Node getNamedItem(final String name) {
            if (name == null) {
                return null;
            }
            return this.get(null, name);
        }
        
        @Override
        public Node setNamedItem(final Node arg) throws DOMException {
            if (arg == null) {
                return null;
            }
            this.checkNode(arg);
            return this.setNamedItem(null, arg.getNodeName(), arg);
        }
        
        @Override
        public Node removeNamedItem(final String name) throws DOMException {
            return this.removeNamedItemNS(null, name);
        }
        
        @Override
        public Node item(final int index) {
            if (index < 0 || index >= this.count) {
                return null;
            }
            int j = 0;
            for (Entry e : this.table) {
                final Entry aTable = e;
                Label_0079: {
                    if (e != null) {
                        while (j++ != index) {
                            e = e.next;
                            if (e == null) {
                                break Label_0079;
                            }
                        }
                        return e.value;
                    }
                }
            }
            return null;
        }
        
        @Override
        public int getLength() {
            return this.count;
        }
        
        @Override
        public Node getNamedItemNS(String namespaceURI, final String localName) {
            if (namespaceURI != null && namespaceURI.length() == 0) {
                namespaceURI = null;
            }
            return this.get(namespaceURI, localName);
        }
        
        @Override
        public Node setNamedItemNS(final Node arg) throws DOMException {
            if (arg == null) {
                return null;
            }
            final String nsURI = arg.getNamespaceURI();
            return this.setNamedItem(nsURI, (nsURI == null) ? arg.getNodeName() : arg.getLocalName(), arg);
        }
        
        @Override
        public Node removeNamedItemNS(String namespaceURI, final String localName) throws DOMException {
            if (AbstractElement.this.isReadonly()) {
                throw AbstractElement.this.createDOMException((short)7, "readonly.node.map", new Object[0]);
            }
            if (localName == null) {
                throw AbstractElement.this.createDOMException((short)8, "attribute.missing", new Object[] { "" });
            }
            if (namespaceURI != null && namespaceURI.length() == 0) {
                namespaceURI = null;
            }
            final AbstractAttr n = (AbstractAttr)this.remove(namespaceURI, localName);
            if (n == null) {
                throw AbstractElement.this.createDOMException((short)8, "attribute.missing", new Object[] { localName });
            }
            n.setOwnerElement(null);
            AbstractElement.this.fireDOMAttrModifiedEvent(n.getNodeName(), n, n.getNodeValue(), "", (short)3);
            return n;
        }
        
        public Node setNamedItem(String ns, final String name, final Node arg) throws DOMException {
            if (ns != null && ns.length() == 0) {
                ns = null;
            }
            ((AbstractAttr)arg).setOwnerElement(AbstractElement.this);
            final AbstractAttr result = (AbstractAttr)this.put(ns, name, arg);
            if (result != null) {
                result.setOwnerElement(null);
                AbstractElement.this.fireDOMAttrModifiedEvent(name, result, result.getNodeValue(), "", (short)3);
            }
            AbstractElement.this.fireDOMAttrModifiedEvent(name, (Attr)arg, "", arg.getNodeValue(), (short)2);
            return result;
        }
        
        protected void checkNode(final Node arg) {
            if (AbstractElement.this.isReadonly()) {
                throw AbstractElement.this.createDOMException((short)7, "readonly.node.map", new Object[0]);
            }
            if (AbstractElement.this.getOwnerDocument() != arg.getOwnerDocument()) {
                throw AbstractElement.this.createDOMException((short)4, "node.from.wrong.document", new Object[] { arg.getNodeType(), arg.getNodeName() });
            }
            if (arg.getNodeType() == 2 && ((Attr)arg).getOwnerElement() != null) {
                throw AbstractElement.this.createDOMException((short)4, "inuse.attribute", new Object[] { arg.getNodeName() });
            }
        }
        
        protected Node get(final String ns, final String nm) {
            final int hash = this.hashCode(ns, nm) & Integer.MAX_VALUE;
            final int index = hash % this.table.length;
            for (Entry e = this.table[index]; e != null; e = e.next) {
                if (e.hash == hash && e.match(ns, nm)) {
                    return e.value;
                }
            }
            return null;
        }
        
        protected Node put(final String ns, final String nm, final Node value) {
            final int hash = this.hashCode(ns, nm) & Integer.MAX_VALUE;
            int index = hash % this.table.length;
            for (Entry e = this.table[index]; e != null; e = e.next) {
                if (e.hash == hash && e.match(ns, nm)) {
                    final Node old = e.value;
                    e.value = value;
                    return old;
                }
            }
            final int len = this.table.length;
            if (this.count++ >= len - (len >> 2)) {
                this.rehash();
                index = hash % this.table.length;
            }
            final Entry e2 = new Entry(hash, ns, nm, value, this.table[index]);
            this.table[index] = e2;
            return null;
        }
        
        protected Node remove(final String ns, final String nm) {
            final int hash = this.hashCode(ns, nm) & Integer.MAX_VALUE;
            final int index = hash % this.table.length;
            Entry p = null;
            for (Entry e = this.table[index]; e != null; e = e.next) {
                if (e.hash == hash && e.match(ns, nm)) {
                    final Node result = e.value;
                    if (p == null) {
                        this.table[index] = e.next;
                    }
                    else {
                        p.next = e.next;
                    }
                    --this.count;
                    return result;
                }
                p = e;
            }
            return null;
        }
        
        protected void rehash() {
            final Entry[] oldTable = this.table;
            this.table = new Entry[oldTable.length * 2 + 1];
            for (int i = oldTable.length - 1; i >= 0; --i) {
                Entry e;
                int index;
                for (Entry old = oldTable[i]; old != null; old = old.next, index = e.hash % this.table.length, e.next = this.table[index], this.table[index] = e) {
                    e = old;
                }
            }
        }
        
        protected int hashCode(final String ns, final String nm) {
            final int result = (ns == null) ? 0 : ns.hashCode();
            return result ^ nm.hashCode();
        }
    }
    
    protected static class Entry implements Serializable
    {
        public int hash;
        public String namespaceURI;
        public String name;
        public Node value;
        public Entry next;
        
        public Entry(final int hash, final String ns, final String nm, final Node value, final Entry next) {
            this.hash = hash;
            this.namespaceURI = ns;
            this.name = nm;
            this.value = value;
            this.next = next;
        }
        
        public boolean match(final String ns, final String nm) {
            if (this.namespaceURI != null) {
                if (!this.namespaceURI.equals(ns)) {
                    return false;
                }
            }
            else if (ns != null) {
                return false;
            }
            return this.name.equals(nm);
        }
    }
    
    public static class ElementTypeInfo implements TypeInfo
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
