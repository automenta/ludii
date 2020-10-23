// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.geom;

import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.Polygon;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.awt.Shape;

public class Polygon2D implements Shape, Cloneable, Serializable
{
    public int npoints;
    public float[] xpoints;
    public float[] ypoints;
    protected Rectangle2D bounds;
    private GeneralPath path;
    private GeneralPath closedPath;
    
    public Polygon2D() {
        this.xpoints = new float[4];
        this.ypoints = new float[4];
    }
    
    public Polygon2D(final Rectangle2D rec) {
        if (rec == null) {
            throw new IndexOutOfBoundsException("null Rectangle");
        }
        this.npoints = 4;
        this.xpoints = new float[4];
        this.ypoints = new float[4];
        this.xpoints[0] = (float)rec.getMinX();
        this.ypoints[0] = (float)rec.getMinY();
        this.xpoints[1] = (float)rec.getMaxX();
        this.ypoints[1] = (float)rec.getMinY();
        this.xpoints[2] = (float)rec.getMaxX();
        this.ypoints[2] = (float)rec.getMaxY();
        this.xpoints[3] = (float)rec.getMinX();
        this.ypoints[3] = (float)rec.getMaxY();
        this.calculatePath();
    }
    
    public Polygon2D(final Polygon pol) {
        if (pol == null) {
            throw new IndexOutOfBoundsException("null Polygon");
        }
        this.npoints = pol.npoints;
        this.xpoints = new float[pol.npoints];
        this.ypoints = new float[pol.npoints];
        for (int i = 0; i < pol.npoints; ++i) {
            this.xpoints[i] = (float)pol.xpoints[i];
            this.ypoints[i] = (float)pol.ypoints[i];
        }
        this.calculatePath();
    }
    
    public Polygon2D(final float[] xpoints, final float[] ypoints, final int npoints) {
        if (npoints > xpoints.length || npoints > ypoints.length) {
            throw new IndexOutOfBoundsException("npoints > xpoints.length || npoints > ypoints.length");
        }
        this.npoints = npoints;
        this.xpoints = new float[npoints];
        this.ypoints = new float[npoints];
        System.arraycopy(xpoints, 0, this.xpoints, 0, npoints);
        System.arraycopy(ypoints, 0, this.ypoints, 0, npoints);
        this.calculatePath();
    }
    
    public Polygon2D(final int[] xpoints, final int[] ypoints, final int npoints) {
        if (npoints > xpoints.length || npoints > ypoints.length) {
            throw new IndexOutOfBoundsException("npoints > xpoints.length || npoints > ypoints.length");
        }
        this.npoints = npoints;
        this.xpoints = new float[npoints];
        this.ypoints = new float[npoints];
        for (int i = 0; i < npoints; ++i) {
            this.xpoints[i] = (float)xpoints[i];
            this.ypoints[i] = (float)ypoints[i];
        }
        this.calculatePath();
    }
    
    public void reset() {
        this.npoints = 0;
        this.bounds = null;
        this.path = new GeneralPath();
        this.closedPath = null;
    }
    
    public Object clone() {
        final Polygon2D pol = new Polygon2D();
        for (int i = 0; i < this.npoints; ++i) {
            pol.addPoint(this.xpoints[i], this.ypoints[i]);
        }
        return pol;
    }
    
    private void calculatePath() {
        (this.path = new GeneralPath()).moveTo(this.xpoints[0], this.ypoints[0]);
        for (int i = 1; i < this.npoints; ++i) {
            this.path.lineTo(this.xpoints[i], this.ypoints[i]);
        }
        this.bounds = this.path.getBounds2D();
        this.closedPath = null;
    }
    
    private void updatePath(final float x, final float y) {
        this.closedPath = null;
        if (this.path == null) {
            (this.path = new GeneralPath(0)).moveTo(x, y);
            this.bounds = new Rectangle2D.Float(x, y, 0.0f, 0.0f);
        }
        else {
            this.path.lineTo(x, y);
            float _xmax = (float)this.bounds.getMaxX();
            float _ymax = (float)this.bounds.getMaxY();
            float _xmin = (float)this.bounds.getMinX();
            float _ymin = (float)this.bounds.getMinY();
            if (x < _xmin) {
                _xmin = x;
            }
            else if (x > _xmax) {
                _xmax = x;
            }
            if (y < _ymin) {
                _ymin = y;
            }
            else if (y > _ymax) {
                _ymax = y;
            }
            this.bounds = new Rectangle2D.Float(_xmin, _ymin, _xmax - _xmin, _ymax - _ymin);
        }
    }
    
    public Polyline2D getPolyline2D() {
        final Polyline2D pol = new Polyline2D(this.xpoints, this.ypoints, this.npoints);
        pol.addPoint(this.xpoints[0], this.ypoints[0]);
        return pol;
    }
    
    public Polygon getPolygon() {
        final int[] _xpoints = new int[this.npoints];
        final int[] _ypoints = new int[this.npoints];
        for (int i = 0; i < this.npoints; ++i) {
            _xpoints[i] = (int)this.xpoints[i];
            _ypoints[i] = (int)this.ypoints[i];
        }
        return new Polygon(_xpoints, _ypoints, this.npoints);
    }
    
    public void addPoint(final Point2D p) {
        this.addPoint((float)p.getX(), (float)p.getY());
    }
    
    public void addPoint(final float x, final float y) {
        if (this.npoints == this.xpoints.length) {
            float[] tmp = new float[this.npoints * 2];
            System.arraycopy(this.xpoints, 0, tmp, 0, this.npoints);
            this.xpoints = tmp;
            tmp = new float[this.npoints * 2];
            System.arraycopy(this.ypoints, 0, tmp, 0, this.npoints);
            this.ypoints = tmp;
        }
        this.xpoints[this.npoints] = x;
        this.ypoints[this.npoints] = y;
        ++this.npoints;
        this.updatePath(x, y);
    }
    
    public boolean contains(final Point p) {
        return this.contains(p.x, p.y);
    }
    
    public boolean contains(final int x, final int y) {
        return this.contains(x, (double)y);
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return this.bounds;
    }
    
    @Override
    public Rectangle getBounds() {
        if (this.bounds == null) {
            return null;
        }
        return this.bounds.getBounds();
    }
    
    @Override
    public boolean contains(final double x, final double y) {
        if (this.npoints <= 2 || !this.bounds.contains(x, y)) {
            return false;
        }
        this.updateComputingPath();
        return this.closedPath.contains(x, y);
    }
    
    private void updateComputingPath() {
        if (this.npoints >= 1 && this.closedPath == null) {
            (this.closedPath = (GeneralPath)this.path.clone()).closePath();
        }
    }
    
    @Override
    public boolean contains(final Point2D p) {
        return this.contains(p.getX(), p.getY());
    }
    
    @Override
    public boolean intersects(final double x, final double y, final double w, final double h) {
        if (this.npoints <= 0 || !this.bounds.intersects(x, y, w, h)) {
            return false;
        }
        this.updateComputingPath();
        return this.closedPath.intersects(x, y, w, h);
    }
    
    @Override
    public boolean intersects(final Rectangle2D r) {
        return this.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }
    
    @Override
    public boolean contains(final double x, final double y, final double w, final double h) {
        if (this.npoints <= 0 || !this.bounds.intersects(x, y, w, h)) {
            return false;
        }
        this.updateComputingPath();
        return this.closedPath.contains(x, y, w, h);
    }
    
    @Override
    public boolean contains(final Rectangle2D r) {
        return this.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform at) {
        this.updateComputingPath();
        if (this.closedPath == null) {
            return null;
        }
        return this.closedPath.getPathIterator(at);
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform at, final double flatness) {
        return this.getPathIterator(at);
    }
}
