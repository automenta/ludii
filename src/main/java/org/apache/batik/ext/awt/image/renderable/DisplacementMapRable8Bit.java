// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.DisplacementMapRed;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.util.Map;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.apache.batik.ext.awt.image.ARGBChannel;

public class DisplacementMapRable8Bit extends AbstractColorInterpolationRable implements DisplacementMapRable
{
    private double scale;
    private ARGBChannel xChannelSelector;
    private ARGBChannel yChannelSelector;
    
    public DisplacementMapRable8Bit(final List sources, final double scale, final ARGBChannel xChannelSelector, final ARGBChannel yChannelSelector) {
        this.setSources(sources);
        this.setScale(scale);
        this.setXChannelSelector(xChannelSelector);
        this.setYChannelSelector(yChannelSelector);
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return this.getSources().get(0).getBounds2D();
    }
    
    @Override
    public void setScale(final double scale) {
        this.touch();
        this.scale = scale;
    }
    
    @Override
    public double getScale() {
        return this.scale;
    }
    
    @Override
    public void setSources(final List sources) {
        if (sources.size() != 2) {
            throw new IllegalArgumentException();
        }
        this.init(sources, null);
    }
    
    @Override
    public void setXChannelSelector(final ARGBChannel xChannelSelector) {
        if (xChannelSelector == null) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.xChannelSelector = xChannelSelector;
    }
    
    @Override
    public ARGBChannel getXChannelSelector() {
        return this.xChannelSelector;
    }
    
    @Override
    public void setYChannelSelector(final ARGBChannel yChannelSelector) {
        if (yChannelSelector == null) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.yChannelSelector = yChannelSelector;
    }
    
    @Override
    public ARGBChannel getYChannelSelector() {
        return this.yChannelSelector;
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext rc) {
        final Filter displaced = this.getSources().get(0);
        final Filter map = this.getSources().get(1);
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
        final double atScaleX = Math.sqrt(sx * sx + shy * shy);
        final double atScaleY = Math.sqrt(sy * sy + shx * shx);
        final float scaleX = (float)(this.scale * atScaleX);
        final float scaleY = (float)(this.scale * atScaleY);
        if (scaleX == 0.0f && scaleY == 0.0f) {
            return displaced.createRendering(rc);
        }
        final AffineTransform srcAt = AffineTransform.getScaleInstance(atScaleX, atScaleY);
        Shape origAOI = rc.getAreaOfInterest();
        if (origAOI == null) {
            origAOI = this.getBounds2D();
        }
        Rectangle2D aoiR = origAOI.getBounds2D();
        RenderContext srcRc = new RenderContext(srcAt, aoiR, rh);
        RenderedImage mapRed = map.createRendering(srcRc);
        if (mapRed == null) {
            return null;
        }
        aoiR = new Rectangle2D.Double(aoiR.getX() - this.scale / 2.0, aoiR.getY() - this.scale / 2.0, aoiR.getWidth() + this.scale, aoiR.getHeight() + this.scale);
        final Rectangle2D displacedRect = displaced.getBounds2D();
        if (!aoiR.intersects(displacedRect)) {
            return null;
        }
        aoiR = aoiR.createIntersection(displacedRect);
        srcRc = new RenderContext(srcAt, aoiR, rh);
        final RenderedImage displacedRed = displaced.createRendering(srcRc);
        if (displacedRed == null) {
            return null;
        }
        mapRed = this.convertSourceCS(mapRed);
        CachableRed cr = new DisplacementMapRed(GraphicsUtil.wrap(displacedRed), GraphicsUtil.wrap(mapRed), this.xChannelSelector, this.yChannelSelector, scaleX, scaleY, rh);
        final AffineTransform resAt = new AffineTransform(sx / atScaleX, shy / atScaleX, shx / atScaleY, sy / atScaleY, tx, ty);
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
