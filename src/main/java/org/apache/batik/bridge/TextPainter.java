// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.Graphics2D;

public interface TextPainter
{
    void paint(final TextNode p0, final Graphics2D p1);
    
    Mark selectAt(final double p0, final double p1, final TextNode p2);
    
    Mark selectTo(final double p0, final double p1, final Mark p2);
    
    Mark selectFirst(final TextNode p0);
    
    Mark selectLast(final TextNode p0);
    
    Mark getMark(final TextNode p0, final int p1, final boolean p2);
    
    int[] getSelected(final Mark p0, final Mark p1);
    
    Shape getHighlightShape(final Mark p0, final Mark p1);
    
    Shape getOutline(final TextNode p0);
    
    Rectangle2D getBounds2D(final TextNode p0);
    
    Rectangle2D getGeometryBounds(final TextNode p0);
}
