// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;

public class FillShapePainter implements ShapePainter
{
    protected Shape shape;
    protected Paint paint;
    
    public FillShapePainter(final Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("Shape can not be null!");
        }
        this.shape = shape;
    }
    
    public void setPaint(final Paint newPaint) {
        this.paint = newPaint;
    }
    
    public Paint getPaint() {
        return this.paint;
    }
    
    @Override
    public void paint(final Graphics2D g2d) {
        if (this.paint != null) {
            g2d.setPaint(this.paint);
            g2d.fill(this.shape);
        }
    }
    
    @Override
    public Shape getPaintedArea() {
        if (this.paint == null) {
            return null;
        }
        return this.shape;
    }
    
    @Override
    public Rectangle2D getPaintedBounds2D() {
        if (this.paint == null || this.shape == null) {
            return null;
        }
        return this.shape.getBounds2D();
    }
    
    @Override
    public boolean inPaintedArea(final Point2D pt) {
        return this.paint != null && this.shape != null && this.shape.contains(pt);
    }
    
    @Override
    public Shape getSensitiveArea() {
        return this.shape;
    }
    
    @Override
    public Rectangle2D getSensitiveBounds2D() {
        if (this.shape == null) {
            return null;
        }
        return this.shape.getBounds2D();
    }
    
    @Override
    public boolean inSensitiveArea(final Point2D pt) {
        return this.shape != null && this.shape.contains(pt);
    }
    
    @Override
    public void setShape(final Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        this.shape = shape;
    }
    
    @Override
    public Shape getShape() {
        return this.shape;
    }
}
