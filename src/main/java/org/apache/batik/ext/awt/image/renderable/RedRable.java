// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.AffineTransform;
import java.awt.Rectangle;
import java.awt.Shape;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.TranslateRed;
import java.util.Map;
import java.awt.RenderingHints;
import java.awt.image.renderable.RenderContext;
import java.awt.image.RenderedImage;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.rendered.CachableRed;

public class RedRable extends AbstractRable
{
    CachableRed src;
    
    public RedRable(final CachableRed src) {
        super((Filter)null);
        this.src = src;
    }
    
    public CachableRed getSource() {
        return this.src;
    }
    
    @Override
    public Object getProperty(final String name) {
        return this.src.getProperty(name);
    }
    
    @Override
    public String[] getPropertyNames() {
        return this.src.getPropertyNames();
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return this.getSource().getBounds();
    }
    
    @Override
    public RenderedImage createDefaultRendering() {
        return this.getSource();
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext rc) {
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) {
            rh = new RenderingHints(null);
        }
        final Shape aoi = rc.getAreaOfInterest();
        Rectangle aoiR;
        if (aoi != null) {
            aoiR = aoi.getBounds();
        }
        else {
            aoiR = this.getBounds2D().getBounds();
        }
        final AffineTransform at = rc.getTransform();
        final CachableRed cr = this.getSource();
        if (!aoiR.intersects(cr.getBounds())) {
            return null;
        }
        if (at.isIdentity()) {
            return cr;
        }
        if (at.getScaleX() == 1.0 && at.getScaleY() == 1.0 && at.getShearX() == 0.0 && at.getShearY() == 0.0) {
            final int xloc = (int)(cr.getMinX() + at.getTranslateX());
            final int yloc = (int)(cr.getMinY() + at.getTranslateY());
            final double dx = xloc - (cr.getMinX() + at.getTranslateX());
            final double dy = yloc - (cr.getMinY() + at.getTranslateY());
            if (dx > -1.0E-4 && dx < 1.0E-4 && dy > -1.0E-4 && dy < 1.0E-4) {
                return new TranslateRed(cr, xloc, yloc);
            }
        }
        return new AffineRed(cr, at, rh);
    }
}
