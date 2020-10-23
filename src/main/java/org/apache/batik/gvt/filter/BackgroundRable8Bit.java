// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.filter;

import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.PadMode;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import org.apache.batik.ext.awt.image.renderable.AffineRable8Bit;
import org.apache.batik.ext.awt.image.renderable.CompositeRable8Bit;
import org.apache.batik.ext.awt.image.CompositeRule;
import java.util.ArrayList;
import org.apache.batik.ext.awt.image.renderable.Filter;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.List;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.ext.awt.image.renderable.AbstractRable;

public class BackgroundRable8Bit extends AbstractRable
{
    private GraphicsNode node;
    
    public GraphicsNode getGraphicsNode() {
        return this.node;
    }
    
    public void setGraphicsNode(final GraphicsNode node) {
        if (node == null) {
            throw new IllegalArgumentException();
        }
        this.node = node;
    }
    
    public BackgroundRable8Bit(final GraphicsNode node) {
        if (node == null) {
            throw new IllegalArgumentException();
        }
        this.node = node;
    }
    
    static Rectangle2D addBounds(final CompositeGraphicsNode cgn, final GraphicsNode child, final Rectangle2D init) {
        final List children = cgn.getChildren();
        final Iterator i = children.iterator();
        Rectangle2D r2d = null;
        while (i.hasNext()) {
            final GraphicsNode gn = i.next();
            if (gn == child) {
                break;
            }
            Rectangle2D cr2d = gn.getBounds();
            if (cr2d == null) {
                continue;
            }
            final AffineTransform at = gn.getTransform();
            if (at != null) {
                cr2d = at.createTransformedShape(cr2d).getBounds2D();
            }
            if (r2d == null) {
                r2d = (Rectangle2D)cr2d.clone();
            }
            else {
                r2d.add(cr2d);
            }
        }
        if (r2d == null) {
            if (init == null) {
                return CompositeGraphicsNode.VIEWPORT;
            }
            return init;
        }
        else {
            if (init == null) {
                return r2d;
            }
            init.add(r2d);
            return init;
        }
    }
    
    static Rectangle2D getViewportBounds(final GraphicsNode gn, final GraphicsNode child) {
        Rectangle2D r2d = null;
        if (gn instanceof CompositeGraphicsNode) {
            final CompositeGraphicsNode cgn = (CompositeGraphicsNode)gn;
            r2d = cgn.getBackgroundEnable();
        }
        if (r2d == null) {
            r2d = getViewportBounds(gn.getParent(), gn);
        }
        if (r2d == null) {
            return null;
        }
        if (r2d != CompositeGraphicsNode.VIEWPORT) {
            AffineTransform at = gn.getTransform();
            if (at != null) {
                try {
                    at = at.createInverse();
                    r2d = at.createTransformedShape(r2d).getBounds2D();
                }
                catch (NoninvertibleTransformException nte) {
                    r2d = null;
                }
            }
            if (child != null) {
                final CompositeGraphicsNode cgn2 = (CompositeGraphicsNode)gn;
                r2d = addBounds(cgn2, child, r2d);
            }
            else {
                final Rectangle2D gnb = gn.getPrimitiveBounds();
                if (gnb != null) {
                    r2d.add(gnb);
                }
            }
            return r2d;
        }
        if (child == null) {
            return (Rectangle2D)gn.getPrimitiveBounds().clone();
        }
        final CompositeGraphicsNode cgn = (CompositeGraphicsNode)gn;
        return addBounds(cgn, child, null);
    }
    
    static Rectangle2D getBoundsRecursive(final GraphicsNode gn, final GraphicsNode child) {
        Rectangle2D r2d = null;
        if (gn == null) {
            return null;
        }
        if (gn instanceof CompositeGraphicsNode) {
            final CompositeGraphicsNode cgn = (CompositeGraphicsNode)gn;
            r2d = cgn.getBackgroundEnable();
        }
        if (r2d != null) {
            return r2d;
        }
        r2d = getBoundsRecursive(gn.getParent(), gn);
        if (r2d == null) {
            return new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        }
        if (r2d == CompositeGraphicsNode.VIEWPORT) {
            return r2d;
        }
        AffineTransform at = gn.getTransform();
        if (at != null) {
            try {
                at = at.createInverse();
                r2d = at.createTransformedShape(r2d).getBounds2D();
            }
            catch (NoninvertibleTransformException nte) {
                r2d = null;
            }
        }
        return r2d;
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        Rectangle2D r2d = getBoundsRecursive(this.node, null);
        if (r2d == CompositeGraphicsNode.VIEWPORT) {
            r2d = getViewportBounds(this.node, null);
        }
        return r2d;
    }
    
    public Filter getBackground(final GraphicsNode gn, final GraphicsNode child, final Rectangle2D aoi) {
        if (gn == null) {
            throw new IllegalArgumentException("BackgroundImage requested yet no parent has 'enable-background:new'");
        }
        Rectangle2D r2d = null;
        if (gn instanceof CompositeGraphicsNode) {
            final CompositeGraphicsNode cgn = (CompositeGraphicsNode)gn;
            r2d = cgn.getBackgroundEnable();
        }
        final List srcs = new ArrayList();
        if (r2d == null) {
            Rectangle2D paoi = aoi;
            final AffineTransform at = gn.getTransform();
            if (at != null) {
                paoi = at.createTransformedShape(aoi).getBounds2D();
            }
            final Filter f = this.getBackground(gn.getParent(), gn, paoi);
            if (f != null && f.getBounds2D().intersects(aoi)) {
                srcs.add(f);
            }
        }
        if (child != null) {
            final CompositeGraphicsNode cgn2 = (CompositeGraphicsNode)gn;
            final List children = cgn2.getChildren();
            for (final Object aChildren : children) {
                final GraphicsNode childGN = (GraphicsNode)aChildren;
                if (childGN == child) {
                    break;
                }
                Rectangle2D cbounds = childGN.getBounds();
                if (cbounds == null) {
                    continue;
                }
                final AffineTransform at2 = childGN.getTransform();
                if (at2 != null) {
                    cbounds = at2.createTransformedShape(cbounds).getBounds2D();
                }
                if (!aoi.intersects(cbounds)) {
                    continue;
                }
                srcs.add(childGN.getEnableBackgroundGraphicsNodeRable(true));
            }
        }
        if (srcs.size() == 0) {
            return null;
        }
        Filter ret = null;
        if (srcs.size() == 1) {
            ret = srcs.get(0);
        }
        else {
            ret = new CompositeRable8Bit(srcs, CompositeRule.OVER, false);
        }
        if (child != null) {
            AffineTransform at = child.getTransform();
            if (at != null) {
                try {
                    at = at.createInverse();
                    ret = new AffineRable8Bit(ret, at);
                }
                catch (NoninvertibleTransformException nte) {
                    ret = null;
                }
            }
        }
        return ret;
    }
    
    @Override
    public boolean isDynamic() {
        return false;
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext renderContext) {
        final Rectangle2D r2d = this.getBounds2D();
        final Shape aoi = renderContext.getAreaOfInterest();
        if (aoi != null) {
            final Rectangle2D aoiR2d = aoi.getBounds2D();
            if (!r2d.intersects(aoiR2d)) {
                return null;
            }
            Rectangle2D.intersect(r2d, aoiR2d, r2d);
        }
        Filter background = this.getBackground(this.node, null, r2d);
        if (background == null) {
            return null;
        }
        background = new PadRable8Bit(background, r2d, PadMode.ZERO_PAD);
        final RenderedImage ri = background.createRendering(new RenderContext(renderContext.getTransform(), r2d, renderContext.getRenderingHints()));
        return ri;
    }
}
