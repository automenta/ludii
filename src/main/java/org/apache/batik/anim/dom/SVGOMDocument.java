// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.events.MutationEvent;
import org.apache.batik.dom.events.EventSupport;
import org.w3c.dom.events.Event;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.w3c.dom.css.CSSStyleDeclaration;
import java.util.Iterator;
import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.events.EventListener;
import org.apache.batik.css.engine.CSSNavigableDocumentListener;
import org.apache.batik.dom.GenericAttrNS;
import org.apache.batik.dom.GenericEntityReference;
import org.w3c.dom.EntityReference;
import org.apache.batik.dom.GenericAttr;
import org.w3c.dom.Attr;
import org.apache.batik.dom.GenericProcessingInstruction;
import org.apache.batik.dom.StyleSheetFactory;
import org.w3c.dom.ProcessingInstruction;
import org.apache.batik.dom.GenericCDATASection;
import org.w3c.dom.CDATASection;
import org.apache.batik.dom.GenericComment;
import org.w3c.dom.Comment;
import org.apache.batik.dom.GenericText;
import org.w3c.dom.Text;
import org.apache.batik.dom.GenericDocumentFragment;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DOMException;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.GenericElement;
import org.w3c.dom.Element;
import java.net.MalformedURLException;
import java.net.URL;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.Node;
import org.apache.batik.dom.util.XMLSupport;
import org.w3c.dom.svg.SVGLangSpace;
import java.util.MissingResourceException;
import java.util.Locale;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.apache.batik.dom.svg.SVGContext;
import java.util.LinkedList;
import java.util.HashMap;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.i18n.LocalizableSupport;
import org.apache.batik.dom.svg.IdContainer;
import org.apache.batik.css.engine.CSSNavigableDocument;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.svg.SVGDocument;
import org.apache.batik.dom.AbstractStylableDocument;

public class SVGOMDocument extends AbstractStylableDocument implements SVGDocument, SVGConstants, CSSNavigableDocument, IdContainer
{
    protected static final String RESOURCES = "org.apache.batik.dom.svg.resources.Messages";
    protected transient LocalizableSupport localizableSupport;
    protected String referrer;
    protected ParsedURL url;
    protected transient boolean readonly;
    protected boolean isSVG12;
    protected HashMap cssNavigableDocumentListeners;
    protected AnimatedAttributeListener mainAnimatedAttributeListener;
    protected LinkedList animatedAttributeListeners;
    protected transient SVGContext svgContext;
    
    protected SVGOMDocument() {
        this.localizableSupport = new LocalizableSupport("org.apache.batik.dom.svg.resources.Messages", this.getClass().getClassLoader());
        this.referrer = "";
        this.cssNavigableDocumentListeners = new HashMap();
        this.mainAnimatedAttributeListener = new AnimAttrListener();
        this.animatedAttributeListeners = new LinkedList();
    }
    
    public SVGOMDocument(final DocumentType dt, final DOMImplementation impl) {
        super(dt, impl);
        this.localizableSupport = new LocalizableSupport("org.apache.batik.dom.svg.resources.Messages", this.getClass().getClassLoader());
        this.referrer = "";
        this.cssNavigableDocumentListeners = new HashMap();
        this.mainAnimatedAttributeListener = new AnimAttrListener();
        this.animatedAttributeListeners = new LinkedList();
    }
    
    @Override
    public void setLocale(final Locale l) {
        super.setLocale(l);
        this.localizableSupport.setLocale(l);
    }
    
    @Override
    public String formatMessage(final String key, final Object[] args) throws MissingResourceException {
        try {
            return super.formatMessage(key, args);
        }
        catch (MissingResourceException e) {
            return this.localizableSupport.formatMessage(key, args);
        }
    }
    
    @Override
    public String getTitle() {
        final StringBuffer sb = new StringBuffer();
        boolean preserve = false;
        for (Node n = this.getDocumentElement().getFirstChild(); n != null; n = n.getNextSibling()) {
            final String ns = n.getNamespaceURI();
            if (ns != null && ns.equals("http://www.w3.org/2000/svg") && n.getLocalName().equals("title")) {
                preserve = ((SVGLangSpace)n).getXMLspace().equals("preserve");
                for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
                    if (n.getNodeType() == 3) {
                        sb.append(n.getNodeValue());
                    }
                }
                break;
            }
        }
        final String s = sb.toString();
        return preserve ? XMLSupport.preserveXMLSpace(s) : XMLSupport.defaultXMLSpace(s);
    }
    
    @Override
    public String getReferrer() {
        return this.referrer;
    }
    
    public void setReferrer(final String s) {
        this.referrer = s;
    }
    
    @Override
    public String getDomain() {
        return (this.url == null) ? null : this.url.getHost();
    }
    
    @Override
    public SVGSVGElement getRootElement() {
        return (SVGSVGElement)this.getDocumentElement();
    }
    
    @Override
    public String getURL() {
        return this.documentURI;
    }
    
    public URL getURLObject() {
        try {
            return new URL(this.documentURI);
        }
        catch (MalformedURLException e) {
            return null;
        }
    }
    
    public ParsedURL getParsedURL() {
        return this.url;
    }
    
    public void setURLObject(final URL url) {
        this.setParsedURL(new ParsedURL(url));
    }
    
    public void setParsedURL(final ParsedURL url) {
        this.url = url;
        this.documentURI = ((url == null) ? null : url.toString());
    }
    
    @Override
    public void setDocumentURI(final String uri) {
        this.documentURI = uri;
        this.url = ((uri == null) ? null : new ParsedURL(uri));
    }
    
    @Override
    public Element createElement(final String tagName) throws DOMException {
        return new GenericElement(tagName.intern(), this);
    }
    
    @Override
    public DocumentFragment createDocumentFragment() {
        return new GenericDocumentFragment(this);
    }
    
    @Override
    public Text createTextNode(final String data) {
        return new GenericText(data, this);
    }
    
    @Override
    public Comment createComment(final String data) {
        return new GenericComment(data, this);
    }
    
    @Override
    public CDATASection createCDATASection(final String data) throws DOMException {
        return new GenericCDATASection(data, this);
    }
    
    @Override
    public ProcessingInstruction createProcessingInstruction(final String target, final String data) throws DOMException {
        if ("xml-stylesheet".equals(target)) {
            return new SVGStyleSheetProcessingInstruction(data, this, (StyleSheetFactory)this.getImplementation());
        }
        return new GenericProcessingInstruction(target, data, this);
    }
    
    @Override
    public Attr createAttribute(final String name) throws DOMException {
        return new GenericAttr(name.intern(), this);
    }
    
    @Override
    public EntityReference createEntityReference(final String name) throws DOMException {
        return new GenericEntityReference(name, this);
    }
    
    @Override
    public Attr createAttributeNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        if (namespaceURI == null) {
            return new GenericAttr(qualifiedName.intern(), this);
        }
        return new GenericAttrNS(namespaceURI.intern(), qualifiedName.intern(), this);
    }
    
    @Override
    public Element createElementNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        final SVGDOMImplementation impl = (SVGDOMImplementation)this.implementation;
        return impl.createElementNS(this, namespaceURI, qualifiedName);
    }
    
    public boolean isSVG12() {
        return this.isSVG12;
    }
    
    public void setIsSVG12(final boolean b) {
        this.isSVG12 = b;
    }
    
    @Override
    public boolean isId(final Attr node) {
        if (node.getNamespaceURI() == null) {
            return "id".equals(node.getNodeName());
        }
        return node.getNodeName().equals("xml:id");
    }
    
    public void setSVGContext(final SVGContext ctx) {
        this.svgContext = ctx;
    }
    
    public SVGContext getSVGContext() {
        return this.svgContext;
    }
    
    @Override
    public void addCSSNavigableDocumentListener(final CSSNavigableDocumentListener l) {
        if (this.cssNavigableDocumentListeners.containsKey(l)) {
            return;
        }
        final DOMNodeInsertedListenerWrapper nodeInserted = new DOMNodeInsertedListenerWrapper(l);
        final DOMNodeRemovedListenerWrapper nodeRemoved = new DOMNodeRemovedListenerWrapper(l);
        final DOMSubtreeModifiedListenerWrapper subtreeModified = new DOMSubtreeModifiedListenerWrapper(l);
        final DOMCharacterDataModifiedListenerWrapper cdataModified = new DOMCharacterDataModifiedListenerWrapper(l);
        final DOMAttrModifiedListenerWrapper attrModified = new DOMAttrModifiedListenerWrapper(l);
        this.cssNavigableDocumentListeners.put(l, new EventListener[] { nodeInserted, nodeRemoved, subtreeModified, cdataModified, attrModified });
        this.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", nodeInserted, false, null);
        this.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", nodeRemoved, false, null);
        this.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", subtreeModified, false, null);
        this.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", cdataModified, false, null);
        this.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", attrModified, false, null);
    }
    
    @Override
    public void removeCSSNavigableDocumentListener(final CSSNavigableDocumentListener l) {
        final EventListener[] listeners = this.cssNavigableDocumentListeners.get(l);
        if (listeners == null) {
            return;
        }
        this.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", listeners[0], false);
        this.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", listeners[1], false);
        this.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", listeners[2], false);
        this.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", listeners[3], false);
        this.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", listeners[4], false);
        this.cssNavigableDocumentListeners.remove(l);
    }
    
    protected AnimatedAttributeListener getAnimatedAttributeListener() {
        return this.mainAnimatedAttributeListener;
    }
    
    protected void overrideStyleTextChanged(final CSSStylableElement e, final String text) {
        for (final Object o : this.cssNavigableDocumentListeners.keySet()) {
            final CSSNavigableDocumentListener l = (CSSNavigableDocumentListener)o;
            l.overrideStyleTextChanged(e, text);
        }
    }
    
    protected void overrideStylePropertyRemoved(final CSSStylableElement e, final String name) {
        for (final Object o : this.cssNavigableDocumentListeners.keySet()) {
            final CSSNavigableDocumentListener l = (CSSNavigableDocumentListener)o;
            l.overrideStylePropertyRemoved(e, name);
        }
    }
    
    protected void overrideStylePropertyChanged(final CSSStylableElement e, final String name, final String value, final String prio) {
        for (final Object o : this.cssNavigableDocumentListeners.keySet()) {
            final CSSNavigableDocumentListener l = (CSSNavigableDocumentListener)o;
            l.overrideStylePropertyChanged(e, name, value, prio);
        }
    }
    
    public void addAnimatedAttributeListener(final AnimatedAttributeListener aal) {
        if (this.animatedAttributeListeners.contains(aal)) {
            return;
        }
        this.animatedAttributeListeners.add(aal);
    }
    
    public void removeAnimatedAttributeListener(final AnimatedAttributeListener aal) {
        this.animatedAttributeListeners.remove(aal);
    }
    
    @Override
    public CSSStyleDeclaration getOverrideStyle(final Element elt, final String pseudoElt) {
        if (elt instanceof SVGStylableElement && pseudoElt == null) {
            return ((SVGStylableElement)elt).getOverrideStyle();
        }
        return null;
    }
    
    @Override
    public boolean isReadonly() {
        return this.readonly;
    }
    
    @Override
    public void setReadonly(final boolean v) {
        this.readonly = v;
    }
    
    @Override
    protected Node newNode() {
        return new SVGOMDocument();
    }
    
    @Override
    protected Node copyInto(final Node n) {
        super.copyInto(n);
        final SVGOMDocument sd = (SVGOMDocument)n;
        sd.localizableSupport = new LocalizableSupport("org.apache.batik.dom.svg.resources.Messages", this.getClass().getClassLoader());
        sd.referrer = this.referrer;
        sd.url = this.url;
        return n;
    }
    
    @Override
    protected Node deepCopyInto(final Node n) {
        super.deepCopyInto(n);
        final SVGOMDocument sd = (SVGOMDocument)n;
        sd.localizableSupport = new LocalizableSupport("org.apache.batik.dom.svg.resources.Messages", this.getClass().getClassLoader());
        sd.referrer = this.referrer;
        sd.url = this.url;
        return n;
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.localizableSupport = new LocalizableSupport("org.apache.batik.dom.svg.resources.Messages", this.getClass().getClassLoader());
    }
    
    protected static class DOMNodeInsertedListenerWrapper implements EventListener
    {
        protected CSSNavigableDocumentListener listener;
        
        public DOMNodeInsertedListenerWrapper(final CSSNavigableDocumentListener l) {
            this.listener = l;
        }
        
        @Override
        public void handleEvent(Event evt) {
            evt = EventSupport.getUltimateOriginalEvent(evt);
            this.listener.nodeInserted((Node)evt.getTarget());
        }
    }
    
    protected static class DOMNodeRemovedListenerWrapper implements EventListener
    {
        protected CSSNavigableDocumentListener listener;
        
        public DOMNodeRemovedListenerWrapper(final CSSNavigableDocumentListener l) {
            this.listener = l;
        }
        
        @Override
        public void handleEvent(Event evt) {
            evt = EventSupport.getUltimateOriginalEvent(evt);
            this.listener.nodeToBeRemoved((Node)evt.getTarget());
        }
    }
    
    protected static class DOMSubtreeModifiedListenerWrapper implements EventListener
    {
        protected CSSNavigableDocumentListener listener;
        
        public DOMSubtreeModifiedListenerWrapper(final CSSNavigableDocumentListener l) {
            this.listener = l;
        }
        
        @Override
        public void handleEvent(Event evt) {
            evt = EventSupport.getUltimateOriginalEvent(evt);
            this.listener.subtreeModified((Node)evt.getTarget());
        }
    }
    
    protected static class DOMCharacterDataModifiedListenerWrapper implements EventListener
    {
        protected CSSNavigableDocumentListener listener;
        
        public DOMCharacterDataModifiedListenerWrapper(final CSSNavigableDocumentListener l) {
            this.listener = l;
        }
        
        @Override
        public void handleEvent(Event evt) {
            evt = EventSupport.getUltimateOriginalEvent(evt);
            this.listener.characterDataModified((Node)evt.getTarget());
        }
    }
    
    protected static class DOMAttrModifiedListenerWrapper implements EventListener
    {
        protected CSSNavigableDocumentListener listener;
        
        public DOMAttrModifiedListenerWrapper(final CSSNavigableDocumentListener l) {
            this.listener = l;
        }
        
        @Override
        public void handleEvent(Event evt) {
            evt = EventSupport.getUltimateOriginalEvent(evt);
            final MutationEvent mevt = (MutationEvent)evt;
            this.listener.attrModified((Element)evt.getTarget(), (Attr)mevt.getRelatedNode(), mevt.getAttrChange(), mevt.getPrevValue(), mevt.getNewValue());
        }
    }
    
    protected class AnimAttrListener implements AnimatedAttributeListener
    {
        @Override
        public void animatedAttributeChanged(final Element e, final AnimatedLiveAttributeValue alav) {
            for (final Object animatedAttributeListener : SVGOMDocument.this.animatedAttributeListeners) {
                final AnimatedAttributeListener aal = (AnimatedAttributeListener)animatedAttributeListener;
                aal.animatedAttributeChanged(e, alav);
            }
        }
        
        @Override
        public void otherAnimationChanged(final Element e, final String type) {
            for (final Object animatedAttributeListener : SVGOMDocument.this.animatedAttributeListeners) {
                final AnimatedAttributeListener aal = (AnimatedAttributeListener)animatedAttributeListener;
                aal.otherAnimationChanged(e, type);
            }
        }
    }
}
