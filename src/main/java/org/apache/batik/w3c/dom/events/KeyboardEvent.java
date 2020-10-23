// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.w3c.dom.events;

import org.w3c.dom.views.AbstractView;
import org.w3c.dom.events.UIEvent;

public interface KeyboardEvent extends UIEvent
{
    public static final int DOM_KEY_LOCATION_STANDARD = 0;
    public static final int DOM_KEY_LOCATION_LEFT = 1;
    public static final int DOM_KEY_LOCATION_RIGHT = 2;
    public static final int DOM_KEY_LOCATION_NUMPAD = 3;
    
    String getKeyIdentifier();
    
    int getKeyLocation();
    
    boolean getCtrlKey();
    
    boolean getShiftKey();
    
    boolean getAltKey();
    
    boolean getMetaKey();
    
    boolean getModifierState(final String p0);
    
    void initKeyboardEvent(final String p0, final boolean p1, final boolean p2, final AbstractView p3, final String p4, final int p5, final String p6);
    
    void initKeyboardEventNS(final String p0, final String p1, final boolean p2, final boolean p3, final AbstractView p4, final String p5, final int p6, final String p7);
}
