/*
 * Decompiled with CFR 0.150.
 */
package math;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public final class Bezier {
    private final Point2D[] cps = new Point2D[4];

    public Bezier() {
    }

    public Bezier(List<Point2D> pts) {
        this.cps[0] = pts.get(0);
        this.cps[1] = pts.get(1);
        this.cps[2] = pts.get(2);
        this.cps[3] = pts.get(3);
    }

    public Bezier(Point2D[] pts) {
        this.cps[0] = pts[0];
        this.cps[1] = pts[1];
        this.cps[2] = pts[2];
        this.cps[3] = pts[3];
    }

    public Bezier(Float[][] pts) {
        this.cps[0] = new Point2D.Double(pts[0][0], pts[0][1]);
        this.cps[1] = new Point2D.Double(pts[1][0], pts[1][1]);
        this.cps[2] = new Point2D.Double(pts[2][0], pts[2][1]);
        this.cps[3] = new Point2D.Double(pts[3][0], pts[3][1]);
    }

    public Bezier(Point2D ptA, Point2D ptAperp, Point2D ptB, Point2D ptBperp) {
        this.cps[0] = ptA;
        this.cps[3] = ptB;
        Vector vecA = new Vector(ptA, ptAperp);
        Vector vecB = new Vector(ptB, ptBperp);
        vecA.normalise();
        vecB.normalise();
        double distAB = MathRoutines.distance(ptA, ptB);
        double off = 0.333 * distAB;
        Point2D.Double ptA1 = new Point2D.Double(ptA.getX() - off * vecA.y(), ptA.getY() + off * vecA.x());
        Point2D.Double ptA2 = new Point2D.Double(ptA.getX() + off * vecA.y(), ptA.getY() - off * vecA.x());
        Point2D.Double ptB1 = new Point2D.Double(ptB.getX() - off * vecB.y(), ptB.getY() + off * vecB.x());
        Point2D.Double ptB2 = new Point2D.Double(ptB.getX() + off * vecB.y(), ptB.getY() - off * vecB.x());
        this.cps[1] = MathRoutines.distance(ptA1, ptB) < MathRoutines.distance(ptA2, ptB) ? ptA1 : ptA2;
        this.cps[2] = MathRoutines.distance(ptB1, ptA) < MathRoutines.distance(ptB2, ptA) ? ptB1 : ptB2;
    }

    public Bezier(Point ptA, Point ptAperp, Point ptB, Point ptBperp) {
        this(new Point2D.Double(ptA.x, ptA.y), new Point2D.Double(ptAperp.x, ptAperp.y), new Point2D.Double(ptB.x, ptB.y), new Point2D.Double(ptBperp.x, ptBperp.y));
    }

    public Point2D[] cps() {
        return this.cps;
    }

    public double length() {
        return MathRoutines.distance(this.cps[0], this.cps[1]) + MathRoutines.distance(this.cps[1], this.cps[2]) + MathRoutines.distance(this.cps[2], this.cps[3]);
    }

    public Point2D midpoint() {
        Point2D.Double ab = new Point2D.Double((this.cps[0].getX() + this.cps[1].getX()) / 2.0, (this.cps[0].getY() + this.cps[1].getY()) / 2.0);
        Point2D.Double bc = new Point2D.Double((this.cps[1].getX() + this.cps[2].getX()) / 2.0, (this.cps[1].getY() + this.cps[2].getY()) / 2.0);
        Point2D.Double cd = new Point2D.Double((this.cps[2].getX() + this.cps[3].getX()) / 2.0, (this.cps[2].getY() + this.cps[3].getY()) / 2.0);
        Point2D.Double abbc = new Point2D.Double((((Point2D)ab).getX() + ((Point2D)bc).getX()) / 2.0, (((Point2D)ab).getY() + ((Point2D)bc).getY()) / 2.0);
        Point2D.Double bccd = new Point2D.Double((((Point2D)bc).getX() + ((Point2D)cd).getX()) / 2.0, (((Point2D)bc).getY() + ((Point2D)cd).getY()) / 2.0);
        return new Point2D.Double((((Point2D)abbc).getX() + ((Point2D)bccd).getX()) / 2.0, (((Point2D)abbc).getY() + ((Point2D)bccd).getY()) / 2.0);
    }

    public Point2D sample(double t) {
        Point2D ab = MathRoutines.lerp(t, this.cps[0], this.cps[1]);
        Point2D bc = MathRoutines.lerp(t, this.cps[1], this.cps[2]);
        Point2D cd = MathRoutines.lerp(t, this.cps[2], this.cps[3]);
        Point2D abbc = MathRoutines.lerp(t, ab, bc);
        Point2D bccd = MathRoutines.lerp(t, bc, cd);
        return MathRoutines.lerp(t, abbc, bccd);
    }

    public Rectangle2D bounds() {
        double x0 = 1000000.0;
        double y0 = 1000000.0;
        double x1 = -1000000.0;
        double y1 = -1000000.0;
        for (Point2D pt : this.cps) {
            double x = pt.getX();
            double y = pt.getY();
            if (x < x0) {
                x0 = x;
            }
            if (x > x1) {
                x1 = x;
            }
            if (y < y0) {
                y0 = y;
            }
            if (!(y > y1)) continue;
            y1 = y;
        }
        return new Rectangle2D.Double(x0, y0, x1 - x0, y1 - y0);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bezier:");
        for (Point2D pt : this.cps) {
            sb.append(" (").append(pt.getX()).append(",").append(pt.getY()).append(")");
        }
        return sb.toString();
    }
}

