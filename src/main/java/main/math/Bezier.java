// 
// Decompiled by Procyon v0.5.36
// 

package main.math;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public final class Bezier
{
    private final Point2D[] cps;
    
    public Bezier() {
        this.cps = new Point2D[4];
    }
    
    public Bezier(final List<Point2D> pts) {
        (this.cps = new Point2D[4])[0] = pts.get(0);
        this.cps[1] = pts.get(1);
        this.cps[2] = pts.get(2);
        this.cps[3] = pts.get(3);
    }
    
    public Bezier(final Point2D[] pts) {
        (this.cps = new Point2D[4])[0] = pts[0];
        this.cps[1] = pts[1];
        this.cps[2] = pts[2];
        this.cps[3] = pts[3];
    }
    
    public Bezier(final Float[][] pts) {
        (this.cps = new Point2D[4])[0] = new Point2D.Double(pts[0][0], pts[0][1]);
        this.cps[1] = new Point2D.Double(pts[1][0], pts[1][1]);
        this.cps[2] = new Point2D.Double(pts[2][0], pts[2][1]);
        this.cps[3] = new Point2D.Double(pts[3][0], pts[3][1]);
    }
    
    public Bezier(final Point2D ptA, final Point2D ptAperp, final Point2D ptB, final Point2D ptBperp) {
        (this.cps = new Point2D[4])[0] = ptA;
        this.cps[3] = ptB;
        final Vector vecA = new Vector(ptA, ptAperp);
        final Vector vecB = new Vector(ptB, ptBperp);
        vecA.normalise();
        vecB.normalise();
        final double distAB = MathRoutines.distance(ptA, ptB);
        final double off = 0.333 * distAB;
        final Point2D ptA2 = new Point2D.Double(ptA.getX() - off * vecA.y(), ptA.getY() + off * vecA.x());
        final Point2D ptA3 = new Point2D.Double(ptA.getX() + off * vecA.y(), ptA.getY() - off * vecA.x());
        final Point2D ptB2 = new Point2D.Double(ptB.getX() - off * vecB.y(), ptB.getY() + off * vecB.x());
        final Point2D ptB3 = new Point2D.Double(ptB.getX() + off * vecB.y(), ptB.getY() - off * vecB.x());
        if (MathRoutines.distance(ptA2, ptB) < MathRoutines.distance(ptA3, ptB)) {
            this.cps[1] = ptA2;
        }
        else {
            this.cps[1] = ptA3;
        }
        if (MathRoutines.distance(ptB2, ptA) < MathRoutines.distance(ptB3, ptA)) {
            this.cps[2] = ptB2;
        }
        else {
            this.cps[2] = ptB3;
        }
    }
    
    public Bezier(final Point ptA, final Point ptAperp, final Point ptB, final Point ptBperp) {
        this(new Point2D.Double(ptA.x, ptA.y), new Point2D.Double(ptAperp.x, ptAperp.y), new Point2D.Double(ptB.x, ptB.y), new Point2D.Double(ptBperp.x, ptBperp.y));
    }
    
    public Point2D[] cps() {
        return this.cps;
    }
    
    public double length() {
        return MathRoutines.distance(this.cps[0], this.cps[1]) + MathRoutines.distance(this.cps[1], this.cps[2]) + MathRoutines.distance(this.cps[2], this.cps[3]);
    }
    
    public Point2D midpoint() {
        final Point2D ab = new Point2D.Double((this.cps[0].getX() + this.cps[1].getX()) / 2.0, (this.cps[0].getY() + this.cps[1].getY()) / 2.0);
        final Point2D bc = new Point2D.Double((this.cps[1].getX() + this.cps[2].getX()) / 2.0, (this.cps[1].getY() + this.cps[2].getY()) / 2.0);
        final Point2D cd = new Point2D.Double((this.cps[2].getX() + this.cps[3].getX()) / 2.0, (this.cps[2].getY() + this.cps[3].getY()) / 2.0);
        final Point2D abbc = new Point2D.Double((ab.getX() + bc.getX()) / 2.0, (ab.getY() + bc.getY()) / 2.0);
        final Point2D bccd = new Point2D.Double((bc.getX() + cd.getX()) / 2.0, (bc.getY() + cd.getY()) / 2.0);
        return new Point2D.Double((abbc.getX() + bccd.getX()) / 2.0, (abbc.getY() + bccd.getY()) / 2.0);
    }
    
    public Point2D sample(final double t) {
        final Point2D ab = MathRoutines.lerp(t, this.cps[0], this.cps[1]);
        final Point2D bc = MathRoutines.lerp(t, this.cps[1], this.cps[2]);
        final Point2D cd = MathRoutines.lerp(t, this.cps[2], this.cps[3]);
        final Point2D abbc = MathRoutines.lerp(t, ab, bc);
        final Point2D bccd = MathRoutines.lerp(t, bc, cd);
        return MathRoutines.lerp(t, abbc, bccd);
    }
    
    public Rectangle2D bounds() {
        double x0 = 1000000.0;
        double y0 = 1000000.0;
        double x2 = -1000000.0;
        double y2 = -1000000.0;
        for (final Point2D pt : this.cps) {
            final double x3 = pt.getX();
            final double y3 = pt.getY();
            if (x3 < x0) {
                x0 = x3;
            }
            if (x3 > x2) {
                x2 = x3;
            }
            if (y3 < y0) {
                y0 = y3;
            }
            if (y3 > y2) {
                y2 = y3;
            }
        }
        return new Rectangle2D.Double(x0, y0, x2 - x0, y2 - y0);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Bezier:");
        for (final Point2D pt : this.cps) {
            sb.append(" (" + pt.getX() + "," + pt.getY() + ")");
        }
        return sb.toString();
    }
}
