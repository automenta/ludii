// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.w3c.dom.events.EventTarget;
import org.apache.batik.dom.svg12.SVGGlobal;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.Messages;
import org.apache.batik.dom.events.EventSupport;
import org.w3c.dom.events.Event;
import org.apache.batik.bridge.Window;
import org.apache.batik.script.Interpreter;
import org.w3c.dom.events.EventListener;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.AbstractElement;
import org.w3c.dom.Element;
import org.apache.batik.anim.dom.XBLEventSupport;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.dom.util.TriplyIndexedTable;
import org.apache.batik.bridge.ScriptingEnvironment;

public class SVG12ScriptingEnvironment extends ScriptingEnvironment
{
    public static final String HANDLER_SCRIPT_DESCRIPTION = "SVG12ScriptingEnvironment.constant.handler.script.description";
    protected TriplyIndexedTable handlerScriptingListeners;
    
    public SVG12ScriptingEnvironment(final BridgeContext ctx) {
        super(ctx);
    }
    
    @Override
    protected void addDocumentListeners() {
        this.domNodeInsertedListener = new DOMNodeInsertedListener();
        this.domNodeRemovedListener = new DOMNodeRemovedListener();
        this.domAttrModifiedListener = new DOMAttrModifiedListener();
        final AbstractDocument doc = (AbstractDocument)this.document;
        final XBLEventSupport es = (XBLEventSupport)doc.initializeEventSupport();
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.domNodeInsertedListener, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.domNodeRemovedListener, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.domAttrModifiedListener, false);
    }
    
    @Override
    protected void removeDocumentListeners() {
        final AbstractDocument doc = (AbstractDocument)this.document;
        final XBLEventSupport es = (XBLEventSupport)doc.initializeEventSupport();
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.domNodeInsertedListener, false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.domNodeRemovedListener, false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.domAttrModifiedListener, false);
    }
    
    @Override
    protected void addScriptingListenersOn(final Element elt) {
        final String eltNS = elt.getNamespaceURI();
        final String eltLN = elt.getLocalName();
        if ("http://www.w3.org/2000/svg".equals(eltNS) && "handler".equals(eltLN)) {
            final AbstractElement tgt = (AbstractElement)elt.getParentNode();
            String eventType = elt.getAttributeNS("http://www.w3.org/2001/xml-events", "event");
            String eventNamespaceURI = "http://www.w3.org/2001/xml-events";
            if (eventType.indexOf(58) != -1) {
                final String prefix = DOMUtilities.getPrefix(eventType);
                eventType = DOMUtilities.getLocalName(eventType);
                eventNamespaceURI = elt.lookupNamespaceURI(prefix);
            }
            final EventListener listener = new HandlerScriptingEventListener(eventNamespaceURI, eventType, (AbstractElement)elt);
            tgt.addEventListenerNS(eventNamespaceURI, eventType, listener, false, null);
            if (this.handlerScriptingListeners == null) {
                this.handlerScriptingListeners = new TriplyIndexedTable();
            }
            this.handlerScriptingListeners.put(eventNamespaceURI, eventType, elt, listener);
        }
        super.addScriptingListenersOn(elt);
    }
    
    @Override
    protected void removeScriptingListenersOn(final Element elt) {
        final String eltNS = elt.getNamespaceURI();
        final String eltLN = elt.getLocalName();
        if ("http://www.w3.org/2000/svg".equals(eltNS) && "handler".equals(eltLN)) {
            final AbstractElement tgt = (AbstractElement)elt.getParentNode();
            String eventType = elt.getAttributeNS("http://www.w3.org/2001/xml-events", "event");
            String eventNamespaceURI = "http://www.w3.org/2001/xml-events";
            if (eventType.indexOf(58) != -1) {
                final String prefix = DOMUtilities.getPrefix(eventType);
                eventType = DOMUtilities.getLocalName(eventType);
                eventNamespaceURI = elt.lookupNamespaceURI(prefix);
            }
            final EventListener listener = (EventListener)this.handlerScriptingListeners.put(eventNamespaceURI, eventType, elt, null);
            tgt.removeEventListenerNS(eventNamespaceURI, eventType, listener, false);
        }
        super.removeScriptingListenersOn(elt);
    }
    
    public org.apache.batik.bridge.Window createWindow(final Interpreter interp, final String lang) {
        return new Global(interp, lang);
    }
    
    protected class DOMNodeInsertedListener extends ScriptingEnvironment.DOMNodeInsertedListener
    {
        @Override
        public void handleEvent(final Event evt) {
            super.handleEvent(EventSupport.getUltimateOriginalEvent(evt));
        }
    }
    
    protected class DOMNodeRemovedListener extends ScriptingEnvironment.DOMNodeRemovedListener
    {
        @Override
        public void handleEvent(final Event evt) {
            super.handleEvent(EventSupport.getUltimateOriginalEvent(evt));
        }
    }
    
    protected class DOMAttrModifiedListener extends ScriptingEnvironment.DOMAttrModifiedListener
    {
        @Override
        public void handleEvent(final Event evt) {
            super.handleEvent(EventSupport.getUltimateOriginalEvent(evt));
        }
    }
    
    protected class HandlerScriptingEventListener implements EventListener
    {
        protected String eventNamespaceURI;
        protected String eventType;
        protected AbstractElement handlerElement;
        
        public HandlerScriptingEventListener(final String ns, final String et, final AbstractElement e) {
            this.eventNamespaceURI = ns;
            this.eventType = et;
            this.handlerElement = e;
        }
        
        @Override
        public void handleEvent(final Event evt) {
            final Element elt = (Element)evt.getCurrentTarget();
            final String script = this.handlerElement.getTextContent();
            if (script.length() == 0) {
                return;
            }
            final DocumentLoader dl = SVG12ScriptingEnvironment.this.bridgeContext.getDocumentLoader();
            final AbstractDocument d = (AbstractDocument)this.handlerElement.getOwnerDocument();
            final int line = dl.getLineNumber(this.handlerElement);
            final String desc = Messages.formatMessage("SVG12ScriptingEnvironment.constant.handler.script.description", new Object[] { d.getDocumentURI(), this.eventNamespaceURI, this.eventType, line });
            String lang = this.handlerElement.getAttributeNS(null, "contentScriptType");
            if (lang.length() == 0) {
                Element e;
                for (e = elt; e != null && (!"http://www.w3.org/2000/svg".equals(e.getNamespaceURI()) || !"svg".equals(e.getLocalName())); e = SVGUtilities.getParentElement(e)) {}
                if (e == null) {
                    return;
                }
                lang = e.getAttributeNS(null, "contentScriptType");
            }
            SVG12ScriptingEnvironment.this.runEventHandler(script, evt, lang, desc);
        }
    }
    
    protected class Global extends Window implements SVGGlobal
    {
        public Global(final Interpreter interp, final String lang) {
            super(interp, lang);
        }
        
        @Override
        public void startMouseCapture(final EventTarget target, final boolean sendAll, final boolean autoRelease) {
            ((SVG12BridgeContext)SVG12ScriptingEnvironment.this.bridgeContext.getPrimaryBridgeContext()).startMouseCapture(target, sendAll, autoRelease);
        }
        
        @Override
        public void stopMouseCapture() {
            ((SVG12BridgeContext)SVG12ScriptingEnvironment.this.bridgeContext.getPrimaryBridgeContext()).stopMouseCapture();
        }
    }
}
