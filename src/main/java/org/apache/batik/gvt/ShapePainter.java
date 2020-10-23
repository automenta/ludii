// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.Graphics2D;

public interface ShapePainter
{
    void paint(final Graphics2D p0);
    
    Shape getPaintedArea();
    
    Rectangle2D getPaintedBounds2D();
    
    boolean inPaintedArea(final Point2D p0);
    
    Shape getSensitiveArea();
    
    Rectangle2D getSensitiveBounds2D();
    
    boolean inSensitiveArea(final Point2D p0);
    
    void setShape(final Shape p0);
    
    Shape getShape();
}
