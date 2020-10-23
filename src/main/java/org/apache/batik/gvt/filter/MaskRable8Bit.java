// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.filter;

import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.renderable.PadRable;
import org.apache.batik.ext.awt.image.rendered.MultiplyAlphaRed;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.rendered.RenderedImageCachableRed;
import org.apache.batik.ext.awt.image.renderable.FilterAsAlphaRable;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.PadMode;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.util.Map;
import org.apache.batik.ext.awt.image.renderable.Filter;
import java.awt.geom.Rectangle2D;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.ext.awt.image.renderable.AbstractRable;

public class MaskRable8Bit extends AbstractRable implements Mask
{
    protected GraphicsNode mask;
    protected Rectangle2D filterRegion;
    
    public MaskRable8Bit(final Filter src, final GraphicsNode mask, final Rectangle2D filterRegion) {
        super(src, null);
        this.setMaskNode(mask);
        this.setFilterRegion(filterRegion);
    }
    
    @Override
    public void setSource(final Filter src) {
        this.init(src, null);
    }
    
    @Override
    public Filter getSource() {
        return this.getSources().get(0);
    }
    
    @Override
    public Rectangle2D getFilterRegion() {
        return (Rectangle2D)this.filterRegion.clone();
    }
    
    @Override
    public void setFilterRegion(final Rectangle2D filterRegion) {
        if (filterRegion == null) {
            throw new IllegalArgumentException();
        }
        this.filterRegion = filterRegion;
    }
    
    @Override
    public void setMaskNode(final GraphicsNode mask) {
        this.touch();
        this.mask = mask;
    }
    
    @Override
    public GraphicsNode getMaskNode() {
        return this.mask;
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return (Rectangle2D)this.filterRegion.clone();
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext rc) {
        Filter maskSrc = this.getMaskNode().getGraphicsNodeRable(true);
        final PadRable maskPad = new PadRable8Bit(maskSrc, this.getBounds2D(), PadMode.ZERO_PAD);
        maskSrc = new FilterAsAlphaRable(maskPad);
        RenderedImage ri = maskSrc.createRendering(rc);
        if (ri == null) {
            return null;
        }
        final CachableRed maskCr = RenderedImageCachableRed.wrap(ri);
        final PadRable maskedPad = new PadRable8Bit(this.getSource(), this.getBounds2D(), PadMode.ZERO_PAD);
        ri = maskedPad.createRendering(rc);
        if (ri == null) {
            return null;
        }
        CachableRed cr = GraphicsUtil.wrap(ri);
        cr = GraphicsUtil.convertToLsRGB(cr);
        final CachableRed ret = new MultiplyAlphaRed(cr, maskCr);
        return ret;
    }
}
