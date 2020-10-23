// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.xbl;

import org.w3c.dom.events.Event;

public interface ShadowTreeEvent extends Event
{
    XBLShadowTreeElement getXblShadowTree();
    
    void initShadowTreeEvent(final String p0, final boolean p1, final boolean p2, final XBLShadowTreeElement p3);
    
    void initShadowTreeEventNS(final String p0, final String p1, final boolean p2, final boolean p3, final XBLShadowTreeElement p4);
}
