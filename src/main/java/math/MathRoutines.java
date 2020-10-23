/*
 * Decompiled with CFR 0.150.
 */
package math;

import gnu.trove.list.array.TFloatArrayList;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public final class MathRoutines {
    public static final double EPSILON = 1.0E-7;
    public static final double MAX_RANGE = 1000000.0;

    public static double normaliseLarge(double value) {
        double norm = 0.0;
        if (value > 0.0) {
            norm = Math.min(1.0, Math.log10(value + 1.0) / Math.log10(1000000.0));
        } else if (value < 0.0) {
            norm = Math.min(1.0, -Math.log10(-value + 1.0) / Math.log10(1000000.0));
        }
        return norm;
    }

    public static double normaliseSmall(double value) {
        return Math.tanh(value);
    }

    public static double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    public static Point2D lerp(double t, Point2D a, Point2D b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        return new Point2D.Double(a.getX() + t * dx, a.getY() + t * dy);
    }

    public static Point3D lerp(double t, Point3D a, Point3D b) {
        double dx = b.x() - a.x();
        double dy = b.y() - a.y();
        double dz = b.z() - a.z();
        return new Point3D(a.x() + t * dx, a.y() + t * dy, a.z() + t * dz);
    }

    public static double distance(Point2D a, Point2D b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double distance(Point3D a, Point3D b) {
        double dx = b.x() - a.x();
        double dy = b.y() - a.y();
        double dz = b.z() - a.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static double distanceSquared(Point2D a, Point2D b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        return dx * dx + dy * dy;
    }

    public static double distance(Point a, Point b) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double distance(double ax, double ay, double bx, double by) {
        double dx = bx - ax;
        double dy = by - ay;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double distance(double ax, double ay, double az, double bx, double by, double bz) {
        double dx = bx - ax;
        double dy = by - ay;
        double dz = bz - az;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static Point2D rotate(double theta, Point2D pt) {
        return new Point2D.Double(pt.getX() * Math.cos(theta) - pt.getY() * Math.sin(theta), pt.getY() * Math.cos(theta) + pt.getX() * Math.sin(theta));
    }

    public static boolean coincident(Point2D a, Point2D b) {
        return MathRoutines.distance(a, b) < 1.0E-7;
    }

    public static double angle(Point2D a, Point2D b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        return Math.atan2(dy, dx);
    }

    public static double positiveAngle(double theta) {
        double angle;
        for (angle = theta; angle < 0.0; angle += Math.PI * 2) {
        }
        while (angle > Math.PI * 2) {
            angle -= Math.PI * 2;
        }
        return angle;
    }

    public static double positiveAngle(Point2D a, Point2D b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        return MathRoutines.positiveAngle(Math.atan2(dy, dx));
    }

    public static double angleDifference(Point2D a, Point2D b, Point2D c) {
        double vx = b.getX() - a.getX();
        double vy = b.getY() - a.getY();
        double ux = c.getX() - b.getX();
        double uy = c.getY() - b.getY();
        double difference = Math.atan2(ux * -vy + uy * vx, ux * vx + uy * vy);
        return difference;
    }

    public static boolean isClockwise(List<Point2D> pts) {
        double sum = 0.0;
        int n = 0;
        int m = pts.size() - 1;
        while (n < pts.size()) {
            Point2D ptM = pts.get(m);
            Point2D ptN = pts.get(n);
            sum += (ptN.getX() - ptM.getX()) * (ptN.getY() + ptM.getY());
            m = n++;
        }
        return sum < 0.0;
    }

    public static Point2D.Double normalisedVector(double x0, double y0, double x1, double y1) {
        double dx = x1 - x0;
        double dy = y1 - y0;
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len == 0.0) {
            System.out.println("** Zero length vector.");
            len = 1.0;
        }
        return new Point2D.Double(dx / len, dy / len);
    }

    public static float unionOfProbabilities(TFloatArrayList probs) {
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

    public static Color shade(Color colour, double adjust) {
        int r = Math.max(0, Math.min(255, (int)(colour.getRed() * adjust + 0.5)));
        int g = Math.max(0, Math.min(255, (int)(colour.getGreen() * adjust + 0.5)));
        int b = Math.max(0, Math.min(255, (int)(colour.getBlue() * adjust + 0.5)));
        return new Color(r, g, b);
    }

    public static int clip(int val, int min, int max) {
        if (val <= min) {
            return min;
        }
        return Math.min(val, max);
    }

    public static Point2D.Double average(Point2D a, Point2D b) {
        double xx = b.getX() + a.getX();
        double yy = b.getY() + a.getY();
        return new Point2D.Double(xx * 0.5, yy * 0.5);
    }

    public static double distanceToLine(Point2D pt, Point2D a, Point2D b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        if (Math.abs(dx) + Math.abs(dy) < 1.0E-7) {
            return MathRoutines.distance(pt, a);
        }
        double a2 = (pt.getY() - a.getY()) * dx - (pt.getX() - a.getX()) * dy;
        return Math.sqrt(a2 * a2 / (dx * dx + dy * dy));
    }

    public static double distanceToLineSegment(Point2D pt, Point2D a, Point2D b) {
        if (Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) < 1.0E-7) {
            return MathRoutines.distance(pt, a);
        }
        return Math.sqrt(MathRoutines.distanceToLineSegmentSquared(pt, a, b));
    }

    public static double distanceToLineSegmentSquared(Point2D pt, Point2D a, Point2D b) {
        double ylk;
        double xkj = a.getX() - pt.getX();
        double ykj = a.getY() - pt.getY();
        double xlk = b.getX() - a.getX();
        double denom = xlk * xlk + (ylk = b.getY() - a.getY()) * ylk;
        if (Math.abs(denom) < 1.0E-7) {
            return xkj * xkj + ykj * ykj;
        }
        double t = -(xkj * xlk + ykj * ylk) / denom;
        if (t <= 0.0) {
            return xkj * xkj + ykj * ykj;
        }
        if (t >= 1.0) {
            double xlj = b.getX() - pt.getX();
            double ylj = b.getY() - pt.getY();
            return xlj * xlj + ylj * ylj;
        }
        double xfac = xkj + t * xlk;
        double yfac = ykj + t * ylk;
        return xfac * xfac + yfac * yfac;
    }

    public static double distanceToLineSegment(Point3D pt, Point3D a, Point3D b) {
        if (Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y()) + Math.abs(a.z() - b.z()) < 1.0E-7) {
            return MathRoutines.distance(pt, a);
        }
        return Math.sqrt(MathRoutines.distanceToLineSegmentSquared(pt, a, b));
    }

    public static double distanceToLineSegmentSquared(Point3D pt, Point3D a, Point3D b) {
        double zlk;
        double ylk;
        double xkj = a.x() - pt.x();
        double ykj = a.y() - pt.y();
        double zkj = a.z() - pt.z();
        double xlk = b.x() - a.x();
        double denom = xlk * xlk + (ylk = b.y() - a.y()) * ylk + (zlk = b.z() - a.z()) * zlk;
        if (Math.abs(denom) < 1.0E-7) {
            return xkj * xkj + ykj * ykj + zkj * zkj;
        }
        double t = -(xkj * xlk + ykj * ylk + zkj * zlk) / denom;
        if (t <= 0.0) {
            return xkj * xkj + ykj * ykj + zkj * zkj;
        }
        if (t >= 1.0) {
            double xlj = b.x() - pt.x();
            double ylj = b.y() - pt.y();
            double zlj = b.z() - pt.z();
            return xlj * xlj + ylj * ylj + zlj * zlj;
        }
        double xfac = xkj + t * xlk;
        double yfac = ykj + t * ylk;
        double zfac = zkj + t * zlk;
        return xfac * xfac + yfac * yfac + zfac * zfac;
    }

    public static Point2D.Double intersectionPoint(Point2D a, Point2D b, Point2D c, Point2D d) {
        double dd = (a.getX() - b.getX()) * (c.getY() - d.getY()) - (a.getY() - b.getY()) * (c.getX() - d.getX());
        if (Math.abs(dd) < 1.0E-7) {
            System.out.println("** MathRoutines.intersectionPoint(): Parallel lines.");
            return null;
        }
        double xi = ((c.getX() - d.getX()) * (a.getX() * b.getY() - a.getY() * b.getX()) - (a.getX() - b.getX()) * (c.getX() * d.getY() - c.getY() * d.getX())) / dd;
        double yi = ((c.getY() - d.getY()) * (a.getX() * b.getY() - a.getY() * b.getX()) - (a.getY() - b.getY()) * (c.getX() * d.getY() - c.getY() * d.getX())) / dd;
        return new Point2D.Double(xi, yi);
    }

    public static Point2D.Double projectionPoint(Point2D pt, Point2D a, Point2D b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        Vector v = new Vector(dx, dy);
        v.normalise();
        v.perpendicular();
        Point2D.Double pt2 = new Point2D.Double(pt.getX() + v.x(), pt.getY() + v.y());
        return MathRoutines.intersectionPoint(a, b, pt, pt2);
    }

    public static boolean lineSegmentsIntersect(double a0x, double a0y, double a1x, double a1y, double b0x, double b0y, double b1x, double b1y) {
        double xlk = a1x - a0x;
        double ylk = a1y - a0y;
        double xnm = b1x - b0x;
        double ynm = b1y - b0y;
        double xmk = b0x - a0x;
        double ymk = b0y - a0y;
        double det = xnm * ylk - ynm * xlk;
        if (Math.abs(det) < 1.0E-7) {
            return false;
        }
        double detinv = 1.0 / det;
        double s = (xnm * ymk - ynm * xmk) * detinv;
        double t = (xlk * ymk - ylk * xmk) * detinv;
        return s >= -1.0E-7 && s <= 1.0000001 && t >= -1.0E-7 && t <= 1.0000001;
    }

    public static boolean isCrossing(double a0x, double a0y, double a1x, double a1y, double b0x, double b0y, double b1x, double b1y) {
        double MARGIN = 0.01;
        double xlk = a1x - a0x;
        double ylk = a1y - a0y;
        double xnm = b1x - b0x;
        double ynm = b1y - b0y;
        double xmk = b0x - a0x;
        double ymk = b0y - a0y;
        double det = xnm * ylk - ynm * xlk;
        if (Math.abs(det) < 1.0E-7) {
            return false;
        }
        double detinv = 1.0 / det;
        double s = (xnm * ymk - ynm * xmk) * detinv;
        double t = (xlk * ymk - ylk * xmk) * detinv;
        return s > 0.01 && s < 0.99 && t > 0.01 && t < 0.99;
    }

    public static Point2D crossingPoint(double a0x, double a0y, double a1x, double a1y, double b0x, double b0y, double b1x, double b1y) {
        double MARGIN = 0.01;
        double xlk = a1x - a0x;
        double ylk = a1y - a0y;
        double xnm = b1x - b0x;
        double ynm = b1y - b0y;
        double xmk = b0x - a0x;
        double ymk = b0y - a0y;
        double det = xnm * ylk - ynm * xlk;
        if (Math.abs(det) < 1.0E-7) {
            return null;
        }
        double detinv = 1.0 / det;
        double s = (xnm * ymk - ynm * xmk) * detinv;
        double t = (xlk * ymk - ylk * xmk) * detinv;
        if (s > 0.01 && s < 0.99 && t > 0.01 && t < 0.99) {
            return new Point2D.Double(a0x + s * (a1x - a0x), a0y + s * (a1y - a0y));
        }
        return null;
    }

    public static Point3D touchingPoint(Point3D pt, Point3D a, Point3D b) {
        double MARGIN = 0.001;
        double distToA = MathRoutines.distance(pt, a);
        double distToB = MathRoutines.distance(pt, b);
        double distToAB = MathRoutines.distanceToLineSegment(pt, a, b);
        if (distToA < 0.001 || distToB < 0.001 || distToAB > 0.001) {
            return null;
        }
        double distAB = MathRoutines.distance(a, b);
        return MathRoutines.lerp(distToA / distAB, a, b);
    }

    public static boolean clockwise(double x0, double y0, double x1, double y1, double x2, double y2) {
        double result = (x1 - x0) * (y2 - y0) - (x2 - x0) * (y1 - y0);
        return result < 1.0E-7;
    }

    public static boolean clockwise(Point2D a, Point2D b, Point2D c) {
        return MathRoutines.clockwise(a.getX(), a.getY(), b.getX(), b.getY(), c.getX(), c.getY());
    }

    public static boolean clockwise(List<Point2D> poly) {
        return MathRoutines.polygonArea(poly) < 0.0;
    }

    public static int whichSide(double x, double y, double ax, double ay, double bx, double by) {
        double result = (bx - ax) * (y - ay) - (by - ay) * (x - ax);
        if (result < -1.0E-7) {
            return -1;
        }
        if (result > 1.0E-7) {
            return 1;
        }
        return 0;
    }

    public static int whichSide(Point2D pt, Point2D a, Point2D b) {
        return MathRoutines.whichSide(pt.getX(), pt.getY(), a.getX(), a.getY(), b.getX(), b.getY());
    }

    public static boolean pointInTriangle(Point2D pt, Point2D a, Point2D b, Point2D c) {
        return MathRoutines.whichSide(pt, a, b) >= 0 && MathRoutines.whichSide(pt, b, c) >= 0 && MathRoutines.whichSide(pt, c, a) >= 0;
    }

    public static boolean pointInConvexPolygon(Point2D pt, Point2D[] poly) {
        int sz = poly.length;
        for (int i = 0; i < sz; ++i) {
            Point2D a = poly[i];
            Point2D b = poly[(i + 1) % sz];
            double side = (pt.getX() - a.getX()) * (b.getY() - a.getY()) - (pt.getY() - a.getY()) * (b.getX() - a.getX());
            if (!(side < -1.0E-7)) continue;
            return false;
        }
        return true;
    }

    public static boolean pointInPolygon(Point2D.Double pt, Point2D.Double[] poly) {
        int sz = poly.length;
        int j = sz - 1;
        boolean odd = false;
        int i = 0;
        while (i < sz) {
            if ((poly[i].getY() < pt.getY() && poly[j].getY() >= pt.getY() || poly[j].getY() < pt.getY() && poly[i].getY() >= pt.getY()) && (poly[i].getX() <= pt.getX() || poly[j].getX() <= pt.getX())) {
                odd ^= poly[i].getX() + (pt.getY() - poly[i].getY()) / (poly[j].getY() - poly[i].getY()) * (poly[j].getX() - poly[i].getX()) < pt.getX();
            }
            j = i++;
        }
        return odd;
    }

    public static boolean pointInPolygon(Point2D.Double pt, List<Point2D.Double> poly) {
        int sz = poly.size();
        int j = sz - 1;
        boolean odd = false;
        double x = pt.getX();
        double y = pt.getY();
        int i = 0;
        while (i < sz) {
            double ix = poly.get(i).getX();
            double iy = poly.get(i).getY();
            double jx = poly.get(j).getX();
            double jy = poly.get(j).getY();
            if ((iy < y && jy >= y || jy < y && iy >= y) && (ix <= x || jx <= x)) {
                odd ^= ix + (y - iy) / (jy - iy) * (jx - ix) < x;
            }
            j = i++;
        }
        return odd;
    }

    public static double polygonArea(List<Point2D> poly) {
        double area = 0.0;
        for (int n = 0; n < poly.size(); ++n) {
            Point2D ptN = poly.get(n);
            Point2D ptO = poly.get((n + 1) % poly.size());
            area += ptN.getX() * ptO.getY() - ptO.getX() * ptN.getY();
        }
        return area / 2.0;
    }

    public static void inflatePolygon(List<Point2D> polygon, double amount) {
        int n;
        ArrayList<Point2D.Double> adjustments = new ArrayList<>();
        for (n = 0; n < polygon.size(); ++n) {
            Point2D ptA = polygon.get(n);
            Point2D ptB = polygon.get((n + 1) % polygon.size());
            Point2D ptC = polygon.get((n + 2) % polygon.size());
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
        for (n = 0; n < polygon.size(); ++n) {
            Point2D pt = polygon.get(n);
            Point2D adjustment = adjustments.get((n - 1 + polygon.size()) % polygon.size());
            double xx = pt.getX() + adjustment.getX();
            double yy = pt.getY() + adjustment.getY();
            polygon.remove(n);
            polygon.add(n, new Point2D.Double(xx, yy));
        }
    }

    public static Rectangle2D bounds(List<Point2D> points) {
        double x0 = 1000000.0;
        double y0 = 1000000.0;
        double x1 = -1000000.0;
        double y1 = -1000000.0;
        for (Point2D pt : points) {
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
        if (x0 == 1000000.0 || y0 == 1000000.0) {
            x0 = 0.0;
            y0 = 0.0;
            x1 = 0.0;
            y1 = 0.0;
        }
        return new Rectangle2D.Double(x0, y0, x1 - x0, y1 - y0);
    }
}

