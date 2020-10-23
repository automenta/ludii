// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.BufferedImageCachableRed;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.Point;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.rendered.RenderedImageCachableRed;
import java.awt.Shape;
import org.apache.batik.ext.awt.image.rendered.MorphologyOp;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Map;

public class MorphologyRable8Bit extends AbstractRable implements MorphologyRable
{
    private double radiusX;
    private double radiusY;
    private boolean doDilation;
    
    public MorphologyRable8Bit(final Filter src, final double radiusX, final double radiusY, final boolean doDilation) {
        super(src, null);
        this.setRadiusX(radiusX);
        this.setRadiusY(radiusY);
        this.setDoDilation(doDilation);
    }
    
    @Override
    public Filter getSource() {
        return this.getSources().get(0);
    }
    
    @Override
    public void setSource(final Filter src) {
        this.init(src, null);
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return this.getSource().getBounds2D();
    }
    
    @Override
    public void setRadiusX(final double radiusX) {
        if (radiusX <= 0.0) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.radiusX = radiusX;
    }
    
    @Override
    public void setRadiusY(final double radiusY) {
        if (radiusY <= 0.0) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.radiusY = radiusY;
    }
    
    @Override
    public void setDoDilation(final boolean doDilation) {
        this.touch();
        this.doDilation = doDilation;
    }
    
    @Override
    public boolean getDoDilation() {
        return this.doDilation;
    }
    
    @Override
    public double getRadiusX() {
        return this.radiusX;
    }
    
    @Override
    public double getRadiusY() {
        return this.radiusY;
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
        final double scaleX = Math.sqrt(sx * sx + shy * shy);
        final double scaleY = Math.sqrt(sy * sy + shx * shx);
        final AffineTransform srcAt = AffineTransform.getScaleInstance(scaleX, scaleY);
        final int radX = (int)Math.round(this.radiusX * scaleX);
        final int radY = (int)Math.round(this.radiusY * scaleY);
        MorphologyOp op = null;
        if (radX > 0 && radY > 0) {
            op = new MorphologyOp(radX, radY, this.doDilation);
        }
        final AffineTransform resAt = new AffineTransform(sx / scaleX, shy / scaleX, shx / scaleY, sy / scaleY, tx, ty);
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) {
            aoi = this.getBounds2D();
        }
        Rectangle2D r = aoi.getBounds2D();
        r = new Rectangle2D.Double(r.getX() - radX / scaleX, r.getY() - radY / scaleY, r.getWidth() + 2 * radX / scaleX, r.getHeight() + 2 * radY / scaleY);
        final RenderedImage ri = this.getSource().createRendering(new RenderContext(srcAt, r, rh));
        if (ri == null) {
            return null;
        }
        CachableRed cr = new RenderedImageCachableRed(ri);
        final Shape devShape = srcAt.createTransformedShape(aoi.getBounds2D());
        r = devShape.getBounds2D();
        r = new Rectangle2D.Double(r.getX() - radX, r.getY() - radY, r.getWidth() + 2 * radX, r.getHeight() + 2 * radY);
        cr = new PadRed(cr, r.getBounds(), PadMode.ZERO_PAD, rh);
        final ColorModel cm = ri.getColorModel();
        final Raster rr = cr.getData();
        final Point pt = new Point(0, 0);
        final WritableRaster wr = Raster.createWritableRaster(rr.getSampleModel(), rr.getDataBuffer(), pt);
        final BufferedImage srcBI = new BufferedImage(cm, wr, cm.isAlphaPremultiplied(), null);
        BufferedImage destBI;
        if (op != null) {
            destBI = op.filter(srcBI, null);
        }
        else {
            destBI = srcBI;
        }
        final int rrMinX = cr.getMinX();
        final int rrMinY = cr.getMinY();
        cr = new BufferedImageCachableRed(destBI, rrMinX, rrMinY);
        if (!resAt.isIdentity()) {
            cr = new AffineRed(cr, resAt, rh);
        }
        return cr;
    }
    
    @Override
    public Shape getDependencyRegion(final int srcIndex, final Rectangle2D outputRgn) {
        return super.getDependencyRegion(srcIndex, outputRgn);
    }
    
    @Override
    public Shape getDirtyRegion(final int srcIndex, final Rectangle2D inputRgn) {
        return super.getDirtyRegion(srcIndex, inputRgn);
    }
}
