// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import java.util.List;
import org.w3c.dom.events.MutationEvent;
import org.apache.batik.dom.events.EventSupport;
import org.apache.batik.anim.dom.XBLOMImportElement;
import java.util.LinkedList;
import org.w3c.dom.events.EventTarget;
import org.apache.batik.dom.AbstractNode;
import java.util.Iterator;
import org.apache.batik.dom.xbl.NodeXBL;
import java.util.ArrayList;
import org.apache.batik.dom.xbl.XBLManagerData;
import java.util.Set;
import org.apache.batik.anim.dom.XBLOMContentElement;
import org.w3c.dom.NamedNodeMap;
import org.apache.batik.dom.AbstractAttrNS;
import org.w3c.dom.Attr;
import org.w3c.dom.events.Event;
import org.apache.batik.dom.xbl.ShadowTreeEvent;
import org.w3c.dom.events.DocumentEvent;
import org.apache.batik.dom.xbl.XBLShadowTreeElement;
import org.apache.batik.anim.dom.XBLOMShadowTreeElement;
import org.apache.batik.anim.dom.BindableElement;
import org.apache.batik.anim.dom.XBLOMTemplateElement;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.bridge.BridgeException;
import java.util.TreeSet;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.apache.batik.anim.dom.XBLEventSupport;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.anim.dom.XBLOMDefinitionElement;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.util.HashMap;
import javax.swing.event.EventListenerList;
import java.util.Map;
import org.apache.batik.util.DoublyIndexedTable;
import org.apache.batik.bridge.BridgeContext;
import org.w3c.dom.Document;
import org.apache.batik.util.XBLConstants;
import org.apache.batik.dom.xbl.XBLManager;

public class DefaultXBLManager implements XBLManager, XBLConstants
{
    protected boolean isProcessing;
    protected Document document;
    protected BridgeContext ctx;
    protected DoublyIndexedTable definitionLists;
    protected DoublyIndexedTable definitions;
    protected Map contentManagers;
    protected Map imports;
    protected DocInsertedListener docInsertedListener;
    protected DocRemovedListener docRemovedListener;
    protected DocSubtreeListener docSubtreeListener;
    protected ImportAttrListener importAttrListener;
    protected RefAttrListener refAttrListener;
    protected EventListenerList bindingListenerList;
    protected EventListenerList contentSelectionChangedListenerList;
    
    public DefaultXBLManager(final Document doc, final BridgeContext ctx) {
        this.definitionLists = new DoublyIndexedTable();
        this.definitions = new DoublyIndexedTable();
        this.contentManagers = new HashMap();
        this.imports = new HashMap();
        this.docInsertedListener = new DocInsertedListener();
        this.docRemovedListener = new DocRemovedListener();
        this.docSubtreeListener = new DocSubtreeListener();
        this.importAttrListener = new ImportAttrListener();
        this.refAttrListener = new RefAttrListener();
        this.bindingListenerList = new EventListenerList();
        this.contentSelectionChangedListenerList = new EventListenerList();
        this.document = doc;
        this.ctx = ctx;
        final ImportRecord ir = new ImportRecord(null, null);
        this.imports.put(null, ir);
    }
    
    @Override
    public void startProcessing() {
        if (this.isProcessing) {
            return;
        }
        NodeList nl = this.document.getElementsByTagNameNS("http://www.w3.org/2004/xbl", "definition");
        final XBLOMDefinitionElement[] defs = new XBLOMDefinitionElement[nl.getLength()];
        for (int i = 0; i < defs.length; ++i) {
            defs[i] = (XBLOMDefinitionElement)nl.item(i);
        }
        nl = this.document.getElementsByTagNameNS("http://www.w3.org/2004/xbl", "import");
        final Element[] imports = new Element[nl.getLength()];
        for (int j = 0; j < imports.length; ++j) {
            imports[j] = (Element)nl.item(j);
        }
        final AbstractDocument doc = (AbstractDocument)this.document;
        final XBLEventSupport es = (XBLEventSupport)doc.initializeEventSupport();
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.docRemovedListener, true);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.docInsertedListener, true);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", this.docSubtreeListener, true);
        for (final XBLOMDefinitionElement def : defs) {
            if (def.getAttributeNS(null, "ref").length() != 0) {
                this.addDefinitionRef(def);
            }
            else {
                final String ns = def.getElementNamespaceURI();
                final String ln = def.getElementLocalName();
                this.addDefinition(ns, ln, def, null);
            }
        }
        for (final Element anImport : imports) {
            this.addImport(anImport);
        }
        this.isProcessing = true;
        this.bind(this.document.getDocumentElement());
    }
    
    @Override
    public void stopProcessing() {
        if (!this.isProcessing) {
            return;
        }
        this.isProcessing = false;
        final AbstractDocument doc = (AbstractDocument)this.document;
        final XBLEventSupport es = (XBLEventSupport)doc.initializeEventSupport();
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.docRemovedListener, true);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.docInsertedListener, true);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", this.docSubtreeListener, true);
        final int nSlots = this.imports.values().size();
        final ImportRecord[] irs = new ImportRecord[nSlots];
        this.imports.values().toArray(irs);
        for (final ImportRecord ir : irs) {
            if (ir.importElement.getLocalName().equals("definition")) {
                this.removeDefinitionRef(ir.importElement);
            }
            else {
                this.removeImport(ir.importElement);
            }
        }
        final Object[] defRecs = this.definitions.getValuesArray();
        this.definitions.clear();
        for (final Object defRec1 : defRecs) {
            DefinitionRecord defRec2 = (DefinitionRecord)defRec1;
            final TreeSet defs = (TreeSet)this.definitionLists.get(defRec2.namespaceURI, defRec2.localName);
            if (defs != null) {
                while (!defs.isEmpty()) {
                    defRec2 = defs.first();
                    defs.remove(defRec2);
                    this.removeDefinition(defRec2);
                }
                this.definitionLists.put(defRec2.namespaceURI, defRec2.localName, null);
            }
        }
        this.definitionLists = new DoublyIndexedTable();
        this.contentManagers.clear();
    }
    
    @Override
    public boolean isProcessing() {
        return this.isProcessing;
    }
    
    protected void addDefinitionRef(final Element defRef) {
        final String ref = defRef.getAttributeNS(null, "ref");
        final Element e = this.ctx.getReferencedElement(defRef, ref);
        if (!"http://www.w3.org/2004/xbl".equals(e.getNamespaceURI()) || !"definition".equals(e.getLocalName())) {
            throw new BridgeException(this.ctx, defRef, "uri.badTarget", new Object[] { ref });
        }
        final ImportRecord ir = new ImportRecord(defRef, e);
        this.imports.put(defRef, ir);
        final NodeEventTarget et = (NodeEventTarget)defRef;
        et.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.refAttrListener, false, null);
        final XBLOMDefinitionElement d = (XBLOMDefinitionElement)defRef;
        final String ns = d.getElementNamespaceURI();
        final String ln = d.getElementLocalName();
        this.addDefinition(ns, ln, (XBLOMDefinitionElement)e, defRef);
    }
    
    protected void removeDefinitionRef(final Element defRef) {
        final ImportRecord ir = this.imports.get(defRef);
        final NodeEventTarget et = (NodeEventTarget)defRef;
        et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.refAttrListener, false);
        final DefinitionRecord defRec = (DefinitionRecord)this.definitions.get(ir.node, defRef);
        this.removeDefinition(defRec);
        this.imports.remove(defRef);
    }
    
    protected void addImport(final Element imp) {
        final String bindings = imp.getAttributeNS(null, "bindings");
        final Node n = this.ctx.getReferencedNode(imp, bindings);
        if (n.getNodeType() == 1 && (!"http://www.w3.org/2004/xbl".equals(n.getNamespaceURI()) || !"xbl".equals(n.getLocalName()))) {
            throw new BridgeException(this.ctx, imp, "uri.badTarget", new Object[] { n });
        }
        final ImportRecord ir = new ImportRecord(imp, n);
        this.imports.put(imp, ir);
        NodeEventTarget et = (NodeEventTarget)imp;
        et.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.importAttrListener, false, null);
        et = (NodeEventTarget)n;
        et.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", ir.importInsertedListener, false, null);
        et.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", ir.importRemovedListener, false, null);
        et.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", ir.importSubtreeListener, false, null);
        this.addImportedDefinitions(imp, n);
    }
    
    protected void addImportedDefinitions(final Element imp, Node n) {
        if (n instanceof XBLOMDefinitionElement) {
            final XBLOMDefinitionElement def = (XBLOMDefinitionElement)n;
            final String ns = def.getElementNamespaceURI();
            final String ln = def.getElementLocalName();
            this.addDefinition(ns, ln, def, imp);
        }
        else {
            for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
                this.addImportedDefinitions(imp, n);
            }
        }
    }
    
    protected void removeImport(final Element imp) {
        final ImportRecord ir = this.imports.get(imp);
        NodeEventTarget et = (NodeEventTarget)ir.node;
        et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", ir.importInsertedListener, false);
        et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", ir.importRemovedListener, false);
        et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", ir.importSubtreeListener, false);
        et = (NodeEventTarget)imp;
        et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.importAttrListener, false);
        final Object[] arr$;
        final Object[] defRecs = arr$ = this.definitions.getValuesArray();
        for (final Object defRec1 : arr$) {
            final DefinitionRecord defRec2 = (DefinitionRecord)defRec1;
            if (defRec2.importElement == imp) {
                this.removeDefinition(defRec2);
            }
        }
        this.imports.remove(imp);
    }
    
    protected void addDefinition(final String namespaceURI, final String localName, final XBLOMDefinitionElement def, final Element imp) {
        final ImportRecord ir = this.imports.get(imp);
        DefinitionRecord oldDefRec = null;
        TreeSet defs = (TreeSet)this.definitionLists.get(namespaceURI, localName);
        if (defs == null) {
            defs = new TreeSet();
            this.definitionLists.put(namespaceURI, localName, defs);
        }
        else if (defs.size() > 0) {
            oldDefRec = defs.first();
        }
        XBLOMTemplateElement template = null;
        for (Node n = def.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof XBLOMTemplateElement) {
                template = (XBLOMTemplateElement)n;
                break;
            }
        }
        final DefinitionRecord defRec = new DefinitionRecord(namespaceURI, localName, def, template, imp);
        defs.add(defRec);
        this.definitions.put(def, imp, defRec);
        this.addDefinitionElementListeners(def, ir);
        if (defs.first() != defRec) {
            return;
        }
        if (oldDefRec != null) {
            final XBLOMDefinitionElement oldDef = oldDefRec.definition;
            final XBLOMTemplateElement oldTemplate = oldDefRec.template;
            if (oldTemplate != null) {
                this.removeTemplateElementListeners(oldTemplate, ir);
            }
            this.removeDefinitionElementListeners(oldDef, ir);
        }
        if (template != null) {
            this.addTemplateElementListeners(template, ir);
        }
        if (this.isProcessing) {
            this.rebind(namespaceURI, localName, this.document.getDocumentElement());
        }
    }
    
    protected void addDefinitionElementListeners(final XBLOMDefinitionElement def, final ImportRecord ir) {
        final XBLEventSupport es = (XBLEventSupport)def.initializeEventSupport();
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", ir.defAttrListener, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", ir.defNodeInsertedListener, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", ir.defNodeRemovedListener, false);
    }
    
    protected void addTemplateElementListeners(final XBLOMTemplateElement template, final ImportRecord ir) {
        final XBLEventSupport es = (XBLEventSupport)template.initializeEventSupport();
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", ir.templateMutationListener, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", ir.templateMutationListener, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", ir.templateMutationListener, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", ir.templateMutationListener, false);
    }
    
    protected void removeDefinition(final DefinitionRecord defRec) {
        final TreeSet defs = (TreeSet)this.definitionLists.get(defRec.namespaceURI, defRec.localName);
        if (defs == null) {
            return;
        }
        final Element imp = defRec.importElement;
        final ImportRecord ir = this.imports.get(imp);
        final DefinitionRecord activeDefRec = defs.first();
        defs.remove(defRec);
        this.definitions.remove(defRec.definition, imp);
        this.removeDefinitionElementListeners(defRec.definition, ir);
        if (defRec != activeDefRec) {
            return;
        }
        if (defRec.template != null) {
            this.removeTemplateElementListeners(defRec.template, ir);
        }
        this.rebind(defRec.namespaceURI, defRec.localName, this.document.getDocumentElement());
    }
    
    protected void removeDefinitionElementListeners(final XBLOMDefinitionElement def, final ImportRecord ir) {
        final XBLEventSupport es = (XBLEventSupport)def.initializeEventSupport();
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", ir.defAttrListener, false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", ir.defNodeInsertedListener, false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", ir.defNodeRemovedListener, false);
    }
    
    protected void removeTemplateElementListeners(final XBLOMTemplateElement template, final ImportRecord ir) {
        final XBLEventSupport es = (XBLEventSupport)template.initializeEventSupport();
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", ir.templateMutationListener, false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", ir.templateMutationListener, false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", ir.templateMutationListener, false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", ir.templateMutationListener, false);
    }
    
    protected DefinitionRecord getActiveDefinition(final String namespaceURI, final String localName) {
        final TreeSet defs = (TreeSet)this.definitionLists.get(namespaceURI, localName);
        if (defs == null || defs.size() == 0) {
            return null;
        }
        return defs.first();
    }
    
    protected void unbind(final Element e) {
        if (e instanceof BindableElement) {
            this.setActiveDefinition((BindableElement)e, null);
        }
        else {
            final NodeList nl = this.getXblScopedChildNodes(e);
            for (int i = 0; i < nl.getLength(); ++i) {
                final Node n = nl.item(i);
                if (n.getNodeType() == 1) {
                    this.unbind((Element)n);
                }
            }
        }
    }
    
    protected void bind(final Element e) {
        final AbstractDocument doc = (AbstractDocument)e.getOwnerDocument();
        if (doc != this.document) {
            final XBLManager xm = doc.getXBLManager();
            if (xm instanceof DefaultXBLManager) {
                ((DefaultXBLManager)xm).bind(e);
                return;
            }
        }
        if (e instanceof BindableElement) {
            final DefinitionRecord defRec = this.getActiveDefinition(e.getNamespaceURI(), e.getLocalName());
            this.setActiveDefinition((BindableElement)e, defRec);
        }
        else {
            final NodeList nl = this.getXblScopedChildNodes(e);
            for (int i = 0; i < nl.getLength(); ++i) {
                final Node n = nl.item(i);
                if (n.getNodeType() == 1) {
                    this.bind((Element)n);
                }
            }
        }
    }
    
    protected void rebind(final String namespaceURI, final String localName, final Element e) {
        final AbstractDocument doc = (AbstractDocument)e.getOwnerDocument();
        if (doc != this.document) {
            final XBLManager xm = doc.getXBLManager();
            if (xm instanceof DefaultXBLManager) {
                ((DefaultXBLManager)xm).rebind(namespaceURI, localName, e);
                return;
            }
        }
        if (e instanceof BindableElement && namespaceURI.equals(e.getNamespaceURI()) && localName.equals(e.getLocalName())) {
            final DefinitionRecord defRec = this.getActiveDefinition(e.getNamespaceURI(), e.getLocalName());
            this.setActiveDefinition((BindableElement)e, defRec);
        }
        else {
            final NodeList nl = this.getXblScopedChildNodes(e);
            for (int i = 0; i < nl.getLength(); ++i) {
                final Node n = nl.item(i);
                if (n.getNodeType() == 1) {
                    this.rebind(namespaceURI, localName, (Element)n);
                }
            }
        }
    }
    
    protected void setActiveDefinition(final BindableElement elt, final DefinitionRecord defRec) {
        final XBLRecord rec = this.getRecord(elt);
        rec.definitionElement = ((defRec == null) ? null : defRec.definition);
        if (defRec != null && defRec.definition != null && defRec.template != null) {
            this.setXblShadowTree(elt, this.cloneTemplate(defRec.template));
        }
        else {
            this.setXblShadowTree(elt, null);
        }
    }
    
    protected void setXblShadowTree(final BindableElement elt, final XBLOMShadowTreeElement newShadow) {
        final XBLOMShadowTreeElement oldShadow = (XBLOMShadowTreeElement)this.getXblShadowTree(elt);
        if (oldShadow != null) {
            this.fireShadowTreeEvent(elt, "unbinding", oldShadow);
            final ContentManager cm = this.getContentManager(oldShadow);
            if (cm != null) {
                cm.dispose();
            }
            elt.setShadowTree(null);
            final XBLRecord rec = this.getRecord(oldShadow);
            rec.boundElement = null;
            oldShadow.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", this.docSubtreeListener, false);
        }
        if (newShadow != null) {
            newShadow.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", this.docSubtreeListener, false, null);
            this.fireShadowTreeEvent(elt, "prebind", newShadow);
            elt.setShadowTree(newShadow);
            final XBLRecord rec2 = this.getRecord(newShadow);
            rec2.boundElement = elt;
            final AbstractDocument doc = (AbstractDocument)elt.getOwnerDocument();
            final XBLManager xm = doc.getXBLManager();
            final ContentManager cm2 = new ContentManager(newShadow, xm);
            this.setContentManager(newShadow, cm2);
        }
        this.invalidateChildNodes(elt);
        if (newShadow != null) {
            final NodeList nl = this.getXblScopedChildNodes(elt);
            for (int i = 0; i < nl.getLength(); ++i) {
                final Node n = nl.item(i);
                if (n.getNodeType() == 1) {
                    this.bind((Element)n);
                }
            }
            this.dispatchBindingChangedEvent(elt, newShadow);
            this.fireShadowTreeEvent(elt, "bound", newShadow);
        }
        else {
            this.dispatchBindingChangedEvent(elt, newShadow);
        }
    }
    
    protected void fireShadowTreeEvent(final BindableElement elt, final String type, final XBLShadowTreeElement e) {
        final DocumentEvent de = (DocumentEvent)elt.getOwnerDocument();
        final ShadowTreeEvent evt = (ShadowTreeEvent)de.createEvent("ShadowTreeEvent");
        evt.initShadowTreeEventNS("http://www.w3.org/2004/xbl", type, true, false, e);
        elt.dispatchEvent(evt);
    }
    
    protected XBLOMShadowTreeElement cloneTemplate(final XBLOMTemplateElement template) {
        final XBLOMShadowTreeElement clone = (XBLOMShadowTreeElement)template.getOwnerDocument().createElementNS("http://www.w3.org/2004/xbl", "shadowTree");
        final NamedNodeMap attrs = template.getAttributes();
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr attr = (Attr)attrs.item(i);
            if (attr instanceof AbstractAttrNS) {
                clone.setAttributeNodeNS(attr);
            }
            else {
                clone.setAttributeNode(attr);
            }
        }
        for (Node n = template.getFirstChild(); n != null; n = n.getNextSibling()) {
            clone.appendChild(n.cloneNode(true));
        }
        return clone;
    }
    
    @Override
    public Node getXblParentNode(final Node n) {
        final Node contentElement = this.getXblContentElement(n);
        Node parent = (contentElement == null) ? n.getParentNode() : contentElement.getParentNode();
        if (parent instanceof XBLOMContentElement) {
            parent = parent.getParentNode();
        }
        if (parent instanceof XBLOMShadowTreeElement) {
            parent = this.getXblBoundElement(parent);
        }
        return parent;
    }
    
    @Override
    public NodeList getXblChildNodes(final Node n) {
        final XBLRecord rec = this.getRecord(n);
        if (rec.childNodes == null) {
            rec.childNodes = new XblChildNodes(rec);
        }
        return rec.childNodes;
    }
    
    @Override
    public NodeList getXblScopedChildNodes(final Node n) {
        final XBLRecord rec = this.getRecord(n);
        if (rec.scopedChildNodes == null) {
            rec.scopedChildNodes = new XblScopedChildNodes(rec);
        }
        return rec.scopedChildNodes;
    }
    
    @Override
    public Node getXblFirstChild(final Node n) {
        final NodeList nl = this.getXblChildNodes(n);
        return nl.item(0);
    }
    
    @Override
    public Node getXblLastChild(final Node n) {
        final NodeList nl = this.getXblChildNodes(n);
        return nl.item(nl.getLength() - 1);
    }
    
    @Override
    public Node getXblPreviousSibling(final Node n) {
        final Node p = this.getXblParentNode(n);
        if (p == null || this.getRecord(p).childNodes == null) {
            return n.getPreviousSibling();
        }
        final XBLRecord rec = this.getRecord(n);
        if (!rec.linksValid) {
            this.updateLinks(n);
        }
        return rec.previousSibling;
    }
    
    @Override
    public Node getXblNextSibling(final Node n) {
        final Node p = this.getXblParentNode(n);
        if (p == null || this.getRecord(p).childNodes == null) {
            return n.getNextSibling();
        }
        final XBLRecord rec = this.getRecord(n);
        if (!rec.linksValid) {
            this.updateLinks(n);
        }
        return rec.nextSibling;
    }
    
    @Override
    public Element getXblFirstElementChild(Node n) {
        for (n = this.getXblFirstChild(n); n != null && n.getNodeType() != 1; n = this.getXblNextSibling(n)) {}
        return (Element)n;
    }
    
    @Override
    public Element getXblLastElementChild(Node n) {
        for (n = this.getXblLastChild(n); n != null && n.getNodeType() != 1; n = this.getXblPreviousSibling(n)) {}
        return (Element)n;
    }
    
    @Override
    public Element getXblPreviousElementSibling(Node n) {
        do {
            n = this.getXblPreviousSibling(n);
        } while (n != null && n.getNodeType() != 1);
        return (Element)n;
    }
    
    @Override
    public Element getXblNextElementSibling(Node n) {
        do {
            n = this.getXblNextSibling(n);
        } while (n != null && n.getNodeType() != 1);
        return (Element)n;
    }
    
    @Override
    public Element getXblBoundElement(Node n) {
        while (n != null && !(n instanceof XBLShadowTreeElement)) {
            final XBLOMContentElement content = this.getXblContentElement(n);
            if (content != null) {
                n = content;
            }
            n = n.getParentNode();
        }
        if (n == null) {
            return null;
        }
        return this.getRecord(n).boundElement;
    }
    
    @Override
    public Element getXblShadowTree(final Node n) {
        if (n instanceof BindableElement) {
            final BindableElement elt = (BindableElement)n;
            return elt.getShadowTree();
        }
        return null;
    }
    
    @Override
    public NodeList getXblDefinitions(final Node n) {
        final String namespaceURI = n.getNamespaceURI();
        final String localName = n.getLocalName();
        return new NodeList() {
            @Override
            public Node item(final int i) {
                final TreeSet defs = (TreeSet)DefaultXBLManager.this.definitionLists.get(namespaceURI, localName);
                if (defs != null && defs.size() != 0 && i == 0) {
                    final DefinitionRecord defRec = defs.first();
                    return defRec.definition;
                }
                return null;
            }
            
            @Override
            public int getLength() {
                final Set defs = (TreeSet)DefaultXBLManager.this.definitionLists.get(namespaceURI, localName);
                return (defs != null && defs.size() != 0) ? 1 : 0;
            }
        };
    }
    
    protected XBLRecord getRecord(final Node n) {
        final XBLManagerData xmd = (XBLManagerData)n;
        XBLRecord rec = (XBLRecord)xmd.getManagerData();
        if (rec == null) {
            rec = new XBLRecord();
            rec.node = n;
            xmd.setManagerData(rec);
        }
        return rec;
    }
    
    protected void updateLinks(final Node n) {
        final XBLRecord rec = this.getRecord(n);
        rec.previousSibling = null;
        rec.nextSibling = null;
        rec.linksValid = true;
        final Node p = this.getXblParentNode(n);
        if (p != null) {
            final NodeList xcn = this.getXblChildNodes(p);
            if (xcn instanceof XblChildNodes) {
                ((XblChildNodes)xcn).update();
            }
        }
    }
    
    public XBLOMContentElement getXblContentElement(final Node n) {
        return this.getRecord(n).contentElement;
    }
    
    public static int computeBubbleLimit(Node from, Node to) {
        final ArrayList fromList = new ArrayList(10);
        final ArrayList toList = new ArrayList(10);
        while (from != null) {
            fromList.add(from);
            from = ((NodeXBL)from).getXblParentNode();
        }
        while (to != null) {
            toList.add(to);
            to = ((NodeXBL)to).getXblParentNode();
        }
        for (int fromSize = fromList.size(), toSize = toList.size(), i = 0; i < fromSize && i < toSize; ++i) {
            final Node n1 = fromList.get(fromSize - i - 1);
            final Node n2 = toList.get(toSize - i - 1);
            if (n1 != n2) {
                for (Node prevBoundElement = ((NodeXBL)n1).getXblBoundElement(); i > 0 && prevBoundElement != fromList.get(fromSize - i - 1); --i) {}
                return fromSize - i - 1;
            }
        }
        return 1;
    }
    
    public ContentManager getContentManager(final Node n) {
        final Node b = this.getXblBoundElement(n);
        if (b != null) {
            final Element s = this.getXblShadowTree(b);
            if (s != null) {
                final Document doc = b.getOwnerDocument();
                ContentManager cm;
                if (doc != this.document) {
                    final DefaultXBLManager xm = (DefaultXBLManager)((AbstractDocument)doc).getXBLManager();
                    cm = xm.contentManagers.get(s);
                }
                else {
                    cm = this.contentManagers.get(s);
                }
                return cm;
            }
        }
        return null;
    }
    
    void setContentManager(final Element shadow, final ContentManager cm) {
        if (cm == null) {
            this.contentManagers.remove(shadow);
        }
        else {
            this.contentManagers.put(shadow, cm);
        }
    }
    
    public void invalidateChildNodes(final Node n) {
        final XBLRecord rec = this.getRecord(n);
        if (rec.childNodes != null) {
            rec.childNodes.invalidate();
        }
        if (rec.scopedChildNodes != null) {
            rec.scopedChildNodes.invalidate();
        }
    }
    
    public void addContentSelectionChangedListener(final ContentSelectionChangedListener l) {
        this.contentSelectionChangedListenerList.add(ContentSelectionChangedListener.class, l);
    }
    
    public void removeContentSelectionChangedListener(final ContentSelectionChangedListener l) {
        this.contentSelectionChangedListenerList.remove(ContentSelectionChangedListener.class, l);
    }
    
    protected Object[] getContentSelectionChangedListeners() {
        return this.contentSelectionChangedListenerList.getListenerList();
    }
    
    void shadowTreeSelectedContentChanged(final Set deselected, final Set selected) {
        for (final Node n : deselected) {
            if (n.getNodeType() == 1) {
                this.unbind((Element)n);
            }
        }
        for (final Node n : selected) {
            if (n.getNodeType() == 1) {
                this.bind((Element)n);
            }
        }
    }
    
    public void addBindingListener(final BindingListener l) {
        this.bindingListenerList.add(BindingListener.class, l);
    }
    
    public void removeBindingListener(final BindingListener l) {
        this.bindingListenerList.remove(BindingListener.class, l);
    }
    
    protected void dispatchBindingChangedEvent(final Element bindableElement, final Element shadowTree) {
        final Object[] ls = this.bindingListenerList.getListenerList();
        for (int i = ls.length - 2; i >= 0; i -= 2) {
            final BindingListener l = (BindingListener)ls[i + 1];
            l.bindingChanged(bindableElement, shadowTree);
        }
    }
    
    protected boolean isActiveDefinition(final XBLOMDefinitionElement def, final Element imp) {
        final DefinitionRecord defRec = (DefinitionRecord)this.definitions.get(def, imp);
        return defRec != null && defRec == this.getActiveDefinition(defRec.namespaceURI, defRec.localName);
    }
    
    protected static class DefinitionRecord implements Comparable
    {
        public String namespaceURI;
        public String localName;
        public XBLOMDefinitionElement definition;
        public XBLOMTemplateElement template;
        public Element importElement;
        
        public DefinitionRecord(final String ns, final String ln, final XBLOMDefinitionElement def, final XBLOMTemplateElement t, final Element imp) {
            this.namespaceURI = ns;
            this.localName = ln;
            this.definition = def;
            this.template = t;
            this.importElement = imp;
        }
        
        @Override
        public boolean equals(final Object other) {
            return this.compareTo(other) == 0;
        }
        
        @Override
        public int compareTo(final Object other) {
            final DefinitionRecord rec = (DefinitionRecord)other;
            AbstractNode n1;
            AbstractNode n2;
            if (this.importElement == null) {
                n1 = this.definition;
                if (rec.importElement == null) {
                    n2 = rec.definition;
                }
                else {
                    n2 = (AbstractNode)rec.importElement;
                }
            }
            else if (rec.importElement == null) {
                n1 = (AbstractNode)this.importElement;
                n2 = rec.definition;
            }
            else if (this.definition.getOwnerDocument() == rec.definition.getOwnerDocument()) {
                n1 = this.definition;
                n2 = rec.definition;
            }
            else {
                n1 = (AbstractNode)this.importElement;
                n2 = (AbstractNode)rec.importElement;
            }
            final short comp = n1.compareDocumentPosition(n2);
            if ((comp & 0x2) != 0x0) {
                return -1;
            }
            if ((comp & 0x4) != 0x0) {
                return 1;
            }
            return 0;
        }
    }
    
    protected class ImportRecord
    {
        public Element importElement;
        public Node node;
        public DefNodeInsertedListener defNodeInsertedListener;
        public DefNodeRemovedListener defNodeRemovedListener;
        public DefAttrListener defAttrListener;
        public ImportInsertedListener importInsertedListener;
        public ImportRemovedListener importRemovedListener;
        public ImportSubtreeListener importSubtreeListener;
        public TemplateMutationListener templateMutationListener;
        
        public ImportRecord(final Element imp, final Node n) {
            this.importElement = imp;
            this.node = n;
            this.defNodeInsertedListener = new DefNodeInsertedListener(imp);
            this.defNodeRemovedListener = new DefNodeRemovedListener(imp);
            this.defAttrListener = new DefAttrListener(imp);
            this.importInsertedListener = new ImportInsertedListener(imp);
            this.importRemovedListener = new ImportRemovedListener();
            this.importSubtreeListener = new ImportSubtreeListener(imp, this.importRemovedListener);
            this.templateMutationListener = new TemplateMutationListener(imp);
        }
    }
    
    protected class ImportInsertedListener implements EventListener
    {
        protected Element importElement;
        
        public ImportInsertedListener(final Element importElement) {
            this.importElement = importElement;
        }
        
        @Override
        public void handleEvent(final Event evt) {
            final EventTarget target = evt.getTarget();
            if (target instanceof XBLOMDefinitionElement) {
                final XBLOMDefinitionElement def = (XBLOMDefinitionElement)target;
                DefaultXBLManager.this.addDefinition(def.getElementNamespaceURI(), def.getElementLocalName(), def, this.importElement);
            }
        }
    }
    
    protected static class ImportRemovedListener implements EventListener
    {
        protected LinkedList toBeRemoved;
        
        protected ImportRemovedListener() {
            this.toBeRemoved = new LinkedList();
        }
        
        @Override
        public void handleEvent(final Event evt) {
            this.toBeRemoved.add(evt.getTarget());
        }
    }
    
    protected class ImportSubtreeListener implements EventListener
    {
        protected Element importElement;
        protected ImportRemovedListener importRemovedListener;
        
        public ImportSubtreeListener(final Element imp, final ImportRemovedListener irl) {
            this.importElement = imp;
            this.importRemovedListener = irl;
        }
        
        @Override
        public void handleEvent(final Event evt) {
            final Object[] defs = this.importRemovedListener.toBeRemoved.toArray();
            this.importRemovedListener.toBeRemoved.clear();
            for (final Object def1 : defs) {
                final XBLOMDefinitionElement def2 = (XBLOMDefinitionElement)def1;
                final DefinitionRecord defRec = (DefinitionRecord)DefaultXBLManager.this.definitions.get(def2, this.importElement);
                DefaultXBLManager.this.removeDefinition(defRec);
            }
        }
    }
    
    protected class DocInsertedListener implements EventListener
    {
        @Override
        public void handleEvent(Event evt) {
            EventTarget target = evt.getTarget();
            if (target instanceof XBLOMDefinitionElement) {
                if (DefaultXBLManager.this.getXblBoundElement((Node)target) == null) {
                    final XBLOMDefinitionElement def = (XBLOMDefinitionElement)target;
                    if (def.getAttributeNS(null, "ref").length() == 0) {
                        DefaultXBLManager.this.addDefinition(def.getElementNamespaceURI(), def.getElementLocalName(), def, null);
                    }
                    else {
                        DefaultXBLManager.this.addDefinitionRef(def);
                    }
                }
            }
            else if (target instanceof XBLOMImportElement) {
                if (DefaultXBLManager.this.getXblBoundElement((Node)target) == null) {
                    DefaultXBLManager.this.addImport((Element)target);
                }
            }
            else {
                evt = EventSupport.getUltimateOriginalEvent(evt);
                target = evt.getTarget();
                final Node parent = DefaultXBLManager.this.getXblParentNode((Node)target);
                if (parent != null) {
                    DefaultXBLManager.this.invalidateChildNodes(parent);
                }
                if (target instanceof BindableElement) {
                    for (Node n = ((Node)target).getParentNode(); n != null; n = n.getParentNode()) {
                        if (n instanceof BindableElement && DefaultXBLManager.this.getRecord(n).definitionElement != null) {
                            return;
                        }
                    }
                    DefaultXBLManager.this.bind((Element)target);
                }
            }
        }
    }
    
    protected class DocRemovedListener implements EventListener
    {
        protected LinkedList defsToBeRemoved;
        protected LinkedList importsToBeRemoved;
        protected LinkedList nodesToBeInvalidated;
        
        protected DocRemovedListener() {
            this.defsToBeRemoved = new LinkedList();
            this.importsToBeRemoved = new LinkedList();
            this.nodesToBeInvalidated = new LinkedList();
        }
        
        @Override
        public void handleEvent(final Event evt) {
            final EventTarget target = evt.getTarget();
            if (target instanceof XBLOMDefinitionElement) {
                if (DefaultXBLManager.this.getXblBoundElement((Node)target) == null) {
                    this.defsToBeRemoved.add(target);
                }
            }
            else if (target instanceof XBLOMImportElement && DefaultXBLManager.this.getXblBoundElement((Node)target) == null) {
                this.importsToBeRemoved.add(target);
            }
            final Node parent = DefaultXBLManager.this.getXblParentNode((Node)target);
            if (parent != null) {
                this.nodesToBeInvalidated.add(parent);
            }
        }
    }
    
    protected class DocSubtreeListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            final Object[] defs = DefaultXBLManager.this.docRemovedListener.defsToBeRemoved.toArray();
            DefaultXBLManager.this.docRemovedListener.defsToBeRemoved.clear();
            for (final Object def1 : defs) {
                final XBLOMDefinitionElement def2 = (XBLOMDefinitionElement)def1;
                if (def2.getAttributeNS(null, "ref").length() == 0) {
                    final DefinitionRecord defRec = (DefinitionRecord)DefaultXBLManager.this.definitions.get(def2, null);
                    DefaultXBLManager.this.removeDefinition(defRec);
                }
                else {
                    DefaultXBLManager.this.removeDefinitionRef(def2);
                }
            }
            final Object[] imps = DefaultXBLManager.this.docRemovedListener.importsToBeRemoved.toArray();
            DefaultXBLManager.this.docRemovedListener.importsToBeRemoved.clear();
            for (final Object imp : imps) {
                DefaultXBLManager.this.removeImport((Element)imp);
            }
            final Object[] nodes = DefaultXBLManager.this.docRemovedListener.nodesToBeInvalidated.toArray();
            DefaultXBLManager.this.docRemovedListener.nodesToBeInvalidated.clear();
            for (final Object node : nodes) {
                DefaultXBLManager.this.invalidateChildNodes((Node)node);
            }
        }
    }
    
    protected class TemplateMutationListener implements EventListener
    {
        protected Element importElement;
        
        public TemplateMutationListener(final Element imp) {
            this.importElement = imp;
        }
        
        @Override
        public void handleEvent(final Event evt) {
            Node n;
            for (n = (Node)evt.getTarget(); n != null && !(n instanceof XBLOMDefinitionElement); n = n.getParentNode()) {}
            final DefinitionRecord defRec = (DefinitionRecord)DefaultXBLManager.this.definitions.get(n, this.importElement);
            if (defRec == null) {
                return;
            }
            DefaultXBLManager.this.rebind(defRec.namespaceURI, defRec.localName, DefaultXBLManager.this.document.getDocumentElement());
        }
    }
    
    protected class DefAttrListener implements EventListener
    {
        protected Element importElement;
        
        public DefAttrListener(final Element imp) {
            this.importElement = imp;
        }
        
        @Override
        public void handleEvent(final Event evt) {
            final EventTarget target = evt.getTarget();
            if (!(target instanceof XBLOMDefinitionElement)) {
                return;
            }
            final XBLOMDefinitionElement def = (XBLOMDefinitionElement)target;
            if (!DefaultXBLManager.this.isActiveDefinition(def, this.importElement)) {
                return;
            }
            final MutationEvent mevt = (MutationEvent)evt;
            final String attrName = mevt.getAttrName();
            if (attrName.equals("element")) {
                final DefinitionRecord defRec = (DefinitionRecord)DefaultXBLManager.this.definitions.get(def, this.importElement);
                DefaultXBLManager.this.removeDefinition(defRec);
                DefaultXBLManager.this.addDefinition(def.getElementNamespaceURI(), def.getElementLocalName(), def, this.importElement);
            }
            else if (attrName.equals("ref") && mevt.getNewValue().length() != 0) {
                final DefinitionRecord defRec = (DefinitionRecord)DefaultXBLManager.this.definitions.get(def, this.importElement);
                DefaultXBLManager.this.removeDefinition(defRec);
                DefaultXBLManager.this.addDefinitionRef(def);
            }
        }
    }
    
    protected class DefNodeInsertedListener implements EventListener
    {
        protected Element importElement;
        
        public DefNodeInsertedListener(final Element imp) {
            this.importElement = imp;
        }
        
        @Override
        public void handleEvent(final Event evt) {
            final MutationEvent mevt = (MutationEvent)evt;
            final Node parent = mevt.getRelatedNode();
            if (!(parent instanceof XBLOMDefinitionElement)) {
                return;
            }
            final EventTarget target = evt.getTarget();
            if (!(target instanceof XBLOMTemplateElement)) {
                return;
            }
            final XBLOMTemplateElement template = (XBLOMTemplateElement)target;
            final DefinitionRecord defRec = (DefinitionRecord)DefaultXBLManager.this.definitions.get(parent, this.importElement);
            if (defRec == null) {
                return;
            }
            final ImportRecord ir = DefaultXBLManager.this.imports.get(this.importElement);
            if (defRec.template != null) {
                for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
                    if (n == template) {
                        DefaultXBLManager.this.removeTemplateElementListeners(defRec.template, ir);
                        defRec.template = template;
                        break;
                    }
                    if (n == defRec.template) {
                        return;
                    }
                }
            }
            else {
                defRec.template = template;
            }
            DefaultXBLManager.this.addTemplateElementListeners(template, ir);
            DefaultXBLManager.this.rebind(defRec.namespaceURI, defRec.localName, DefaultXBLManager.this.document.getDocumentElement());
        }
    }
    
    protected class DefNodeRemovedListener implements EventListener
    {
        protected Element importElement;
        
        public DefNodeRemovedListener(final Element imp) {
            this.importElement = imp;
        }
        
        @Override
        public void handleEvent(final Event evt) {
            final MutationEvent mevt = (MutationEvent)evt;
            final Node parent = mevt.getRelatedNode();
            if (!(parent instanceof XBLOMDefinitionElement)) {
                return;
            }
            final EventTarget target = evt.getTarget();
            if (!(target instanceof XBLOMTemplateElement)) {
                return;
            }
            final XBLOMTemplateElement template = (XBLOMTemplateElement)target;
            final DefinitionRecord defRec = (DefinitionRecord)DefaultXBLManager.this.definitions.get(parent, this.importElement);
            if (defRec == null || defRec.template != template) {
                return;
            }
            final ImportRecord ir = DefaultXBLManager.this.imports.get(this.importElement);
            DefaultXBLManager.this.removeTemplateElementListeners(template, ir);
            defRec.template = null;
            for (Node n = template.getNextSibling(); n != null; n = n.getNextSibling()) {
                if (n instanceof XBLOMTemplateElement) {
                    defRec.template = (XBLOMTemplateElement)n;
                    break;
                }
            }
            DefaultXBLManager.this.addTemplateElementListeners(defRec.template, ir);
            DefaultXBLManager.this.rebind(defRec.namespaceURI, defRec.localName, DefaultXBLManager.this.document.getDocumentElement());
        }
    }
    
    protected class ImportAttrListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            final EventTarget target = evt.getTarget();
            if (target != evt.getCurrentTarget()) {
                return;
            }
            final MutationEvent mevt = (MutationEvent)evt;
            if (mevt.getAttrName().equals("bindings")) {
                final Element imp = (Element)target;
                DefaultXBLManager.this.removeImport(imp);
                DefaultXBLManager.this.addImport(imp);
            }
        }
    }
    
    protected class RefAttrListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            final EventTarget target = evt.getTarget();
            if (target != evt.getCurrentTarget()) {
                return;
            }
            final MutationEvent mevt = (MutationEvent)evt;
            if (mevt.getAttrName().equals("ref")) {
                final Element defRef = (Element)target;
                DefaultXBLManager.this.removeDefinitionRef(defRef);
                if (mevt.getNewValue().length() == 0) {
                    final XBLOMDefinitionElement def = (XBLOMDefinitionElement)defRef;
                    final String ns = def.getElementNamespaceURI();
                    final String ln = def.getElementLocalName();
                    DefaultXBLManager.this.addDefinition(ns, ln, (XBLOMDefinitionElement)defRef, null);
                }
                else {
                    DefaultXBLManager.this.addDefinitionRef(defRef);
                }
            }
        }
    }
    
    protected class XBLRecord
    {
        public Node node;
        public XblChildNodes childNodes;
        public XblScopedChildNodes scopedChildNodes;
        public XBLOMContentElement contentElement;
        public XBLOMDefinitionElement definitionElement;
        public BindableElement boundElement;
        public boolean linksValid;
        public Node nextSibling;
        public Node previousSibling;
    }
    
    protected class XblChildNodes implements NodeList
    {
        protected XBLRecord record;
        protected List nodes;
        protected int size;
        
        public XblChildNodes(final XBLRecord rec) {
            this.record = rec;
            this.nodes = new ArrayList();
            this.size = -1;
        }
        
        protected void update() {
            this.size = 0;
            final Node shadowTree = DefaultXBLManager.this.getXblShadowTree(this.record.node);
            Node last = null;
            for (Node m = (shadowTree == null) ? this.record.node.getFirstChild() : shadowTree.getFirstChild(); m != null; m = m.getNextSibling()) {
                last = this.collectXblChildNodes(m, last);
            }
            if (last != null) {
                final XBLRecord rec = DefaultXBLManager.this.getRecord(last);
                rec.nextSibling = null;
                rec.linksValid = true;
            }
        }
        
        protected Node collectXblChildNodes(final Node n, Node prev) {
            boolean isChild = false;
            if (n.getNodeType() == 1) {
                if (!"http://www.w3.org/2004/xbl".equals(n.getNamespaceURI())) {
                    isChild = true;
                }
                else if (n instanceof XBLOMContentElement) {
                    final ContentManager cm = DefaultXBLManager.this.getContentManager(n);
                    if (cm != null) {
                        final NodeList selected = cm.getSelectedContent((XBLOMContentElement)n);
                        for (int i = 0; i < selected.getLength(); ++i) {
                            prev = this.collectXblChildNodes(selected.item(i), prev);
                        }
                    }
                }
            }
            else {
                isChild = true;
            }
            if (isChild) {
                this.nodes.add(n);
                ++this.size;
                if (prev != null) {
                    final XBLRecord rec = DefaultXBLManager.this.getRecord(prev);
                    rec.nextSibling = n;
                    rec.linksValid = true;
                }
                final XBLRecord rec = DefaultXBLManager.this.getRecord(n);
                rec.previousSibling = prev;
                rec.linksValid = true;
                prev = n;
            }
            return prev;
        }
        
        public void invalidate() {
            for (int i = 0; i < this.size; ++i) {
                final XBLRecord rec = DefaultXBLManager.this.getRecord(this.nodes.get(i));
                rec.previousSibling = null;
                rec.nextSibling = null;
                rec.linksValid = false;
            }
            this.nodes.clear();
            this.size = -1;
        }
        
        public Node getFirstNode() {
            if (this.size == -1) {
                this.update();
            }
            return (this.size == 0) ? null : this.nodes.get(0);
        }
        
        public Node getLastNode() {
            if (this.size == -1) {
                this.update();
            }
            return (this.size == 0) ? null : this.nodes.get(this.nodes.size() - 1);
        }
        
        @Override
        public Node item(final int index) {
            if (this.size == -1) {
                this.update();
            }
            if (index < 0 || index >= this.size) {
                return null;
            }
            return this.nodes.get(index);
        }
        
        @Override
        public int getLength() {
            if (this.size == -1) {
                this.update();
            }
            return this.size;
        }
    }
    
    protected class XblScopedChildNodes extends XblChildNodes
    {
        public XblScopedChildNodes(final XBLRecord rec) {
            super(rec);
        }
        
        @Override
        protected void update() {
            this.size = 0;
            final Node shadowTree = DefaultXBLManager.this.getXblShadowTree(this.record.node);
            for (Node n = (shadowTree == null) ? this.record.node.getFirstChild() : shadowTree.getFirstChild(); n != null; n = n.getNextSibling()) {
                this.collectXblScopedChildNodes(n);
            }
        }
        
        protected void collectXblScopedChildNodes(final Node n) {
            boolean isChild = false;
            if (n.getNodeType() == 1) {
                if (!n.getNamespaceURI().equals("http://www.w3.org/2004/xbl")) {
                    isChild = true;
                }
                else if (n instanceof XBLOMContentElement) {
                    final ContentManager cm = DefaultXBLManager.this.getContentManager(n);
                    if (cm != null) {
                        final NodeList selected = cm.getSelectedContent((XBLOMContentElement)n);
                        for (int i = 0; i < selected.getLength(); ++i) {
                            this.collectXblScopedChildNodes(selected.item(i));
                        }
                    }
                }
            }
            else {
                isChild = true;
            }
            if (isChild) {
                this.nodes.add(n);
                ++this.size;
            }
        }
    }
}
