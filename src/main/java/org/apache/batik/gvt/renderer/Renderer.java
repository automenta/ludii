// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.renderer;

import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.geom.RectListManager;
import java.awt.Shape;
import org.apache.batik.gvt.GraphicsNode;

public interface Renderer
{
    void setTree(final GraphicsNode p0);
    
    GraphicsNode getTree();
    
    void repaint(final Shape p0);
    
    void repaint(final RectListManager p0);
    
    void setTransform(final AffineTransform p0);
    
    AffineTransform getTransform();
    
    boolean isDoubleBuffered();
    
    void setDoubleBuffered(final boolean p0);
    
    void dispose();
}
