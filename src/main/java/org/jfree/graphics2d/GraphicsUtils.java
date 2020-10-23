// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public final class GraphicsUtils
{
    private GraphicsUtils() {
    }
    
    public static Shape copyOf(final Shape shape) {
        Args.nullNotPermitted(shape, "shape");
        if (shape instanceof Line2D) {
            final Line2D l = (Line2D)shape;
            return new Line2D.Double(l.getX1(), l.getY1(), l.getX2(), l.getY2());
        }
        if (shape instanceof Rectangle2D) {
            final Rectangle2D r = (Rectangle2D)shape;
            return new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        }
        if (shape instanceof RoundRectangle2D) {
            final RoundRectangle2D rr = (RoundRectangle2D)shape;
            return new RoundRectangle2D.Double(rr.getX(), rr.getY(), rr.getWidth(), rr.getHeight(), rr.getArcWidth(), rr.getArcHeight());
        }
        if (shape instanceof Arc2D) {
            final Arc2D arc = (Arc2D)shape;
            return new Arc2D.Double(arc.getX(), arc.getY(), arc.getWidth(), arc.getHeight(), arc.getAngleStart(), arc.getAngleExtent(), arc.getArcType());
        }
        if (shape instanceof Ellipse2D) {
            final Ellipse2D ell = (Ellipse2D)shape;
            return new Ellipse2D.Double(ell.getX(), ell.getY(), ell.getWidth(), ell.getHeight());
        }
        if (shape instanceof Polygon) {
            final Polygon p = (Polygon)shape;
            return new Polygon(p.xpoints, p.ypoints, p.npoints);
        }
        return new Path2D.Double(shape);
    }
    
    public static GeneralPath createPolygon(final int[] xPoints, final int[] yPoints, final int nPoints, final boolean close) {
        final GeneralPath p = new GeneralPath();
        p.moveTo((float)xPoints[0], (float)yPoints[0]);
        for (int i = 1; i < nPoints; ++i) {
            p.lineTo((float)xPoints[i], (float)yPoints[i]);
        }
        if (close) {
            p.closePath();
        }
        return p;
    }
    
    public static BufferedImage convertRenderedImage(final RenderedImage img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage)img;
        }
        final ColorModel cm = img.getColorModel();
        final int width = img.getWidth();
        final int height = img.getHeight();
        final WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
        final boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        final Hashtable properties = new Hashtable();
        final String[] keys = img.getPropertyNames();
        if (keys != null) {
            for (int i = 0; i < keys.length; ++i) {
                properties.put(keys[i], img.getProperty(keys[i]));
            }
        }
        final BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
        img.copyData(raster);
        return result;
    }
}
