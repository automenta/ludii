// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.w3c.dom.events;

import org.w3c.dom.views.AbstractView;
import org.w3c.dom.events.UIEvent;

public interface TextEvent extends UIEvent
{
    String getData();
    
    void initTextEvent(final String p0, final boolean p1, final boolean p2, final AbstractView p3, final String p4);
    
    void initTextEventNS(final String p0, final String p1, final boolean p2, final boolean p3, final AbstractView p4, final String p5);
}
