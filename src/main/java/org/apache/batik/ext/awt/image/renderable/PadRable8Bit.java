// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.rendered.CachableRed;
import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.Composite;
import java.awt.image.renderable.RenderableImage;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.Shape;
import org.apache.batik.ext.awt.image.SVGComposite;
import java.awt.Graphics2D;
import java.util.Map;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.PadMode;

public class PadRable8Bit extends AbstractRable implements PadRable, PaintRable
{
    PadMode padMode;
    Rectangle2D padRect;
    
    public PadRable8Bit(final Filter src, final Rectangle2D padRect, final PadMode padMode) {
        super.init(src, null);
        this.padRect = padRect;
        this.padMode = padMode;
    }
    
    @Override
    public Filter getSource() {
        return this.srcs.get(0);
    }
    
    @Override
    public void setSource(final Filter src) {
        super.init(src, null);
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return (Rectangle2D)this.padRect.clone();
    }
    
    @Override
    public void setPadRect(final Rectangle2D rect) {
        this.touch();
        this.padRect = rect;
    }
    
    @Override
    public Rectangle2D getPadRect() {
        return (Rectangle2D)this.padRect.clone();
    }
    
    @Override
    public void setPadMode(final PadMode padMode) {
        this.touch();
        this.padMode = padMode;
    }
    
    @Override
    public PadMode getPadMode() {
        return this.padMode;
    }
    
    @Override
    public boolean paintRable(final Graphics2D g2d) {
        final Composite c = g2d.getComposite();
        if (!SVGComposite.OVER.equals(c)) {
            return false;
        }
        if (this.getPadMode() != PadMode.ZERO_PAD) {
            return false;
        }
        final Rectangle2D padBounds = this.getPadRect();
        final Shape clip = g2d.getClip();
        g2d.clip(padBounds);
        GraphicsUtil.drawImage(g2d, this.getSource());
        g2d.setClip(clip);
        return true;
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext rc) {
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) {
            rh = new RenderingHints(null);
        }
        final Filter src = this.getSource();
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) {
            aoi = this.getBounds2D();
        }
        final AffineTransform usr2dev = rc.getTransform();
        Rectangle2D srect = src.getBounds2D();
        final Rectangle2D rect = this.getBounds2D();
        Rectangle2D arect = aoi.getBounds2D();
        if (!arect.intersects(rect)) {
            return null;
        }
        Rectangle2D.intersect(arect, rect, arect);
        RenderedImage ri = null;
        if (arect.intersects(srect)) {
            srect = (Rectangle2D)srect.clone();
            Rectangle2D.intersect(srect, arect, srect);
            final RenderContext srcRC = new RenderContext(usr2dev, srect, rh);
            ri = src.createRendering(srcRC);
        }
        if (ri == null) {
            ri = new BufferedImage(1, 1, 2);
        }
        CachableRed cr = GraphicsUtil.wrap(ri);
        arect = usr2dev.createTransformedShape(arect).getBounds2D();
        cr = new PadRed(cr, arect.getBounds(), this.padMode, rh);
        return cr;
    }
    
    @Override
    public Shape getDependencyRegion(final int srcIndex, final Rectangle2D outputRgn) {
        if (srcIndex != 0) {
            throw new IndexOutOfBoundsException("Affine only has one input");
        }
        final Rectangle2D srect = this.getSource().getBounds2D();
        if (!srect.intersects(outputRgn)) {
            return new Rectangle2D.Float();
        }
        Rectangle2D.intersect(srect, outputRgn, srect);
        final Rectangle2D bounds = this.getBounds2D();
        if (!srect.intersects(bounds)) {
            return new Rectangle2D.Float();
        }
        Rectangle2D.intersect(srect, bounds, srect);
        return srect;
    }
    
    @Override
    public Shape getDirtyRegion(final int srcIndex, Rectangle2D inputRgn) {
        if (srcIndex != 0) {
            throw new IndexOutOfBoundsException("Affine only has one input");
        }
        inputRgn = (Rectangle2D)inputRgn.clone();
        final Rectangle2D bounds = this.getBounds2D();
        if (!inputRgn.intersects(bounds)) {
            return new Rectangle2D.Float();
        }
        Rectangle2D.intersect(inputRgn, bounds, inputRgn);
        return inputRgn;
    }
}
