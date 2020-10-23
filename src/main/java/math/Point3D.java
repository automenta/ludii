/*
 * Decompiled with CFR 0.150.
 */
package math;

import java.awt.geom.Point2D;

public class Point3D {
    private double x;
    private double y;
    private double z;

    public Point3D(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0.0;
    }

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D(Point3D other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public Point3D(Point2D other) {
        this.x = other.getX();
        this.y = other.getY();
        this.z = 0.0;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double z() {
        return this.z;
    }

    public void set(double xx, double yy) {
        this.x = xx;
        this.y = yy;
    }

    public void set(double xx, double yy, double zz) {
        this.x = xx;
        this.y = yy;
        this.z = zz;
    }

    public void set(Point3D other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public void set(Point2D other) {
        this.x = other.getX();
        this.y = other.getY();
        this.z = 0.0;
    }

    public double distance(Point3D other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double dz = other.z - this.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double distance(Point2D other) {
        double dx = other.getX() - this.x;
        double dy = other.getY() - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public void translate(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public void translate(double dx, double dy, double dz) {
        this.x += dx;
        this.y += dy;
        this.z += dz;
    }

    public void scale(double s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
    }

    public void scale(double sx, double sy) {
        this.x *= sx;
        this.y *= sy;
    }

    public void scale(double sx, double sy, double sz) {
        this.x *= sx;
        this.y *= sy;
        this.z *= sz;
    }
}

