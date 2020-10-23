// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.events;

import org.w3c.dom.events.EventListener;
import org.w3c.dom.DOMException;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

public interface NodeEventTarget extends EventTarget
{
    EventSupport getEventSupport();
    
    NodeEventTarget getParentNodeEventTarget();
    
    boolean dispatchEvent(final Event p0) throws EventException, DOMException;
    
    void addEventListenerNS(final String p0, final String p1, final EventListener p2, final boolean p3, final Object p4);
    
    void removeEventListenerNS(final String p0, final String p1, final EventListener p2, final boolean p3);
}
