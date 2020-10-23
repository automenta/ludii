// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.rendered.CachableRed;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.image.rendered.CompositeRed;
import org.apache.batik.ext.awt.image.rendered.FloodRed;
import java.util.ArrayList;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.util.Iterator;
import java.awt.color.ColorSpace;
import java.awt.Composite;
import java.awt.image.renderable.RenderableImage;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.SVGComposite;
import java.awt.Graphics2D;
import java.util.Map;
import java.util.List;
import org.apache.batik.ext.awt.image.CompositeRule;

public class CompositeRable8Bit extends AbstractColorInterpolationRable implements CompositeRable, PaintRable
{
    protected CompositeRule rule;
    
    public CompositeRable8Bit(final List srcs, final CompositeRule rule, final boolean csIsLinear) {
        super(srcs);
        this.setColorSpaceLinear(csIsLinear);
        this.rule = rule;
    }
    
    @Override
    public void setSources(final List srcs) {
        this.init(srcs, null);
    }
    
    @Override
    public void setCompositeRule(final CompositeRule cr) {
        this.touch();
        this.rule = cr;
    }
    
    @Override
    public CompositeRule getCompositeRule() {
        return this.rule;
    }
    
    @Override
    public boolean paintRable(final Graphics2D g2d) {
        final Composite c = g2d.getComposite();
        if (!SVGComposite.OVER.equals(c)) {
            return false;
        }
        if (this.getCompositeRule() != CompositeRule.OVER) {
            return false;
        }
        final ColorSpace crCS = this.getOperationColorSpace();
        final ColorSpace g2dCS = GraphicsUtil.getDestinationColorSpace(g2d);
        if (g2dCS == null || g2dCS != crCS) {
            return false;
        }
        for (final Object o : this.getSources()) {
            GraphicsUtil.drawImage(g2d, (RenderableImage)o);
        }
        return true;
    }
    
    @Override
    public RenderedImage createRendering(RenderContext rc) {
        if (this.srcs.size() == 0) {
            return null;
        }
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) {
            rh = new RenderingHints(null);
        }
        final AffineTransform at = rc.getTransform();
        final Shape aoi = rc.getAreaOfInterest();
        Rectangle2D aoiR;
        if (aoi == null) {
            aoiR = this.getBounds2D();
        }
        else {
            aoiR = aoi.getBounds2D();
            final Rectangle2D bounds2d = this.getBounds2D();
            if (!bounds2d.intersects(aoiR)) {
                return null;
            }
            Rectangle2D.intersect(aoiR, bounds2d, aoiR);
        }
        final Rectangle devRect = at.createTransformedShape(aoiR).getBounds();
        rc = new RenderContext(at, aoiR, rh);
        final List srcs = new ArrayList();
        for (final Object o : this.getSources()) {
            final Filter filt = (Filter)o;
            final RenderedImage ri = filt.createRendering(rc);
            if (ri != null) {
                final CachableRed cr = this.convertSourceCS(ri);
                srcs.add(cr);
            }
            else {
                switch (this.rule.getRule()) {
                    case 2: {
                        return null;
                    }
                    case 3: {
                        srcs.clear();
                        continue;
                    }
                    case 6: {
                        srcs.add(new FloodRed(devRect));
                        continue;
                    }
                }
            }
        }
        if (srcs.size() == 0) {
            return null;
        }
        final CachableRed cr2 = new CompositeRed(srcs, this.rule);
        return cr2;
    }
}
