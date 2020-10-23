// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

import org.apache.batik.w3c.dom.events.KeyboardEvent;
import org.apache.batik.dom.events.DOMKeyEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;
import org.apache.batik.dom.events.NodeEventTarget;
import org.w3c.dom.events.EventListener;

public class AccesskeyTimingSpecifier extends EventLikeTimingSpecifier implements EventListener
{
    protected char accesskey;
    protected boolean isSVG12AccessKey;
    protected String keyName;
    
    public AccesskeyTimingSpecifier(final TimedElement owner, final boolean isBegin, final float offset, final char accesskey) {
        super(owner, isBegin, offset);
        this.accesskey = accesskey;
    }
    
    public AccesskeyTimingSpecifier(final TimedElement owner, final boolean isBegin, final float offset, final String keyName) {
        super(owner, isBegin, offset);
        this.isSVG12AccessKey = true;
        this.keyName = keyName;
    }
    
    @Override
    public String toString() {
        if (this.isSVG12AccessKey) {
            return "accessKey(" + this.keyName + ")" + ((this.offset != 0.0f) ? super.toString() : "");
        }
        return "accesskey(" + this.accesskey + ")" + ((this.offset != 0.0f) ? super.toString() : "");
    }
    
    @Override
    public void initialize() {
        if (this.isSVG12AccessKey) {
            final NodeEventTarget eventTarget = (NodeEventTarget)this.owner.getRootEventTarget();
            eventTarget.addEventListenerNS("http://www.w3.org/2001/xml-events", "keydown", this, false, null);
        }
        else {
            final EventTarget eventTarget2 = this.owner.getRootEventTarget();
            eventTarget2.addEventListener("keypress", this, false);
        }
    }
    
    @Override
    public void deinitialize() {
        if (this.isSVG12AccessKey) {
            final NodeEventTarget eventTarget = (NodeEventTarget)this.owner.getRootEventTarget();
            eventTarget.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keydown", this, false);
        }
        else {
            final EventTarget eventTarget2 = this.owner.getRootEventTarget();
            eventTarget2.removeEventListener("keypress", this, false);
        }
    }
    
    @Override
    public void handleEvent(final Event e) {
        boolean matched;
        if (e.getType().charAt(3) == 'p') {
            final DOMKeyEvent evt = (DOMKeyEvent)e;
            matched = (evt.getCharCode() == this.accesskey);
        }
        else {
            final KeyboardEvent evt2 = (KeyboardEvent)e;
            matched = evt2.getKeyIdentifier().equals(this.keyName);
        }
        if (matched) {
            this.owner.eventOccurred(this, e);
        }
    }
    
    @Override
    public void resolve(final Event e) {
        final float time = this.owner.getRoot().convertEpochTime(e.getTimeStamp());
        final InstanceTime instance = new InstanceTime(this, time + this.offset, true);
        this.owner.addInstanceTime(instance, this.isBegin);
    }
}
