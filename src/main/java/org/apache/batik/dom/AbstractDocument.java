// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom;

import org.w3c.dom.xpath.XPathResult;
import org.apache.xpath.objects.XObject;
import javax.xml.transform.TransformerException;
import org.apache.xml.utils.PrefixResolver;
import javax.xml.transform.SourceLocator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPath;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.DOMLocator;
import org.apache.batik.util.CleanerThread;
import java.lang.reflect.Method;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.w3c.dom.NodeList;
import org.w3c.dom.xpath.XPathException;
import org.w3c.dom.xpath.XPathExpression;
import org.w3c.dom.xpath.XPathNSResolver;
import org.w3c.dom.DOMError;
import org.apache.batik.xml.XMLUtilities;
import java.util.LinkedList;
import org.w3c.dom.DOMErrorHandler;
import org.apache.batik.dom.events.EventSupport;
import org.apache.batik.w3c.dom.events.MutationNameEvent;
import org.apache.batik.dom.util.DOMUtilities;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.traversal.TreeWalker;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.events.Event;
import org.apache.batik.util.SoftDoublyIndexedTable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import java.util.MissingResourceException;
import java.util.Locale;
import org.w3c.dom.Node;
import org.w3c.dom.DocumentType;
import org.apache.batik.dom.xbl.GenericXBLManager;
import java.util.Map;
import org.apache.batik.dom.xbl.XBLManager;
import java.util.WeakHashMap;
import org.apache.batik.dom.events.DocumentEventSupport;
import org.apache.batik.dom.traversal.TraversalSupport;
import org.w3c.dom.DOMImplementation;
import org.apache.batik.i18n.LocalizableSupport;
import org.w3c.dom.xpath.XPathEvaluator;
import org.apache.batik.i18n.Localizable;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.Document;

public abstract class AbstractDocument extends AbstractParentNode implements Document, DocumentEvent, DocumentTraversal, Localizable, XPathEvaluator
{
    protected static final String RESOURCES = "org.apache.batik.dom.resources.Messages";
    protected transient LocalizableSupport localizableSupport;
    protected transient DOMImplementation implementation;
    protected transient TraversalSupport traversalSupport;
    protected transient DocumentEventSupport documentEventSupport;
    protected transient boolean eventsEnabled;
    protected transient WeakHashMap elementsByTagNames;
    protected transient WeakHashMap elementsByTagNamesNS;
    protected String inputEncoding;
    protected String xmlEncoding;
    protected String xmlVersion;
    protected boolean xmlStandalone;
    protected String documentURI;
    protected boolean strictErrorChecking;
    protected DocumentConfiguration domConfig;
    protected transient XBLManager xblManager;
    protected transient Map elementsById;
    
    protected AbstractDocument() {
        this.localizableSupport = new LocalizableSupport("org.apache.batik.dom.resources.Messages", this.getClass().getClassLoader());
        this.xmlVersion = "1.0";
        this.strictErrorChecking = true;
        this.xblManager = new GenericXBLManager();
    }
    
    public AbstractDocument(final DocumentType dt, final DOMImplementation impl) {
        this.localizableSupport = new LocalizableSupport("org.apache.batik.dom.resources.Messages", this.getClass().getClassLoader());
        this.xmlVersion = "1.0";
        this.strictErrorChecking = true;
        this.xblManager = new GenericXBLManager();
        this.implementation = impl;
        if (dt != null) {
            if (dt instanceof GenericDocumentType) {
                final GenericDocumentType gdt = (GenericDocumentType)dt;
                if (gdt.getOwnerDocument() == null) {
                    gdt.setOwnerDocument(this);
                }
            }
            this.appendChild(dt);
        }
    }
    
    public void setDocumentInputEncoding(final String ie) {
        this.inputEncoding = ie;
    }
    
    public void setDocumentXmlEncoding(final String xe) {
        this.xmlEncoding = xe;
    }
    
    @Override
    public void setLocale(final Locale l) {
        this.localizableSupport.setLocale(l);
    }
    
    @Override
    public Locale getLocale() {
        return this.localizableSupport.getLocale();
    }
    
    @Override
    public String formatMessage(final String key, final Object[] args) throws MissingResourceException {
        return this.localizableSupport.formatMessage(key, args);
    }
    
    public boolean getEventsEnabled() {
        return this.eventsEnabled;
    }
    
    public void setEventsEnabled(final boolean b) {
        this.eventsEnabled = b;
    }
    
    @Override
    public String getNodeName() {
        return "#document";
    }
    
    @Override
    public short getNodeType() {
        return 9;
    }
    
    @Override
    public DocumentType getDoctype() {
        for (Node n = this.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 10) {
                return (DocumentType)n;
            }
        }
        return null;
    }
    
    public void setDoctype(final DocumentType dt) {
        if (dt != null) {
            this.appendChild(dt);
            ((ExtendedNode)dt).setReadonly(true);
        }
    }
    
    @Override
    public DOMImplementation getImplementation() {
        return this.implementation;
    }
    
    @Override
    public Element getDocumentElement() {
        for (Node n = this.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                return (Element)n;
            }
        }
        return null;
    }
    
    @Override
    public Node importNode(final Node importedNode, final boolean deep) throws DOMException {
        return this.importNode(importedNode, deep, false);
    }
    
    public Node importNode(final Node importedNode, boolean deep, final boolean trimId) {
        Node result = null;
        switch (importedNode.getNodeType()) {
            case 1: {
                final Element e = (Element)(result = this.createElementNS(importedNode.getNamespaceURI(), importedNode.getNodeName()));
                if (importedNode.hasAttributes()) {
                    final NamedNodeMap attr = importedNode.getAttributes();
                    for (int len = attr.getLength(), i = 0; i < len; ++i) {
                        final Attr a = (Attr)attr.item(i);
                        if (a.getSpecified()) {
                            final AbstractAttr aa = (AbstractAttr)this.importNode(a, true);
                            if (trimId && aa.isId()) {
                                aa.setIsId(false);
                            }
                            e.setAttributeNodeNS(aa);
                        }
                    }
                    break;
                }
                break;
            }
            case 2: {
                result = this.createAttributeNS(importedNode.getNamespaceURI(), importedNode.getNodeName());
                break;
            }
            case 3: {
                result = this.createTextNode(importedNode.getNodeValue());
                deep = false;
                break;
            }
            case 4: {
                result = this.createCDATASection(importedNode.getNodeValue());
                deep = false;
                break;
            }
            case 5: {
                result = this.createEntityReference(importedNode.getNodeName());
                break;
            }
            case 7: {
                result = this.createProcessingInstruction(importedNode.getNodeName(), importedNode.getNodeValue());
                deep = false;
                break;
            }
            case 8: {
                result = this.createComment(importedNode.getNodeValue());
                deep = false;
                break;
            }
            case 11: {
                result = this.createDocumentFragment();
                break;
            }
            case 10: {
                final DocumentType docType = (DocumentType)importedNode;
                final GenericDocumentType copy = new GenericDocumentType(docType.getName(), docType.getPublicId(), docType.getSystemId());
                copy.ownerDocument = this;
                result = copy;
                break;
            }
            default: {
                throw this.createDOMException((short)9, "import.node", new Object[0]);
            }
        }
        if (importedNode instanceof AbstractNode) {
            this.fireUserDataHandlers((short)2, importedNode, result);
        }
        if (deep) {
            for (Node n = importedNode.getFirstChild(); n != null; n = n.getNextSibling()) {
                result.appendChild(this.importNode(n, true));
            }
        }
        return result;
    }
    
    @Override
    public Node cloneNode(final boolean deep) {
        final Document n = (Document)this.newNode();
        this.copyInto(n);
        this.fireUserDataHandlers((short)1, this, n);
        if (deep) {
            for (Node c = this.getFirstChild(); c != null; c = c.getNextSibling()) {
                n.appendChild(n.importNode(c, deep));
            }
        }
        return n;
    }
    
    public abstract boolean isId(final Attr p0);
    
    @Override
    public Element getElementById(final String id) {
        return this.getChildElementById(this.getDocumentElement(), id);
    }
    
    public Element getChildElementById(final Node requestor, final String id) {
        if (id == null || id.length() == 0) {
            return null;
        }
        if (this.elementsById == null) {
            return null;
        }
        final Node root = this.getRoot(requestor);
        Object o = this.elementsById.get(id);
        if (o == null) {
            return null;
        }
        if (!(o instanceof IdSoftRef)) {
            final List l = (List)o;
            final Iterator li = l.iterator();
            while (li.hasNext()) {
                final IdSoftRef sr = li.next();
                o = sr.get();
                if (o == null) {
                    li.remove();
                }
                else {
                    final Element e = (Element)o;
                    if (this.getRoot(e) == root) {
                        return e;
                    }
                    continue;
                }
            }
            return null;
        }
        o = ((IdSoftRef)o).get();
        if (o == null) {
            this.elementsById.remove(id);
            return null;
        }
        final Element e2 = (Element)o;
        if (this.getRoot(e2) == root) {
            return e2;
        }
        return null;
    }
    
    protected Node getRoot(Node n) {
        Node r = n;
        while (n != null) {
            r = n;
            n = n.getParentNode();
        }
        return r;
    }
    
    public void removeIdEntry(final Element e, final String id) {
        if (id == null) {
            return;
        }
        if (this.elementsById == null) {
            return;
        }
        synchronized (this.elementsById) {
            Object o = this.elementsById.get(id);
            if (o == null) {
                return;
            }
            if (o instanceof IdSoftRef) {
                this.elementsById.remove(id);
                return;
            }
            final List l = (List)o;
            final Iterator li = l.iterator();
            while (li.hasNext()) {
                final IdSoftRef ip = li.next();
                o = ip.get();
                if (o == null) {
                    li.remove();
                }
                else {
                    if (e == o) {
                        li.remove();
                        break;
                    }
                    continue;
                }
            }
            if (l.size() == 0) {
                this.elementsById.remove(id);
            }
        }
    }
    
    public void addIdEntry(final Element e, final String id) {
        if (id == null) {
            return;
        }
        if (this.elementsById == null) {
            final Map tmp = new HashMap();
            tmp.put(id, new IdSoftRef(e, id));
            this.elementsById = tmp;
            return;
        }
        synchronized (this.elementsById) {
            final Object o = this.elementsById.get(id);
            if (o == null) {
                this.elementsById.put(id, new IdSoftRef(e, id));
                return;
            }
            if (o instanceof IdSoftRef) {
                final IdSoftRef ip = (IdSoftRef)o;
                final Object r = ip.get();
                if (r == null) {
                    this.elementsById.put(id, new IdSoftRef(e, id));
                    return;
                }
                final List l = new ArrayList(4);
                ip.setList(l);
                l.add(ip);
                l.add(new IdSoftRef(e, id, l));
                this.elementsById.put(id, l);
            }
            else {
                final List i = (List)o;
                i.add(new IdSoftRef(e, id, i));
            }
        }
    }
    
    public void updateIdEntry(final Element e, final String oldId, final String newId) {
        if (oldId == newId || (oldId != null && oldId.equals(newId))) {
            return;
        }
        this.removeIdEntry(e, oldId);
        this.addIdEntry(e, newId);
    }
    
    public ElementsByTagName getElementsByTagName(final Node n, final String ln) {
        if (this.elementsByTagNames == null) {
            return null;
        }
        final SoftDoublyIndexedTable t = this.elementsByTagNames.get(n);
        if (t == null) {
            return null;
        }
        return (ElementsByTagName)t.get(null, ln);
    }
    
    public void putElementsByTagName(final Node n, final String ln, final ElementsByTagName l) {
        if (this.elementsByTagNames == null) {
            this.elementsByTagNames = new WeakHashMap(11);
        }
        SoftDoublyIndexedTable t = this.elementsByTagNames.get(n);
        if (t == null) {
            this.elementsByTagNames.put(n, t = new SoftDoublyIndexedTable());
        }
        t.put(null, ln, l);
    }
    
    public ElementsByTagNameNS getElementsByTagNameNS(final Node n, final String ns, final String ln) {
        if (this.elementsByTagNamesNS == null) {
            return null;
        }
        final SoftDoublyIndexedTable t = this.elementsByTagNamesNS.get(n);
        if (t == null) {
            return null;
        }
        return (ElementsByTagNameNS)t.get(ns, ln);
    }
    
    public void putElementsByTagNameNS(final Node n, final String ns, final String ln, final ElementsByTagNameNS l) {
        if (this.elementsByTagNamesNS == null) {
            this.elementsByTagNamesNS = new WeakHashMap(11);
        }
        SoftDoublyIndexedTable t = this.elementsByTagNamesNS.get(n);
        if (t == null) {
            this.elementsByTagNamesNS.put(n, t = new SoftDoublyIndexedTable());
        }
        t.put(ns, ln, l);
    }
    
    @Override
    public Event createEvent(final String eventType) throws DOMException {
        if (this.documentEventSupport == null) {
            this.documentEventSupport = ((AbstractDOMImplementation)this.implementation).createDocumentEventSupport();
        }
        return this.documentEventSupport.createEvent(eventType);
    }
    
    public boolean canDispatch(String ns, final String eventType) {
        if (eventType == null) {
            return false;
        }
        if (ns != null && ns.length() == 0) {
            ns = null;
        }
        return (ns == null || ns.equals("http://www.w3.org/2001/xml-events")) && (eventType.equals("Event") || eventType.equals("MutationEvent") || eventType.equals("MutationNameEvent") || eventType.equals("UIEvent") || eventType.equals("MouseEvent") || eventType.equals("KeyEvent") || eventType.equals("KeyboardEvent") || eventType.equals("TextEvent") || eventType.equals("CustomEvent"));
    }
    
    @Override
    public NodeIterator createNodeIterator(final Node root, final int whatToShow, final NodeFilter filter, final boolean entityReferenceExpansion) throws DOMException {
        if (this.traversalSupport == null) {
            this.traversalSupport = new TraversalSupport();
        }
        return this.traversalSupport.createNodeIterator(this, root, whatToShow, filter, entityReferenceExpansion);
    }
    
    @Override
    public TreeWalker createTreeWalker(final Node root, final int whatToShow, final NodeFilter filter, final boolean entityReferenceExpansion) throws DOMException {
        return TraversalSupport.createTreeWalker(this, root, whatToShow, filter, entityReferenceExpansion);
    }
    
    public void detachNodeIterator(final NodeIterator it) {
        this.traversalSupport.detachNodeIterator(it);
    }
    
    public void nodeToBeRemoved(final Node node) {
        if (this.traversalSupport != null) {
            this.traversalSupport.nodeToBeRemoved(node);
        }
    }
    
    @Override
    protected AbstractDocument getCurrentDocument() {
        return this;
    }
    
    protected Node export(final Node n, final Document d) {
        throw this.createDOMException((short)9, "import.document", new Object[0]);
    }
    
    protected Node deepExport(final Node n, final Document d) {
        throw this.createDOMException((short)9, "import.document", new Object[0]);
    }
    
    @Override
    protected Node copyInto(final Node n) {
        super.copyInto(n);
        final AbstractDocument ad = (AbstractDocument)n;
        ad.implementation = this.implementation;
        ad.localizableSupport = new LocalizableSupport("org.apache.batik.dom.resources.Messages", this.getClass().getClassLoader());
        ad.inputEncoding = this.inputEncoding;
        ad.xmlEncoding = this.xmlEncoding;
        ad.xmlVersion = this.xmlVersion;
        ad.xmlStandalone = this.xmlStandalone;
        ad.documentURI = this.documentURI;
        ad.strictErrorChecking = this.strictErrorChecking;
        return n;
    }
    
    @Override
    protected Node deepCopyInto(final Node n) {
        super.deepCopyInto(n);
        final AbstractDocument ad = (AbstractDocument)n;
        ad.implementation = this.implementation;
        ad.localizableSupport = new LocalizableSupport("org.apache.batik.dom.resources.Messages", this.getClass().getClassLoader());
        return n;
    }
    
    @Override
    protected void checkChildType(final Node n, final boolean replace) {
        final short t = n.getNodeType();
        switch (t) {
            case 1:
            case 7:
            case 8:
            case 10:
            case 11: {
                if ((!replace && t == 1 && this.getDocumentElement() != null) || (t == 10 && this.getDoctype() != null)) {
                    throw this.createDOMException((short)9, "document.child.already.exists", new Object[] { t, n.getNodeName() });
                }
            }
            default: {
                throw this.createDOMException((short)3, "child.type", new Object[] { this.getNodeType(), this.getNodeName(), t, n.getNodeName() });
            }
        }
    }
    
    @Override
    public String getInputEncoding() {
        return this.inputEncoding;
    }
    
    @Override
    public String getXmlEncoding() {
        return this.xmlEncoding;
    }
    
    @Override
    public boolean getXmlStandalone() {
        return this.xmlStandalone;
    }
    
    @Override
    public void setXmlStandalone(final boolean b) throws DOMException {
        this.xmlStandalone = b;
    }
    
    @Override
    public String getXmlVersion() {
        return this.xmlVersion;
    }
    
    @Override
    public void setXmlVersion(final String v) throws DOMException {
        if (v == null || (!v.equals("1.0") && !v.equals("1.1"))) {
            throw this.createDOMException((short)9, "xml.version", new Object[] { v });
        }
        this.xmlVersion = v;
    }
    
    @Override
    public boolean getStrictErrorChecking() {
        return this.strictErrorChecking;
    }
    
    @Override
    public void setStrictErrorChecking(final boolean b) {
        this.strictErrorChecking = b;
    }
    
    @Override
    public String getDocumentURI() {
        return this.documentURI;
    }
    
    @Override
    public void setDocumentURI(final String uri) {
        this.documentURI = uri;
    }
    
    @Override
    public DOMConfiguration getDomConfig() {
        if (this.domConfig == null) {
            this.domConfig = new DocumentConfiguration();
        }
        return this.domConfig;
    }
    
    @Override
    public Node adoptNode(final Node n) throws DOMException {
        if (!(n instanceof AbstractNode)) {
            return null;
        }
        switch (n.getNodeType()) {
            case 9: {
                throw this.createDOMException((short)9, "adopt.document", new Object[0]);
            }
            case 10: {
                throw this.createDOMException((short)9, "adopt.document.type", new Object[0]);
            }
            case 6:
            case 12: {
                return null;
            }
            default: {
                final AbstractNode an = (AbstractNode)n;
                if (an.isReadonly()) {
                    throw this.createDOMException((short)7, "readonly.node", new Object[] { an.getNodeType(), an.getNodeName() });
                }
                final Node parent = n.getParentNode();
                if (parent != null) {
                    parent.removeChild(n);
                }
                this.adoptNode1((AbstractNode)n);
                return n;
            }
        }
    }
    
    protected void adoptNode1(final AbstractNode n) {
        n.ownerDocument = this;
        switch (n.getNodeType()) {
            case 2: {
                final AbstractAttr attr = (AbstractAttr)n;
                attr.ownerElement = null;
                attr.unspecified = false;
                break;
            }
            case 1: {
                final NamedNodeMap nnm = n.getAttributes();
                for (int len = nnm.getLength(), i = 0; i < len; ++i) {
                    final AbstractAttr attr = (AbstractAttr)nnm.item(i);
                    if (attr.getSpecified()) {
                        this.adoptNode1(attr);
                    }
                }
                break;
            }
            case 5: {
                while (n.getFirstChild() != null) {
                    n.removeChild(n.getFirstChild());
                }
                break;
            }
        }
        this.fireUserDataHandlers((short)5, n, null);
        Node m = n.getFirstChild();
        while (m != null) {
            switch (m.getNodeType()) {
                case 6:
                case 10:
                case 12: {}
                default: {
                    this.adoptNode1((AbstractNode)m);
                    m = m.getNextSibling();
                    continue;
                }
            }
        }
    }
    
    @Override
    public Node renameNode(final Node n, String ns, final String qn) {
        final AbstractNode an = (AbstractNode)n;
        if (an == this.getDocumentElement()) {
            throw this.createDOMException((short)9, "rename.document.element", new Object[0]);
        }
        final int nt = n.getNodeType();
        if (nt != 1 && nt != 2) {
            throw this.createDOMException((short)9, "rename.node", new Object[] { nt, n.getNodeName() });
        }
        if ((this.xmlVersion.equals("1.1") && !DOMUtilities.isValidName11(qn)) || !DOMUtilities.isValidName(qn)) {
            throw this.createDOMException((short)9, "wf.invalid.name", new Object[] { qn });
        }
        if (n.getOwnerDocument() != this) {
            throw this.createDOMException((short)9, "node.from.wrong.document", new Object[] { nt, n.getNodeName() });
        }
        final int i = qn.indexOf(58);
        if (i == 0 || i == qn.length() - 1) {
            throw this.createDOMException((short)14, "qname", new Object[] { nt, n.getNodeName(), qn });
        }
        final String prefix = DOMUtilities.getPrefix(qn);
        if (ns != null && ns.length() == 0) {
            ns = null;
        }
        if (prefix != null && ns == null) {
            throw this.createDOMException((short)14, "prefix", new Object[] { nt, n.getNodeName(), prefix });
        }
        if (this.strictErrorChecking && (("xml".equals(prefix) && !"http://www.w3.org/XML/1998/namespace".equals(ns)) || ("xmlns".equals(prefix) && !"http://www.w3.org/2000/xmlns/".equals(ns)))) {
            throw this.createDOMException((short)14, "namespace", new Object[] { nt, n.getNodeName(), ns });
        }
        final String prevNamespaceURI = n.getNamespaceURI();
        final String prevNodeName = n.getNodeName();
        if (nt == 1) {
            final Node parent = n.getParentNode();
            final AbstractElement e = (AbstractElement)this.createElementNS(ns, qn);
            final EventSupport es1 = an.getEventSupport();
            if (es1 != null) {
                EventSupport es2 = e.getEventSupport();
                if (es2 == null) {
                    final AbstractDOMImplementation di = (AbstractDOMImplementation)this.implementation;
                    es2 = di.createEventSupport(e);
                    this.setEventsEnabled(true);
                    e.eventSupport = es2;
                }
                es1.moveEventListeners(e.getEventSupport());
            }
            e.userData = ((e.userData == null) ? null : ((HashMap)an.userData.clone()));
            e.userDataHandlers = ((e.userDataHandlers == null) ? null : ((HashMap)an.userDataHandlers.clone()));
            final Node next = null;
            if (parent != null) {
                n.getNextSibling();
                parent.removeChild(n);
            }
            while (n.getFirstChild() != null) {
                e.appendChild(n.getFirstChild());
            }
            final NamedNodeMap nnm = n.getAttributes();
            for (int j = 0; j < nnm.getLength(); ++j) {
                final Attr a = (Attr)nnm.item(j);
                e.setAttributeNodeNS(a);
            }
            if (parent != null) {
                if (next == null) {
                    parent.appendChild(e);
                }
                else {
                    parent.insertBefore(next, e);
                }
            }
            this.fireUserDataHandlers((short)4, n, e);
            if (this.getEventsEnabled()) {
                final MutationNameEvent ev = (MutationNameEvent)this.createEvent("MutationNameEvent");
                ev.initMutationNameEventNS("http://www.w3.org/2001/xml-events", "DOMElementNameChanged", true, false, null, prevNamespaceURI, prevNodeName);
                this.dispatchEvent(ev);
            }
            return e;
        }
        if (n instanceof AbstractAttrNS) {
            final AbstractAttrNS a2 = (AbstractAttrNS)n;
            final Element e2 = a2.getOwnerElement();
            if (e2 != null) {
                e2.removeAttributeNode(a2);
            }
            a2.namespaceURI = ns;
            a2.nodeName = qn;
            if (e2 != null) {
                e2.setAttributeNodeNS(a2);
            }
            this.fireUserDataHandlers((short)4, a2, null);
            if (this.getEventsEnabled()) {
                final MutationNameEvent ev2 = (MutationNameEvent)this.createEvent("MutationNameEvent");
                ev2.initMutationNameEventNS("http://www.w3.org/2001/xml-events", "DOMAttrNameChanged", true, false, a2, prevNamespaceURI, prevNodeName);
                this.dispatchEvent(ev2);
            }
            return a2;
        }
        final AbstractAttr a3 = (AbstractAttr)n;
        final Element e2 = a3.getOwnerElement();
        if (e2 != null) {
            e2.removeAttributeNode(a3);
        }
        final AbstractAttr a4 = (AbstractAttr)this.createAttributeNS(ns, qn);
        a4.setNodeValue(a3.getNodeValue());
        a4.userData = ((a3.userData == null) ? null : ((HashMap)a3.userData.clone()));
        a4.userDataHandlers = ((a3.userDataHandlers == null) ? null : ((HashMap)a3.userDataHandlers.clone()));
        if (e2 != null) {
            e2.setAttributeNodeNS(a4);
        }
        this.fireUserDataHandlers((short)4, a3, a4);
        if (this.getEventsEnabled()) {
            final MutationNameEvent ev3 = (MutationNameEvent)this.createEvent("MutationNameEvent");
            ev3.initMutationNameEventNS("http://www.w3.org/2001/xml-events", "DOMAttrNameChanged", true, false, a4, prevNamespaceURI, prevNodeName);
            this.dispatchEvent(ev3);
        }
        return a4;
    }
    
    @Override
    public void normalizeDocument() {
        if (this.domConfig == null) {
            this.domConfig = new DocumentConfiguration();
        }
        final boolean cdataSections = this.domConfig.getBooleanParameter("cdata-sections");
        final boolean comments = this.domConfig.getBooleanParameter("comments");
        final boolean elementContentWhitespace = this.domConfig.getBooleanParameter("element-content-whitespace");
        final boolean namespaceDeclarations = this.domConfig.getBooleanParameter("namespace-declarations");
        final boolean namespaces = this.domConfig.getBooleanParameter("namespaces");
        final boolean splitCdataSections = this.domConfig.getBooleanParameter("split-cdata-sections");
        final DOMErrorHandler errorHandler = (DOMErrorHandler)this.domConfig.getParameter("error-handler");
        this.normalizeDocument(this.getDocumentElement(), cdataSections, comments, elementContentWhitespace, namespaceDeclarations, namespaces, splitCdataSections, errorHandler);
    }
    
    protected boolean normalizeDocument(final Element e, final boolean cdataSections, final boolean comments, final boolean elementContentWhitepace, final boolean namespaceDeclarations, final boolean namespaces, final boolean splitCdataSections, final DOMErrorHandler errorHandler) {
        final AbstractElement ae = (AbstractElement)e;
        Node n = e.getFirstChild();
        while (n != null) {
            int nt = n.getNodeType();
            if (nt == 3 || (!cdataSections && nt == 4)) {
                final Node t = n;
                final StringBuffer sb = new StringBuffer();
                sb.append(t.getNodeValue());
                Node next;
                for (n = n.getNextSibling(); n != null && (n.getNodeType() == 3 || (!cdataSections && n.getNodeType() == 4)); n = next) {
                    sb.append(n.getNodeValue());
                    next = n.getNextSibling();
                    e.removeChild(n);
                }
                final String s = sb.toString();
                if (s.length() == 0) {
                    final Node next2 = n.getNextSibling();
                    e.removeChild(n);
                    n = next2;
                    continue;
                }
                if (!s.equals(t.getNodeValue())) {
                    if (!cdataSections && nt == 3) {
                        n = this.createTextNode(s);
                        e.replaceChild(n, t);
                    }
                    else {
                        n = t;
                        t.setNodeValue(s);
                    }
                }
                else {
                    n = t;
                }
                if (!elementContentWhitepace) {
                    nt = n.getNodeType();
                    if (nt == 3) {
                        final AbstractText tn = (AbstractText)n;
                        if (tn.isElementContentWhitespace()) {
                            final Node next3 = n.getNextSibling();
                            e.removeChild(n);
                            n = next3;
                            continue;
                        }
                    }
                }
                if (nt == 4 && splitCdataSections && !this.splitCdata(e, n, errorHandler)) {
                    return false;
                }
            }
            else if (nt == 4 && splitCdataSections) {
                if (!this.splitCdata(e, n, errorHandler)) {
                    return false;
                }
            }
            else if (nt == 8 && !comments) {
                Node next4 = n.getPreviousSibling();
                if (next4 == null) {
                    next4 = n.getNextSibling();
                }
                e.removeChild(n);
                n = next4;
                continue;
            }
            n = n.getNextSibling();
        }
        NamedNodeMap nnm = e.getAttributes();
        final LinkedList toRemove = new LinkedList();
        final HashMap names = new HashMap();
        for (int i = 0; i < nnm.getLength(); ++i) {
            final Attr a = (Attr)nnm.item(i);
            final String prefix = a.getPrefix();
            if ((a != null && "xmlns".equals(prefix)) || a.getNodeName().equals("xmlns")) {
                if (!namespaceDeclarations) {
                    toRemove.add(a);
                }
                else {
                    final String ns = a.getNodeValue();
                    if (!a.getNodeValue().equals("http://www.w3.org/2000/xmlns/")) {
                        if (ns.equals("http://www.w3.org/2000/xmlns/")) {
                            names.put(prefix, ns);
                        }
                    }
                }
            }
        }
        if (!namespaceDeclarations) {
            for (final Object aToRemove : toRemove) {
                e.removeAttributeNode((Attr)aToRemove);
            }
        }
        else if (namespaces) {
            final String ens = e.getNamespaceURI();
            if (ens != null) {
                final String eprefix = e.getPrefix();
                if (!this.compareStrings(ae.lookupNamespaceURI(eprefix), ens)) {
                    e.setAttributeNS("http://www.w3.org/2000/xmlns/", (eprefix == null) ? "xmlns" : ("xmlns:" + eprefix), ens);
                }
            }
            else if (e.getLocalName() != null) {
                if (ae.lookupNamespaceURI(null) == null) {
                    e.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "");
                }
            }
            nnm = e.getAttributes();
            for (int j = 0; j < nnm.getLength(); ++j) {
                final Attr a2 = (Attr)nnm.item(j);
                final String ans = a2.getNamespaceURI();
                if (ans != null) {
                    final String apre = a2.getPrefix();
                    if (apre == null || (!apre.equals("xml") && !apre.equals("xmlns"))) {
                        if (!ans.equals("http://www.w3.org/2000/xmlns/")) {
                            final String aprens = (apre == null) ? null : ae.lookupNamespaceURI(apre);
                            if (apre == null || aprens == null || !aprens.equals(ans)) {
                                String newpre = ae.lookupPrefix(ans);
                                if (newpre != null) {
                                    a2.setPrefix(newpre);
                                }
                                else if (apre != null && ae.lookupNamespaceURI(apre) == null) {
                                    e.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + apre, ans);
                                }
                                else {
                                    final int index = 1;
                                    do {
                                        newpre = "NS" + index;
                                    } while (ae.lookupPrefix(newpre) != null);
                                    e.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + newpre, ans);
                                    a2.setPrefix(newpre);
                                }
                            }
                        }
                    }
                }
                else if (a2.getLocalName() == null) {}
            }
        }
        nnm = e.getAttributes();
        for (int i = 0; i < nnm.getLength(); ++i) {
            final Attr a = (Attr)nnm.item(i);
            if (!this.checkName(a.getNodeName()) && errorHandler != null && !errorHandler.handleError(this.createDOMError("wf-invalid-character-in-node-name", (short)2, "wf.invalid.name", new Object[] { a.getNodeName() }, a, null))) {
                return false;
            }
            if (!this.checkChars(a.getNodeValue()) && errorHandler != null && !errorHandler.handleError(this.createDOMError("wf-invalid-character", (short)2, "wf.invalid.character", new Object[] { 2, a.getNodeName(), a.getNodeValue() }, a, null))) {
                return false;
            }
        }
        for (Node m = e.getFirstChild(); m != null; m = m.getNextSibling()) {
            final int nt2 = m.getNodeType();
            switch (nt2) {
                case 3: {
                    final String s2 = m.getNodeValue();
                    if (!this.checkChars(s2) && errorHandler != null && !errorHandler.handleError(this.createDOMError("wf-invalid-character", (short)2, "wf.invalid.character", new Object[] { m.getNodeType(), m.getNodeName(), s2 }, m, null))) {
                        return false;
                    }
                    break;
                }
                case 8: {
                    final String s2 = m.getNodeValue();
                    if ((!this.checkChars(s2) || s2.indexOf("--") != -1 || s2.charAt(s2.length() - 1) == '-') && errorHandler != null && !errorHandler.handleError(this.createDOMError("wf-invalid-character", (short)2, "wf.invalid.character", new Object[] { m.getNodeType(), m.getNodeName(), s2 }, m, null))) {
                        return false;
                    }
                    break;
                }
                case 4: {
                    final String s2 = m.getNodeValue();
                    if ((!this.checkChars(s2) || s2.indexOf("]]>") != -1) && errorHandler != null && !errorHandler.handleError(this.createDOMError("wf-invalid-character", (short)2, "wf.invalid.character", new Object[] { m.getNodeType(), m.getNodeName(), s2 }, m, null))) {
                        return false;
                    }
                    break;
                }
                case 7: {
                    if (m.getNodeName().equalsIgnoreCase("xml") && errorHandler != null && !errorHandler.handleError(this.createDOMError("wf-invalid-character-in-node-name", (short)2, "wf.invalid.name", new Object[] { m.getNodeName() }, m, null))) {
                        return false;
                    }
                    final String s2 = m.getNodeValue();
                    if ((!this.checkChars(s2) || s2.indexOf("?>") != -1) && errorHandler != null && !errorHandler.handleError(this.createDOMError("wf-invalid-character", (short)2, "wf.invalid.character", new Object[] { m.getNodeType(), m.getNodeName(), s2 }, m, null))) {
                        return false;
                    }
                    break;
                }
                case 1: {
                    if (!this.checkName(m.getNodeName()) && errorHandler != null && !errorHandler.handleError(this.createDOMError("wf-invalid-character-in-node-name", (short)2, "wf.invalid.name", new Object[] { m.getNodeName() }, m, null))) {
                        return false;
                    }
                    if (!this.normalizeDocument((Element)m, cdataSections, comments, elementContentWhitepace, namespaceDeclarations, namespaces, splitCdataSections, errorHandler)) {
                        return false;
                    }
                    break;
                }
            }
        }
        return true;
    }
    
    protected boolean splitCdata(final Element e, final Node n, final DOMErrorHandler errorHandler) {
        final String s2 = n.getNodeValue();
        final int index = s2.indexOf("]]>");
        if (index != -1) {
            final String before = s2.substring(0, index + 2);
            final String after = s2.substring(index + 2);
            n.setNodeValue(before);
            final Node next = n.getNextSibling();
            if (next == null) {
                e.appendChild(this.createCDATASection(after));
            }
            else {
                e.insertBefore(this.createCDATASection(after), next);
            }
            if (errorHandler != null && !errorHandler.handleError(this.createDOMError("cdata-sections-splitted", (short)1, "cdata.section.split", new Object[0], n, null))) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean checkChars(final String s) {
        final int len = s.length();
        if (this.xmlVersion.equals("1.1")) {
            for (int i = 0; i < len; ++i) {
                if (!XMLUtilities.isXML11Character(s.charAt(i))) {
                    return false;
                }
            }
        }
        else {
            for (int i = 0; i < len; ++i) {
                if (!XMLUtilities.isXMLCharacter(s.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    protected boolean checkName(final String s) {
        if (this.xmlVersion.equals("1.1")) {
            return DOMUtilities.isValidName11(s);
        }
        return DOMUtilities.isValidName(s);
    }
    
    protected DOMError createDOMError(final String type, final short severity, final String key, final Object[] args, final Node related, final Exception e) {
        try {
            return new DocumentError(type, severity, this.getCurrentDocument().formatMessage(key, args), related, e);
        }
        catch (Exception ex) {
            return new DocumentError(type, severity, key, related, e);
        }
    }
    
    @Override
    public void setTextContent(final String s) throws DOMException {
    }
    
    public void setXBLManager(XBLManager m) {
        final boolean wasProcessing = this.xblManager.isProcessing();
        this.xblManager.stopProcessing();
        if (m == null) {
            m = new GenericXBLManager();
        }
        this.xblManager = m;
        if (wasProcessing) {
            this.xblManager.startProcessing();
        }
    }
    
    public XBLManager getXBLManager() {
        return this.xblManager;
    }
    
    @Override
    public XPathExpression createExpression(final String expression, final XPathNSResolver resolver) throws DOMException, XPathException {
        return new XPathExpr(expression, resolver);
    }
    
    @Override
    public XPathNSResolver createNSResolver(final Node n) {
        return new XPathNodeNSResolver(n);
    }
    
    @Override
    public Object evaluate(final String expression, final Node contextNode, final XPathNSResolver resolver, final short type, final Object result) throws XPathException, DOMException {
        final XPathExpression xpath = this.createExpression(expression, resolver);
        return xpath.evaluate(contextNode, type, result);
    }
    
    public XPathException createXPathException(final short type, final String key, final Object[] args) {
        try {
            return new XPathException(type, this.formatMessage(key, args));
        }
        catch (Exception e) {
            return new XPathException(type, key);
        }
    }
    
    @Override
    public Node getXblParentNode() {
        return this.xblManager.getXblParentNode(this);
    }
    
    @Override
    public NodeList getXblChildNodes() {
        return this.xblManager.getXblChildNodes(this);
    }
    
    @Override
    public NodeList getXblScopedChildNodes() {
        return this.xblManager.getXblScopedChildNodes(this);
    }
    
    @Override
    public Node getXblFirstChild() {
        return this.xblManager.getXblFirstChild(this);
    }
    
    @Override
    public Node getXblLastChild() {
        return this.xblManager.getXblLastChild(this);
    }
    
    @Override
    public Node getXblPreviousSibling() {
        return this.xblManager.getXblPreviousSibling(this);
    }
    
    @Override
    public Node getXblNextSibling() {
        return this.xblManager.getXblNextSibling(this);
    }
    
    @Override
    public Element getXblFirstElementChild() {
        return this.xblManager.getXblFirstElementChild(this);
    }
    
    @Override
    public Element getXblLastElementChild() {
        return this.xblManager.getXblLastElementChild(this);
    }
    
    @Override
    public Element getXblPreviousElementSibling() {
        return this.xblManager.getXblPreviousElementSibling(this);
    }
    
    @Override
    public Element getXblNextElementSibling() {
        return this.xblManager.getXblNextElementSibling(this);
    }
    
    @Override
    public Element getXblBoundElement() {
        return this.xblManager.getXblBoundElement(this);
    }
    
    @Override
    public Element getXblShadowTree() {
        return this.xblManager.getXblShadowTree(this);
    }
    
    @Override
    public NodeList getXblDefinitions() {
        return this.xblManager.getXblDefinitions(this);
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeObject(this.implementation.getClass().getName());
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.localizableSupport = new LocalizableSupport("org.apache.batik.dom.resources.Messages", this.getClass().getClassLoader());
        final Class c = Class.forName((String)s.readObject());
        try {
            final Method m = c.getMethod("getDOMImplementation", (Class[])null);
            this.implementation = (DOMImplementation)m.invoke(null, (Object[])null);
        }
        catch (Exception e) {
            if (!DOMImplementation.class.isAssignableFrom(c)) {
                throw new SecurityException("Trying to create object that is not a DOMImplementation.");
            }
            try {
                this.implementation = c.getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            }
            catch (Exception ex) {}
        }
    }
    
    protected class IdSoftRef extends CleanerThread.SoftReferenceCleared
    {
        String id;
        List list;
        
        IdSoftRef(final Object o, final String id) {
            super(o);
            this.id = id;
        }
        
        IdSoftRef(final Object o, final String id, final List list) {
            super(o);
            this.id = id;
            this.list = list;
        }
        
        public void setList(final List list) {
            this.list = list;
        }
        
        @Override
        public void cleared() {
            if (AbstractDocument.this.elementsById == null) {
                return;
            }
            synchronized (AbstractDocument.this.elementsById) {
                if (this.list != null) {
                    this.list.remove(this);
                }
                else {
                    final Object o = AbstractDocument.this.elementsById.remove(this.id);
                    if (o != this) {
                        AbstractDocument.this.elementsById.put(this.id, o);
                    }
                }
            }
        }
    }
    
    protected static class DocumentError implements DOMError
    {
        protected String type;
        protected short severity;
        protected String message;
        protected Node relatedNode;
        protected Object relatedException;
        protected DOMLocator domLocator;
        
        public DocumentError(final String type, final short severity, final String message, final Node relatedNode, final Exception relatedException) {
            this.type = type;
            this.severity = severity;
            this.message = message;
            this.relatedNode = relatedNode;
            this.relatedException = relatedException;
        }
        
        @Override
        public String getType() {
            return this.type;
        }
        
        @Override
        public short getSeverity() {
            return this.severity;
        }
        
        @Override
        public String getMessage() {
            return this.message;
        }
        
        @Override
        public Object getRelatedData() {
            return this.relatedNode;
        }
        
        @Override
        public Object getRelatedException() {
            return this.relatedException;
        }
        
        @Override
        public DOMLocator getLocation() {
            if (this.domLocator == null) {
                this.domLocator = new ErrorLocation(this.relatedNode);
            }
            return this.domLocator;
        }
        
        protected static class ErrorLocation implements DOMLocator
        {
            protected Node node;
            
            public ErrorLocation(final Node n) {
                this.node = n;
            }
            
            @Override
            public int getLineNumber() {
                return -1;
            }
            
            @Override
            public int getColumnNumber() {
                return -1;
            }
            
            @Override
            public int getByteOffset() {
                return -1;
            }
            
            @Override
            public int getUtf16Offset() {
                return -1;
            }
            
            @Override
            public Node getRelatedNode() {
                return this.node;
            }
            
            @Override
            public String getUri() {
                final AbstractDocument doc = (AbstractDocument)this.node.getOwnerDocument();
                return doc.getDocumentURI();
            }
        }
    }
    
    protected class DocumentConfiguration implements DOMConfiguration
    {
        protected String[] booleanParamNames;
        protected boolean[] booleanParamValues;
        protected boolean[] booleanParamReadOnly;
        protected Map booleanParamIndexes;
        protected Object errorHandler;
        protected ParameterNameList paramNameList;
        
        protected DocumentConfiguration() {
            this.booleanParamNames = new String[] { "canonical-form", "cdata-sections", "check-character-normalization", "comments", "datatype-normalization", "element-content-whitespace", "entities", "infoset", "namespaces", "namespace-declarations", "normalize-characters", "split-cdata-sections", "validate", "validate-if-schema", "well-formed" };
            this.booleanParamValues = new boolean[] { false, true, false, true, false, false, true, false, true, true, false, true, false, false, true };
            this.booleanParamReadOnly = new boolean[] { true, false, true, false, true, false, false, false, false, false, true, false, true, true, false };
            this.booleanParamIndexes = new HashMap();
            for (int i = 0; i < this.booleanParamNames.length; ++i) {
                this.booleanParamIndexes.put(this.booleanParamNames[i], i);
            }
        }
        
        @Override
        public void setParameter(final String name, final Object value) {
            if ("error-handler".equals(name)) {
                if (value != null && !(value instanceof DOMErrorHandler)) {
                    throw AbstractDocument.this.createDOMException((short)17, "domconfig.param.type", new Object[] { name });
                }
                this.errorHandler = value;
            }
            else {
                final Integer i = this.booleanParamIndexes.get(name);
                if (i == null) {
                    throw AbstractDocument.this.createDOMException((short)8, "domconfig.param.not.found", new Object[] { name });
                }
                if (value == null) {
                    throw AbstractDocument.this.createDOMException((short)9, "domconfig.param.value", new Object[] { name });
                }
                if (!(value instanceof Boolean)) {
                    throw AbstractDocument.this.createDOMException((short)17, "domconfig.param.type", new Object[] { name });
                }
                final int index = i;
                final boolean val = (boolean)value;
                if (this.booleanParamReadOnly[index] && this.booleanParamValues[index] != val) {
                    throw AbstractDocument.this.createDOMException((short)9, "domconfig.param.value", new Object[] { name });
                }
                this.booleanParamValues[index] = val;
                if (name.equals("infoset")) {
                    this.setParameter("validate-if-schema", Boolean.FALSE);
                    this.setParameter("entities", Boolean.FALSE);
                    this.setParameter("datatype-normalization", Boolean.FALSE);
                    this.setParameter("cdata-sections", Boolean.FALSE);
                    this.setParameter("well-formed", Boolean.TRUE);
                    this.setParameter("element-content-whitespace", Boolean.TRUE);
                    this.setParameter("comments", Boolean.TRUE);
                    this.setParameter("namespaces", Boolean.TRUE);
                }
            }
        }
        
        @Override
        public Object getParameter(final String name) {
            if ("error-handler".equals(name)) {
                return this.errorHandler;
            }
            final Integer index = this.booleanParamIndexes.get(name);
            if (index == null) {
                throw AbstractDocument.this.createDOMException((short)8, "domconfig.param.not.found", new Object[] { name });
            }
            return this.booleanParamValues[index] ? Boolean.TRUE : Boolean.FALSE;
        }
        
        public boolean getBooleanParameter(final String name) {
            final Boolean b = (Boolean)this.getParameter(name);
            return b;
        }
        
        @Override
        public boolean canSetParameter(final String name, final Object value) {
            if (name.equals("error-handler")) {
                return value == null || value instanceof DOMErrorHandler;
            }
            final Integer i = this.booleanParamIndexes.get(name);
            if (i == null || value == null || !(value instanceof Boolean)) {
                return false;
            }
            final int index = i;
            final boolean val = (boolean)value;
            return !this.booleanParamReadOnly[index] || this.booleanParamValues[index] == val;
        }
        
        @Override
        public DOMStringList getParameterNames() {
            if (this.paramNameList == null) {
                this.paramNameList = new ParameterNameList();
            }
            return this.paramNameList;
        }
        
        protected class ParameterNameList implements DOMStringList
        {
            @Override
            public String item(final int index) {
                if (index < 0) {
                    return null;
                }
                if (index < DocumentConfiguration.this.booleanParamNames.length) {
                    return DocumentConfiguration.this.booleanParamNames[index];
                }
                if (index == DocumentConfiguration.this.booleanParamNames.length) {
                    return "error-handler";
                }
                return null;
            }
            
            @Override
            public int getLength() {
                return DocumentConfiguration.this.booleanParamNames.length + 1;
            }
            
            @Override
            public boolean contains(final String s) {
                if ("error-handler".equals(s)) {
                    return true;
                }
                for (final String booleanParamName : DocumentConfiguration.this.booleanParamNames) {
                    if (booleanParamName.equals(s)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }
    
    protected class XPathExpr implements XPathExpression
    {
        protected XPath xpath;
        protected XPathNSResolver resolver;
        protected NSPrefixResolver prefixResolver;
        protected XPathContext context;
        
        public XPathExpr(final String expr, final XPathNSResolver res) throws DOMException, XPathException {
            this.resolver = res;
            this.prefixResolver = new NSPrefixResolver();
            try {
                this.xpath = new XPath(expr, (SourceLocator)null, (PrefixResolver)this.prefixResolver, 0);
                this.context = new XPathContext();
            }
            catch (TransformerException te) {
                throw AbstractDocument.this.createXPathException((short)51, "xpath.invalid.expression", new Object[] { expr, te.getMessage() });
            }
        }
        
        @Override
        public Object evaluate(final Node contextNode, final short type, final Object res) throws XPathException, DOMException {
            if ((contextNode.getNodeType() != 9 && contextNode.getOwnerDocument() != AbstractDocument.this) || (contextNode.getNodeType() == 9 && contextNode != AbstractDocument.this)) {
                throw AbstractDocument.this.createDOMException((short)4, "node.from.wrong.document", new Object[] { contextNode.getNodeType(), contextNode.getNodeName() });
            }
            if (type < 0 || type > 9) {
                throw AbstractDocument.this.createDOMException((short)9, "xpath.invalid.result.type", new Object[] { type });
            }
            switch (contextNode.getNodeType()) {
                case 5:
                case 6:
                case 10:
                case 11:
                case 12: {
                    throw AbstractDocument.this.createDOMException((short)9, "xpath.invalid.context.node", new Object[] { contextNode.getNodeType(), contextNode.getNodeName() });
                }
                default: {
                    this.context.reset();
                    XObject result = null;
                    try {
                        result = this.xpath.execute(this.context, contextNode, (PrefixResolver)this.prefixResolver);
                    }
                    catch (TransformerException te) {
                        throw AbstractDocument.this.createXPathException((short)51, "xpath.error", new Object[] { this.xpath.getPatternString(), te.getMessage() });
                    }
                    try {
                        Label_0425: {
                            switch (type) {
                                case 8:
                                case 9: {
                                    return this.convertSingleNode(result, type);
                                }
                                case 3: {
                                    return this.convertBoolean(result);
                                }
                                case 1: {
                                    return this.convertNumber(result);
                                }
                                case 4:
                                case 5:
                                case 6:
                                case 7: {
                                    return this.convertNodeIterator(result, type);
                                }
                                case 2: {
                                    return this.convertString(result);
                                }
                                case 0: {
                                    switch (result.getType()) {
                                        case 1: {
                                            return this.convertBoolean(result);
                                        }
                                        case 2: {
                                            return this.convertNumber(result);
                                        }
                                        case 3: {
                                            return this.convertString(result);
                                        }
                                        case 4: {
                                            return this.convertNodeIterator(result, (short)4);
                                        }
                                        default: {
                                            break Label_0425;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    catch (TransformerException te) {
                        throw AbstractDocument.this.createXPathException((short)52, "xpath.cannot.convert.result", new Object[] { type, te.getMessage() });
                    }
                    return null;
                }
            }
        }
        
        protected Result convertSingleNode(final XObject xo, final short type) throws TransformerException {
            return new Result(xo.nodelist().item(0), type);
        }
        
        protected Result convertBoolean(final XObject xo) throws TransformerException {
            return new Result(xo.bool());
        }
        
        protected Result convertNumber(final XObject xo) throws TransformerException {
            return new Result(xo.num());
        }
        
        protected Result convertString(final XObject xo) {
            return new Result(xo.str());
        }
        
        protected Result convertNodeIterator(final XObject xo, final short type) throws TransformerException {
            return new Result(xo.nodelist(), type);
        }
        
        public class Result implements XPathResult
        {
            protected short resultType;
            protected double numberValue;
            protected String stringValue;
            protected boolean booleanValue;
            protected Node singleNodeValue;
            protected NodeList iterator;
            protected int iteratorPosition;
            
            public Result(final Node n, final short type) {
                this.resultType = type;
                this.singleNodeValue = n;
            }
            
            public Result(final boolean b) throws TransformerException {
                this.resultType = 3;
                this.booleanValue = b;
            }
            
            public Result(final double d) throws TransformerException {
                this.resultType = 1;
                this.numberValue = d;
            }
            
            public Result(final String s) {
                this.resultType = 2;
                this.stringValue = s;
            }
            
            public Result(final NodeList nl, final short type) {
                this.resultType = type;
                this.iterator = nl;
            }
            
            @Override
            public short getResultType() {
                return this.resultType;
            }
            
            @Override
            public boolean getBooleanValue() {
                if (this.resultType != 3) {
                    throw AbstractDocument.this.createXPathException((short)52, "xpath.invalid.result.type", new Object[] { this.resultType });
                }
                return this.booleanValue;
            }
            
            @Override
            public double getNumberValue() {
                if (this.resultType != 1) {
                    throw AbstractDocument.this.createXPathException((short)52, "xpath.invalid.result.type", new Object[] { this.resultType });
                }
                return this.numberValue;
            }
            
            @Override
            public String getStringValue() {
                if (this.resultType != 2) {
                    throw AbstractDocument.this.createXPathException((short)52, "xpath.invalid.result.type", new Object[] { this.resultType });
                }
                return this.stringValue;
            }
            
            @Override
            public Node getSingleNodeValue() {
                if (this.resultType != 8 && this.resultType != 9) {
                    throw AbstractDocument.this.createXPathException((short)52, "xpath.invalid.result.type", new Object[] { this.resultType });
                }
                return this.singleNodeValue;
            }
            
            @Override
            public boolean getInvalidIteratorState() {
                return false;
            }
            
            @Override
            public int getSnapshotLength() {
                if (this.resultType != 6 && this.resultType != 7) {
                    throw AbstractDocument.this.createXPathException((short)52, "xpath.invalid.result.type", new Object[] { this.resultType });
                }
                return this.iterator.getLength();
            }
            
            @Override
            public Node iterateNext() {
                if (this.resultType != 4 && this.resultType != 5) {
                    throw AbstractDocument.this.createXPathException((short)52, "xpath.invalid.result.type", new Object[] { this.resultType });
                }
                return this.iterator.item(this.iteratorPosition++);
            }
            
            @Override
            public Node snapshotItem(final int i) {
                if (this.resultType != 6 && this.resultType != 7) {
                    throw AbstractDocument.this.createXPathException((short)52, "xpath.invalid.result.type", new Object[] { this.resultType });
                }
                return this.iterator.item(i);
            }
        }
        
        protected class NSPrefixResolver implements PrefixResolver
        {
            public String getBaseIdentifier() {
                return null;
            }
            
            public String getNamespaceForPrefix(final String prefix) {
                if (XPathExpr.this.resolver == null) {
                    return null;
                }
                return XPathExpr.this.resolver.lookupNamespaceURI(prefix);
            }
            
            public String getNamespaceForPrefix(final String prefix, final Node context) {
                if (XPathExpr.this.resolver == null) {
                    return null;
                }
                return XPathExpr.this.resolver.lookupNamespaceURI(prefix);
            }
            
            public boolean handlesNullPrefixes() {
                return false;
            }
        }
    }
    
    protected static class XPathNodeNSResolver implements XPathNSResolver
    {
        protected Node contextNode;
        
        public XPathNodeNSResolver(final Node n) {
            this.contextNode = n;
        }
        
        @Override
        public String lookupNamespaceURI(final String prefix) {
            return this.contextNode.lookupNamespaceURI(prefix);
        }
    }
}
