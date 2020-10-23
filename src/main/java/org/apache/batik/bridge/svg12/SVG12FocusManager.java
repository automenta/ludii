// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.apache.batik.dom.events.EventSupport;
import org.w3c.dom.events.Event;
import org.w3c.dom.Node;
import org.w3c.dom.views.AbstractView;
import org.apache.batik.dom.events.DOMUIEvent;
import org.w3c.dom.Element;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.EventTarget;
import org.apache.batik.anim.dom.XBLEventSupport;
import org.apache.batik.dom.AbstractNode;
import org.w3c.dom.Document;
import org.apache.batik.bridge.FocusManager;

public class SVG12FocusManager extends FocusManager
{
    public SVG12FocusManager(final Document doc) {
        super(doc);
    }
    
    @Override
    protected void addEventListeners(final Document doc) {
        final AbstractNode n = (AbstractNode)doc;
        final XBLEventSupport es = (XBLEventSupport)n.initializeEventSupport();
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.mouseclickListener = new MouseClickTracker(), true);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.mouseoverListener = new MouseOverTracker(), true);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.mouseoutListener = new MouseOutTracker(), true);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusIn", this.domFocusInListener = new DOMFocusInTracker(), true);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusOut", this.domFocusOutListener = new DOMFocusOutTracker(), true);
    }
    
    @Override
    protected void removeEventListeners(final Document doc) {
        final AbstractNode n = (AbstractNode)doc;
        final XBLEventSupport es = (XBLEventSupport)n.getEventSupport();
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.mouseclickListener, true);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.mouseoverListener, true);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.mouseoutListener, true);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusIn", this.domFocusInListener, true);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusOut", this.domFocusOutListener, true);
    }
    
    @Override
    protected void fireDOMFocusInEvent(final EventTarget target, final EventTarget relatedTarget) {
        final DocumentEvent docEvt = (DocumentEvent)((Element)target).getOwnerDocument();
        final DOMUIEvent uiEvt = (DOMUIEvent)docEvt.createEvent("UIEvents");
        uiEvt.initUIEventNS("http://www.w3.org/2001/xml-events", "DOMFocusIn", true, false, null, 0);
        final int limit = DefaultXBLManager.computeBubbleLimit((Node)relatedTarget, (Node)target);
        uiEvt.setBubbleLimit(limit);
        target.dispatchEvent(uiEvt);
    }
    
    @Override
    protected void fireDOMFocusOutEvent(final EventTarget target, final EventTarget relatedTarget) {
        final DocumentEvent docEvt = (DocumentEvent)((Element)target).getOwnerDocument();
        final DOMUIEvent uiEvt = (DOMUIEvent)docEvt.createEvent("UIEvents");
        uiEvt.initUIEventNS("http://www.w3.org/2001/xml-events", "DOMFocusOut", true, false, null, 0);
        final int limit = DefaultXBLManager.computeBubbleLimit((Node)target, (Node)relatedTarget);
        uiEvt.setBubbleLimit(limit);
        target.dispatchEvent(uiEvt);
    }
    
    protected class MouseClickTracker extends FocusManager.MouseClickTracker
    {
        @Override
        public void handleEvent(final Event evt) {
            super.handleEvent(EventSupport.getUltimateOriginalEvent(evt));
        }
    }
    
    protected class DOMFocusInTracker extends FocusManager.DOMFocusInTracker
    {
        @Override
        public void handleEvent(final Event evt) {
            super.handleEvent(EventSupport.getUltimateOriginalEvent(evt));
        }
    }
    
    protected class MouseOverTracker extends FocusManager.MouseOverTracker
    {
        @Override
        public void handleEvent(final Event evt) {
            super.handleEvent(EventSupport.getUltimateOriginalEvent(evt));
        }
    }
    
    protected class MouseOutTracker extends FocusManager.MouseOutTracker
    {
        @Override
        public void handleEvent(final Event evt) {
            super.handleEvent(EventSupport.getUltimateOriginalEvent(evt));
        }
    }
}
