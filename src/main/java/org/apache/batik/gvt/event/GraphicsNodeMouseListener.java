// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.event;

import java.util.EventListener;

public interface GraphicsNodeMouseListener extends EventListener
{
    void mouseClicked(final GraphicsNodeMouseEvent p0);
    
    void mousePressed(final GraphicsNodeMouseEvent p0);
    
    void mouseReleased(final GraphicsNodeMouseEvent p0);
    
    void mouseEntered(final GraphicsNodeMouseEvent p0);
    
    void mouseExited(final GraphicsNodeMouseEvent p0);
    
    void mouseDragged(final GraphicsNodeMouseEvent p0);
    
    void mouseMoved(final GraphicsNodeMouseEvent p0);
}
