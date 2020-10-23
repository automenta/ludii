// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.geom;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Point2D;

public class Cubic extends AbstractSegment
{
    public Point2D.Double p1;
    public Point2D.Double p2;
    public Point2D.Double p3;
    public Point2D.Double p4;
    private static int count;
    
    public Cubic() {
        this.p1 = new Point2D.Double();
        this.p2 = new Point2D.Double();
        this.p3 = new Point2D.Double();
        this.p4 = new Point2D.Double();
    }
    
    public Cubic(final double x1, final double y1, final double x2, final double y2, final double x3, final double y3, final double x4, final double y4) {
        this.p1 = new Point2D.Double(x1, y1);
        this.p2 = new Point2D.Double(x2, y2);
        this.p3 = new Point2D.Double(x3, y3);
        this.p4 = new Point2D.Double(x4, y4);
    }
    
    public Cubic(final Point2D.Double p1, final Point2D.Double p2, final Point2D.Double p3, final Point2D.Double p4) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
    }
    
    public Object clone() {
        return new Cubic(new Point2D.Double(this.p1.x, this.p1.y), new Point2D.Double(this.p2.x, this.p2.y), new Point2D.Double(this.p3.x, this.p3.y), new Point2D.Double(this.p4.x, this.p4.y));
    }
    
    public Segment reverse() {
        return new Cubic(new Point2D.Double(this.p4.x, this.p4.y), new Point2D.Double(this.p3.x, this.p3.y), new Point2D.Double(this.p2.x, this.p2.y), new Point2D.Double(this.p1.x, this.p1.y));
    }
    
    private void getMinMax(final double p1, final double p2, final double p3, final double p4, final double[] minMax) {
        if (p4 > p1) {
            minMax[0] = p1;
            minMax[1] = p4;
        }
        else {
            minMax[0] = p4;
            minMax[1] = p1;
        }
        final double c0 = 3.0 * (p2 - p1);
        final double c2 = 6.0 * (p3 - p2);
        final double c3 = 3.0 * (p4 - p3);
        final double[] eqn = { c0, c2 - 2.0 * c0, c3 - c2 + c0 };
        for (int roots = QuadCurve2D.solveQuadratic(eqn), r = 0; r < roots; ++r) {
            double tv = eqn[r];
            if (tv > 0.0) {
                if (tv < 1.0) {
                    tv = (1.0 - tv) * (1.0 - tv) * (1.0 - tv) * p1 + 3.0 * tv * (1.0 - tv) * (1.0 - tv) * p2 + 3.0 * tv * tv * (1.0 - tv) * p3 + tv * tv * tv * p4;
                    if (tv < minMax[0]) {
                        minMax[0] = tv;
                    }
                    else if (tv > minMax[1]) {
                        minMax[1] = tv;
                    }
                }
            }
        }
    }
    
    @Override
    public double minX() {
        final double[] minMax = { 0.0, 0.0 };
        this.getMinMax(this.p1.x, this.p2.x, this.p3.x, this.p4.x, minMax);
        return minMax[0];
    }
    
    @Override
    public double maxX() {
        final double[] minMax = { 0.0, 0.0 };
        this.getMinMax(this.p1.x, this.p2.x, this.p3.x, this.p4.x, minMax);
        return minMax[1];
    }
    
    @Override
    public double minY() {
        final double[] minMax = { 0.0, 0.0 };
        this.getMinMax(this.p1.y, this.p2.y, this.p3.y, this.p4.y, minMax);
        return minMax[0];
    }
    
    @Override
    public double maxY() {
        final double[] minMax = { 0.0, 0.0 };
        this.getMinMax(this.p1.y, this.p2.y, this.p3.y, this.p4.y, minMax);
        return minMax[1];
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        final double[] minMaxX = { 0.0, 0.0 };
        this.getMinMax(this.p1.x, this.p2.x, this.p3.x, this.p4.x, minMaxX);
        final double[] minMaxY = { 0.0, 0.0 };
        this.getMinMax(this.p1.y, this.p2.y, this.p3.y, this.p4.y, minMaxY);
        return new Rectangle2D.Double(minMaxX[0], minMaxY[0], minMaxX[1] - minMaxX[0], minMaxY[1] - minMaxY[0]);
    }
    
    @Override
    protected int findRoots(final double y, final double[] roots) {
        final double[] eqn = { this.p1.y - y, 3.0 * (this.p2.y - this.p1.y), 3.0 * (this.p1.y - 2.0 * this.p2.y + this.p3.y), 3.0 * this.p2.y - this.p1.y + this.p4.y - 3.0 * this.p3.y };
        return CubicCurve2D.solveCubic(eqn, roots);
    }
    
    @Override
    public Point2D.Double evalDt(final double t) {
        final double x = 3.0 * ((this.p2.x - this.p1.x) * (1.0 - t) * (1.0 - t) + 2.0 * (this.p3.x - this.p2.x) * (1.0 - t) * t + (this.p4.x - this.p3.x) * t * t);
        final double y = 3.0 * ((this.p2.y - this.p1.y) * (1.0 - t) * (1.0 - t) + 2.0 * (this.p3.y - this.p2.y) * (1.0 - t) * t + (this.p4.y - this.p3.y) * t * t);
        return new Point2D.Double(x, y);
    }
    
    @Override
    public Point2D.Double eval(final double t) {
        final double x = (1.0 - t) * (1.0 - t) * (1.0 - t) * this.p1.x + 3.0 * (t * (1.0 - t) * (1.0 - t) * this.p2.x + t * t * (1.0 - t) * this.p3.x) + t * t * t * this.p4.x;
        final double y = (1.0 - t) * (1.0 - t) * (1.0 - t) * this.p1.y + 3.0 * (t * (1.0 - t) * (1.0 - t) * this.p2.y + t * t * (1.0 - t) * this.p3.y) + t * t * t * this.p4.y;
        return new Point2D.Double(x, y);
    }
    
    @Override
    public void subdivide(final Segment s0, final Segment s1) {
        Cubic c0 = null;
        Cubic c2 = null;
        if (s0 instanceof Cubic) {
            c0 = (Cubic)s0;
        }
        if (s1 instanceof Cubic) {
            c2 = (Cubic)s1;
        }
        this.subdivide(c0, c2);
    }
    
    @Override
    public void subdivide(final double t, final Segment s0, final Segment s1) {
        Cubic c0 = null;
        Cubic c2 = null;
        if (s0 instanceof Cubic) {
            c0 = (Cubic)s0;
        }
        if (s1 instanceof Cubic) {
            c2 = (Cubic)s1;
        }
        this.subdivide(t, c0, c2);
    }
    
    public void subdivide(final Cubic c0, final Cubic c1) {
        if (c0 == null && c1 == null) {
            return;
        }
        final double npX = (this.p1.x + 3.0 * (this.p2.x + this.p3.x) + this.p4.x) * 0.125;
        final double npY = (this.p1.y + 3.0 * (this.p2.y + this.p3.y) + this.p4.y) * 0.125;
        final double npdx = (this.p2.x - this.p1.x + 2.0 * (this.p3.x - this.p2.x) + (this.p4.x - this.p3.x)) * 0.125;
        final double npdy = (this.p2.y - this.p1.y + 2.0 * (this.p3.y - this.p2.y) + (this.p4.y - this.p3.y)) * 0.125;
        if (c0 != null) {
            c0.p1.x = this.p1.x;
            c0.p1.y = this.p1.y;
            c0.p2.x = (this.p2.x + this.p1.x) * 0.5;
            c0.p2.y = (this.p2.y + this.p1.y) * 0.5;
            c0.p3.x = npX - npdx;
            c0.p3.y = npY - npdy;
            c0.p4.x = npX;
            c0.p4.y = npY;
        }
        if (c1 != null) {
            c1.p1.x = npX;
            c1.p1.y = npY;
            c1.p2.x = npX + npdx;
            c1.p2.y = npY + npdy;
            c1.p3.x = (this.p4.x + this.p3.x) * 0.5;
            c1.p3.y = (this.p4.y + this.p3.y) * 0.5;
            c1.p4.x = this.p4.x;
            c1.p4.y = this.p4.y;
        }
    }
    
    public void subdivide(final double t, final Cubic c0, final Cubic c1) {
        if (c0 == null && c1 == null) {
            return;
        }
        final Point2D.Double np = this.eval(t);
        final Point2D.Double npd = this.evalDt(t);
        if (c0 != null) {
            c0.p1.x = this.p1.x;
            c0.p1.y = this.p1.y;
            c0.p2.x = this.p1.x + (this.p2.x - this.p1.x) * t;
            c0.p2.y = this.p1.y + (this.p2.y - this.p1.y) * t;
            c0.p3.x = np.x - npd.x * t / 3.0;
            c0.p3.y = np.y - npd.y * t / 3.0;
            c0.p4.x = np.x;
            c0.p4.y = np.y;
        }
        if (c1 != null) {
            c1.p1.x = np.x;
            c1.p1.y = np.y;
            c1.p2.x = np.x + npd.x * (1.0 - t) / 3.0;
            c1.p2.y = np.y + npd.y * (1.0 - t) / 3.0;
            c1.p3.x = this.p4.x + (this.p3.x - this.p4.x) * (1.0 - t);
            c1.p3.y = this.p4.y + (this.p3.y - this.p4.y) * (1.0 - t);
            c1.p4.x = this.p4.x;
            c1.p4.y = this.p4.y;
        }
    }
    
    @Override
    public Segment getSegment(final double t0, final double t1) {
        final double dt = t1 - t0;
        final Point2D.Double np1 = this.eval(t0);
        final Point2D.Double dp1 = this.evalDt(t0);
        final Point2D.Double np2 = new Point2D.Double(np1.x + dt * dp1.x / 3.0, np1.y + dt * dp1.y / 3.0);
        final Point2D.Double np3 = this.eval(t1);
        final Point2D.Double dp2 = this.evalDt(t1);
        final Point2D.Double np4 = new Point2D.Double(np3.x - dt * dp2.x / 3.0, np3.y - dt * dp2.y / 3.0);
        return new Cubic(np1, np2, np4, np3);
    }
    
    protected double subLength(final double leftLegLen, final double rightLegLen, final double maxErr) {
        ++Cubic.count;
        final double cldx = this.p3.x - this.p2.x;
        final double cldy = this.p3.y - this.p2.y;
        final double crossLegLen = Math.sqrt(cldx * cldx + cldy * cldy);
        final double cdx = this.p4.x - this.p1.x;
        final double cdy = this.p4.y - this.p1.y;
        final double cordLen = Math.sqrt(cdx * cdx + cdy * cdy);
        final double hullLen = leftLegLen + rightLegLen + crossLegLen;
        if (hullLen < maxErr) {
            return (hullLen + cordLen) / 2.0;
        }
        final double err = hullLen - cordLen;
        if (err < maxErr) {
            return (hullLen + cordLen) / 2.0;
        }
        final Cubic c = new Cubic();
        final double npX = (this.p1.x + 3.0 * (this.p2.x + this.p3.x) + this.p4.x) * 0.125;
        final double npY = (this.p1.y + 3.0 * (this.p2.y + this.p3.y) + this.p4.y) * 0.125;
        final double npdx = (cldx + cdx) * 0.125;
        final double npdy = (cldy + cdy) * 0.125;
        c.p1.x = this.p1.x;
        c.p1.y = this.p1.y;
        c.p2.x = (this.p2.x + this.p1.x) * 0.5;
        c.p2.y = (this.p2.y + this.p1.y) * 0.5;
        c.p3.x = npX - npdx;
        c.p3.y = npY - npdy;
        c.p4.x = npX;
        c.p4.y = npY;
        final double midLen = Math.sqrt(npdx * npdx + npdy * npdy);
        double len = c.subLength(leftLegLen / 2.0, midLen, maxErr / 2.0);
        c.p1.x = npX;
        c.p1.y = npY;
        c.p2.x = npX + npdx;
        c.p2.y = npY + npdy;
        c.p3.x = (this.p4.x + this.p3.x) * 0.5;
        c.p3.y = (this.p4.y + this.p3.y) * 0.5;
        c.p4.x = this.p4.x;
        c.p4.y = this.p4.y;
        len += c.subLength(midLen, rightLegLen / 2.0, maxErr / 2.0);
        return len;
    }
    
    @Override
    public double getLength() {
        return this.getLength(1.0E-6);
    }
    
    @Override
    public double getLength(final double maxErr) {
        double dx = this.p2.x - this.p1.x;
        double dy = this.p2.y - this.p1.y;
        final double leftLegLen = Math.sqrt(dx * dx + dy * dy);
        dx = this.p4.x - this.p3.x;
        dy = this.p4.y - this.p3.y;
        final double rightLegLen = Math.sqrt(dx * dx + dy * dy);
        dx = this.p3.x - this.p2.x;
        dy = this.p3.y - this.p2.y;
        final double crossLegLen = Math.sqrt(dx * dx + dy * dy);
        final double eps = maxErr * (leftLegLen + rightLegLen + crossLegLen);
        return this.subLength(leftLegLen, rightLegLen, eps);
    }
    
    @Override
    public String toString() {
        return "M" + this.p1.x + ',' + this.p1.y + 'C' + this.p2.x + ',' + this.p2.y + ' ' + this.p3.x + ',' + this.p3.y + ' ' + this.p4.x + ',' + this.p4.y;
    }
    
    static {
        Cubic.count = 0;
    }
}
