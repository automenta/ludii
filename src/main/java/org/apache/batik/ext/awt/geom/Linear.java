// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.geom;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

public class Linear implements Segment
{
    public Point2D.Double p1;
    public Point2D.Double p2;
    
    public Linear() {
        this.p1 = new Point2D.Double();
        this.p2 = new Point2D.Double();
    }
    
    public Linear(final double x1, final double y1, final double x2, final double y2) {
        this.p1 = new Point2D.Double(x1, y1);
        this.p2 = new Point2D.Double(x2, y2);
    }
    
    public Linear(final Point2D.Double p1, final Point2D.Double p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
    
    public Object clone() {
        return new Linear(new Point2D.Double(this.p1.x, this.p1.y), new Point2D.Double(this.p2.x, this.p2.y));
    }
    
    public Segment reverse() {
        return new Linear(new Point2D.Double(this.p2.x, this.p2.y), new Point2D.Double(this.p1.x, this.p1.y));
    }
    
    @Override
    public double minX() {
        if (this.p1.x < this.p2.x) {
            return this.p1.x;
        }
        return this.p2.x;
    }
    
    @Override
    public double maxX() {
        if (this.p1.x > this.p2.x) {
            return this.p1.x;
        }
        return this.p2.x;
    }
    
    @Override
    public double minY() {
        if (this.p1.y < this.p2.y) {
            return this.p1.y;
        }
        return this.p2.y;
    }
    
    @Override
    public double maxY() {
        if (this.p1.y > this.p2.y) {
            return this.p2.y;
        }
        return this.p1.y;
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        double x;
        double w;
        if (this.p1.x < this.p2.x) {
            x = this.p1.x;
            w = this.p2.x - this.p1.x;
        }
        else {
            x = this.p2.x;
            w = this.p1.x - this.p2.x;
        }
        double y;
        double h;
        if (this.p1.y < this.p2.y) {
            y = this.p1.y;
            h = this.p2.y - this.p1.y;
        }
        else {
            y = this.p2.y;
            h = this.p1.y - this.p2.y;
        }
        return new Rectangle2D.Double(x, y, w, h);
    }
    
    @Override
    public Point2D.Double evalDt(final double t) {
        final double x = this.p2.x - this.p1.x;
        final double y = this.p2.y - this.p1.y;
        return new Point2D.Double(x, y);
    }
    
    @Override
    public Point2D.Double eval(final double t) {
        final double x = this.p1.x + t * (this.p2.x - this.p1.x);
        final double y = this.p1.y + t * (this.p2.y - this.p1.y);
        return new Point2D.Double(x, y);
    }
    
    @Override
    public SplitResults split(final double y) {
        if (y == this.p1.y || y == this.p2.y) {
            return null;
        }
        if (y <= this.p1.y && y <= this.p2.y) {
            return null;
        }
        if (y >= this.p1.y && y >= this.p2.y) {
            return null;
        }
        final double t = (y - this.p1.y) / (this.p2.y - this.p1.y);
        final Segment[] t2 = { this.getSegment(0.0, t) };
        final Segment[] t3 = { this.getSegment(t, 1.0) };
        if (this.p2.y < y) {
            return new SplitResults(t2, t3);
        }
        return new SplitResults(t3, t2);
    }
    
    @Override
    public Segment getSegment(final double t0, final double t1) {
        final Point2D.Double np1 = this.eval(t0);
        final Point2D.Double np2 = this.eval(t1);
        return new Linear(np1, np2);
    }
    
    @Override
    public Segment splitBefore(final double t) {
        return new Linear(this.p1, this.eval(t));
    }
    
    @Override
    public Segment splitAfter(final double t) {
        return new Linear(this.eval(t), this.p2);
    }
    
    @Override
    public void subdivide(final Segment s0, final Segment s1) {
        Linear l0 = null;
        Linear l2 = null;
        if (s0 instanceof Linear) {
            l0 = (Linear)s0;
        }
        if (s1 instanceof Linear) {
            l2 = (Linear)s1;
        }
        this.subdivide(l0, l2);
    }
    
    @Override
    public void subdivide(final double t, final Segment s0, final Segment s1) {
        Linear l0 = null;
        Linear l2 = null;
        if (s0 instanceof Linear) {
            l0 = (Linear)s0;
        }
        if (s1 instanceof Linear) {
            l2 = (Linear)s1;
        }
        this.subdivide(t, l0, l2);
    }
    
    public void subdivide(final Linear l0, final Linear l1) {
        if (l0 == null && l1 == null) {
            return;
        }
        final double x = (this.p1.x + this.p2.x) * 0.5;
        final double y = (this.p1.y + this.p2.y) * 0.5;
        if (l0 != null) {
            l0.p1.x = this.p1.x;
            l0.p1.y = this.p1.y;
            l0.p2.x = x;
            l0.p2.y = y;
        }
        if (l1 != null) {
            l1.p1.x = x;
            l1.p1.y = y;
            l1.p2.x = this.p2.x;
            l1.p2.y = this.p2.y;
        }
    }
    
    public void subdivide(final double t, final Linear l0, final Linear l1) {
        if (l0 == null && l1 == null) {
            return;
        }
        final double x = this.p1.x + t * (this.p2.x - this.p1.x);
        final double y = this.p1.y + t * (this.p2.y - this.p1.y);
        if (l0 != null) {
            l0.p1.x = this.p1.x;
            l0.p1.y = this.p1.y;
            l0.p2.x = x;
            l0.p2.y = y;
        }
        if (l1 != null) {
            l1.p1.x = x;
            l1.p1.y = y;
            l1.p2.x = this.p2.x;
            l1.p2.y = this.p2.y;
        }
    }
    
    @Override
    public double getLength() {
        final double dx = this.p2.x - this.p1.x;
        final double dy = this.p2.y - this.p1.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public double getLength(final double maxErr) {
        return this.getLength();
    }
    
    @Override
    public String toString() {
        return "M" + this.p1.x + ',' + this.p1.y + 'L' + this.p2.x + ',' + this.p2.y;
    }
}
