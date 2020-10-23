// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.rendered.CachableRed;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.rendered.MultiplyAlphaRed;
import org.apache.batik.ext.awt.image.rendered.BufferedImageCachableRed;
import org.apache.batik.ext.awt.image.rendered.RenderedImageCachableRed;
import java.awt.Paint;
import java.awt.Color;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.awt.Shape;

public class ClipRable8Bit extends AbstractRable implements ClipRable
{
    protected boolean useAA;
    protected Shape clipPath;
    
    public ClipRable8Bit(final Filter src, final Shape clipPath) {
        super(src, null);
        this.setClipPath(clipPath);
        this.setUseAntialiasedClip(false);
    }
    
    public ClipRable8Bit(final Filter src, final Shape clipPath, final boolean useAA) {
        super(src, null);
        this.setClipPath(clipPath);
        this.setUseAntialiasedClip(useAA);
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
    public void setUseAntialiasedClip(final boolean useAA) {
        this.touch();
        this.useAA = useAA;
    }
    
    @Override
    public boolean getUseAntialiasedClip() {
        return this.useAA;
    }
    
    @Override
    public void setClipPath(final Shape clipPath) {
        this.touch();
        this.clipPath = clipPath;
    }
    
    @Override
    public Shape getClipPath() {
        return this.clipPath;
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return this.getSource().getBounds2D();
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext rc) {
        final AffineTransform usr2dev = rc.getTransform();
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) {
            rh = new RenderingHints(null);
        }
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) {
            aoi = this.getBounds2D();
        }
        final Rectangle2D rect = this.getBounds2D();
        final Rectangle2D clipRect = this.clipPath.getBounds2D();
        final Rectangle2D aoiRect = aoi.getBounds2D();
        if (!rect.intersects(clipRect)) {
            return null;
        }
        Rectangle2D.intersect(rect, clipRect, rect);
        if (!rect.intersects(aoiRect)) {
            return null;
        }
        Rectangle2D.intersect(rect, aoi.getBounds2D(), rect);
        final Rectangle devR = usr2dev.createTransformedShape(rect).getBounds();
        if (devR.width == 0 || devR.height == 0) {
            return null;
        }
        final BufferedImage bi = new BufferedImage(devR.width, devR.height, 10);
        final Shape devShape = usr2dev.createTransformedShape(this.getClipPath());
        final Rectangle devAOIR = usr2dev.createTransformedShape(aoi).getBounds();
        final Graphics2D g2d = GraphicsUtil.createGraphics(bi, rh);
        g2d.translate(-devR.x, -devR.y);
        g2d.setPaint(Color.white);
        g2d.fill(devShape);
        g2d.dispose();
        final RenderedImage ri = this.getSource().createRendering(new RenderContext(usr2dev, rect, rh));
        final CachableRed cr = RenderedImageCachableRed.wrap(ri);
        final CachableRed clipCr = new BufferedImageCachableRed(bi, devR.x, devR.y);
        CachableRed ret = new MultiplyAlphaRed(cr, clipCr);
        ret = new PadRed(ret, devAOIR, PadMode.ZERO_PAD, rh);
        return ret;
    }
}
