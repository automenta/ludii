// 
// Decompiled by Procyon v0.5.36
// 

package main.math;

import gnu.trove.list.array.TIntArrayList;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Polygon
{
    private final List<Point2D> points;
    
    public Polygon() {
        this.points = new ArrayList<>();
    }
    
    public Polygon(final List<Point2D> pts) {
        this.points = new ArrayList<>();
        for (final Point2D pt : pts) {
            this.points.add(new Point2D.Double(pt.getX(), pt.getY()));
        }
    }
    
    public Polygon(final Point2D[] pts) {
        this.points = new ArrayList<>();
        for (final Point2D pt : pts) {
            this.points.add(new Point2D.Double(pt.getX(), pt.getY()));
        }
    }
    
    public Polygon(final Float[][] pts) {
        this.points = new ArrayList<>();
        for (final Float[] pair : pts) {
            if (pair.length < 2) {
                System.out.println("** Polygon: Two points expected.");
                this.points.clear();
                break;
            }
            this.points.add(new Point2D.Double(pair[0], pair[1]));
        }
    }
    
    public Polygon(final int numSides) {
        this.points = new ArrayList<>();
        final double r = numSides / 6.283185307179586;
        for (int n = 0; n < numSides; ++n) {
            final double theta = 1.5707963267948966 + n / (double)numSides * 2.0 * 3.141592653589793;
            final double x = r * Math.cos(theta);
            final double y = r * Math.sin(theta);
            this.points.add(new Point2D.Double(x, y));
        }
    }
    
    public List<Point2D> points() {
        return Collections.unmodifiableList(this.points);
    }
    
    public int size() {
        return this.points.size();
    }
    
    public boolean isEmpty() {
        return this.points.isEmpty();
    }
    
    public void clear() {
        this.points.clear();
    }
    
    public void add(final Point2D pt) {
        this.points.add(pt);
    }
    
    public void setFrom(final Polygon other) {
        this.clear();
        for (final Point2D pt : other.points()) {
            this.add(new Point2D.Double(pt.getX(), pt.getY()));
        }
    }
    
    public double length() {
        double length = 0.0;
        for (int n = 0; n < this.points.size(); ++n) {
            length += MathRoutines.distance(this.points.get(n), this.points.get((n + 1) % this.points.size()));
        }
        return length;
    }
    
    public Point2D midpoint() {
        if (this.points.isEmpty()) {
            return new Point2D.Double();
        }
        double avgX = 0.0;
        double avgY = 0.0;
        for (int n = 0; n < this.points.size(); ++n) {
            avgX += this.points.get(n).getX();
            avgY += this.points.get(n).getY();
        }
        avgX /= this.points.size();
        avgY /= this.points.size();
        return new Point2D.Double(avgX, avgY);
    }
    
    public double area() {
        double area = 0.0;
        for (int n = 0; n < this.points.size(); ++n) {
            final Point2D ptN = this.points.get(n);
            final Point2D ptO = this.points.get((n + 1) % this.points.size());
            area += ptN.getX() * ptO.getY() - ptO.getX() * ptN.getY();
        }
        return area / 2.0;
    }
    
    public boolean isClockwise() {
        double sum = 0.0;
        int n = 0;
        int m = this.points.size() - 1;
        while (n < this.points.size()) {
            final Point2D ptM = this.points.get(m);
            final Point2D ptN = this.points.get(n);
            sum += (ptN.getX() - ptM.getX()) * (ptN.getY() + ptM.getY());
            m = n++;
        }
        return sum < 0.0;
    }
    
    public boolean clockwise() {
        return this.area() < 0.0;
    }
    
    public boolean contains(final double x, final double y) {
        return this.contains(new Point2D.Double(x, y));
    }
    
    public boolean contains(final Point2D pt) {
        final int numPoints = this.points.size();
        int j = numPoints - 1;
        boolean odd = false;
        final double x = pt.getX();
        final double y = pt.getY();
        for (int i = 0; i < numPoints; ++i) {
            final double ix = this.points.get(i).getX();
            final double iy = this.points.get(i).getY();
            final double jx = this.points.get(j).getX();
            final double jy = this.points.get(j).getY();
            if (((iy < y && jy >= y) || (jy < y && iy >= y)) && (ix <= x || jx <= x)) {
                odd ^= (ix + (y - iy) / (jy - iy) * (jx - ix) < x);
            }
            j = i;
        }
        return odd;
    }
    
    public void fromSides(final TIntArrayList sides, final int[][] steps) {
        int step = steps.length - 1;
        int row = 0;
        int col = 0;
        this.clear();
        this.points.add(new Point2D.Double(col, row));
        for (int n = 0; n < sides.size(); ++n) {
            final int nextStep = sides.get(n);
            step = (step + ((nextStep < 0) ? -1 : 1) + steps.length) % steps.length;
            row += nextStep * steps[step][0];
            col += nextStep * steps[step][1];
            this.points.add(new Point2D.Double(col, row));
        }
    }
    
    public void fromSides(final TIntArrayList sides, final double[][] steps) {
        int step = steps.length - 1;
        double x = 0.0;
        double y = 0.0;
        this.clear();
        this.points.add(new Point2D.Double(x, y));
        for (int n = 0; n < sides.size(); ++n) {
            final int nextStep = sides.get(n);
            step = (step + ((nextStep < 0) ? -1 : 1) + steps.length) % steps.length;
            x += nextStep * steps[step][0];
            y += nextStep * steps[step][1];
            this.points.add(new Point2D.Double(x, y));
        }
    }
    
    public void inflate(final double amount) {
        final List<Point2D> adjustments = new ArrayList<>();
        for (int n = 0; n < this.points.size(); ++n) {
            final Point2D ptA = this.points.get(n);
            final Point2D ptB = this.points.get((n + 1) % this.points.size());
            final Point2D ptC = this.points.get((n + 2) % this.points.size());
            final boolean clockwise = MathRoutines.clockwise(ptA, ptB, ptC);
            Vector vecIn = null;
            Vector vecOut = null;
            if (clockwise) {
                vecIn = new Vector(ptA, ptB);
                vecOut = new Vector(ptC, ptB);
            }
            else {
                vecIn = new Vector(ptB, ptA);
                vecOut = new Vector(ptB, ptC);
            }
            vecIn.normalise();
            vecOut.normalise();
            vecIn.scale(amount, amount);
            vecOut.scale(amount, amount);
            final double xx = (vecIn.x() + vecOut.x()) * 0.5;
            final double yy = (vecIn.y() + vecOut.y()) * 0.5;
            adjustments.add(new Point2D.Double(xx, yy));
        }
        for (int n = 0; n < this.points.size(); ++n) {
            final Point2D pt = this.points.get(n);
            final Point2D adjustment = adjustments.get((n - 1 + this.points.size()) % this.points.size());
            final double xx2 = pt.getX() + adjustment.getX();
            final double yy2 = pt.getY() + adjustment.getY();
            this.points.remove(n);
            this.points.add(n, new Point2D.Double(xx2, yy2));
        }
    }
    
    public Rectangle2D bounds() {
        if (this.points.isEmpty()) {
            return new Rectangle2D.Double();
        }
        double x0 = 1000000.0;
        double y0 = 1000000.0;
        double x2 = -1000000.0;
        double y2 = -1000000.0;
        for (final Point2D pt : this.points) {
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
        sb.append("Polygon:");
        for (final Point2D pt : this.points) {
            sb.append(" (" + pt.getX() + "," + pt.getY() + ")");
        }
        return sb.toString();
    }
}
