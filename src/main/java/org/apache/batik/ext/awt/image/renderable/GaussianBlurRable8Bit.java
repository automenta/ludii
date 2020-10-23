// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.rendered.CachableRed;
import java.awt.Rectangle;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.PadMode;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.Shape;
import org.apache.batik.ext.awt.image.rendered.GaussianBlurRed8Bit;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Map;

public class GaussianBlurRable8Bit extends AbstractColorInterpolationRable implements GaussianBlurRable
{
    private double stdDeviationX;
    private double stdDeviationY;
    static final double DSQRT2PI;
    public static final double eps = 1.0E-4;
    
    public GaussianBlurRable8Bit(final Filter src, final double stdevX, final double stdevY) {
        super(src, null);
        this.setStdDeviationX(stdevX);
        this.setStdDeviationY(stdevY);
    }
    
    @Override
    public void setStdDeviationX(final double stdDeviationX) {
        if (stdDeviationX < 0.0) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.stdDeviationX = stdDeviationX;
    }
    
    @Override
    public void setStdDeviationY(final double stdDeviationY) {
        if (stdDeviationY < 0.0) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.stdDeviationY = stdDeviationY;
    }
    
    @Override
    public double getStdDeviationX() {
        return this.stdDeviationX;
    }
    
    @Override
    public double getStdDeviationY() {
        return this.stdDeviationY;
    }
    
    @Override
    public void setSource(final Filter src) {
        this.init(src, null);
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        final Rectangle2D src = this.getSource().getBounds2D();
        final float dX = (float)(this.stdDeviationX * GaussianBlurRable8Bit.DSQRT2PI);
        final float dY = (float)(this.stdDeviationY * GaussianBlurRable8Bit.DSQRT2PI);
        final float radX = 3.0f * dX / 2.0f;
        final float radY = 3.0f * dY / 2.0f;
        return new Rectangle2D.Float((float)(src.getMinX() - radX), (float)(src.getMinY() - radY), (float)(src.getWidth() + 2.0f * radX), (float)(src.getHeight() + 2.0f * radY));
    }
    
    @Override
    public Filter getSource() {
        return this.getSources().get(0);
    }
    
    public static boolean eps_eq(final double f1, final double f2) {
        return f1 >= f2 - 1.0E-4 && f1 <= f2 + 1.0E-4;
    }
    
    public static boolean eps_abs_eq(double f1, double f2) {
        if (f1 < 0.0) {
            f1 = -f1;
        }
        if (f2 < 0.0) {
            f2 = -f2;
        }
        return eps_eq(f1, f2);
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext rc) {
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) {
            rh = new RenderingHints(null);
        }
        final AffineTransform at = rc.getTransform();
        final double sx = at.getScaleX();
        final double sy = at.getScaleY();
        final double shx = at.getShearX();
        final double shy = at.getShearY();
        final double tx = at.getTranslateX();
        final double ty = at.getTranslateY();
        double scaleX = Math.sqrt(sx * sx + shy * shy);
        double scaleY = Math.sqrt(sy * sy + shx * shx);
        double sdx = this.stdDeviationX * scaleX;
        double sdy = this.stdDeviationY * scaleY;
        AffineTransform srcAt;
        AffineTransform resAt;
        int outsetX;
        int outsetY;
        if (sdx < 10.0 && sdy < 10.0 && eps_eq(sdx, sdy) && eps_abs_eq(sx / scaleX, sy / scaleY)) {
            srcAt = at;
            resAt = null;
            outsetX = 0;
            outsetY = 0;
        }
        else {
            if (sdx > 10.0) {
                scaleX = scaleX * 10.0 / sdx;
                sdx = 10.0;
            }
            if (sdy > 10.0) {
                scaleY = scaleY * 10.0 / sdy;
                sdy = 10.0;
            }
            srcAt = AffineTransform.getScaleInstance(scaleX, scaleY);
            resAt = new AffineTransform(sx / scaleX, shy / scaleX, shx / scaleY, sy / scaleY, tx, ty);
            outsetX = 1;
            outsetY = 1;
        }
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) {
            aoi = this.getBounds2D();
        }
        final Shape devShape = srcAt.createTransformedShape(aoi);
        final Rectangle devRect = devShape.getBounds();
        outsetX += GaussianBlurRed8Bit.surroundPixels(sdx, rh);
        outsetY += GaussianBlurRed8Bit.surroundPixels(sdy, rh);
        final Rectangle rectangle = devRect;
        rectangle.x -= outsetX;
        final Rectangle rectangle2 = devRect;
        rectangle2.y -= outsetY;
        final Rectangle rectangle3 = devRect;
        rectangle3.width += 2 * outsetX;
        final Rectangle rectangle4 = devRect;
        rectangle4.height += 2 * outsetY;
        Rectangle2D r;
        try {
            final AffineTransform invSrcAt = srcAt.createInverse();
            r = invSrcAt.createTransformedShape(devRect).getBounds2D();
        }
        catch (NoninvertibleTransformException nte) {
            r = aoi.getBounds2D();
            r = new Rectangle2D.Double(r.getX() - outsetX / scaleX, r.getY() - outsetY / scaleY, r.getWidth() + 2 * outsetX / scaleX, r.getHeight() + 2 * outsetY / scaleY);
        }
        final RenderedImage ri = this.getSource().createRendering(new RenderContext(srcAt, r, rh));
        if (ri == null) {
            return null;
        }
        CachableRed cr = this.convertSourceCS(ri);
        if (!devRect.equals(cr.getBounds())) {
            cr = new PadRed(cr, devRect, PadMode.ZERO_PAD, rh);
        }
        cr = new GaussianBlurRed8Bit(cr, sdx, sdy, rh);
        if (resAt != null && !resAt.isIdentity()) {
            cr = new AffineRed(cr, resAt, rh);
        }
        return cr;
    }
    
    @Override
    public Shape getDependencyRegion(final int srcIndex, Rectangle2D outputRgn) {
        if (srcIndex != 0) {
            outputRgn = null;
        }
        else {
            final float dX = (float)(this.stdDeviationX * GaussianBlurRable8Bit.DSQRT2PI);
            final float dY = (float)(this.stdDeviationY * GaussianBlurRable8Bit.DSQRT2PI);
            final float radX = 3.0f * dX / 2.0f;
            final float radY = 3.0f * dY / 2.0f;
            outputRgn = new Rectangle2D.Float((float)(outputRgn.getMinX() - radX), (float)(outputRgn.getMinY() - radY), (float)(outputRgn.getWidth() + 2.0f * radX), (float)(outputRgn.getHeight() + 2.0f * radY));
            final Rectangle2D bounds = this.getBounds2D();
            if (!outputRgn.intersects(bounds)) {
                return new Rectangle2D.Float();
            }
            outputRgn = outputRgn.createIntersection(bounds);
        }
        return outputRgn;
    }
    
    @Override
    public Shape getDirtyRegion(final int srcIndex, Rectangle2D inputRgn) {
        Rectangle2D dirtyRegion = null;
        if (srcIndex == 0) {
            final float dX = (float)(this.stdDeviationX * GaussianBlurRable8Bit.DSQRT2PI);
            final float dY = (float)(this.stdDeviationY * GaussianBlurRable8Bit.DSQRT2PI);
            final float radX = 3.0f * dX / 2.0f;
            final float radY = 3.0f * dY / 2.0f;
            inputRgn = new Rectangle2D.Float((float)(inputRgn.getMinX() - radX), (float)(inputRgn.getMinY() - radY), (float)(inputRgn.getWidth() + 2.0f * radX), (float)(inputRgn.getHeight() + 2.0f * radY));
            final Rectangle2D bounds = this.getBounds2D();
            if (!inputRgn.intersects(bounds)) {
                return new Rectangle2D.Float();
            }
            dirtyRegion = inputRgn.createIntersection(bounds);
        }
        return dirtyRegion;
    }
    
    static {
        DSQRT2PI = Math.sqrt(6.283185307179586) * 3.0 / 4.0;
    }
}
