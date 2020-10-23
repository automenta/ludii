// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.w3c.dom.events;

import org.w3c.dom.events.Event;

public interface CustomEvent extends Event
{
    Object getDetail();
    
    void initCustomEventNS(final String p0, final String p1, final boolean p2, final boolean p3, final Object p4);
}
