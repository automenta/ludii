// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.event;

import java.awt.event.InputEvent;
import java.util.EventListener;
import java.util.EventObject;
import java.awt.geom.AffineTransform;
import org.apache.batik.gvt.GraphicsNode;

public interface EventDispatcher
{
    void setRootNode(final GraphicsNode p0);
    
    GraphicsNode getRootNode();
    
    void setBaseTransform(final AffineTransform p0);
    
    AffineTransform getBaseTransform();
    
    void dispatchEvent(final EventObject p0);
    
    void addGraphicsNodeMouseListener(final GraphicsNodeMouseListener p0);
    
    void removeGraphicsNodeMouseListener(final GraphicsNodeMouseListener p0);
    
    void addGraphicsNodeMouseWheelListener(final GraphicsNodeMouseWheelListener p0);
    
    void removeGraphicsNodeMouseWheelListener(final GraphicsNodeMouseWheelListener p0);
    
    void addGraphicsNodeKeyListener(final GraphicsNodeKeyListener p0);
    
    void removeGraphicsNodeKeyListener(final GraphicsNodeKeyListener p0);
    
    EventListener[] getListeners(final Class p0);
    
    void setNodeIncrementEvent(final InputEvent p0);
    
    void setNodeDecrementEvent(final InputEvent p0);
}
