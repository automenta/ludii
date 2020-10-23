// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Area;
import java.awt.Graphics2D;
import java.awt.Shape;

public class CompositeShapePainter implements ShapePainter
{
    protected Shape shape;
    protected ShapePainter[] painters;
    protected int count;
    
    public CompositeShapePainter(final Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        this.shape = shape;
    }
    
    public void addShapePainter(final ShapePainter shapePainter) {
        if (shapePainter == null) {
            return;
        }
        if (this.shape != shapePainter.getShape()) {
            shapePainter.setShape(this.shape);
        }
        if (this.painters == null) {
            this.painters = new ShapePainter[2];
        }
        if (this.count == this.painters.length) {
            final ShapePainter[] newPainters = new ShapePainter[this.count + this.count / 2 + 1];
            System.arraycopy(this.painters, 0, newPainters, 0, this.count);
            this.painters = newPainters;
        }
        this.painters[this.count++] = shapePainter;
    }
    
    public ShapePainter getShapePainter(final int index) {
        return this.painters[index];
    }
    
    public int getShapePainterCount() {
        return this.count;
    }
    
    @Override
    public void paint(final Graphics2D g2d) {
        if (this.painters != null) {
            for (int i = 0; i < this.count; ++i) {
                this.painters[i].paint(g2d);
            }
        }
    }
    
    @Override
    public Shape getPaintedArea() {
        if (this.painters == null) {
            return null;
        }
        final Area paintedArea = new Area();
        for (int i = 0; i < this.count; ++i) {
            final Shape s = this.painters[i].getPaintedArea();
            if (s != null) {
                paintedArea.add(new Area(s));
            }
        }
        return paintedArea;
    }
    
    @Override
    public Rectangle2D getPaintedBounds2D() {
        if (this.painters == null) {
            return null;
        }
        Rectangle2D bounds = null;
        for (int i = 0; i < this.count; ++i) {
            final Rectangle2D pb = this.painters[i].getPaintedBounds2D();
            if (pb != null) {
                if (bounds == null) {
                    bounds = (Rectangle2D)pb.clone();
                }
                else {
                    bounds.add(pb);
                }
            }
        }
        return bounds;
    }
    
    @Override
    public boolean inPaintedArea(final Point2D pt) {
        if (this.painters == null) {
            return false;
        }
        for (int i = 0; i < this.count; ++i) {
            if (this.painters[i].inPaintedArea(pt)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Shape getSensitiveArea() {
        if (this.painters == null) {
            return null;
        }
        final Area paintedArea = new Area();
        for (int i = 0; i < this.count; ++i) {
            final Shape s = this.painters[i].getSensitiveArea();
            if (s != null) {
                paintedArea.add(new Area(s));
            }
        }
        return paintedArea;
    }
    
    @Override
    public Rectangle2D getSensitiveBounds2D() {
        if (this.painters == null) {
            return null;
        }
        Rectangle2D bounds = null;
        for (int i = 0; i < this.count; ++i) {
            final Rectangle2D pb = this.painters[i].getSensitiveBounds2D();
            if (pb != null) {
                if (bounds == null) {
                    bounds = (Rectangle2D)pb.clone();
                }
                else {
                    bounds.add(pb);
                }
            }
        }
        return bounds;
    }
    
    @Override
    public boolean inSensitiveArea(final Point2D pt) {
        if (this.painters == null) {
            return false;
        }
        for (int i = 0; i < this.count; ++i) {
            if (this.painters[i].inSensitiveArea(pt)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void setShape(final Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        if (this.painters != null) {
            for (int i = 0; i < this.count; ++i) {
                this.painters[i].setShape(shape);
            }
        }
        this.shape = shape;
    }
    
    @Override
    public Shape getShape() {
        return this.shape;
    }
}
