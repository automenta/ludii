// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;

public interface CachableRed extends RenderedImage
{
    Rectangle getBounds();
    
    Shape getDependencyRegion(final int p0, final Rectangle p1);
    
    Shape getDirtyRegion(final int p0, final Rectangle p1);
}
