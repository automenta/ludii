// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt;

import java.awt.geom.NoninvertibleTransformException;
import java.awt.PaintContext;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.geom.AffineTransform;
import java.awt.Color;
import java.awt.geom.Point2D;

public final class LinearGradientPaint extends MultipleGradientPaint
{
    private Point2D start;
    private Point2D end;
    
    public LinearGradientPaint(final float startX, final float startY, final float endX, final float endY, final float[] fractions, final Color[] colors) {
        this(new Point2D.Float(startX, startY), new Point2D.Float(endX, endY), fractions, colors, LinearGradientPaint.NO_CYCLE, LinearGradientPaint.SRGB);
    }
    
    public LinearGradientPaint(final float startX, final float startY, final float endX, final float endY, final float[] fractions, final Color[] colors, final CycleMethodEnum cycleMethod) {
        this(new Point2D.Float(startX, startY), new Point2D.Float(endX, endY), fractions, colors, cycleMethod, LinearGradientPaint.SRGB);
    }
    
    public LinearGradientPaint(final Point2D start, final Point2D end, final float[] fractions, final Color[] colors) {
        this(start, end, fractions, colors, LinearGradientPaint.NO_CYCLE, LinearGradientPaint.SRGB);
    }
    
    public LinearGradientPaint(final Point2D start, final Point2D end, final float[] fractions, final Color[] colors, final CycleMethodEnum cycleMethod, final ColorSpaceEnum colorSpace) {
        this(start, end, fractions, colors, cycleMethod, colorSpace, new AffineTransform());
    }
    
    public LinearGradientPaint(final Point2D start, final Point2D end, final float[] fractions, final Color[] colors, final CycleMethodEnum cycleMethod, final ColorSpaceEnum colorSpace, final AffineTransform gradientTransform) {
        super(fractions, colors, cycleMethod, colorSpace, gradientTransform);
        if (start == null || end == null) {
            throw new NullPointerException("Start and end points must benon-null");
        }
        if (start.equals(end)) {
            throw new IllegalArgumentException("Start point cannot equalendpoint");
        }
        this.start = (Point2D)start.clone();
        this.end = (Point2D)end.clone();
    }
    
    @Override
    public PaintContext createContext(final ColorModel cm, final Rectangle deviceBounds, final Rectangle2D userBounds, AffineTransform transform, final RenderingHints hints) {
        transform = new AffineTransform(transform);
        transform.concatenate(this.gradientTransform);
        try {
            return new LinearGradientPaintContext(cm, deviceBounds, userBounds, transform, hints, this.start, this.end, this.fractions, this.getColors(), this.cycleMethod, this.colorSpace);
        }
        catch (NoninvertibleTransformException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("transform should beinvertible");
        }
    }
    
    public Point2D getStartPoint() {
        return new Point2D.Double(this.start.getX(), this.start.getY());
    }
    
    public Point2D getEndPoint() {
        return new Point2D.Double(this.end.getX(), this.end.getY());
    }
}
