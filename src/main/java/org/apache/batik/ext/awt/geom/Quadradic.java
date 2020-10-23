// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.geom;

import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

public class Quadradic extends AbstractSegment
{
    public Point2D.Double p1;
    public Point2D.Double p2;
    public Point2D.Double p3;
    static int count;
    
    public Quadradic() {
        this.p1 = new Point2D.Double();
        this.p2 = new Point2D.Double();
        this.p3 = new Point2D.Double();
    }
    
    public Quadradic(final double x1, final double y1, final double x2, final double y2, final double x3, final double y3) {
        this.p1 = new Point2D.Double(x1, y1);
        this.p2 = new Point2D.Double(x2, y2);
        this.p3 = new Point2D.Double(x3, y3);
    }
    
    public Quadradic(final Point2D.Double p1, final Point2D.Double p2, final Point2D.Double p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }
    
    public Object clone() {
        return new Quadradic(new Point2D.Double(this.p1.x, this.p1.y), new Point2D.Double(this.p2.x, this.p2.y), new Point2D.Double(this.p3.x, this.p3.y));
    }
    
    public Segment reverse() {
        return new Quadradic(new Point2D.Double(this.p3.x, this.p3.y), new Point2D.Double(this.p2.x, this.p2.y), new Point2D.Double(this.p1.x, this.p1.y));
    }
    
    private void getMinMax(final double p1, final double p2, final double p3, final double[] minMax) {
        if (p3 > p1) {
            minMax[0] = p1;
            minMax[1] = p3;
        }
        else {
            minMax[0] = p3;
            minMax[1] = p1;
        }
        final double a = p1 - 2.0 * p2 + p3;
        final double b = p3 - p2;
        if (a == 0.0) {
            return;
        }
        double tv = b / a;
        if (tv <= 0.0 || tv >= 1.0) {
            return;
        }
        tv = ((p1 - 2.0 * p2 + p3) * tv + 2.0 * (p2 - p1)) * tv + p1;
        if (tv < minMax[0]) {
            minMax[0] = tv;
        }
        else if (tv > minMax[1]) {
            minMax[1] = tv;
        }
    }
    
    @Override
    public double minX() {
        final double[] minMax = { 0.0, 0.0 };
        this.getMinMax(this.p1.x, this.p2.x, this.p3.x, minMax);
        return minMax[0];
    }
    
    @Override
    public double maxX() {
        final double[] minMax = { 0.0, 0.0 };
        this.getMinMax(this.p1.x, this.p2.x, this.p3.x, minMax);
        return minMax[1];
    }
    
    @Override
    public double minY() {
        final double[] minMax = { 0.0, 0.0 };
        this.getMinMax(this.p1.y, this.p2.y, this.p3.y, minMax);
        return minMax[0];
    }
    
    @Override
    public double maxY() {
        final double[] minMax = { 0.0, 0.0 };
        this.getMinMax(this.p1.y, this.p2.y, this.p3.y, minMax);
        return minMax[1];
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        final double[] minMaxX = { 0.0, 0.0 };
        this.getMinMax(this.p1.x, this.p2.x, this.p3.x, minMaxX);
        final double[] minMaxY = { 0.0, 0.0 };
        this.getMinMax(this.p1.y, this.p2.y, this.p3.y, minMaxY);
        return new Rectangle2D.Double(minMaxX[0], minMaxY[0], minMaxX[1] - minMaxX[0], minMaxY[1] - minMaxY[0]);
    }
    
    @Override
    protected int findRoots(final double y, final double[] roots) {
        final double[] eqn = { this.p1.y - y, 2.0 * (this.p2.y - this.p1.y), this.p1.y - 2.0 * this.p2.y + this.p3.y };
        return QuadCurve2D.solveQuadratic(eqn, roots);
    }
    
    @Override
    public Point2D.Double evalDt(final double t) {
        final double x = 2.0 * (this.p1.x - 2.0 * this.p2.x + this.p3.x) * t + 2.0 * (this.p2.x - this.p1.x);
        final double y = 2.0 * (this.p1.y - 2.0 * this.p2.y + this.p3.y) * t + 2.0 * (this.p2.y - this.p1.y);
        return new Point2D.Double(x, y);
    }
    
    @Override
    public Point2D.Double eval(final double t) {
        final double x = ((this.p1.x - 2.0 * this.p2.x + this.p3.x) * t + 2.0 * (this.p2.x - this.p1.x)) * t + this.p1.x;
        final double y = ((this.p1.y - 2.0 * this.p2.y + this.p3.y) * t + 2.0 * (this.p2.y - this.p1.y)) * t + this.p1.y;
        return new Point2D.Double(x, y);
    }
    
    @Override
    public Segment getSegment(final double t0, final double t1) {
        final double dt = t1 - t0;
        final Point2D.Double np1 = this.eval(t0);
        final Point2D.Double dp1 = this.evalDt(t0);
        final Point2D.Double np2 = new Point2D.Double(np1.x + 0.5 * dt * dp1.x, np1.y + 0.5 * dt * dp1.y);
        final Point2D.Double np3 = this.eval(t1);
        return new Quadradic(np1, np2, np3);
    }
    
    public void subdivide(final Quadradic q0, final Quadradic q1) {
        if (q0 == null && q1 == null) {
            return;
        }
        final double x = (this.p1.x - 2.0 * this.p2.x + this.p3.x) * 0.25 + (this.p2.x - this.p1.x) + this.p1.x;
        final double y = (this.p1.y - 2.0 * this.p2.y + this.p3.y) * 0.25 + (this.p2.y - this.p1.y) + this.p1.y;
        final double dx = (this.p1.x - 2.0 * this.p2.x + this.p3.x) * 0.25 + (this.p2.x - this.p1.x) * 0.5;
        final double dy = (this.p1.y - 2.0 * this.p2.y + this.p3.y) * 0.25 + (this.p2.y - this.p1.y) * 0.5;
        if (q0 != null) {
            q0.p1.x = this.p1.x;
            q0.p1.y = this.p1.y;
            q0.p2.x = x - dx;
            q0.p2.y = y - dy;
            q0.p3.x = x;
            q0.p3.y = y;
        }
        if (q1 != null) {
            q1.p1.x = x;
            q1.p1.y = y;
            q1.p2.x = x + dx;
            q1.p2.y = y + dy;
            q1.p3.x = this.p3.x;
            q1.p3.y = this.p3.y;
        }
    }
    
    public void subdivide(final double t, final Quadradic q0, final Quadradic q1) {
        final Point2D.Double np = this.eval(t);
        final Point2D.Double npd = this.evalDt(t);
        if (q0 != null) {
            q0.p1.x = this.p1.x;
            q0.p1.y = this.p1.y;
            q0.p2.x = np.x - npd.x * t * 0.5;
            q0.p2.y = np.y - npd.y * t * 0.5;
            q0.p3.x = np.x;
            q0.p3.y = np.y;
        }
        if (q1 != null) {
            q1.p1.x = np.x;
            q1.p1.y = np.y;
            q1.p2.x = np.x + npd.x * (1.0 - t) * 0.5;
            q1.p2.y = np.y + npd.y * (1.0 - t) * 0.5;
            q1.p3.x = this.p3.x;
            q1.p3.y = this.p3.y;
        }
    }
    
    @Override
    public void subdivide(final Segment s0, final Segment s1) {
        Quadradic q0 = null;
        Quadradic q2 = null;
        if (s0 instanceof Quadradic) {
            q0 = (Quadradic)s0;
        }
        if (s1 instanceof Quadradic) {
            q2 = (Quadradic)s1;
        }
        this.subdivide(q0, q2);
    }
    
    @Override
    public void subdivide(final double t, final Segment s0, final Segment s1) {
        Quadradic q0 = null;
        Quadradic q2 = null;
        if (s0 instanceof Quadradic) {
            q0 = (Quadradic)s0;
        }
        if (s1 instanceof Quadradic) {
            q2 = (Quadradic)s1;
        }
        this.subdivide(t, q0, q2);
    }
    
    protected double subLength(final double leftLegLen, final double rightLegLen, final double maxErr) {
        ++Quadradic.count;
        double dx = this.p3.x - this.p1.x;
        double dy = this.p3.y - this.p1.y;
        final double cordLen = Math.sqrt(dx * dx + dy * dy);
        final double hullLen = leftLegLen + rightLegLen;
        if (hullLen < maxErr) {
            return (hullLen + cordLen) * 0.5;
        }
        final double err = hullLen - cordLen;
        if (err < maxErr) {
            return (hullLen + cordLen) * 0.5;
        }
        final Quadradic q = new Quadradic();
        final double x = (this.p1.x + 2.0 * this.p2.x + this.p3.x) * 0.25;
        final double y = (this.p1.y + 2.0 * this.p2.y + this.p3.y) * 0.25;
        dx *= 0.25;
        dy *= 0.25;
        q.p1.x = this.p1.x;
        q.p1.y = this.p1.y;
        q.p2.x = x - dx;
        q.p2.y = y - dy;
        q.p3.x = x;
        q.p3.y = y;
        final double midLen = 0.25 * cordLen;
        double len = q.subLength(leftLegLen * 0.5, midLen, maxErr * 0.5);
        q.p1.x = x;
        q.p1.y = y;
        q.p2.x = x + dx;
        q.p2.y = y + dy;
        q.p3.x = this.p3.x;
        q.p3.y = this.p3.y;
        len += q.subLength(midLen, rightLegLen * 0.5, maxErr * 0.5);
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
        dx = this.p3.x - this.p2.x;
        dy = this.p3.y - this.p2.y;
        final double rightLegLen = Math.sqrt(dx * dx + dy * dy);
        final double eps = maxErr * (leftLegLen + rightLegLen);
        return this.subLength(leftLegLen, rightLegLen, eps);
    }
    
    @Override
    public String toString() {
        return "M" + this.p1.x + ',' + this.p1.y + 'Q' + this.p2.x + ',' + this.p2.y + ' ' + this.p3.x + ',' + this.p3.y;
    }
    
    static {
        Quadradic.count = 0;
    }
}
