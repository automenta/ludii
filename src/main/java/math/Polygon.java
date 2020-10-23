/*
 * Decompiled with CFR 0.150.
 */
package math;

import gnu.trove.list.array.TIntArrayList;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Polygon {
    private final List<Point2D> points = new ArrayList<>();

    public Polygon() {
    }

    public Polygon(List<Point2D> pts) {
        for (Point2D pt : pts) {
            this.points.add(new Point2D.Double(pt.getX(), pt.getY()));
        }
    }

    public Polygon(Point2D[] pts) {
        for (Point2D pt : pts) {
            this.points.add(new Point2D.Double(pt.getX(), pt.getY()));
        }
    }

    public Polygon(Float[][] pts) {
        for (Float[] pair : pts) {
            if (pair.length < 2) {
                System.out.println("** Polygon: Two points expected.");
                this.points.clear();
                break;
            }
            this.points.add(new Point2D.Double(pair[0], pair[1]));
        }
    }

    public Polygon(int numSides) {
        double r = numSides / (Math.PI * 2);
        for (int n = 0; n < numSides; ++n) {
            double theta = 1.5707963267948966 + (double)n / numSides * 2.0 * Math.PI;
            double x = r * Math.cos(theta);
            double y = r * Math.sin(theta);
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

    public void add(Point2D pt) {
        this.points.add(pt);
    }

    public void setFrom(Polygon other) {
        this.clear();
        for (Point2D pt : other.points()) {
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
        for (Point2D point : this.points) {
            avgX += point.getX();
            avgY += point.getY();
        }
        return new Point2D.Double(avgX /= this.points.size(), avgY /= this.points.size());
    }

    public double area() {
        double area = 0.0;
        for (int n = 0; n < this.points.size(); ++n) {
            Point2D ptN = this.points.get(n);
            Point2D ptO = this.points.get((n + 1) % this.points.size());
            area += ptN.getX() * ptO.getY() - ptO.getX() * ptN.getY();
        }
        return area / 2.0;
    }

    public boolean isClockwise() {
        double sum = 0.0;
        int n = 0;
        int m = this.points.size() - 1;
        while (n < this.points.size()) {
            Point2D ptM = this.points.get(m);
            Point2D ptN = this.points.get(n);
            sum += (ptN.getX() - ptM.getX()) * (ptN.getY() + ptM.getY());
            m = n++;
        }
        return sum < 0.0;
    }

    public boolean clockwise() {
        return this.area() < 0.0;
    }

    public boolean contains(double x, double y) {
        return this.contains(new Point2D.Double(x, y));
    }

    public boolean contains(Point2D pt) {
        int numPoints = this.points.size();
        int j = numPoints - 1;
        boolean odd = false;
        double x = pt.getX();
        double y = pt.getY();
        int i = 0;
        while (i < numPoints) {
            double ix = this.points.get(i).getX();
            double iy = this.points.get(i).getY();
            double jx = this.points.get(j).getX();
            double jy = this.points.get(j).getY();
            if ((iy < y && jy >= y || jy < y && iy >= y) && (ix <= x || jx <= x)) {
                odd ^= ix + (y - iy) / (jy - iy) * (jx - ix) < x;
            }
            j = i++;
        }
        return odd;
    }

    public void fromSides(TIntArrayList sides, int[][] steps) {
        int step = steps.length - 1;
        int row = 0;
        int col = 0;
        this.clear();
        this.points.add(new Point2D.Double(col, row));
        for (int n = 0; n < sides.size(); ++n) {
            int nextStep = sides.get(n);
            step = (step + (nextStep < 0 ? -1 : 1) + steps.length) % steps.length;
            this.points.add(new Point2D.Double(col += nextStep * steps[step][1], row += nextStep * steps[step][0]));
        }
    }

    public void fromSides(TIntArrayList sides, double[][] steps) {
        int step = steps.length - 1;
        double x = 0.0;
        double y = 0.0;
        this.clear();
        this.points.add(new Point2D.Double(x, y));
        for (int n = 0; n < sides.size(); ++n) {
            int nextStep = sides.get(n);
            step = (step + (nextStep < 0 ? -1 : 1) + steps.length) % steps.length;
            this.points.add(new Point2D.Double(x += nextStep * steps[step][0], y += nextStep * steps[step][1]));
        }
    }

    public void inflate(double amount) {
        int n;
        ArrayList<Point2D.Double> adjustments = new ArrayList<>();
        for (n = 0; n < this.points.size(); ++n) {
            Point2D ptA = this.points.get(n);
            Point2D ptB = this.points.get((n + 1) % this.points.size());
            Point2D ptC = this.points.get((n + 2) % this.points.size());
            boolean clockwise = MathRoutines.clockwise(ptA, ptB, ptC);
            Vector vecIn = null;
            Vector vecOut = null;
            if (clockwise) {
                vecIn = new Vector(ptA, ptB);
                vecOut = new Vector(ptC, ptB);
            } else {
                vecIn = new Vector(ptB, ptA);
                vecOut = new Vector(ptB, ptC);
            }
            vecIn.normalise();
            vecOut.normalise();
            vecIn.scale(amount, amount);
            vecOut.scale(amount, amount);
            double xx = (vecIn.x() + vecOut.x()) * 0.5;
            double yy = (vecIn.y() + vecOut.y()) * 0.5;
            adjustments.add(new Point2D.Double(xx, yy));
        }
        for (n = 0; n < this.points.size(); ++n) {
            Point2D pt = this.points.get(n);
            Point2D adjustment = adjustments.get((n - 1 + this.points.size()) % this.points.size());
            double xx = pt.getX() + adjustment.getX();
            double yy = pt.getY() + adjustment.getY();
            this.points.remove(n);
            this.points.add(n, new Point2D.Double(xx, yy));
        }
    }

    public Rectangle2D bounds() {
        if (this.points.isEmpty()) {
            return new Rectangle2D.Double();
        }
        double x0 = 1000000.0;
        double y0 = 1000000.0;
        double x1 = -1000000.0;
        double y1 = -1000000.0;
        for (Point2D pt : this.points) {
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
        sb.append("Polygon:");
        for (Point2D pt : this.points) {
            sb.append(" (" + pt.getX() + "," + pt.getY() + ")");
        }
        return sb.toString();
    }
}

