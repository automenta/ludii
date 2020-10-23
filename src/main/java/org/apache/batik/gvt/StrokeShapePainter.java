// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Shape;

public class StrokeShapePainter implements ShapePainter
{
    protected Shape shape;
    protected Shape strokedShape;
    protected Stroke stroke;
    protected Paint paint;
    
    public StrokeShapePainter(final Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        this.shape = shape;
    }
    
    public void setStroke(final Stroke newStroke) {
        this.stroke = newStroke;
        this.strokedShape = null;
    }
    
    public Stroke getStroke() {
        return this.stroke;
    }
    
    public void setPaint(final Paint newPaint) {
        this.paint = newPaint;
    }
    
    public Paint getPaint() {
        return this.paint;
    }
    
    @Override
    public void paint(final Graphics2D g2d) {
        if (this.stroke != null && this.paint != null) {
            g2d.setPaint(this.paint);
            g2d.setStroke(this.stroke);
            g2d.draw(this.shape);
        }
    }
    
    @Override
    public Shape getPaintedArea() {
        if (this.paint == null || this.stroke == null) {
            return null;
        }
        if (this.strokedShape == null) {
            this.strokedShape = this.stroke.createStrokedShape(this.shape);
        }
        return this.strokedShape;
    }
    
    @Override
    public Rectangle2D getPaintedBounds2D() {
        final Shape painted = this.getPaintedArea();
        if (painted == null) {
            return null;
        }
        return painted.getBounds2D();
    }
    
    @Override
    public boolean inPaintedArea(final Point2D pt) {
        final Shape painted = this.getPaintedArea();
        return painted != null && painted.contains(pt);
    }
    
    @Override
    public Shape getSensitiveArea() {
        if (this.stroke == null) {
            return null;
        }
        if (this.strokedShape == null) {
            this.strokedShape = this.stroke.createStrokedShape(this.shape);
        }
        return this.strokedShape;
    }
    
    @Override
    public Rectangle2D getSensitiveBounds2D() {
        final Shape sensitive = this.getSensitiveArea();
        if (sensitive == null) {
            return null;
        }
        return sensitive.getBounds2D();
    }
    
    @Override
    public boolean inSensitiveArea(final Point2D pt) {
        final Shape sensitive = this.getSensitiveArea();
        return sensitive != null && sensitive.contains(pt);
    }
    
    @Override
    public void setShape(final Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        this.shape = shape;
        this.strokedShape = null;
    }
    
    @Override
    public Shape getShape() {
        return this.shape;
    }
}
