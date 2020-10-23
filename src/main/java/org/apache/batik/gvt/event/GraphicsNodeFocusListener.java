// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.event;

import java.util.EventListener;

public interface GraphicsNodeFocusListener extends EventListener
{
    void focusGained(final GraphicsNodeFocusEvent p0);
    
    void focusLost(final GraphicsNodeFocusEvent p0);
}
