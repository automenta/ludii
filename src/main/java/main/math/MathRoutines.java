// 
// Decompiled by Procyon v0.5.36
// 

package main.math;

import gnu.trove.list.array.TFloatArrayList;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public final class MathRoutines
{
    public static final double EPSILON = 1.0E-7;
    public static final double MAX_RANGE = 1000000.0;
    
    public static double normaliseLarge(final double value) {
        double norm = 0.0;
        if (value > 0.0) {
            norm = Math.min(1.0, Math.log10(value + 1.0) / Math.log10(1000000.0));
        }
        else if (value < 0.0) {
            norm = Math.min(1.0, -Math.log10(-value + 1.0) / Math.log10(1000000.0));
        }
        return norm;
    }
    
    public static double normaliseSmall(final double value) {
        return Math.tanh(value);
    }
    
    public static double lerp(final double t, final double a, final double b) {
        return a + t * (b - a);
    }
    
    public static Point2D lerp(final double t, final Point2D a, final Point2D b) {
        final double dx = b.getX() - a.getX();
        final double dy = b.getY() - a.getY();
        return new Point2D.Double(a.getX() + t * dx, a.getY() + t * dy);
    }
    
    public static Point3D lerp(final double t, final Point3D a, final Point3D b) {
        final double dx = b.x() - a.x();
        final double dy = b.y() - a.y();
        final double dz = b.z() - a.z();
        return new Point3D(a.x() + t * dx, a.y() + t * dy, a.z() + t * dz);
    }
    
    public static double distance(final Point2D a, final Point2D b) {
        final double dx = b.getX() - a.getX();
        final double dy = b.getY() - a.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public static double distance(final Point3D a, final Point3D b) {
        final double dx = b.x() - a.x();
        final double dy = b.y() - a.y();
        final double dz = b.z() - a.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public static double distanceSquared(final Point2D a, final Point2D b) {
        final double dx = b.getX() - a.getX();
        final double dy = b.getY() - a.getY();
        return dx * dx + dy * dy;
    }
    
    public static double distance(final Point a, final Point b) {
        final double dx = b.x - a.x;
        final double dy = b.y - a.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public static double distance(final double ax, final double ay, final double bx, final double by) {
        final double dx = bx - ax;
        final double dy = by - ay;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public static double distance(final double ax, final double ay, final double az, final double bx, final double by, final double bz) {
        final double dx = bx - ax;
        final double dy = by - ay;
        final double dz = bz - az;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public static Point2D rotate(final double theta, final Point2D pt) {
        return new Point2D.Double(pt.getX() * Math.cos(theta) - pt.getY() * Math.sin(theta), pt.getY() * Math.cos(theta) + pt.getX() * Math.sin(theta));
    }
    
    public static boolean coincident(final Point2D a, final Point2D b) {
        return distance(a, b) < 1.0E-7;
    }
    
    public static double angle(final Point2D a, final Point2D b) {
        final double dx = b.getX() - a.getX();
        final double dy = b.getY() - a.getY();
        return Math.atan2(dy, dx);
    }
    
    public static double positiveAngle(final double theta) {
        double angle;
        for (angle = theta; angle < 0.0; angle += 6.283185307179586) {}
        while (angle > 6.283185307179586) {
            angle -= 6.283185307179586;
        }
        return angle;
    }
    
    public static double positiveAngle(final Point2D a, final Point2D b) {
        final double dx = b.getX() - a.getX();
        final double dy = b.getY() - a.getY();
        return positiveAngle(Math.atan2(dy, dx));
    }
    
    public static double angleDifference(final Point2D a, final Point2D b, final Point2D c) {
        final double vx = b.getX() - a.getX();
        final double vy = b.getY() - a.getY();
        final double ux = c.getX() - b.getX();
        final double uy = c.getY() - b.getY();
        final double difference = Math.atan2(ux * -vy + uy * vx, ux * vx + uy * vy);
        return difference;
    }
    
    public static boolean isClockwise(final List<Point2D> pts) {
        double sum = 0.0;
        int n = 0;
        int m = pts.size() - 1;
        while (n < pts.size()) {
            final Point2D ptM = pts.get(m);
            final Point2D ptN = pts.get(n);
            sum += (ptN.getX() - ptM.getX()) * (ptN.getY() + ptM.getY());
            m = n++;
        }
        return sum < 0.0;
    }
    
    public static Point2D.Double normalisedVector(final double x0, final double y0, final double x1, final double y1) {
        final double dx = x1 - x0;
        final double dy = y1 - y0;
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len == 0.0) {
            System.out.println("** Zero length vector.");
            len = 1.0;
        }
        return new Point2D.Double(dx / len, dy / len);
    }
    
    public static float unionOfProbabilities(final TFloatArrayList probs) {
        float union = 0.0f;
        for (int i = 0; i < probs.size(); ++i) {
            float baseEval = probs.getQuick(i);
            for (int j = 0; j < i; ++j) {
                baseEval *= 1.0f - probs.getQuick(j);
            }
            union += baseEval;
        }
        return union;
    }
    
    public static Color shade(final Color colour, final double adjust) {
        final int r = Math.max(0, Math.min(255, (int)(colour.getRed() * adjust + 0.5)));
        final int g = Math.max(0, Math.min(255, (int)(colour.getGreen() * adjust + 0.5)));
        final int b = Math.max(0, Math.min(255, (int)(colour.getBlue() * adjust + 0.5)));
        return new Color(r, g, b);
    }
    
    public static int clip(final int val, final int min, final int max) {
        if (val <= min) {
            return min;
        }
        if (val >= max) {
            return max;
        }
        return val;
    }
    
    public static Point2D.Double average(final Point2D a, final Point2D b) {
        final double xx = b.getX() + a.getX();
        final double yy = b.getY() + a.getY();
        return new Point2D.Double(xx * 0.5, yy * 0.5);
    }
    
    public static double distanceToLine(final Point2D pt, final Point2D a, final Point2D b) {
        final double dx = b.getX() - a.getX();
        final double dy = b.getY() - a.getY();
        if (Math.abs(dx) + Math.abs(dy) < 1.0E-7) {
            return distance(pt, a);
        }
        final double a2 = (pt.getY() - a.getY()) * dx - (pt.getX() - a.getX()) * dy;
        return Math.sqrt(a2 * a2 / (dx * dx + dy * dy));
    }
    
    public static double distanceToLineSegment(final Point2D pt, final Point2D a, final Point2D b) {
        if (Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) < 1.0E-7) {
            return distance(pt, a);
        }
        return Math.sqrt(distanceToLineSegmentSquared(pt, a, b));
    }
    
    public static double distanceToLineSegmentSquared(final Point2D pt, final Point2D a, final Point2D b) {
        final double xkj = a.getX() - pt.getX();
        final double ykj = a.getY() - pt.getY();
        final double xlk = b.getX() - a.getX();
        final double ylk = b.getY() - a.getY();
        final double denom = xlk * xlk + ylk * ylk;
        if (Math.abs(denom) < 1.0E-7) {
            return xkj * xkj + ykj * ykj;
        }
        final double t = -(xkj * xlk + ykj * ylk) / denom;
        if (t <= 0.0) {
            return xkj * xkj + ykj * ykj;
        }
        if (t >= 1.0) {
            final double xlj = b.getX() - pt.getX();
            final double ylj = b.getY() - pt.getY();
            return xlj * xlj + ylj * ylj;
        }
        final double xfac = xkj + t * xlk;
        final double yfac = ykj + t * ylk;
        return xfac * xfac + yfac * yfac;
    }
    
    public static double distanceToLineSegment(final Point3D pt, final Point3D a, final Point3D b) {
        if (Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y()) + Math.abs(a.z() - b.z()) < 1.0E-7) {
            return distance(pt, a);
        }
        return Math.sqrt(distanceToLineSegmentSquared(pt, a, b));
    }
    
    public static double distanceToLineSegmentSquared(final Point3D pt, final Point3D a, final Point3D b) {
        final double xkj = a.x() - pt.x();
        final double ykj = a.y() - pt.y();
        final double zkj = a.z() - pt.z();
        final double xlk = b.x() - a.x();
        final double ylk = b.y() - a.y();
        final double zlk = b.z() - a.z();
        final double denom = xlk * xlk + ylk * ylk + zlk * zlk;
        if (Math.abs(denom) < 1.0E-7) {
            return xkj * xkj + ykj * ykj + zkj * zkj;
        }
        final double t = -(xkj * xlk + ykj * ylk + zkj * zlk) / denom;
        if (t <= 0.0) {
            return xkj * xkj + ykj * ykj + zkj * zkj;
        }
        if (t >= 1.0) {
            final double xlj = b.x() - pt.x();
            final double ylj = b.y() - pt.y();
            final double zlj = b.z() - pt.z();
            return xlj * xlj + ylj * ylj + zlj * zlj;
        }
        final double xfac = xkj + t * xlk;
        final double yfac = ykj + t * ylk;
        final double zfac = zkj + t * zlk;
        return xfac * xfac + yfac * yfac + zfac * zfac;
    }
    
    public static Point2D.Double intersectionPoint(final Point2D a, final Point2D b, final Point2D c, final Point2D d) {
        final double dd = (a.getX() - b.getX()) * (c.getY() - d.getY()) - (a.getY() - b.getY()) * (c.getX() - d.getX());
        if (Math.abs(dd) < 1.0E-7) {
            System.out.println("** MathRoutines.intersectionPoint(): Parallel lines.");
            return null;
        }
        final double xi = ((c.getX() - d.getX()) * (a.getX() * b.getY() - a.getY() * b.getX()) - (a.getX() - b.getX()) * (c.getX() * d.getY() - c.getY() * d.getX())) / dd;
        final double yi = ((c.getY() - d.getY()) * (a.getX() * b.getY() - a.getY() * b.getX()) - (a.getY() - b.getY()) * (c.getX() * d.getY() - c.getY() * d.getX())) / dd;
        return new Point2D.Double(xi, yi);
    }
    
    public static Point2D.Double projectionPoint(final Point2D pt, final Point2D a, final Point2D b) {
        final double dx = b.getX() - a.getX();
        final double dy = b.getY() - a.getY();
        final Vector v = new Vector(dx, dy);
        v.normalise();
        v.perpendicular();
        final Point2D.Double pt2 = new Point2D.Double(pt.getX() + v.x(), pt.getY() + v.y());
        return intersectionPoint(a, b, pt, pt2);
    }
    
    public static boolean lineSegmentsIntersect(final double a0x, final double a0y, final double a1x, final double a1y, final double b0x, final double b0y, final double b1x, final double b1y) {
        final double xlk = a1x - a0x;
        final double ylk = a1y - a0y;
        final double xnm = b1x - b0x;
        final double ynm = b1y - b0y;
        final double xmk = b0x - a0x;
        final double ymk = b0y - a0y;
        final double det = xnm * ylk - ynm * xlk;
        if (Math.abs(det) < 1.0E-7) {
            return false;
        }
        final double detinv = 1.0 / det;
        final double s = (xnm * ymk - ynm * xmk) * detinv;
        final double t = (xlk * ymk - ylk * xmk) * detinv;
        return s >= -1.0E-7 && s <= 1.0000001 && t >= -1.0E-7 && t <= 1.0000001;
    }
    
    public static boolean isCrossing(final double a0x, final double a0y, final double a1x, final double a1y, final double b0x, final double b0y, final double b1x, final double b1y) {
        final double MARGIN = 0.01;
        final double xlk = a1x - a0x;
        final double ylk = a1y - a0y;
        final double xnm = b1x - b0x;
        final double ynm = b1y - b0y;
        final double xmk = b0x - a0x;
        final double ymk = b0y - a0y;
        final double det = xnm * ylk - ynm * xlk;
        if (Math.abs(det) < 1.0E-7) {
            return false;
        }
        final double detinv = 1.0 / det;
        final double s = (xnm * ymk - ynm * xmk) * detinv;
        final double t = (xlk * ymk - ylk * xmk) * detinv;
        return s > 0.01 && s < 0.99 && t > 0.01 && t < 0.99;
    }
    
    public static Point2D crossingPoint(final double a0x, final double a0y, final double a1x, final double a1y, final double b0x, final double b0y, final double b1x, final double b1y) {
        final double MARGIN = 0.01;
        final double xlk = a1x - a0x;
        final double ylk = a1y - a0y;
        final double xnm = b1x - b0x;
        final double ynm = b1y - b0y;
        final double xmk = b0x - a0x;
        final double ymk = b0y - a0y;
        final double det = xnm * ylk - ynm * xlk;
        if (Math.abs(det) < 1.0E-7) {
            return null;
        }
        final double detinv = 1.0 / det;
        final double s = (xnm * ymk - ynm * xmk) * detinv;
        final double t = (xlk * ymk - ylk * xmk) * detinv;
        if (s > 0.01 && s < 0.99 && t > 0.01 && t < 0.99) {
            return new Point2D.Double(a0x + s * (a1x - a0x), a0y + s * (a1y - a0y));
        }
        return null;
    }
    
    public static Point3D touchingPoint(final Point3D pt, final Point3D a, final Point3D b) {
        final double MARGIN = 0.001;
        final double distToA = distance(pt, a);
        final double distToB = distance(pt, b);
        final double distToAB = distanceToLineSegment(pt, a, b);
        if (distToA < 0.001 || distToB < 0.001 || distToAB > 0.001) {
            return null;
        }
        final double distAB = distance(a, b);
        return lerp(distToA / distAB, a, b);
    }
    
    public static boolean clockwise(final double x0, final double y0, final double x1, final double y1, final double x2, final double y2) {
        final double result = (x1 - x0) * (y2 - y0) - (x2 - x0) * (y1 - y0);
        return result < 1.0E-7;
    }
    
    public static boolean clockwise(final Point2D a, final Point2D b, final Point2D c) {
        return clockwise(a.getX(), a.getY(), b.getX(), b.getY(), c.getX(), c.getY());
    }
    
    public static boolean clockwise(final List<Point2D> poly) {
        return polygonArea(poly) < 0.0;
    }
    
    public static int whichSide(final double x, final double y, final double ax, final double ay, final double bx, final double by) {
        final double result = (bx - ax) * (y - ay) - (by - ay) * (x - ax);
        if (result < -1.0E-7) {
            return -1;
        }
        if (result > 1.0E-7) {
            return 1;
        }
        return 0;
    }
    
    public static int whichSide(final Point2D pt, final Point2D a, final Point2D b) {
        return whichSide(pt.getX(), pt.getY(), a.getX(), a.getY(), b.getX(), b.getY());
    }
    
    public static boolean pointInTriangle(final Point2D pt, final Point2D a, final Point2D b, final Point2D c) {
        return whichSide(pt, a, b) >= 0 && whichSide(pt, b, c) >= 0 && whichSide(pt, c, a) >= 0;
    }
    
    public static boolean pointInConvexPolygon(final Point2D pt, final Point2D[] poly) {
        for (int sz = poly.length, i = 0; i < sz; ++i) {
            final Point2D a = poly[i];
            final Point2D b = poly[(i + 1) % sz];
            final double side = (pt.getX() - a.getX()) * (b.getY() - a.getY()) - (pt.getY() - a.getY()) * (b.getX() - a.getX());
            if (side < -1.0E-7) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean pointInPolygon(final Point2D.Double pt, final Point2D.Double[] poly) {
        final int sz = poly.length;
        int j = sz - 1;
        boolean odd = false;
        for (int i = 0; i < sz; ++i) {
            if (((poly[i].getY() < pt.getY() && poly[j].getY() >= pt.getY()) || (poly[j].getY() < pt.getY() && poly[i].getY() >= pt.getY())) && (poly[i].getX() <= pt.getX() || poly[j].getX() <= pt.getX())) {
                odd ^= (poly[i].getX() + (pt.getY() - poly[i].getY()) / (poly[j].getY() - poly[i].getY()) * (poly[j].getX() - poly[i].getX()) < pt.getX());
            }
            j = i;
        }
        return odd;
    }
    
    public static boolean pointInPolygon(final Point2D.Double pt, final List<Point2D.Double> poly) {
        final int sz = poly.size();
        int j = sz - 1;
        boolean odd = false;
        final double x = pt.getX();
        final double y = pt.getY();
        for (int i = 0; i < sz; ++i) {
            final double ix = poly.get(i).getX();
            final double iy = poly.get(i).getY();
            final double jx = poly.get(j).getX();
            final double jy = poly.get(j).getY();
            if (((iy < y && jy >= y) || (jy < y && iy >= y)) && (ix <= x || jx <= x)) {
                odd ^= (ix + (y - iy) / (jy - iy) * (jx - ix) < x);
            }
            j = i;
        }
        return odd;
    }
    
    public static double polygonArea(final List<Point2D> poly) {
        double area = 0.0;
        for (int n = 0; n < poly.size(); ++n) {
            final Point2D ptN = poly.get(n);
            final Point2D ptO = poly.get((n + 1) % poly.size());
            area += ptN.getX() * ptO.getY() - ptO.getX() * ptN.getY();
        }
        return area / 2.0;
    }
    
    public static void inflatePolygon(final List<Point2D> polygon, final double amount) {
        final List<Point2D> adjustments = new ArrayList<>();
        for (int n = 0; n < polygon.size(); ++n) {
            final Point2D ptA = polygon.get(n);
            final Point2D ptB = polygon.get((n + 1) % polygon.size());
            final Point2D ptC = polygon.get((n + 2) % polygon.size());
            final boolean clockwise = clockwise(ptA, ptB, ptC);
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
        for (int n = 0; n < polygon.size(); ++n) {
            final Point2D pt = polygon.get(n);
            final Point2D adjustment = adjustments.get((n - 1 + polygon.size()) % polygon.size());
            final double xx2 = pt.getX() + adjustment.getX();
            final double yy2 = pt.getY() + adjustment.getY();
            polygon.remove(n);
            polygon.add(n, new Point2D.Double(xx2, yy2));
        }
    }
    
    public static Rectangle2D bounds(final List<Point2D> points) {
        double x0 = 1000000.0;
        double y0 = 1000000.0;
        double x2 = -1000000.0;
        double y2 = -1000000.0;
        for (final Point2D pt : points) {
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
        if (x0 == 1000000.0 || y0 == 1000000.0) {
            x0 = 0.0;
            y0 = 0.0;
            x2 = 0.0;
            y2 = 0.0;
        }
        return new Rectangle2D.Double(x0, y0, x2 - x0, y2 - y0);
    }
}
