// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.events.EventListener;
import org.apache.batik.css.engine.CSSNavigableDocumentListener;
import org.w3c.dom.Node;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;

public class SVG12OMDocument extends SVGOMDocument
{
    protected SVG12OMDocument() {
    }
    
    public SVG12OMDocument(final DocumentType dt, final DOMImplementation impl) {
        super(dt, impl);
    }
    
    @Override
    protected Node newNode() {
        return new SVG12OMDocument();
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
        final XBLEventSupport es = (XBLEventSupport)this.initializeEventSupport();
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", nodeInserted, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", nodeRemoved, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", subtreeModified, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", cdataModified, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", attrModified, false);
    }
    
    @Override
    public void removeCSSNavigableDocumentListener(final CSSNavigableDocumentListener l) {
        final EventListener[] listeners = this.cssNavigableDocumentListeners.get(l);
        if (listeners == null) {
            return;
        }
        final XBLEventSupport es = (XBLEventSupport)this.initializeEventSupport();
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", listeners[0], false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", listeners[1], false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", listeners[2], false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", listeners[3], false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", listeners[4], false);
        this.cssNavigableDocumentListeners.remove(l);
    }
}
