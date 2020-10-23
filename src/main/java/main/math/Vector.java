// 
// Decompiled by Procyon v0.5.36
// 

package main.math;

import java.awt.geom.Point2D;

public class Vector
{
    private double x;
    private double y;
    private double z;
    
    public Vector(final double x, final double y) {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.x = x;
        this.y = y;
    }
    
    public Vector(final double x, final double y, final double z) {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector(final Point2D ptA, final Point2D ptB) {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.x = ptB.getX() - ptA.getX();
        this.y = ptB.getY() - ptA.getY();
    }
    
    public Vector(final Point3D ptA, final Point3D ptB) {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.x = ptB.x() - ptA.x();
        this.y = ptB.y() - ptA.y();
        this.z = ptB.z() - ptA.z();
    }
    
    public Vector(final Vector other) {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
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
    
    public void set(final double xx, final double yy, final double zz) {
        this.x = xx;
        this.y = yy;
        this.z = zz;
    }
    
    public double magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public void normalise() {
        final double mag = this.magnitude();
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
        final double tmp = this.x;
        this.x = -this.y;
        this.y = tmp;
    }
    
    public void translate(final double dx, final double dy) {
        this.x += dx;
        this.y += dy;
    }
    
    public void translate(final double dx, final double dy, final double dz) {
        this.x += dx;
        this.y += dy;
        this.z += dz;
    }
    
    public void scale(final double factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;
    }
    
    public void scale(final double sx, final double sy) {
        this.x *= sx;
        this.y *= sy;
    }
    
    public void scale(final double sx, final double sy, final double sz) {
        this.x *= sx;
        this.y *= sy;
        this.z *= sz;
    }
    
    public void rotate(final double theta) {
        final double xx = this.x * Math.cos(theta) - this.y * Math.sin(theta);
        final double yy = this.y * Math.cos(theta) + this.x * Math.sin(theta);
        this.x = xx;
        this.y = yy;
    }
    
    public double dotProduct(final Vector other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }
    
    public double determinant(final Vector other) {
        return this.x * other.y - this.y * other.x;
    }
    
    @Override
    public String toString() {
        return String.format("<%.3f,%.3f,%.3f>", this.x, this.y, this.z);
    }
}
