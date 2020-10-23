// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.Rectangle2D;
import java.awt.Paint;

public interface FloodRable extends Filter
{
    void setFloodPaint(final Paint p0);
    
    Paint getFloodPaint();
    
    void setFloodRegion(final Rectangle2D p0);
    
    Rectangle2D getFloodRegion();
}
