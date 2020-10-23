// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.geom;

import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.Shape;

public class ShapeExtender implements ExtendedShape
{
    Shape shape;
    
    public ShapeExtender(final Shape shape) {
        this.shape = shape;
    }
    
    @Override
    public boolean contains(final double x, final double y) {
        return this.shape.contains(x, y);
    }
    
    @Override
    public boolean contains(final double x, final double y, final double w, final double h) {
        return this.shape.contains(x, y, w, h);
    }
    
    @Override
    public boolean contains(final Point2D p) {
        return this.shape.contains(p);
    }
    
    @Override
    public boolean contains(final Rectangle2D r) {
        return this.shape.contains(r);
    }
    
    @Override
    public Rectangle getBounds() {
        return this.shape.getBounds();
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return this.shape.getBounds2D();
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform at) {
        return this.shape.getPathIterator(at);
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform at, final double flatness) {
        return this.shape.getPathIterator(at, flatness);
    }
    
    @Override
    public ExtendedPathIterator getExtendedPathIterator() {
        return new EPIWrap(this.shape.getPathIterator(null));
    }
    
    @Override
    public boolean intersects(final double x, final double y, final double w, final double h) {
        return this.shape.intersects(x, y, w, h);
    }
    
    @Override
    public boolean intersects(final Rectangle2D r) {
        return this.shape.intersects(r);
    }
    
    public static class EPIWrap implements ExtendedPathIterator
    {
        PathIterator pi;
        
        public EPIWrap(final PathIterator pi) {
            this.pi = null;
            this.pi = pi;
        }
        
        @Override
        public int currentSegment() {
            final float[] coords = new float[6];
            return this.pi.currentSegment(coords);
        }
        
        @Override
        public int currentSegment(final double[] coords) {
            return this.pi.currentSegment(coords);
        }
        
        @Override
        public int currentSegment(final float[] coords) {
            return this.pi.currentSegment(coords);
        }
        
        @Override
        public int getWindingRule() {
            return this.pi.getWindingRule();
        }
        
        @Override
        public boolean isDone() {
            return this.pi.isDone();
        }
        
        @Override
        public void next() {
            this.pi.next();
        }
    }
}
