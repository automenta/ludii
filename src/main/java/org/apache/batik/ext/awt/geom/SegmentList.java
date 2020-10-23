// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.geom;

import java.util.Iterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.util.LinkedList;
import java.util.List;

public class SegmentList
{
    List segments;
    
    public SegmentList() {
        this.segments = new LinkedList();
    }
    
    public SegmentList(final Shape s) {
        this.segments = new LinkedList();
        final PathIterator pi = s.getPathIterator(null);
        final float[] pts = new float[6];
        Point2D.Double loc = null;
        Point2D.Double openLoc = null;
        while (!pi.isDone()) {
            final int type = pi.currentSegment(pts);
            switch (type) {
                case 0: {
                    loc = (openLoc = new Point2D.Double(pts[0], pts[1]));
                    break;
                }
                case 1: {
                    final Point2D.Double p0 = new Point2D.Double(pts[0], pts[1]);
                    this.segments.add(new Linear(loc, p0));
                    loc = p0;
                    break;
                }
                case 2: {
                    final Point2D.Double p0 = new Point2D.Double(pts[0], pts[1]);
                    final Point2D.Double p2 = new Point2D.Double(pts[2], pts[3]);
                    this.segments.add(new Quadradic(loc, p0, p2));
                    loc = p2;
                    break;
                }
                case 3: {
                    final Point2D.Double p0 = new Point2D.Double(pts[0], pts[1]);
                    final Point2D.Double p2 = new Point2D.Double(pts[2], pts[3]);
                    final Point2D.Double p3 = new Point2D.Double(pts[4], pts[5]);
                    this.segments.add(new Cubic(loc, p0, p2, p3));
                    loc = p3;
                    break;
                }
                case 4: {
                    this.segments.add(new Linear(loc, openLoc));
                    loc = openLoc;
                    break;
                }
            }
            pi.next();
        }
    }
    
    public Rectangle2D getBounds2D() {
        final Iterator iter = this.iterator();
        if (!iter.hasNext()) {
            return null;
        }
        final Rectangle2D ret = (Rectangle2D)iter.next().getBounds2D().clone();
        while (iter.hasNext()) {
            final Segment seg = iter.next();
            final Rectangle2D segB = seg.getBounds2D();
            Rectangle2D.union(segB, ret, ret);
        }
        return ret;
    }
    
    public void add(final Segment s) {
        this.segments.add(s);
    }
    
    public Iterator iterator() {
        return this.segments.iterator();
    }
    
    public int size() {
        return this.segments.size();
    }
    
    public SplitResults split(final double y) {
        final Iterator iter = this.segments.iterator();
        final SegmentList above = new SegmentList();
        final SegmentList below = new SegmentList();
        while (iter.hasNext()) {
            final Segment seg = iter.next();
            final Segment.SplitResults results = seg.split(y);
            if (results == null) {
                final Rectangle2D bounds = seg.getBounds2D();
                if (bounds.getY() > y) {
                    below.add(seg);
                }
                else if (bounds.getY() == y) {
                    if (bounds.getHeight() == 0.0) {
                        continue;
                    }
                    below.add(seg);
                }
                else {
                    above.add(seg);
                }
            }
            else {
                final Segment[] arr$;
                final Segment[] resAbove = arr$ = results.getAbove();
                for (final Segment aResAbove : arr$) {
                    above.add(aResAbove);
                }
                final Segment[] arr$2;
                final Segment[] resBelow = arr$2 = results.getBelow();
                for (final Segment aResBelow : arr$2) {
                    below.add(aResBelow);
                }
            }
        }
        return new SplitResults(above, below);
    }
    
    public static class SplitResults
    {
        final SegmentList above;
        final SegmentList below;
        
        public SplitResults(final SegmentList above, final SegmentList below) {
            if (above != null && above.size() > 0) {
                this.above = above;
            }
            else {
                this.above = null;
            }
            if (below != null && below.size() > 0) {
                this.below = below;
            }
            else {
                this.below = null;
            }
        }
        
        public SegmentList getAbove() {
            return this.above;
        }
        
        public SegmentList getBelow() {
            return this.below;
        }
    }
}
