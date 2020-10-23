// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.Shape;
import org.apache.batik.gvt.GraphicsNode;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.Dimension;
import org.w3c.dom.Element;
import java.lang.ref.SoftReference;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.gvt.AbstractGraphicsNode;

public class MultiResGraphicsNode extends AbstractGraphicsNode implements SVGConstants
{
    SoftReference[] srcs;
    Element[] srcElems;
    Dimension[] minSz;
    Dimension[] maxSz;
    Rectangle2D bounds;
    BridgeContext ctx;
    Element multiImgElem;
    
    public MultiResGraphicsNode(final Element multiImgElem, final Rectangle2D bounds, final Element[] srcElems, final Dimension[] minSz, final Dimension[] maxSz, final BridgeContext ctx) {
        this.multiImgElem = multiImgElem;
        this.srcElems = new Element[srcElems.length];
        this.minSz = new Dimension[srcElems.length];
        this.maxSz = new Dimension[srcElems.length];
        this.ctx = ctx;
        for (int i = 0; i < srcElems.length; ++i) {
            this.srcElems[i] = srcElems[i];
            this.minSz[i] = minSz[i];
            this.maxSz[i] = maxSz[i];
        }
        this.srcs = new SoftReference[srcElems.length];
        this.bounds = bounds;
    }
    
    @Override
    public void primitivePaint(final Graphics2D g2d) {
        final AffineTransform at = g2d.getTransform();
        double scx = Math.sqrt(at.getShearY() * at.getShearY() + at.getScaleX() * at.getScaleX());
        double scy = Math.sqrt(at.getShearX() * at.getShearX() + at.getScaleY() * at.getScaleY());
        GraphicsNode gn = null;
        int idx = -1;
        final double w = this.bounds.getWidth() * scx;
        double minDist = this.calcDist(w, this.minSz[0], this.maxSz[0]);
        int minIdx = 0;
        for (int i = 0; i < this.minSz.length; ++i) {
            final double dist = this.calcDist(w, this.minSz[i], this.maxSz[i]);
            if (dist < minDist) {
                minDist = dist;
                minIdx = i;
            }
            if ((this.minSz[i] == null || w >= this.minSz[i].width) && (this.maxSz[i] == null || w <= this.maxSz[i].width) && (idx == -1 || minIdx == i)) {
                idx = i;
            }
        }
        if (idx == -1) {
            idx = minIdx;
        }
        gn = this.getGraphicsNode(idx);
        if (gn == null) {
            return;
        }
        final Rectangle2D gnBounds = gn.getBounds();
        if (gnBounds == null) {
            return;
        }
        double gnDevW = gnBounds.getWidth() * scx;
        double gnDevH = gnBounds.getHeight() * scy;
        final double gnDevX = gnBounds.getX() * scx;
        final double gnDevY = gnBounds.getY() * scy;
        double gnDevX2;
        double gnDevX3;
        if (gnDevW < 0.0) {
            gnDevX2 = gnDevX + gnDevW;
            gnDevX3 = gnDevX;
        }
        else {
            gnDevX2 = gnDevX;
            gnDevX3 = gnDevX + gnDevW;
        }
        double gnDevY2;
        double gnDevY3;
        if (gnDevH < 0.0) {
            gnDevY2 = gnDevY + gnDevH;
            gnDevY3 = gnDevY;
        }
        else {
            gnDevY2 = gnDevY;
            gnDevY3 = gnDevY + gnDevH;
        }
        gnDevW = (int)(Math.ceil(gnDevX3) - Math.floor(gnDevX2));
        gnDevH = (int)(Math.ceil(gnDevY3) - Math.floor(gnDevY2));
        scx = gnDevW / gnBounds.getWidth() / scx;
        scy = gnDevH / gnBounds.getHeight() / scy;
        AffineTransform nat = g2d.getTransform();
        nat = new AffineTransform(nat.getScaleX() * scx, nat.getShearY() * scx, nat.getShearX() * scy, nat.getScaleY() * scy, nat.getTranslateX(), nat.getTranslateY());
        g2d.setTransform(nat);
        gn.paint(g2d);
    }
    
    public double calcDist(final double loc, final Dimension min, final Dimension max) {
        if (min == null) {
            if (max == null) {
                return 1.0E11;
            }
            return Math.abs(loc - max.width);
        }
        else {
            if (max == null) {
                return Math.abs(loc - min.width);
            }
            final double mid = (max.width + min.width) / 2.0;
            return Math.abs(loc - mid);
        }
    }
    
    @Override
    public Rectangle2D getPrimitiveBounds() {
        return this.bounds;
    }
    
    @Override
    public Rectangle2D getGeometryBounds() {
        return this.bounds;
    }
    
    @Override
    public Rectangle2D getSensitiveBounds() {
        return this.bounds;
    }
    
    @Override
    public Shape getOutline() {
        return this.bounds;
    }
    
    public GraphicsNode getGraphicsNode(final int idx) {
        if (this.srcs[idx] != null) {
            final Object o = this.srcs[idx].get();
            if (o != null) {
                return (GraphicsNode)o;
            }
        }
        try {
            final GVTBuilder builder = this.ctx.getGVTBuilder();
            final GraphicsNode gn = builder.build(this.ctx, this.srcElems[idx]);
            this.srcs[idx] = new SoftReference((T)gn);
            return gn;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
