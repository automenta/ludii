// 
// Decompiled by Procyon v0.5.36
// 

package main.math;

import java.awt.geom.Point2D;

public class Point3D
{
    private double x;
    private double y;
    private double z;
    
    public Point3D(final double x, final double y) {
        this.x = x;
        this.y = y;
        this.z = 0.0;
    }
    
    public Point3D(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Point3D(final Point3D other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }
    
    public Point3D(final Point2D other) {
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
    
    public void set(final double xx, final double yy) {
        this.x = xx;
        this.y = yy;
    }
    
    public void set(final double xx, final double yy, final double zz) {
        this.x = xx;
        this.y = yy;
        this.z = zz;
    }
    
    public void set(final Point3D other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }
    
    public void set(final Point2D other) {
        this.x = other.getX();
        this.y = other.getY();
        this.z = 0.0;
    }
    
    public double distance(final Point3D other) {
        final double dx = other.x - this.x;
        final double dy = other.y - this.y;
        final double dz = other.z - this.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public double distance(final Point2D other) {
        final double dx = other.getX() - this.x;
        final double dy = other.getY() - this.y;
        return Math.sqrt(dx * dx + dy * dy);
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
    
    public void scale(final double s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
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
}
