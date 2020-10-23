// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views;

import app.DesktopApp;

import java.awt.*;

public abstract class View
{
    protected Rectangle placement;
    private final boolean debug = false;
    
    public View() {
        this.placement = new Rectangle(0, 0, DesktopApp.view().getWidth(), DesktopApp.view().getHeight());
    }
    
    public Rectangle placement() {
        return this.placement;
    }
    
    public void setPlacement(final Rectangle rect) {
        this.placement = (Rectangle)rect.clone();
    }
    
    public abstract void paint(final Graphics2D p0);
    
    public void paintDebug(final Graphics2D g2d, final Color colour) {
    }
    
    public int containerIndex() {
        return -1;
    }
    
    public void mouseOverAt(final Point pt) {
    }
}
