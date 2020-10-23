/*
 * Decompiled with CFR 0.150.
 */
package math;

import java.awt.geom.Point2D;

public class Vector {
    private double x = 0.0;
    private double y = 0.0;
    private double z = 0.0;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(Point2D ptA, Point2D ptB) {
        this.x = ptB.getX() - ptA.getX();
        this.y = ptB.getY() - ptA.getY();
    }

    public Vector(Point3D ptA, Point3D ptB) {
        this.x = ptB.x() - ptA.x();
        this.y = ptB.y() - ptA.y();
        this.z = ptB.z() - ptA.z();
    }

    public Vector(Vector other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
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

    public void set(double xx, double yy, double zz) {
        this.x = xx;
        this.y = yy;
        this.z = zz;
    }

    public double magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public void normalise() {
        double mag = this.magnitude();
        if (mag < 1.0E-7) {
            return;
        }
        this.x /= mag;
        this.y /= mag;
        this.z /= mag;
    }

    public double direction() {
        return Math.atan2(this.y, this.x);
    }

    public void reverse() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    public void perpendicular() {
        double tmp = this.x;
        this.x = -this.y;
        this.y = tmp;
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

    public void scale(double factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;
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

    public void rotate(double theta) {
        double xx = this.x * Math.cos(theta) - this.y * Math.sin(theta);
        double yy = this.y * Math.cos(theta) + this.x * Math.sin(theta);
        this.x = xx;
        this.y = yy;
    }

    public double dotProduct(Vector other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public double determinant(Vector other) {
        return this.x * other.y - this.y * other.x;
    }

    public String toString() {
        return String.format("<%.3f,%.3f,%.3f>", this.x, this.y, this.z);
    }
}

