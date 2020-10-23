// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.event;

import org.apache.batik.gvt.GraphicsNode;

public class GraphicsNodeMouseWheelEvent extends GraphicsNodeInputEvent
{
    public static final int MOUSE_WHEEL = 600;
    protected int wheelDelta;
    
    public GraphicsNodeMouseWheelEvent(final GraphicsNode source, final int id, final long when, final int modifiers, final int lockState, final int wheelDelta) {
        super(source, id, when, modifiers, lockState);
        this.wheelDelta = wheelDelta;
    }
    
    public int getWheelDelta() {
        return this.wheelDelta;
    }
}
