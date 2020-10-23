// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.dom.AbstractNode;
import java.awt.Cursor;
import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.events.Event;
import org.apache.batik.util.CleanerThread;
import org.apache.batik.util.Service;
import java.util.ListIterator;
import java.util.Collection;
import org.apache.batik.bridge.svg12.SVG12BridgeExtension;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.css.engine.SystemColorSupport;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.dom.svg.SVGContext;
import org.w3c.dom.events.EventTarget;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.gvt.GraphicsNode;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import org.w3c.dom.svg.SVGDocument;
import java.util.Iterator;
import org.apache.batik.script.ImportInfo;
import org.apache.batik.script.Interpreter;
import java.lang.ref.SoftReference;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.css.engine.CSSEngineUserAgent;
import org.apache.batik.dom.AbstractStylableDocument;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.bridge.svg12.SVG12BridgeContext;
import org.apache.batik.css.engine.CSSEngine;
import java.lang.ref.WeakReference;
import org.apache.batik.anim.dom.SVGOMDocument;
import java.util.LinkedList;
import java.util.WeakHashMap;
import java.util.HashMap;
import org.apache.batik.anim.dom.AnimatedAttributeListener;
import org.apache.batik.css.engine.CSSEngineListener;
import org.w3c.dom.events.EventListener;
import java.util.HashSet;
import org.apache.batik.dom.xbl.XBLManager;
import java.awt.geom.Dimension2D;
import org.apache.batik.script.InterpreterPool;
import java.util.Set;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.apache.batik.css.engine.CSSContext;

public class BridgeContext implements ErrorConstants, CSSContext
{
    protected Document document;
    protected boolean isSVG12;
    protected GVTBuilder gvtBuilder;
    protected Map interpreterMap;
    private Map fontFamilyMap;
    protected Map viewportMap;
    protected List viewportStack;
    protected UserAgent userAgent;
    protected Map elementNodeMap;
    protected Map nodeElementMap;
    protected Map namespaceURIMap;
    protected Bridge defaultBridge;
    protected Set reservedNamespaceSet;
    protected Map elementDataMap;
    protected InterpreterPool interpreterPool;
    protected DocumentLoader documentLoader;
    protected Dimension2D documentSize;
    protected TextPainter textPainter;
    public static final int STATIC = 0;
    public static final int INTERACTIVE = 1;
    public static final int DYNAMIC = 2;
    protected int dynamicStatus;
    protected UpdateManager updateManager;
    protected XBLManager xblManager;
    protected BridgeContext primaryContext;
    protected HashSet childContexts;
    protected SVGAnimationEngine animationEngine;
    protected int animationLimitingMode;
    protected float animationLimitingAmount;
    private static InterpreterPool sharedPool;
    protected Set eventListenerSet;
    protected EventListener domCharacterDataModifiedEventListener;
    protected EventListener domAttrModifiedEventListener;
    protected EventListener domNodeInsertedEventListener;
    protected EventListener domNodeRemovedEventListener;
    protected CSSEngineListener cssPropertiesChangedListener;
    protected AnimatedAttributeListener animatedAttributeListener;
    protected FocusManager focusManager;
    protected CursorManager cursorManager;
    protected List extensions;
    protected static List globalExtensions;
    
    protected BridgeContext() {
        this.interpreterMap = new HashMap(7);
        this.viewportMap = new WeakHashMap();
        this.viewportStack = new LinkedList();
        this.dynamicStatus = 0;
        this.childContexts = new HashSet();
        this.eventListenerSet = new HashSet();
        this.cursorManager = new CursorManager(this);
        this.extensions = null;
    }
    
    public final FontFamilyResolver getFontFamilyResolver() {
        return this.userAgent.getFontFamilyResolver();
    }
    
    public BridgeContext(final UserAgent userAgent) {
        this(userAgent, BridgeContext.sharedPool, new DocumentLoader(userAgent));
    }
    
    public BridgeContext(final UserAgent userAgent, final DocumentLoader loader) {
        this(userAgent, BridgeContext.sharedPool, loader);
    }
    
    public BridgeContext(final UserAgent userAgent, final InterpreterPool interpreterPool, final DocumentLoader documentLoader) {
        this.interpreterMap = new HashMap(7);
        this.viewportMap = new WeakHashMap();
        this.viewportStack = new LinkedList();
        this.dynamicStatus = 0;
        this.childContexts = new HashSet();
        this.eventListenerSet = new HashSet();
        this.cursorManager = new CursorManager(this);
        this.extensions = null;
        this.userAgent = userAgent;
        this.viewportMap.put(userAgent, new UserAgentViewport(userAgent));
        this.interpreterPool = interpreterPool;
        this.documentLoader = documentLoader;
    }
    
    @Override
    protected void finalize() {
        if (this.primaryContext != null) {
            this.dispose();
        }
    }
    
    public BridgeContext createSubBridgeContext(final SVGOMDocument newDoc) {
        final CSSEngine eng = newDoc.getCSSEngine();
        if (eng != null) {
            final BridgeContext subCtx = (BridgeContext)newDoc.getCSSEngine().getCSSContext();
            return subCtx;
        }
        final BridgeContext subCtx = this.createBridgeContext(newDoc);
        subCtx.primaryContext = ((this.primaryContext != null) ? this.primaryContext : this);
        subCtx.primaryContext.childContexts.add(new WeakReference<BridgeContext>(subCtx));
        subCtx.dynamicStatus = this.dynamicStatus;
        subCtx.setGVTBuilder(this.getGVTBuilder());
        subCtx.setTextPainter(this.getTextPainter());
        subCtx.setDocument(newDoc);
        subCtx.initializeDocument(newDoc);
        if (this.isInteractive()) {
            subCtx.addUIEventListeners(newDoc);
        }
        return subCtx;
    }
    
    public BridgeContext createBridgeContext(final SVGOMDocument doc) {
        if (doc.isSVG12()) {
            return new SVG12BridgeContext(this.getUserAgent(), this.getDocumentLoader());
        }
        return new BridgeContext(this.getUserAgent(), this.getDocumentLoader());
    }
    
    protected void initializeDocument(final Document document) {
        final SVGOMDocument doc = (SVGOMDocument)document;
        CSSEngine eng = doc.getCSSEngine();
        if (eng == null) {
            final SVGDOMImplementation impl = (SVGDOMImplementation)doc.getImplementation();
            eng = impl.createCSSEngine(doc, this);
            eng.setCSSEngineUserAgent(new CSSEngineUserAgentWrapper(this.userAgent));
            doc.setCSSEngine(eng);
            eng.setMedia(this.userAgent.getMedia());
            final String uri = this.userAgent.getUserStyleSheetURI();
            if (uri != null) {
                try {
                    final ParsedURL url = new ParsedURL(uri);
                    eng.setUserAgentStyleSheet(eng.parseStyleSheet(url, "all"));
                }
                catch (Exception e) {
                    this.userAgent.displayError(e);
                }
            }
            eng.setAlternateStyleSheet(this.userAgent.getAlternateStyleSheet());
        }
    }
    
    @Override
    public CSSEngine getCSSEngineForElement(final Element e) {
        final SVGOMDocument doc = (SVGOMDocument)e.getOwnerDocument();
        return doc.getCSSEngine();
    }
    
    public void setTextPainter(final TextPainter textPainter) {
        this.textPainter = textPainter;
    }
    
    public TextPainter getTextPainter() {
        return this.textPainter;
    }
    
    public Document getDocument() {
        return this.document;
    }
    
    protected void setDocument(final Document document) {
        if (this.document != document) {
            this.fontFamilyMap = null;
        }
        this.document = document;
        this.isSVG12 = ((SVGOMDocument)document).isSVG12();
        this.registerSVGBridges();
    }
    
    public Map getFontFamilyMap() {
        if (this.fontFamilyMap == null) {
            this.fontFamilyMap = new HashMap();
        }
        return this.fontFamilyMap;
    }
    
    protected void setFontFamilyMap(final Map fontFamilyMap) {
        this.fontFamilyMap = fontFamilyMap;
    }
    
    public void setElementData(final Node n, final Object data) {
        if (this.elementDataMap == null) {
            this.elementDataMap = new WeakHashMap();
        }
        this.elementDataMap.put(n, new SoftReference<Object>(data));
    }
    
    public Object getElementData(final Node n) {
        if (this.elementDataMap == null) {
            return null;
        }
        Object o = this.elementDataMap.get(n);
        if (o == null) {
            return null;
        }
        final SoftReference sr = (SoftReference)o;
        o = sr.get();
        if (o == null) {
            this.elementDataMap.remove(n);
        }
        return o;
    }
    
    public UserAgent getUserAgent() {
        return this.userAgent;
    }
    
    protected void setUserAgent(final UserAgent userAgent) {
        this.userAgent = userAgent;
    }
    
    public GVTBuilder getGVTBuilder() {
        return this.gvtBuilder;
    }
    
    protected void setGVTBuilder(final GVTBuilder gvtBuilder) {
        this.gvtBuilder = gvtBuilder;
    }
    
    public InterpreterPool getInterpreterPool() {
        return this.interpreterPool;
    }
    
    public FocusManager getFocusManager() {
        return this.focusManager;
    }
    
    public CursorManager getCursorManager() {
        return this.cursorManager;
    }
    
    protected void setInterpreterPool(final InterpreterPool interpreterPool) {
        this.interpreterPool = interpreterPool;
    }
    
    public Interpreter getInterpreter(final String language) {
        if (this.document == null) {
            throw new RuntimeException("Unknown document");
        }
        Interpreter interpreter = this.interpreterMap.get(language);
        if (interpreter == null) {
            try {
                interpreter = this.interpreterPool.createInterpreter(this.document, language, null);
                final String[] arr$;
                final String[] mimeTypes = arr$ = interpreter.getMimeTypes();
                for (final String mimeType : arr$) {
                    this.interpreterMap.put(mimeType, interpreter);
                }
            }
            catch (Exception e) {
                if (this.userAgent != null) {
                    this.userAgent.displayError(e);
                    return null;
                }
            }
        }
        if (interpreter == null && this.userAgent != null) {
            this.userAgent.displayError(new Exception("Unknown language: " + language));
        }
        return interpreter;
    }
    
    public DocumentLoader getDocumentLoader() {
        return this.documentLoader;
    }
    
    protected void setDocumentLoader(final DocumentLoader newDocumentLoader) {
        this.documentLoader = newDocumentLoader;
    }
    
    public Dimension2D getDocumentSize() {
        return this.documentSize;
    }
    
    protected void setDocumentSize(final Dimension2D d) {
        this.documentSize = d;
    }
    
    @Override
    public boolean isDynamic() {
        return this.dynamicStatus == 2;
    }
    
    @Override
    public boolean isInteractive() {
        return this.dynamicStatus != 0;
    }
    
    public void setDynamicState(final int status) {
        this.dynamicStatus = status;
    }
    
    public void setDynamic(final boolean dynamic) {
        if (dynamic) {
            this.setDynamicState(2);
        }
        else {
            this.setDynamicState(0);
        }
    }
    
    public void setInteractive(final boolean interactive) {
        if (interactive) {
            this.setDynamicState(1);
        }
        else {
            this.setDynamicState(0);
        }
    }
    
    public UpdateManager getUpdateManager() {
        return this.updateManager;
    }
    
    protected void setUpdateManager(final UpdateManager um) {
        this.updateManager = um;
    }
    
    protected void setUpdateManager(final BridgeContext ctx, final UpdateManager um) {
        ctx.setUpdateManager(um);
    }
    
    protected void setXBLManager(final BridgeContext ctx, final XBLManager xm) {
        ctx.xblManager = xm;
    }
    
    public boolean isSVG12() {
        return this.isSVG12;
    }
    
    public BridgeContext getPrimaryBridgeContext() {
        if (this.primaryContext != null) {
            return this.primaryContext;
        }
        return this;
    }
    
    public BridgeContext[] getChildContexts() {
        final BridgeContext[] res = new BridgeContext[this.childContexts.size()];
        final Iterator it = this.childContexts.iterator();
        for (int i = 0; i < res.length; ++i) {
            final WeakReference wr = it.next();
            res[i] = (BridgeContext)wr.get();
        }
        return res;
    }
    
    public SVGAnimationEngine getAnimationEngine() {
        if (this.animationEngine == null) {
            this.animationEngine = new SVGAnimationEngine(this.document, this);
            this.setAnimationLimitingMode();
        }
        return this.animationEngine;
    }
    
    public URIResolver createURIResolver(final SVGDocument doc, final DocumentLoader dl) {
        return new URIResolver(doc, dl);
    }
    
    public Node getReferencedNode(final Element e, final String uri) {
        try {
            final SVGDocument document = (SVGDocument)e.getOwnerDocument();
            final URIResolver ur = this.createURIResolver(document, this.documentLoader);
            final Node ref = ur.getNode(uri, e);
            if (ref == null) {
                throw new BridgeException(this, e, "uri.badTarget", new Object[] { uri });
            }
            final SVGOMDocument refDoc = (SVGOMDocument)((ref.getNodeType() == 9) ? ref : ref.getOwnerDocument());
            if (refDoc != document) {
                this.createSubBridgeContext(refDoc);
            }
            return ref;
        }
        catch (MalformedURLException ex) {
            throw new BridgeException(this, e, ex, "uri.malformed", new Object[] { uri });
        }
        catch (InterruptedIOException ex4) {
            throw new InterruptedBridgeException();
        }
        catch (IOException ex2) {
            throw new BridgeException(this, e, ex2, "uri.io", new Object[] { uri });
        }
        catch (SecurityException ex3) {
            throw new BridgeException(this, e, ex3, "uri.unsecure", new Object[] { uri });
        }
    }
    
    public Element getReferencedElement(final Element e, final String uri) {
        final Node ref = this.getReferencedNode(e, uri);
        if (ref != null && ref.getNodeType() != 1) {
            throw new BridgeException(this, e, "uri.referenceDocument", new Object[] { uri });
        }
        return (Element)ref;
    }
    
    public Viewport getViewport(Element e) {
        if (this.viewportStack == null) {
            Viewport viewport;
            for (e = SVGUtilities.getParentElement(e); e != null; e = SVGUtilities.getParentElement(e)) {
                viewport = this.viewportMap.get(e);
                if (viewport != null) {
                    return viewport;
                }
            }
            return this.viewportMap.get(this.userAgent);
        }
        if (this.viewportStack.size() == 0) {
            return this.viewportMap.get(this.userAgent);
        }
        return this.viewportStack.get(0);
    }
    
    public void openViewport(final Element e, final Viewport viewport) {
        this.viewportMap.put(e, viewport);
        if (this.viewportStack == null) {
            this.viewportStack = new LinkedList();
        }
        this.viewportStack.add(0, viewport);
    }
    
    public void removeViewport(final Element e) {
        this.viewportMap.remove(e);
    }
    
    public void closeViewport(final Element e) {
        this.viewportStack.remove(0);
        if (this.viewportStack.size() == 0) {
            this.viewportStack = null;
        }
    }
    
    public void bind(final Node node, final GraphicsNode gn) {
        if (this.elementNodeMap == null) {
            this.elementNodeMap = new WeakHashMap();
            this.nodeElementMap = new WeakHashMap();
        }
        this.elementNodeMap.put(node, new SoftReference<GraphicsNode>(gn));
        this.nodeElementMap.put(gn, new SoftReference<Node>(node));
    }
    
    public void unbind(final Node node) {
        if (this.elementNodeMap == null) {
            return;
        }
        GraphicsNode gn = null;
        final SoftReference sr = this.elementNodeMap.get(node);
        if (sr != null) {
            gn = sr.get();
        }
        this.elementNodeMap.remove(node);
        if (gn != null) {
            this.nodeElementMap.remove(gn);
        }
    }
    
    public GraphicsNode getGraphicsNode(final Node node) {
        if (this.elementNodeMap != null) {
            final SoftReference sr = this.elementNodeMap.get(node);
            if (sr != null) {
                return sr.get();
            }
        }
        return null;
    }
    
    public Element getElement(final GraphicsNode gn) {
        if (this.nodeElementMap != null) {
            final SoftReference sr = this.nodeElementMap.get(gn);
            if (sr != null) {
                final Node n = sr.get();
                if (n.getNodeType() == 1) {
                    return (Element)n;
                }
            }
        }
        return null;
    }
    
    public boolean hasGraphicsNodeBridge(final Element element) {
        if (this.namespaceURIMap == null || element == null) {
            return false;
        }
        final String localName = element.getLocalName();
        String namespaceURI = element.getNamespaceURI();
        namespaceURI = ((namespaceURI == null) ? "" : namespaceURI);
        final HashMap localNameMap = this.namespaceURIMap.get(namespaceURI);
        return localNameMap != null && localNameMap.get(localName) instanceof GraphicsNodeBridge;
    }
    
    public DocumentBridge getDocumentBridge() {
        return new SVGDocumentBridge();
    }
    
    public Bridge getBridge(final Element element) {
        if (this.namespaceURIMap == null || element == null) {
            return null;
        }
        final String localName = element.getLocalName();
        String namespaceURI = element.getNamespaceURI();
        namespaceURI = ((namespaceURI == null) ? "" : namespaceURI);
        return this.getBridge(namespaceURI, localName);
    }
    
    public Bridge getBridge(final String namespaceURI, final String localName) {
        Bridge bridge = null;
        if (this.namespaceURIMap != null) {
            final HashMap localNameMap = this.namespaceURIMap.get(namespaceURI);
            if (localNameMap != null) {
                bridge = localNameMap.get(localName);
            }
        }
        if (bridge == null && (this.reservedNamespaceSet == null || !this.reservedNamespaceSet.contains(namespaceURI))) {
            bridge = this.defaultBridge;
        }
        if (this.isDynamic()) {
            return (bridge == null) ? null : bridge.getInstance();
        }
        return bridge;
    }
    
    public void putBridge(String namespaceURI, final String localName, final Bridge bridge) {
        if (!namespaceURI.equals(bridge.getNamespaceURI()) || !localName.equals(bridge.getLocalName())) {
            throw new RuntimeException("Invalid Bridge: " + namespaceURI + "/" + bridge.getNamespaceURI() + " " + localName + "/" + bridge.getLocalName() + " " + bridge.getClass());
        }
        if (this.namespaceURIMap == null) {
            this.namespaceURIMap = new HashMap();
        }
        namespaceURI = ((namespaceURI == null) ? "" : namespaceURI);
        HashMap localNameMap = this.namespaceURIMap.get(namespaceURI);
        if (localNameMap == null) {
            localNameMap = new HashMap();
            this.namespaceURIMap.put(namespaceURI, localNameMap);
        }
        localNameMap.put(localName, bridge);
    }
    
    public void putBridge(final Bridge bridge) {
        this.putBridge(bridge.getNamespaceURI(), bridge.getLocalName(), bridge);
    }
    
    public void removeBridge(String namespaceURI, final String localName) {
        if (this.namespaceURIMap == null) {
            return;
        }
        namespaceURI = ((namespaceURI == null) ? "" : namespaceURI);
        final HashMap localNameMap = this.namespaceURIMap.get(namespaceURI);
        if (localNameMap != null) {
            localNameMap.remove(localName);
            if (localNameMap.isEmpty()) {
                this.namespaceURIMap.remove(namespaceURI);
                if (this.namespaceURIMap.isEmpty()) {
                    this.namespaceURIMap = null;
                }
            }
        }
    }
    
    public void setDefaultBridge(final Bridge bridge) {
        this.defaultBridge = bridge;
    }
    
    public void putReservedNamespaceURI(String namespaceURI) {
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        if (this.reservedNamespaceSet == null) {
            this.reservedNamespaceSet = new HashSet();
        }
        this.reservedNamespaceSet.add(namespaceURI);
    }
    
    public void removeReservedNamespaceURI(String namespaceURI) {
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        if (this.reservedNamespaceSet != null) {
            this.reservedNamespaceSet.remove(namespaceURI);
            if (this.reservedNamespaceSet.isEmpty()) {
                this.reservedNamespaceSet = null;
            }
        }
    }
    
    public void addUIEventListeners(final Document doc) {
        final NodeEventTarget evtTarget = (NodeEventTarget)doc.getDocumentElement();
        final DOMMouseOverEventListener domMouseOverListener = new DOMMouseOverEventListener();
        evtTarget.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", domMouseOverListener, true, null);
        this.storeEventListenerNS(evtTarget, "http://www.w3.org/2001/xml-events", "mouseover", domMouseOverListener, true);
        final DOMMouseOutEventListener domMouseOutListener = new DOMMouseOutEventListener();
        evtTarget.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", domMouseOutListener, true, null);
        this.storeEventListenerNS(evtTarget, "http://www.w3.org/2001/xml-events", "mouseout", domMouseOutListener, true);
    }
    
    public void removeUIEventListeners(final Document doc) {
        final EventTarget evtTarget = (EventTarget)doc.getDocumentElement();
        synchronized (this.eventListenerSet) {
            for (final Object anEventListenerSet : this.eventListenerSet) {
                final EventListenerMememto elm = (EventListenerMememto)anEventListenerSet;
                final NodeEventTarget et = elm.getTarget();
                if (et == evtTarget) {
                    final EventListener el = elm.getListener();
                    final boolean uc = elm.getUseCapture();
                    final String t = elm.getEventType();
                    final boolean n = elm.getNamespaced();
                    if (et == null || el == null) {
                        continue;
                    }
                    if (t == null) {
                        continue;
                    }
                    if (n) {
                        final String ns = elm.getNamespaceURI();
                        et.removeEventListenerNS(ns, t, el, uc);
                    }
                    else {
                        et.removeEventListener(t, el, uc);
                    }
                }
            }
        }
    }
    
    public void addDOMListeners() {
        final SVGOMDocument doc = (SVGOMDocument)this.document;
        doc.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.domAttrModifiedEventListener = new DOMAttrModifiedEventListener(), true, null);
        doc.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.domNodeInsertedEventListener = new DOMNodeInsertedEventListener(), true, null);
        doc.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.domNodeRemovedEventListener = new DOMNodeRemovedEventListener(), true, null);
        doc.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", this.domCharacterDataModifiedEventListener = new DOMCharacterDataModifiedEventListener(), true, null);
        doc.addAnimatedAttributeListener(this.animatedAttributeListener = new AnimatedAttrListener());
        this.focusManager = new FocusManager(this.document);
        final CSSEngine cssEngine = doc.getCSSEngine();
        cssEngine.addCSSEngineListener(this.cssPropertiesChangedListener = new CSSPropertiesChangedListener());
    }
    
    protected void removeDOMListeners() {
        final SVGOMDocument doc = (SVGOMDocument)this.document;
        doc.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.domAttrModifiedEventListener, true);
        doc.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.domNodeInsertedEventListener, true);
        doc.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.domNodeRemovedEventListener, true);
        doc.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", this.domCharacterDataModifiedEventListener, true);
        doc.removeAnimatedAttributeListener(this.animatedAttributeListener);
        final CSSEngine cssEngine = doc.getCSSEngine();
        if (cssEngine != null) {
            cssEngine.removeCSSEngineListener(this.cssPropertiesChangedListener);
            cssEngine.dispose();
            doc.setCSSEngine(null);
        }
    }
    
    protected void storeEventListener(final EventTarget t, final String s, final EventListener l, final boolean b) {
        synchronized (this.eventListenerSet) {
            this.eventListenerSet.add(new EventListenerMememto(t, s, l, b, this));
        }
    }
    
    protected void storeEventListenerNS(final EventTarget t, final String n, final String s, final EventListener l, final boolean b) {
        synchronized (this.eventListenerSet) {
            this.eventListenerSet.add(new EventListenerMememto(t, n, s, l, b, this));
        }
    }
    
    public void addGVTListener(final Document doc) {
        BridgeEventSupport.addGVTListener(this, doc);
    }
    
    protected void clearChildContexts() {
        this.childContexts.clear();
    }
    
    public void dispose() {
        this.clearChildContexts();
        synchronized (this.eventListenerSet) {
            for (final Object anEventListenerSet : this.eventListenerSet) {
                final EventListenerMememto m = (EventListenerMememto)anEventListenerSet;
                final NodeEventTarget et = m.getTarget();
                final EventListener el = m.getListener();
                final boolean uc = m.getUseCapture();
                final String t = m.getEventType();
                final boolean n = m.getNamespaced();
                if (et != null && el != null) {
                    if (t == null) {
                        continue;
                    }
                    if (n) {
                        final String ns = m.getNamespaceURI();
                        et.removeEventListenerNS(ns, t, el, uc);
                    }
                    else {
                        et.removeEventListener(t, el, uc);
                    }
                }
            }
        }
        if (this.document != null) {
            this.removeDOMListeners();
            AbstractGraphicsNodeBridge.disposeTree(this.document);
        }
        if (this.animationEngine != null) {
            this.animationEngine.dispose();
            this.animationEngine = null;
        }
        for (final Object o : this.interpreterMap.values()) {
            final Interpreter interpreter = (Interpreter)o;
            if (interpreter != null) {
                interpreter.dispose();
            }
        }
        this.interpreterMap.clear();
        if (this.focusManager != null) {
            this.focusManager.dispose();
        }
        if (this.elementDataMap != null) {
            this.elementDataMap.clear();
        }
        if (this.nodeElementMap != null) {
            this.nodeElementMap.clear();
        }
        if (this.elementNodeMap != null) {
            this.elementNodeMap.clear();
        }
    }
    
    protected static SVGContext getSVGContext(final Node node) {
        if (node instanceof SVGOMElement) {
            return ((SVGOMElement)node).getSVGContext();
        }
        if (node instanceof SVGOMDocument) {
            return ((SVGOMDocument)node).getSVGContext();
        }
        return null;
    }
    
    protected static BridgeUpdateHandler getBridgeUpdateHandler(final Node node) {
        final SVGContext ctx = getSVGContext(node);
        return (ctx == null) ? null : ((BridgeUpdateHandler)ctx);
    }
    
    @Override
    public Value getSystemColor(final String ident) {
        return SystemColorSupport.getSystemColor(ident);
    }
    
    @Override
    public Value getDefaultFontFamily() {
        final SVGOMDocument doc = (SVGOMDocument)this.document;
        final SVGStylableElement root = (SVGStylableElement)doc.getRootElement();
        final String str = this.userAgent.getDefaultFontFamily();
        return doc.getCSSEngine().parsePropertyValue(root, "font-family", str);
    }
    
    @Override
    public float getLighterFontWeight(final float f) {
        return this.userAgent.getLighterFontWeight(f);
    }
    
    @Override
    public float getBolderFontWeight(final float f) {
        return this.userAgent.getBolderFontWeight(f);
    }
    
    @Override
    public float getPixelUnitToMillimeter() {
        return this.userAgent.getPixelUnitToMillimeter();
    }
    
    @Override
    public float getPixelToMillimeter() {
        return this.getPixelUnitToMillimeter();
    }
    
    @Override
    public float getMediumFontSize() {
        return this.userAgent.getMediumFontSize();
    }
    
    @Override
    public float getBlockWidth(final Element elt) {
        return this.getViewport(elt).getWidth();
    }
    
    @Override
    public float getBlockHeight(final Element elt) {
        return this.getViewport(elt).getHeight();
    }
    
    @Override
    public void checkLoadExternalResource(final ParsedURL resourceURL, final ParsedURL docURL) throws SecurityException {
        this.userAgent.checkLoadExternalResource(resourceURL, docURL);
    }
    
    public boolean isDynamicDocument(final Document doc) {
        return BaseScriptingEnvironment.isDynamicDocument(this, doc);
    }
    
    public boolean isInteractiveDocument(final Document doc) {
        final Element root = ((SVGDocument)doc).getRootElement();
        return "http://www.w3.org/2000/svg".equals(root.getNamespaceURI()) && this.checkInteractiveElement(root);
    }
    
    public boolean checkInteractiveElement(final Element e) {
        return this.checkInteractiveElement((SVGDocument)e.getOwnerDocument(), e);
    }
    
    public boolean checkInteractiveElement(final SVGDocument doc, final Element e) {
        final String tag = e.getLocalName();
        if ("a".equals(tag)) {
            return true;
        }
        if ("title".equals(tag)) {
            return e.getParentNode() != doc.getRootElement();
        }
        if ("desc".equals(tag)) {
            return e.getParentNode() != doc.getRootElement();
        }
        if ("cursor".equals(tag)) {
            return true;
        }
        if (e.getAttribute("cursor").length() > 0) {
            return true;
        }
        final String svg_ns = "http://www.w3.org/2000/svg";
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                final Element child = (Element)n;
                if ("http://www.w3.org/2000/svg".equals(child.getNamespaceURI()) && this.checkInteractiveElement(child)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setAnimationLimitingNone() {
        this.animationLimitingMode = 0;
        if (this.animationEngine != null) {
            this.setAnimationLimitingMode();
        }
    }
    
    public void setAnimationLimitingCPU(final float pc) {
        this.animationLimitingMode = 1;
        this.animationLimitingAmount = pc;
        if (this.animationEngine != null) {
            this.setAnimationLimitingMode();
        }
    }
    
    public void setAnimationLimitingFPS(final float fps) {
        this.animationLimitingMode = 2;
        this.animationLimitingAmount = fps;
        if (this.animationEngine != null) {
            this.setAnimationLimitingMode();
        }
    }
    
    protected void setAnimationLimitingMode() {
        switch (this.animationLimitingMode) {
            case 0: {
                this.animationEngine.setAnimationLimitingNone();
                break;
            }
            case 1: {
                this.animationEngine.setAnimationLimitingCPU(this.animationLimitingAmount);
                break;
            }
            case 2: {
                this.animationEngine.setAnimationLimitingFPS(this.animationLimitingAmount);
                break;
            }
        }
    }
    
    public void registerSVGBridges() {
        final UserAgent ua = this.getUserAgent();
        final List ext = this.getBridgeExtensions(this.document);
        for (final Object anExt : ext) {
            final BridgeExtension be = (BridgeExtension)anExt;
            be.registerTags(this);
            ua.registerExtension(be);
        }
    }
    
    public List getBridgeExtensions(final Document doc) {
        final Element root = ((SVGOMDocument)doc).getRootElement();
        final String ver = root.getAttributeNS(null, "version");
        BridgeExtension svgBE;
        if (ver.length() == 0 || ver.equals("1.0") || ver.equals("1.1")) {
            svgBE = new SVGBridgeExtension();
        }
        else {
            svgBE = new SVG12BridgeExtension();
        }
        final float priority = svgBE.getPriority();
        this.extensions = new LinkedList(getGlobalBridgeExtensions());
        final ListIterator li = this.extensions.listIterator();
        while (li.hasNext()) {
            final BridgeExtension lbe = li.next();
            if (lbe.getPriority() > priority) {
                li.previous();
                li.add(svgBE);
                return this.extensions;
            }
        }
        li.add(svgBE);
        return this.extensions;
    }
    
    public static synchronized List getGlobalBridgeExtensions() {
        if (BridgeContext.globalExtensions != null) {
            return BridgeContext.globalExtensions;
        }
        BridgeContext.globalExtensions = new LinkedList();
        final Iterator iter = Service.providers(BridgeExtension.class);
    Label_0027:
        while (iter.hasNext()) {
            final BridgeExtension be = iter.next();
            final float priority = be.getPriority();
            final ListIterator li = BridgeContext.globalExtensions.listIterator();
            while (true) {
                while (li.hasNext()) {
                    final BridgeExtension lbe = li.next();
                    if (lbe.getPriority() > priority) {
                        li.previous();
                        li.add(be);
                        continue Label_0027;
                    }
                }
                li.add(be);
                continue;
            }
        }
        return BridgeContext.globalExtensions;
    }
    
    static {
        BridgeContext.sharedPool = new InterpreterPool();
        BridgeContext.globalExtensions = null;
    }
    
    public static class SoftReferenceMememto extends CleanerThread.SoftReferenceCleared
    {
        Object mememto;
        Set set;
        
        SoftReferenceMememto(final Object ref, final Object mememto, final Set set) {
            super(ref);
            this.mememto = mememto;
            this.set = set;
        }
        
        @Override
        public void cleared() {
            synchronized (this.set) {
                this.set.remove(this.mememto);
                this.mememto = null;
                this.set = null;
            }
        }
    }
    
    protected static class EventListenerMememto
    {
        public SoftReference target;
        public SoftReference listener;
        public boolean useCapture;
        public String namespaceURI;
        public String eventType;
        public boolean namespaced;
        
        public EventListenerMememto(final EventTarget t, final String s, final EventListener l, final boolean b, final BridgeContext ctx) {
            final Set set = ctx.eventListenerSet;
            this.target = new SoftReferenceMememto(t, this, set);
            this.listener = new SoftReferenceMememto(l, this, set);
            this.eventType = s;
            this.useCapture = b;
        }
        
        public EventListenerMememto(final EventTarget t, final String n, final String s, final EventListener l, final boolean b, final BridgeContext ctx) {
            this(t, s, l, b, ctx);
            this.namespaceURI = n;
            this.namespaced = true;
        }
        
        public EventListener getListener() {
            return this.listener.get();
        }
        
        public NodeEventTarget getTarget() {
            return this.target.get();
        }
        
        public boolean getUseCapture() {
            return this.useCapture;
        }
        
        public String getNamespaceURI() {
            return this.namespaceURI;
        }
        
        public String getEventType() {
            return this.eventType;
        }
        
        public boolean getNamespaced() {
            return this.namespaced;
        }
    }
    
    protected class DOMAttrModifiedEventListener implements EventListener
    {
        public DOMAttrModifiedEventListener() {
        }
        
        @Override
        public void handleEvent(final Event evt) {
            final Node node = (Node)evt.getTarget();
            final BridgeUpdateHandler h = BridgeContext.getBridgeUpdateHandler(node);
            if (h != null) {
                try {
                    h.handleDOMAttrModifiedEvent((MutationEvent)evt);
                }
                catch (Exception e) {
                    BridgeContext.this.userAgent.displayError(e);
                }
            }
        }
    }
    
    protected class DOMMouseOutEventListener implements EventListener
    {
        public DOMMouseOutEventListener() {
        }
        
        @Override
        public void handleEvent(final Event evt) {
            final MouseEvent me = (MouseEvent)evt;
            final Element newTarget = (Element)me.getRelatedTarget();
            Cursor cursor = CursorManager.DEFAULT_CURSOR;
            if (newTarget != null) {
                cursor = CSSUtilities.convertCursor(newTarget, BridgeContext.this);
            }
            if (cursor == null) {
                cursor = CursorManager.DEFAULT_CURSOR;
            }
            BridgeContext.this.userAgent.setSVGCursor(cursor);
        }
    }
    
    protected class DOMMouseOverEventListener implements EventListener
    {
        public DOMMouseOverEventListener() {
        }
        
        @Override
        public void handleEvent(final Event evt) {
            final Element target = (Element)evt.getTarget();
            final Cursor cursor = CSSUtilities.convertCursor(target, BridgeContext.this);
            if (cursor != null) {
                BridgeContext.this.userAgent.setSVGCursor(cursor);
            }
        }
    }
    
    protected class DOMNodeInsertedEventListener implements EventListener
    {
        public DOMNodeInsertedEventListener() {
        }
        
        @Override
        public void handleEvent(final Event evt) {
            final MutationEvent me = (MutationEvent)evt;
            final BridgeUpdateHandler h = BridgeContext.getBridgeUpdateHandler(me.getRelatedNode());
            if (h != null) {
                try {
                    h.handleDOMNodeInsertedEvent(me);
                }
                catch (InterruptedBridgeException ibe) {}
                catch (Exception e) {
                    BridgeContext.this.userAgent.displayError(e);
                }
            }
        }
    }
    
    protected class DOMNodeRemovedEventListener implements EventListener
    {
        public DOMNodeRemovedEventListener() {
        }
        
        @Override
        public void handleEvent(final Event evt) {
            final Node node = (Node)evt.getTarget();
            final BridgeUpdateHandler h = BridgeContext.getBridgeUpdateHandler(node);
            if (h != null) {
                try {
                    h.handleDOMNodeRemovedEvent((MutationEvent)evt);
                }
                catch (Exception e) {
                    BridgeContext.this.userAgent.displayError(e);
                }
            }
        }
    }
    
    protected class DOMCharacterDataModifiedEventListener implements EventListener
    {
        public DOMCharacterDataModifiedEventListener() {
        }
        
        @Override
        public void handleEvent(final Event evt) {
            Node node;
            for (node = (Node)evt.getTarget(); node != null && !(node instanceof SVGOMElement); node = (Node)((AbstractNode)node).getParentNodeEventTarget()) {}
            final BridgeUpdateHandler h = BridgeContext.getBridgeUpdateHandler(node);
            if (h != null) {
                try {
                    h.handleDOMCharacterDataModified((MutationEvent)evt);
                }
                catch (Exception e) {
                    BridgeContext.this.userAgent.displayError(e);
                }
            }
        }
    }
    
    protected class CSSPropertiesChangedListener implements CSSEngineListener
    {
        public CSSPropertiesChangedListener() {
        }
        
        @Override
        public void propertiesChanged(final CSSEngineEvent evt) {
            final Element elem = evt.getElement();
            final SVGContext ctx = BridgeContext.getSVGContext(elem);
            if (ctx == null) {
                final GraphicsNode pgn = BridgeContext.this.getGraphicsNode(elem.getParentNode());
                if (pgn == null || !(pgn instanceof CompositeGraphicsNode)) {
                    return;
                }
                final CompositeGraphicsNode parent = (CompositeGraphicsNode)pgn;
                final int[] arr$;
                final int[] properties = arr$ = evt.getProperties();
                final int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    final int property = arr$[i$];
                    if (property == 12) {
                        if (!CSSUtilities.convertDisplay(elem)) {
                            break;
                        }
                        final GVTBuilder builder = BridgeContext.this.getGVTBuilder();
                        final GraphicsNode childNode = builder.build(BridgeContext.this, elem);
                        if (childNode == null) {
                            break;
                        }
                        int idx = -1;
                        for (Node ps = elem.getPreviousSibling(); ps != null; ps = ps.getPreviousSibling()) {
                            if (ps.getNodeType() == 1) {
                                final Element pse = (Element)ps;
                                final GraphicsNode gn = BridgeContext.this.getGraphicsNode(pse);
                                if (gn != null) {
                                    idx = parent.indexOf(gn);
                                    if (idx != -1) {
                                        break;
                                    }
                                }
                            }
                        }
                        ++idx;
                        parent.add(idx, childNode);
                        break;
                    }
                    else {
                        ++i$;
                    }
                }
            }
            if (ctx != null && ctx instanceof BridgeUpdateHandler) {
                ((BridgeUpdateHandler)ctx).handleCSSEngineEvent(evt);
            }
        }
    }
    
    protected class AnimatedAttrListener implements AnimatedAttributeListener
    {
        public AnimatedAttrListener() {
        }
        
        @Override
        public void animatedAttributeChanged(final Element e, final AnimatedLiveAttributeValue alav) {
            final BridgeUpdateHandler h = BridgeContext.getBridgeUpdateHandler(e);
            if (h != null) {
                try {
                    h.handleAnimatedAttributeChanged(alav);
                }
                catch (Exception ex) {
                    BridgeContext.this.userAgent.displayError(ex);
                }
            }
        }
        
        @Override
        public void otherAnimationChanged(final Element e, final String type) {
            final BridgeUpdateHandler h = BridgeContext.getBridgeUpdateHandler(e);
            if (h != null) {
                try {
                    h.handleOtherAnimationChanged(type);
                }
                catch (Exception ex) {
                    BridgeContext.this.userAgent.displayError(ex);
                }
            }
        }
    }
    
    public static class CSSEngineUserAgentWrapper implements CSSEngineUserAgent
    {
        UserAgent ua;
        
        CSSEngineUserAgentWrapper(final UserAgent ua) {
            this.ua = ua;
        }
        
        @Override
        public void displayError(final Exception ex) {
            this.ua.displayError(ex);
        }
        
        @Override
        public void displayMessage(final String message) {
            this.ua.displayMessage(message);
        }
    }
}
