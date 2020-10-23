// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Rectangle;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import java.awt.geom.Rectangle2D;
import java.lang.ref.SoftReference;
import org.apache.batik.ext.awt.image.rendered.TileCacheRed;
import java.awt.image.renderable.RenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.RenderingHints;
import java.awt.Composite;
import org.apache.batik.ext.awt.image.SVGComposite;
import java.util.ListIterator;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.CompositeRule;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.List;
import java.awt.image.renderable.RenderableImage;
import java.util.Map;
import java.lang.ref.Reference;

public class FilterResRable8Bit extends AbstractRable implements FilterResRable, PaintRable
{
    private int filterResolutionX;
    private int filterResolutionY;
    Reference resRed;
    float resScale;
    
    public FilterResRable8Bit() {
        this.filterResolutionX = -1;
        this.filterResolutionY = -1;
        this.resRed = null;
        this.resScale = 0.0f;
    }
    
    public FilterResRable8Bit(final Filter src, final int filterResX, final int filterResY) {
        this.filterResolutionX = -1;
        this.filterResolutionY = -1;
        this.resRed = null;
        this.resScale = 0.0f;
        this.init(src, null);
        this.setFilterResolutionX(filterResX);
        this.setFilterResolutionY(filterResY);
    }
    
    @Override
    public Filter getSource() {
        return this.srcs.get(0);
    }
    
    @Override
    public void setSource(final Filter src) {
        this.init(src, null);
    }
    
    @Override
    public int getFilterResolutionX() {
        return this.filterResolutionX;
    }
    
    @Override
    public void setFilterResolutionX(final int filterResolutionX) {
        if (filterResolutionX < 0) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.filterResolutionX = filterResolutionX;
    }
    
    @Override
    public int getFilterResolutionY() {
        return this.filterResolutionY;
    }
    
    @Override
    public void setFilterResolutionY(final int filterResolutionY) {
        this.touch();
        this.filterResolutionY = filterResolutionY;
    }
    
    public boolean allPaintRable(final RenderableImage ri) {
        if (!(ri instanceof PaintRable)) {
            return false;
        }
        final List v = ri.getSources();
        if (v == null) {
            return true;
        }
        for (final Object aV : v) {
            final RenderableImage nri = (RenderableImage)aV;
            if (!this.allPaintRable(nri)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean distributeAcross(final RenderableImage src, final Graphics2D g2d) {
        if (src instanceof PadRable) {
            final PadRable pad = (PadRable)src;
            final Shape clip = g2d.getClip();
            g2d.clip(pad.getPadRect());
            final boolean ret = this.distributeAcross(pad.getSource(), g2d);
            g2d.setClip(clip);
            return ret;
        }
        if (!(src instanceof CompositeRable)) {
            return false;
        }
        final CompositeRable comp = (CompositeRable)src;
        if (comp.getCompositeRule() != CompositeRule.OVER) {
            return false;
        }
        final List v = comp.getSources();
        if (v == null) {
            return true;
        }
        final ListIterator li = v.listIterator(v.size());
        while (li.hasPrevious()) {
            final RenderableImage csrc = li.previous();
            if (!this.allPaintRable(csrc)) {
                li.next();
                break;
            }
        }
        if (!li.hasPrevious()) {
            GraphicsUtil.drawImage(g2d, comp);
            return true;
        }
        if (!li.hasNext()) {
            return false;
        }
        final int idx = li.nextIndex();
        Filter f = new CompositeRable8Bit(v.subList(0, idx), comp.getCompositeRule(), comp.isColorSpaceLinear());
        f = new FilterResRable8Bit(f, this.getFilterResolutionX(), this.getFilterResolutionY());
        GraphicsUtil.drawImage(g2d, f);
        while (li.hasNext()) {
            final PaintRable pr = li.next();
            if (!pr.paintRable(g2d)) {
                Filter prf = (Filter)pr;
                prf = new FilterResRable8Bit(prf, this.getFilterResolutionX(), this.getFilterResolutionY());
                GraphicsUtil.drawImage(g2d, prf);
            }
        }
        return true;
    }
    
    @Override
    public boolean paintRable(final Graphics2D g2d) {
        final Composite c = g2d.getComposite();
        if (!SVGComposite.OVER.equals(c)) {
            return false;
        }
        final Filter src = this.getSource();
        return this.distributeAcross(src, g2d);
    }
    
    private float getResScale() {
        return this.resScale;
    }
    
    private RenderedImage getResRed(final RenderingHints hints) {
        final Rectangle2D imageRect = this.getBounds2D();
        final double resScaleX = this.getFilterResolutionX() / imageRect.getWidth();
        final double resScaleY = this.getFilterResolutionY() / imageRect.getHeight();
        final float resScale = (float)Math.min(resScaleX, resScaleY);
        if (resScale == this.resScale) {
            final RenderedImage ret = this.resRed.get();
            if (ret != null) {
                return ret;
            }
        }
        final AffineTransform resUsr2Dev = AffineTransform.getScaleInstance(resScale, resScale);
        final RenderContext newRC = new RenderContext(resUsr2Dev, null, hints);
        RenderedImage ret = this.getSource().createRendering(newRC);
        ret = new TileCacheRed(GraphicsUtil.wrap(ret));
        this.resScale = resScale;
        this.resRed = new SoftReference(ret);
        return ret;
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext renderContext) {
        AffineTransform usr2dev = renderContext.getTransform();
        if (usr2dev == null) {
            usr2dev = new AffineTransform();
        }
        final RenderingHints hints = renderContext.getRenderingHints();
        final int filterResolutionX = this.getFilterResolutionX();
        final int filterResolutionY = this.getFilterResolutionY();
        if (filterResolutionX <= 0 || filterResolutionY == 0) {
            return null;
        }
        final Rectangle2D imageRect = this.getBounds2D();
        final Rectangle devRect = usr2dev.createTransformedShape(imageRect).getBounds();
        float scaleX = 1.0f;
        if (filterResolutionX < devRect.width) {
            scaleX = filterResolutionX / (float)devRect.width;
        }
        float scaleY = 1.0f;
        if (filterResolutionY < 0) {
            scaleY = scaleX;
        }
        else if (filterResolutionY < devRect.height) {
            scaleY = filterResolutionY / (float)devRect.height;
        }
        if (scaleX >= 1.0f && scaleY >= 1.0f) {
            return this.getSource().createRendering(renderContext);
        }
        final RenderedImage resRed = this.getResRed(hints);
        final float resScale = this.getResScale();
        final AffineTransform residualAT = new AffineTransform(usr2dev.getScaleX() / resScale, usr2dev.getShearY() / resScale, usr2dev.getShearX() / resScale, usr2dev.getScaleY() / resScale, usr2dev.getTranslateX(), usr2dev.getTranslateY());
        return new AffineRed(GraphicsUtil.wrap(resRed), residualAT, hints);
    }
}
