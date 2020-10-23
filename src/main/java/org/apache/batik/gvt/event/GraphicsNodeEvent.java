// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.event;

import org.apache.batik.gvt.GraphicsNode;
import java.util.EventObject;

public class GraphicsNodeEvent extends EventObject
{
    private boolean consumed;
    protected int id;
    
    public GraphicsNodeEvent(final GraphicsNode source, final int id) {
        super(source);
        this.consumed = false;
        this.id = id;
    }
    
    public int getID() {
        return this.id;
    }
    
    public GraphicsNode getGraphicsNode() {
        return (GraphicsNode)this.source;
    }
    
    public void consume() {
        this.consumed = true;
    }
    
    public boolean isConsumed() {
        return this.consumed;
    }
}
