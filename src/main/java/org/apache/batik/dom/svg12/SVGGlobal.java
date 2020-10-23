// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg12;

import org.w3c.dom.events.EventTarget;

public interface SVGGlobal extends Global
{
    void startMouseCapture(final EventTarget p0, final boolean p1, final boolean p2);
    
    void stopMouseCapture();
}
