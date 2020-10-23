// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.events;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.events.EventTarget;
import org.apache.batik.dom.xbl.OriginalEvent;
import org.w3c.dom.events.Event;

public abstract class AbstractEvent implements Event, OriginalEvent, Cloneable
{
    protected String type;
    protected boolean isBubbling;
    protected boolean cancelable;
    protected EventTarget currentTarget;
    protected EventTarget target;
    protected short eventPhase;
    protected long timeStamp;
    protected boolean stopPropagation;
    protected boolean stopImmediatePropagation;
    protected boolean preventDefault;
    protected String namespaceURI;
    protected Event originalEvent;
    protected List defaultActions;
    protected int bubbleLimit;
    
    public AbstractEvent() {
        this.timeStamp = System.currentTimeMillis();
        this.stopPropagation = false;
        this.stopImmediatePropagation = false;
        this.preventDefault = false;
        this.bubbleLimit = 0;
    }
    
    @Override
    public String getType() {
        return this.type;
    }
    
    @Override
    public EventTarget getCurrentTarget() {
        return this.currentTarget;
    }
    
    @Override
    public EventTarget getTarget() {
        return this.target;
    }
    
    @Override
    public short getEventPhase() {
        return this.eventPhase;
    }
    
    @Override
    public boolean getBubbles() {
        return this.isBubbling;
    }
    
    @Override
    public boolean getCancelable() {
        return this.cancelable;
    }
    
    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }
    
    public String getNamespaceURI() {
        return this.namespaceURI;
    }
    
    @Override
    public Event getOriginalEvent() {
        return this.originalEvent;
    }
    
    @Override
    public void stopPropagation() {
        this.stopPropagation = true;
    }
    
    @Override
    public void preventDefault() {
        this.preventDefault = true;
    }
    
    public boolean getDefaultPrevented() {
        return this.preventDefault;
    }
    
    public List getDefaultActions() {
        return this.defaultActions;
    }
    
    public void addDefaultAction(final Runnable rable) {
        if (this.defaultActions == null) {
            this.defaultActions = new ArrayList();
        }
        this.defaultActions.add(rable);
    }
    
    public void stopImmediatePropagation() {
        this.stopImmediatePropagation = true;
    }
    
    @Override
    public void initEvent(final String eventTypeArg, final boolean canBubbleArg, final boolean cancelableArg) {
        this.type = eventTypeArg;
        this.isBubbling = canBubbleArg;
        this.cancelable = cancelableArg;
    }
    
    public void initEventNS(final String namespaceURIArg, final String eventTypeArg, final boolean canBubbleArg, final boolean cancelableArg) {
        if (this.namespaceURI != null && this.namespaceURI.length() == 0) {
            this.namespaceURI = null;
        }
        this.namespaceURI = namespaceURIArg;
        this.type = eventTypeArg;
        this.isBubbling = canBubbleArg;
        this.cancelable = cancelableArg;
    }
    
    boolean getStopPropagation() {
        return this.stopPropagation;
    }
    
    boolean getStopImmediatePropagation() {
        return this.stopImmediatePropagation;
    }
    
    void setEventPhase(final short eventPhase) {
        this.eventPhase = eventPhase;
    }
    
    void stopPropagation(final boolean state) {
        this.stopPropagation = state;
    }
    
    void stopImmediatePropagation(final boolean state) {
        this.stopImmediatePropagation = state;
    }
    
    void preventDefault(final boolean state) {
        this.preventDefault = state;
    }
    
    void setCurrentTarget(final EventTarget currentTarget) {
        this.currentTarget = currentTarget;
    }
    
    void setTarget(final EventTarget target) {
        this.target = target;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final AbstractEvent newEvent = (AbstractEvent)super.clone();
        newEvent.timeStamp = System.currentTimeMillis();
        return newEvent;
    }
    
    public AbstractEvent cloneEvent() {
        try {
            final AbstractEvent newEvent = (AbstractEvent)this.clone();
            newEvent.originalEvent = this;
            return newEvent;
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }
    
    public int getBubbleLimit() {
        return this.bubbleLimit;
    }
    
    public void setBubbleLimit(final int n) {
        this.bubbleLimit = n;
    }
}
