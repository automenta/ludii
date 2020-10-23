// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.smil;

import org.w3c.dom.events.Event;
import org.w3c.dom.views.AbstractView;

public interface TimeEvent extends Event
{
    AbstractView getView();
    
    int getDetail();
    
    void initTimeEvent(final String p0, final AbstractView p1, final int p2);
}
