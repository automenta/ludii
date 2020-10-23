// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.rendered.CachableRed;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.image.rendered.FilterAlphaRed;
import org.apache.batik.ext.awt.image.rendered.RenderedImageCachableRed;
import org.apache.batik.ext.awt.ColorSpaceHintKey;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Map;

public class FilterAlphaRable extends AbstractRable
{
    public FilterAlphaRable(final Filter src) {
        super(src, null);
    }
    
    public Filter getSource() {
        return this.getSources().get(0);
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return this.getSource().getBounds2D();
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext rc) {
        final AffineTransform at = rc.getTransform();
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) {
            rh = new RenderingHints(null);
        }
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) {
            aoi = this.getBounds2D();
        }
        rh.put(RenderingHintsKeyExt.KEY_COLORSPACE, ColorSpaceHintKey.VALUE_COLORSPACE_ALPHA);
        final RenderedImage ri = this.getSource().createRendering(new RenderContext(at, aoi, rh));
        if (ri == null) {
            return null;
        }
        final CachableRed cr = RenderedImageCachableRed.wrap(ri);
        final Object val = cr.getProperty("org.apache.batik.gvt.filter.Colorspace");
        if (val == ColorSpaceHintKey.VALUE_COLORSPACE_ALPHA) {
            return cr;
        }
        return new FilterAlphaRed(cr);
    }
}
