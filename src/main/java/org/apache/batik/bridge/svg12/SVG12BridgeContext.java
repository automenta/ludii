// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.apache.batik.dom.xbl.NodeXBL;
import org.apache.batik.anim.dom.XBLOMShadowTreeElement;
import org.w3c.dom.Element;
import org.apache.batik.dom.events.EventSupport;
import org.w3c.dom.events.Event;
import org.apache.batik.bridge.BridgeUpdateHandler;
import org.apache.batik.bridge.ScriptingEnvironment;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.dom.xbl.XBLManager;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.events.EventListener;
import org.apache.batik.dom.events.NodeEventTarget;
import java.util.Iterator;
import org.apache.batik.script.Interpreter;
import org.apache.batik.anim.dom.XBLEventSupport;
import org.apache.batik.dom.AbstractNode;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.apache.batik.bridge.URIResolver;
import org.w3c.dom.svg.SVGDocument;
import org.apache.batik.script.InterpreterPool;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.UserAgent;
import org.w3c.dom.events.EventTarget;
import org.apache.batik.bridge.BridgeContext;

public class SVG12BridgeContext extends BridgeContext
{
    protected XBLBindingListener bindingListener;
    protected XBLContentListener contentListener;
    protected EventTarget mouseCaptureTarget;
    protected boolean mouseCaptureSendAll;
    protected boolean mouseCaptureAutoRelease;
    
    public SVG12BridgeContext(final UserAgent userAgent) {
        super(userAgent);
    }
    
    public SVG12BridgeContext(final UserAgent userAgent, final DocumentLoader loader) {
        super(userAgent, loader);
    }
    
    public SVG12BridgeContext(final UserAgent userAgent, final InterpreterPool interpreterPool, final DocumentLoader documentLoader) {
        super(userAgent, interpreterPool, documentLoader);
    }
    
    @Override
    public URIResolver createURIResolver(final SVGDocument doc, final DocumentLoader dl) {
        return new SVG12URIResolver(doc, dl);
    }
    
    @Override
    public void addGVTListener(final Document doc) {
        SVG12BridgeEventSupport.addGVTListener(this, doc);
    }
    
    @Override
    public void dispose() {
        this.clearChildContexts();
        synchronized (this.eventListenerSet) {
            for (final Object anEventListenerSet : this.eventListenerSet) {
                final EventListenerMememto m = (EventListenerMememto)anEventListenerSet;
                final NodeEventTarget et = m.getTarget();
                final EventListener el = m.getListener();
                final boolean uc = m.getUseCapture();
                final String t = m.getEventType();
                final boolean in = m.getNamespaced();
                if (et != null && el != null) {
                    if (t == null) {
                        continue;
                    }
                    if (m instanceof ImplementationEventListenerMememto) {
                        final String ns = m.getNamespaceURI();
                        final Node nde = (Node)et;
                        final AbstractNode n = (AbstractNode)nde.getOwnerDocument();
                        if (n == null) {
                            continue;
                        }
                        final XBLEventSupport es = (XBLEventSupport)n.initializeEventSupport();
                        es.removeImplementationEventListenerNS(ns, t, el, uc);
                    }
                    else if (in) {
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
            this.removeBindingListener();
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
    }
    
    public void addBindingListener() {
        final AbstractDocument doc = (AbstractDocument)this.document;
        final DefaultXBLManager xm = (DefaultXBLManager)doc.getXBLManager();
        if (xm != null) {
            xm.addBindingListener(this.bindingListener = new XBLBindingListener());
            xm.addContentSelectionChangedListener(this.contentListener = new XBLContentListener());
        }
    }
    
    public void removeBindingListener() {
        final AbstractDocument doc = (AbstractDocument)this.document;
        final XBLManager xm = doc.getXBLManager();
        if (xm instanceof DefaultXBLManager) {
            final DefaultXBLManager dxm = (DefaultXBLManager)xm;
            dxm.removeBindingListener(this.bindingListener);
            dxm.removeContentSelectionChangedListener(this.contentListener);
        }
    }
    
    @Override
    public void addDOMListeners() {
        final SVGOMDocument doc = (SVGOMDocument)this.document;
        final XBLEventSupport evtSupport = (XBLEventSupport)doc.initializeEventSupport();
        evtSupport.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.domAttrModifiedEventListener = new EventListenerWrapper(new DOMAttrModifiedEventListener()), true);
        evtSupport.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.domNodeInsertedEventListener = new EventListenerWrapper(new DOMNodeInsertedEventListener()), true);
        evtSupport.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.domNodeRemovedEventListener = new EventListenerWrapper(new DOMNodeRemovedEventListener()), true);
        evtSupport.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", this.domCharacterDataModifiedEventListener = new EventListenerWrapper(new DOMCharacterDataModifiedEventListener()), true);
        doc.addAnimatedAttributeListener(this.animatedAttributeListener = new AnimatedAttrListener());
        this.focusManager = new SVG12FocusManager(this.document);
        final CSSEngine cssEngine = doc.getCSSEngine();
        cssEngine.addCSSEngineListener(this.cssPropertiesChangedListener = new CSSPropertiesChangedListener());
    }
    
    @Override
    public void addUIEventListeners(final Document doc) {
        final EventTarget evtTarget = (EventTarget)doc.getDocumentElement();
        final AbstractNode n = (AbstractNode)evtTarget;
        final XBLEventSupport evtSupport = (XBLEventSupport)n.initializeEventSupport();
        final EventListener domMouseOverListener = new EventListenerWrapper(new DOMMouseOverEventListener());
        evtSupport.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", domMouseOverListener, true);
        this.storeImplementationEventListenerNS(evtTarget, "http://www.w3.org/2001/xml-events", "mouseover", domMouseOverListener, true);
        final EventListener domMouseOutListener = new EventListenerWrapper(new DOMMouseOutEventListener());
        evtSupport.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", domMouseOutListener, true);
        this.storeImplementationEventListenerNS(evtTarget, "http://www.w3.org/2001/xml-events", "mouseout", domMouseOutListener, true);
    }
    
    @Override
    public void removeUIEventListeners(final Document doc) {
        final EventTarget evtTarget = (EventTarget)doc.getDocumentElement();
        final AbstractNode n = (AbstractNode)evtTarget;
        final XBLEventSupport es = (XBLEventSupport)n.initializeEventSupport();
        synchronized (this.eventListenerSet) {
            for (final Object anEventListenerSet : this.eventListenerSet) {
                final EventListenerMememto elm = (EventListenerMememto)anEventListenerSet;
                final NodeEventTarget et = elm.getTarget();
                if (et == evtTarget) {
                    final EventListener el = elm.getListener();
                    final boolean uc = elm.getUseCapture();
                    final String t = elm.getEventType();
                    final boolean in = elm.getNamespaced();
                    if (et == null || el == null) {
                        continue;
                    }
                    if (t == null) {
                        continue;
                    }
                    if (elm instanceof ImplementationEventListenerMememto) {
                        final String ns = elm.getNamespaceURI();
                        es.removeImplementationEventListenerNS(ns, t, el, uc);
                    }
                    else if (in) {
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
    
    @Override
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
    
    protected void storeImplementationEventListenerNS(final EventTarget t, final String ns, final String s, final EventListener l, final boolean b) {
        synchronized (this.eventListenerSet) {
            final ImplementationEventListenerMememto m = new ImplementationEventListenerMememto(t, ns, s, l, b, this);
            this.eventListenerSet.add(m);
        }
    }
    
    @Override
    public BridgeContext createSubBridgeContext(final SVGOMDocument newDoc) {
        final CSSEngine eng = newDoc.getCSSEngine();
        if (eng != null) {
            return (BridgeContext)newDoc.getCSSEngine().getCSSContext();
        }
        final BridgeContext subCtx = super.createSubBridgeContext(newDoc);
        if (this.isDynamic() && subCtx.isDynamic()) {
            this.setUpdateManager(subCtx, this.updateManager);
            if (this.updateManager != null) {
                ScriptingEnvironment se;
                if (newDoc.isSVG12()) {
                    se = new SVG12ScriptingEnvironment(subCtx);
                }
                else {
                    se = new ScriptingEnvironment(subCtx);
                }
                se.loadScripts();
                se.dispatchSVGLoadEvent();
                if (newDoc.isSVG12()) {
                    final DefaultXBLManager xm = new DefaultXBLManager(newDoc, subCtx);
                    this.setXBLManager(subCtx, xm);
                    newDoc.setXBLManager(xm);
                    xm.startProcessing();
                }
            }
        }
        return subCtx;
    }
    
    public void startMouseCapture(final EventTarget target, final boolean sendAll, final boolean autoRelease) {
        this.mouseCaptureTarget = target;
        this.mouseCaptureSendAll = sendAll;
        this.mouseCaptureAutoRelease = autoRelease;
    }
    
    public void stopMouseCapture() {
        this.mouseCaptureTarget = null;
    }
    
    protected static class ImplementationEventListenerMememto extends EventListenerMememto
    {
        public ImplementationEventListenerMememto(final EventTarget t, final String s, final EventListener l, final boolean b, final BridgeContext c) {
            super(t, s, l, b, c);
        }
        
        public ImplementationEventListenerMememto(final EventTarget t, final String n, final String s, final EventListener l, final boolean b, final BridgeContext c) {
            super(t, n, s, l, b, c);
        }
    }
    
    protected static class EventListenerWrapper implements EventListener
    {
        protected EventListener listener;
        
        public EventListenerWrapper(final EventListener l) {
            this.listener = l;
        }
        
        @Override
        public void handleEvent(final Event evt) {
            this.listener.handleEvent(EventSupport.getUltimateOriginalEvent(evt));
        }
        
        @Override
        public String toString() {
            return super.toString() + " [wrapping " + this.listener.toString() + "]";
        }
    }
    
    protected class XBLBindingListener implements BindingListener
    {
        @Override
        public void bindingChanged(final Element bindableElement, final Element shadowTree) {
            final BridgeUpdateHandler h = BridgeContext.getBridgeUpdateHandler(bindableElement);
            if (h instanceof SVG12BridgeUpdateHandler) {
                final SVG12BridgeUpdateHandler h2 = (SVG12BridgeUpdateHandler)h;
                try {
                    h2.handleBindingEvent(bindableElement, shadowTree);
                }
                catch (Exception e) {
                    SVG12BridgeContext.this.userAgent.displayError(e);
                }
            }
        }
    }
    
    protected class XBLContentListener implements ContentSelectionChangedListener
    {
        @Override
        public void contentSelectionChanged(final ContentSelectionChangedEvent csce) {
            Element e = (Element)csce.getContentElement().getParentNode();
            if (e instanceof XBLOMShadowTreeElement) {
                e = ((NodeXBL)e).getXblBoundElement();
            }
            final BridgeUpdateHandler h = BridgeContext.getBridgeUpdateHandler(e);
            if (h instanceof SVG12BridgeUpdateHandler) {
                final SVG12BridgeUpdateHandler h2 = (SVG12BridgeUpdateHandler)h;
                try {
                    h2.handleContentSelectionChangedEvent(csce);
                }
                catch (Exception ex) {
                    SVG12BridgeContext.this.userAgent.displayError(ex);
                }
            }
        }
    }
}
