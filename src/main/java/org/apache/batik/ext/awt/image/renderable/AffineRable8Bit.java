// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.util.Map;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.Graphics2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

public class AffineRable8Bit extends AbstractRable implements AffineRable, PaintRable
{
    AffineTransform affine;
    AffineTransform invAffine;
    
    public AffineRable8Bit(final Filter src, final AffineTransform affine) {
        this.init(src);
        this.setAffine(affine);
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        final Filter src = this.getSource();
        final Rectangle2D r = src.getBounds2D();
        return this.affine.createTransformedShape(r).getBounds2D();
    }
    
    @Override
    public Filter getSource() {
        return this.srcs.get(0);
    }
    
    @Override
    public void setSource(final Filter src) {
        this.init(src);
    }
    
    @Override
    public void setAffine(final AffineTransform affine) {
        this.touch();
        this.affine = affine;
        try {
            this.invAffine = affine.createInverse();
        }
        catch (NoninvertibleTransformException e) {
            this.invAffine = null;
        }
    }
    
    @Override
    public AffineTransform getAffine() {
        return (AffineTransform)this.affine.clone();
    }
    
    @Override
    public boolean paintRable(final Graphics2D g2d) {
        final AffineTransform at = g2d.getTransform();
        g2d.transform(this.getAffine());
        GraphicsUtil.drawImage(g2d, this.getSource());
        g2d.setTransform(at);
        return true;
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext rc) {
        if (this.invAffine == null) {
            return null;
        }
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) {
            rh = new RenderingHints(null);
        }
        Shape aoi = rc.getAreaOfInterest();
        if (aoi != null) {
            aoi = this.invAffine.createTransformedShape(aoi);
        }
        final AffineTransform at = rc.getTransform();
        at.concatenate(this.affine);
        return this.getSource().createRendering(new RenderContext(at, aoi, rh));
    }
    
    @Override
    public Shape getDependencyRegion(final int srcIndex, final Rectangle2D outputRgn) {
        if (srcIndex != 0) {
            throw new IndexOutOfBoundsException("Affine only has one input");
        }
        if (this.invAffine == null) {
            return null;
        }
        return this.invAffine.createTransformedShape(outputRgn);
    }
    
    @Override
    public Shape getDirtyRegion(final int srcIndex, final Rectangle2D inputRgn) {
        if (srcIndex != 0) {
            throw new IndexOutOfBoundsException("Affine only has one input");
        }
        return this.affine.createTransformedShape(inputRgn);
    }
}
