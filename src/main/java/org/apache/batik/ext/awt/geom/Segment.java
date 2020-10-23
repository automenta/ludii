// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.geom;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface Segment extends Cloneable
{
    double minX();
    
    double maxX();
    
    double minY();
    
    double maxY();
    
    Rectangle2D getBounds2D();
    
    Point2D.Double evalDt(final double p0);
    
    Point2D.Double eval(final double p0);
    
    Segment getSegment(final double p0, final double p1);
    
    Segment splitBefore(final double p0);
    
    Segment splitAfter(final double p0);
    
    void subdivide(final Segment p0, final Segment p1);
    
    void subdivide(final double p0, final Segment p1, final Segment p2);
    
    double getLength();
    
    double getLength(final double p0);
    
    SplitResults split(final double p0);
    
    public static class SplitResults
    {
        Segment[] above;
        Segment[] below;
        
        SplitResults(final Segment[] below, final Segment[] above) {
            this.below = below;
            this.above = above;
        }
        
        Segment[] getBelow() {
            return this.below;
        }
        
        Segment[] getAbove() {
            return this.above;
        }
    }
}
