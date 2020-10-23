// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.views.AbstractView;
import org.apache.batik.dom.events.DOMUIEvent;
import org.w3c.dom.Element;
import org.w3c.dom.events.DocumentEvent;
import org.apache.batik.dom.events.NodeEventTarget;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventTarget;

public class FocusManager
{
    protected EventTarget lastFocusEventTarget;
    protected Document document;
    protected EventListener mouseclickListener;
    protected EventListener domFocusInListener;
    protected EventListener domFocusOutListener;
    protected EventListener mouseoverListener;
    protected EventListener mouseoutListener;
    
    public FocusManager(final Document doc) {
        this.addEventListeners(this.document = doc);
    }
    
    protected void addEventListeners(final Document doc) {
        final NodeEventTarget target = (NodeEventTarget)doc;
        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.mouseclickListener = new MouseClickTracker(), true, null);
        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.mouseoverListener = new MouseOverTracker(), true, null);
        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.mouseoutListener = new MouseOutTracker(), true, null);
        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusIn", this.domFocusInListener = new DOMFocusInTracker(), true, null);
        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusOut", this.domFocusOutListener = new DOMFocusOutTracker(), true, null);
    }
    
    protected void removeEventListeners(final Document doc) {
        final NodeEventTarget target = (NodeEventTarget)doc;
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.mouseclickListener, true);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.mouseoverListener, true);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.mouseoutListener, true);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusIn", this.domFocusInListener, true);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusOut", this.domFocusOutListener, true);
    }
    
    public EventTarget getCurrentEventTarget() {
        return this.lastFocusEventTarget;
    }
    
    public void dispose() {
        if (this.document == null) {
            return;
        }
        this.removeEventListeners(this.document);
        this.lastFocusEventTarget = null;
        this.document = null;
    }
    
    protected void fireDOMFocusInEvent(final EventTarget target, final EventTarget relatedTarget) {
        final DocumentEvent docEvt = (DocumentEvent)((Element)target).getOwnerDocument();
        final DOMUIEvent uiEvt = (DOMUIEvent)docEvt.createEvent("UIEvents");
        uiEvt.initUIEventNS("http://www.w3.org/2001/xml-events", "DOMFocusIn", true, false, null, 0);
        target.dispatchEvent(uiEvt);
    }
    
    protected void fireDOMFocusOutEvent(final EventTarget target, final EventTarget relatedTarget) {
        final DocumentEvent docEvt = (DocumentEvent)((Element)target).getOwnerDocument();
        final DOMUIEvent uiEvt = (DOMUIEvent)docEvt.createEvent("UIEvents");
        uiEvt.initUIEventNS("http://www.w3.org/2001/xml-events", "DOMFocusOut", true, false, null, 0);
        target.dispatchEvent(uiEvt);
    }
    
    protected void fireDOMActivateEvent(final EventTarget target, final int detailArg) {
        final DocumentEvent docEvt = (DocumentEvent)((Element)target).getOwnerDocument();
        final DOMUIEvent uiEvt = (DOMUIEvent)docEvt.createEvent("UIEvents");
        uiEvt.initUIEventNS("http://www.w3.org/2001/xml-events", "DOMActivate", true, true, null, 0);
        target.dispatchEvent(uiEvt);
    }
    
    protected class MouseClickTracker implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            final MouseEvent mevt = (MouseEvent)evt;
            FocusManager.this.fireDOMActivateEvent(evt.getTarget(), mevt.getDetail());
        }
    }
    
    protected class DOMFocusInTracker implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            final EventTarget newTarget = evt.getTarget();
            if (FocusManager.this.lastFocusEventTarget != null && FocusManager.this.lastFocusEventTarget != newTarget) {
                FocusManager.this.fireDOMFocusOutEvent(FocusManager.this.lastFocusEventTarget, newTarget);
            }
            FocusManager.this.lastFocusEventTarget = evt.getTarget();
        }
    }
    
    protected class DOMFocusOutTracker implements EventListener
    {
        public DOMFocusOutTracker() {
        }
        
        @Override
        public void handleEvent(final Event evt) {
            FocusManager.this.lastFocusEventTarget = null;
        }
    }
    
    protected class MouseOverTracker implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            final MouseEvent me = (MouseEvent)evt;
            final EventTarget target = evt.getTarget();
            final EventTarget relatedTarget = me.getRelatedTarget();
            FocusManager.this.fireDOMFocusInEvent(target, relatedTarget);
        }
    }
    
    protected class MouseOutTracker implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            final MouseEvent me = (MouseEvent)evt;
            final EventTarget target = evt.getTarget();
            final EventTarget relatedTarget = me.getRelatedTarget();
            FocusManager.this.fireDOMFocusOutEvent(target, relatedTarget);
        }
    }
}
