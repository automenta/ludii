// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.event;

import java.util.EventListener;

public interface GraphicsNodeKeyListener extends EventListener
{
    void keyPressed(final GraphicsNodeKeyEvent p0);
    
    void keyReleased(final GraphicsNodeKeyEvent p0);
    
    void keyTyped(final GraphicsNodeKeyEvent p0);
}
