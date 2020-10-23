// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.event;

import org.apache.batik.gvt.GraphicsNode;

public class GraphicsNodeKeyEvent extends GraphicsNodeInputEvent
{
    static final int KEY_FIRST = 400;
    public static final int KEY_TYPED = 400;
    public static final int KEY_PRESSED = 401;
    public static final int KEY_RELEASED = 402;
    protected int keyCode;
    protected char keyChar;
    protected int keyLocation;
    
    public GraphicsNodeKeyEvent(final GraphicsNode source, final int id, final long when, final int modifiers, final int lockState, final int keyCode, final char keyChar, final int keyLocation) {
        super(source, id, when, modifiers, lockState);
        this.keyCode = keyCode;
        this.keyChar = keyChar;
        this.keyLocation = keyLocation;
    }
    
    public int getKeyCode() {
        return this.keyCode;
    }
    
    public char getKeyChar() {
        return this.keyChar;
    }
    
    public int getKeyLocation() {
        return this.keyLocation;
    }
}
