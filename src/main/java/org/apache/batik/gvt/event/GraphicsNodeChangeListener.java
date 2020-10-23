// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.event;

import java.util.EventListener;

public interface GraphicsNodeChangeListener extends EventListener
{
    void changeStarted(final GraphicsNodeChangeEvent p0);
    
    void changeCompleted(final GraphicsNodeChangeEvent p0);
}
