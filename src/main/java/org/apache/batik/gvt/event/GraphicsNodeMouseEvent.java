// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.event;

import java.awt.geom.Point2D;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import org.apache.batik.gvt.GraphicsNode;

public class GraphicsNodeMouseEvent extends GraphicsNodeInputEvent
{
    static final int MOUSE_FIRST = 500;
    public static final int MOUSE_CLICKED = 500;
    public static final int MOUSE_PRESSED = 501;
    public static final int MOUSE_RELEASED = 502;
    public static final int MOUSE_MOVED = 503;
    public static final int MOUSE_ENTERED = 504;
    public static final int MOUSE_EXITED = 505;
    public static final int MOUSE_DRAGGED = 506;
    float x;
    float y;
    int clientX;
    int clientY;
    int screenX;
    int screenY;
    int clickCount;
    int button;
    GraphicsNode relatedNode;
    
    public GraphicsNodeMouseEvent(final GraphicsNode source, final int id, final long when, final int modifiers, final int lockState, final int button, final float x, final float y, final int clientX, final int clientY, final int screenX, final int screenY, final int clickCount, final GraphicsNode relatedNode) {
        super(source, id, when, modifiers, lockState);
        this.relatedNode = null;
        this.button = button;
        this.x = x;
        this.y = y;
        this.clientX = clientX;
        this.clientY = clientY;
        this.screenX = screenX;
        this.screenY = screenY;
        this.clickCount = clickCount;
        this.relatedNode = relatedNode;
    }
    
    public GraphicsNodeMouseEvent(final GraphicsNode source, final MouseEvent evt, final int button, final int lockState) {
        super(source, evt, lockState);
        this.relatedNode = null;
        this.button = button;
        this.x = (float)evt.getX();
        this.y = (float)evt.getY();
        this.clickCount = evt.getClickCount();
    }
    
    public int getButton() {
        return this.button;
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getClientX() {
        return (float)this.clientX;
    }
    
    public float getClientY() {
        return (float)this.clientY;
    }
    
    public int getScreenX() {
        return this.screenX;
    }
    
    public int getScreenY() {
        return this.screenY;
    }
    
    public Point getScreenPoint() {
        return new Point(this.screenX, this.screenY);
    }
    
    public Point getClientPoint() {
        return new Point(this.clientX, this.clientY);
    }
    
    public Point2D getPoint2D() {
        return new Point2D.Float(this.x, this.y);
    }
    
    public int getClickCount() {
        return this.clickCount;
    }
    
    public GraphicsNode getRelatedNode() {
        return this.relatedNode;
    }
}
