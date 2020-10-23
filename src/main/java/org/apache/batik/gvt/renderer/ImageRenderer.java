// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.renderer;

import java.util.Collection;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

public interface ImageRenderer extends Renderer
{
    void dispose();
    
    void updateOffScreen(final int p0, final int p1);
    
    void setTransform(final AffineTransform p0);
    
    AffineTransform getTransform();
    
    void setRenderingHints(final RenderingHints p0);
    
    RenderingHints getRenderingHints();
    
    BufferedImage getOffScreen();
    
    void clearOffScreen();
    
    void flush();
    
    void flush(final Rectangle p0);
    
    void flush(final Collection p0);
}
