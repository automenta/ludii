// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.filter;

import org.apache.batik.ext.awt.image.rendered.TranslateRed;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.Composite;
import java.awt.color.ColorSpace;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.SVGComposite;
import java.awt.Graphics2D;
import java.awt.Shape;
import org.apache.batik.ext.awt.image.renderable.Filter;
import java.util.Map;
import org.apache.batik.gvt.GraphicsNode;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.image.renderable.PaintRable;
import org.apache.batik.ext.awt.image.renderable.AbstractRable;

public class GraphicsNodeRable8Bit extends AbstractRable implements GraphicsNodeRable, PaintRable
{
    private AffineTransform cachedGn2dev;
    private AffineTransform cachedUsr2dev;
    private CachableRed cachedRed;
    private Rectangle2D cachedBounds;
    private boolean usePrimitivePaint;
    private GraphicsNode node;
    
    @Override
    public boolean getUsePrimitivePaint() {
        return this.usePrimitivePaint;
    }
    
    @Override
    public void setUsePrimitivePaint(final boolean usePrimitivePaint) {
        this.usePrimitivePaint = usePrimitivePaint;
    }
    
    @Override
    public GraphicsNode getGraphicsNode() {
        return this.node;
    }
    
    @Override
    public void setGraphicsNode(final GraphicsNode node) {
        if (node == null) {
            throw new IllegalArgumentException();
        }
        this.node = node;
    }
    
    public void clearCache() {
        this.cachedRed = null;
        this.cachedUsr2dev = null;
        this.cachedGn2dev = null;
        this.cachedBounds = null;
    }
    
    public GraphicsNodeRable8Bit(final GraphicsNode node) {
        this.cachedGn2dev = null;
        this.cachedUsr2dev = null;
        this.cachedRed = null;
        this.cachedBounds = null;
        this.usePrimitivePaint = true;
        if (node == null) {
            throw new IllegalArgumentException();
        }
        this.node = node;
        this.usePrimitivePaint = true;
    }
    
    public GraphicsNodeRable8Bit(final GraphicsNode node, final Map props) {
        super((Filter)null, props);
        this.cachedGn2dev = null;
        this.cachedUsr2dev = null;
        this.cachedRed = null;
        this.cachedBounds = null;
        this.usePrimitivePaint = true;
        if (node == null) {
            throw new IllegalArgumentException();
        }
        this.node = node;
        this.usePrimitivePaint = true;
    }
    
    public GraphicsNodeRable8Bit(final GraphicsNode node, final boolean usePrimitivePaint) {
        this.cachedGn2dev = null;
        this.cachedUsr2dev = null;
        this.cachedRed = null;
        this.cachedBounds = null;
        this.usePrimitivePaint = true;
        if (node == null) {
            throw new IllegalArgumentException();
        }
        this.node = node;
        this.usePrimitivePaint = usePrimitivePaint;
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        if (this.usePrimitivePaint) {
            final Rectangle2D primitiveBounds = this.node.getPrimitiveBounds();
            if (primitiveBounds == null) {
                return new Rectangle2D.Double(0.0, 0.0, 0.0, 0.0);
            }
            return (Rectangle2D)primitiveBounds.clone();
        }
        else {
            Rectangle2D bounds = this.node.getBounds();
            if (bounds == null) {
                return new Rectangle2D.Double(0.0, 0.0, 0.0, 0.0);
            }
            final AffineTransform at = this.node.getTransform();
            if (at != null) {
                bounds = at.createTransformedShape(bounds).getBounds2D();
            }
            return bounds;
        }
    }
    
    @Override
    public boolean isDynamic() {
        return false;
    }
    
    @Override
    public boolean paintRable(final Graphics2D g2d) {
        final Composite c = g2d.getComposite();
        if (!SVGComposite.OVER.equals(c)) {
            return false;
        }
        final ColorSpace g2dCS = GraphicsUtil.getDestinationColorSpace(g2d);
        if (g2dCS == null || g2dCS != ColorSpace.getInstance(1000)) {
            return false;
        }
        final GraphicsNode gn = this.getGraphicsNode();
        if (this.getUsePrimitivePaint()) {
            gn.primitivePaint(g2d);
        }
        else {
            gn.paint(g2d);
        }
        return true;
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext renderContext) {
        AffineTransform usr2dev = renderContext.getTransform();
        AffineTransform gn2dev;
        if (usr2dev == null) {
            usr2dev = (gn2dev = new AffineTransform());
        }
        else {
            gn2dev = (AffineTransform)usr2dev.clone();
        }
        final AffineTransform gn2usr = this.node.getTransform();
        if (gn2usr != null) {
            gn2dev.concatenate(gn2usr);
        }
        final Rectangle2D bounds2D = this.getBounds2D();
        if (this.cachedBounds != null && this.cachedGn2dev != null && this.cachedBounds.equals(bounds2D) && gn2dev.getScaleX() == this.cachedGn2dev.getScaleX() && gn2dev.getScaleY() == this.cachedGn2dev.getScaleY() && gn2dev.getShearX() == this.cachedGn2dev.getShearX() && gn2dev.getShearY() == this.cachedGn2dev.getShearY()) {
            final double deltaX = usr2dev.getTranslateX() - this.cachedUsr2dev.getTranslateX();
            final double deltaY = usr2dev.getTranslateY() - this.cachedUsr2dev.getTranslateY();
            if (deltaX == 0.0 && deltaY == 0.0) {
                return this.cachedRed;
            }
            if (deltaX == (int)deltaX && deltaY == (int)deltaY) {
                return new TranslateRed(this.cachedRed, (int)Math.round(this.cachedRed.getMinX() + deltaX), (int)Math.round(this.cachedRed.getMinY() + deltaY));
            }
        }
        if (bounds2D.getWidth() > 0.0 && bounds2D.getHeight() > 0.0) {
            this.cachedUsr2dev = (AffineTransform)usr2dev.clone();
            this.cachedGn2dev = gn2dev;
            this.cachedBounds = bounds2D;
            return this.cachedRed = new GraphicsNodeRed8Bit(this.node, usr2dev, this.usePrimitivePaint, renderContext.getRenderingHints());
        }
        this.cachedUsr2dev = null;
        this.cachedGn2dev = null;
        this.cachedBounds = null;
        this.cachedRed = null;
        return null;
    }
}
