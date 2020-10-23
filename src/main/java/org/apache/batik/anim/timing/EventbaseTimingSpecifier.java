// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

import org.w3c.dom.events.Event;
import org.apache.batik.dom.events.NodeEventTarget;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.EventListener;

public class EventbaseTimingSpecifier extends EventLikeTimingSpecifier implements EventListener
{
    protected String eventbaseID;
    protected TimedElement eventbase;
    protected EventTarget eventTarget;
    protected String eventNamespaceURI;
    protected String eventType;
    protected String eventName;
    
    public EventbaseTimingSpecifier(final TimedElement owner, final boolean isBegin, final float offset, final String eventbaseID, final String eventName) {
        super(owner, isBegin, offset);
        this.eventbaseID = eventbaseID;
        this.eventName = eventName;
        final TimedDocumentRoot root = owner.getRoot();
        this.eventNamespaceURI = root.getEventNamespaceURI(eventName);
        this.eventType = root.getEventType(eventName);
        if (eventbaseID == null) {
            this.eventTarget = owner.getAnimationEventTarget();
        }
        else {
            this.eventTarget = owner.getEventTargetById(eventbaseID);
        }
    }
    
    @Override
    public String toString() {
        return ((this.eventbaseID == null) ? "" : (this.eventbaseID + ".")) + this.eventName + ((this.offset != 0.0f) ? super.toString() : "");
    }
    
    @Override
    public void initialize() {
        ((NodeEventTarget)this.eventTarget).addEventListenerNS(this.eventNamespaceURI, this.eventType, this, false, null);
    }
    
    @Override
    public void deinitialize() {
        ((NodeEventTarget)this.eventTarget).removeEventListenerNS(this.eventNamespaceURI, this.eventType, this, false);
    }
    
    @Override
    public void handleEvent(final Event e) {
        this.owner.eventOccurred(this, e);
    }
    
    @Override
    public void resolve(final Event e) {
        final float time = this.owner.getRoot().convertEpochTime(e.getTimeStamp());
        final InstanceTime instance = new InstanceTime(this, time + this.offset, true);
        this.owner.addInstanceTime(instance, this.isBegin);
    }
}
