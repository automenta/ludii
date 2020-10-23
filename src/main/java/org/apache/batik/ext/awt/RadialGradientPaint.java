// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt;

import java.awt.geom.NoninvertibleTransformException;
import java.awt.PaintContext;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.Color;
import java.awt.geom.Point2D;

public final class RadialGradientPaint extends MultipleGradientPaint
{
    private Point2D focus;
    private Point2D center;
    private float radius;
    
    public RadialGradientPaint(final float cx, final float cy, final float radius, final float[] fractions, final Color[] colors) {
        this(cx, cy, radius, cx, cy, fractions, colors);
    }
    
    public RadialGradientPaint(final Point2D center, final float radius, final float[] fractions, final Color[] colors) {
        this(center, radius, center, fractions, colors);
    }
    
    public RadialGradientPaint(final float cx, final float cy, final float radius, final float fx, final float fy, final float[] fractions, final Color[] colors) {
        this(new Point2D.Float(cx, cy), radius, new Point2D.Float(fx, fy), fractions, colors, RadialGradientPaint.NO_CYCLE, RadialGradientPaint.SRGB);
    }
    
    public RadialGradientPaint(final Point2D center, final float radius, final Point2D focus, final float[] fractions, final Color[] colors) {
        this(center, radius, focus, fractions, colors, RadialGradientPaint.NO_CYCLE, RadialGradientPaint.SRGB);
    }
    
    public RadialGradientPaint(final Point2D center, final float radius, final Point2D focus, final float[] fractions, final Color[] colors, final CycleMethodEnum cycleMethod, final ColorSpaceEnum colorSpace) {
        this(center, radius, focus, fractions, colors, cycleMethod, colorSpace, new AffineTransform());
    }
    
    public RadialGradientPaint(final Point2D center, final float radius, final Point2D focus, final float[] fractions, final Color[] colors, final CycleMethodEnum cycleMethod, final ColorSpaceEnum colorSpace, final AffineTransform gradientTransform) {
        super(fractions, colors, cycleMethod, colorSpace, gradientTransform);
        if (center == null) {
            throw new NullPointerException("Center point should not be null.");
        }
        if (focus == null) {
            throw new NullPointerException("Focus point should not be null.");
        }
        if (radius <= 0.0f) {
            throw new IllegalArgumentException("radius should be greater than zero");
        }
        this.center = (Point2D)center.clone();
        this.focus = (Point2D)focus.clone();
        this.radius = radius;
    }
    
    public RadialGradientPaint(final Rectangle2D gradientBounds, final float[] fractions, final Color[] colors) {
        this((float)gradientBounds.getX() + (float)gradientBounds.getWidth() / 2.0f, (float)gradientBounds.getY() + (float)gradientBounds.getWidth() / 2.0f, (float)gradientBounds.getWidth() / 2.0f, fractions, colors);
    }
    
    @Override
    public PaintContext createContext(final ColorModel cm, final Rectangle deviceBounds, final Rectangle2D userBounds, AffineTransform transform, final RenderingHints hints) {
        transform = new AffineTransform(transform);
        transform.concatenate(this.gradientTransform);
        try {
            return new RadialGradientPaintContext(cm, deviceBounds, userBounds, transform, hints, (float)this.center.getX(), (float)this.center.getY(), this.radius, (float)this.focus.getX(), (float)this.focus.getY(), this.fractions, this.colors, this.cycleMethod, this.colorSpace);
        }
        catch (NoninvertibleTransformException e) {
            throw new IllegalArgumentException("transform should be invertible");
        }
    }
    
    public Point2D getCenterPoint() {
        return new Point2D.Double(this.center.getX(), this.center.getY());
    }
    
    public Point2D getFocusPoint() {
        return new Point2D.Double(this.focus.getX(), this.focus.getY());
    }
    
    public float getRadius() {
        return this.radius;
    }
}
