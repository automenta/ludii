// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.renderable.RenderableImage;

public interface Filter extends RenderableImage
{
    Rectangle2D getBounds2D();
    
    long getTimeStamp();
    
    Shape getDependencyRegion(final int p0, final Rectangle2D p1);
    
    Shape getDirtyRegion(final int p0, final Rectangle2D p1);
}
