// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.renderer;

import java.awt.image.SampleModel;
import java.awt.image.Raster;
import java.awt.Point;
import java.awt.image.RenderedImage;
import org.apache.batik.ext.awt.image.rendered.TranslateRed;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.image.renderable.RenderContext;
import java.util.Iterator;
import java.util.Collection;
import org.apache.batik.ext.awt.image.rendered.TileCacheRed;
import java.awt.Rectangle;
import org.apache.batik.util.HaltingThread;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.geom.RectListManager;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.image.ColorModel;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.util.Hashtable;
import java.util.Map;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.lang.ref.SoftReference;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;

public class StaticRenderer implements ImageRenderer
{
    protected GraphicsNode rootGN;
    protected Filter rootFilter;
    protected CachableRed rootCR;
    protected SoftReference lastCR;
    protected SoftReference lastCache;
    protected boolean isDoubleBuffered;
    protected WritableRaster currentBaseRaster;
    protected WritableRaster currentRaster;
    protected BufferedImage currentOffScreen;
    protected WritableRaster workingBaseRaster;
    protected WritableRaster workingRaster;
    protected BufferedImage workingOffScreen;
    protected int offScreenWidth;
    protected int offScreenHeight;
    protected RenderingHints renderingHints;
    protected AffineTransform usr2dev;
    protected static RenderingHints defaultRenderingHints;
    
    public StaticRenderer(final RenderingHints rh, final AffineTransform at) {
        this.isDoubleBuffered = false;
        (this.renderingHints = new RenderingHints(null)).add(rh);
        this.usr2dev = new AffineTransform(at);
    }
    
    public StaticRenderer() {
        this.isDoubleBuffered = false;
        (this.renderingHints = new RenderingHints(null)).add(StaticRenderer.defaultRenderingHints);
        this.usr2dev = new AffineTransform();
    }
    
    @Override
    public void dispose() {
        this.rootGN = null;
        this.rootFilter = null;
        this.rootCR = null;
        this.workingOffScreen = null;
        this.workingBaseRaster = null;
        this.workingRaster = null;
        this.currentOffScreen = null;
        this.currentBaseRaster = null;
        this.currentRaster = null;
        this.renderingHints = null;
        this.lastCache = null;
        this.lastCR = null;
    }
    
    @Override
    public void setTree(final GraphicsNode rootGN) {
        this.rootGN = rootGN;
        this.rootFilter = null;
        this.rootCR = null;
        this.workingOffScreen = null;
        this.workingRaster = null;
        this.currentOffScreen = null;
        this.currentRaster = null;
    }
    
    @Override
    public GraphicsNode getTree() {
        return this.rootGN;
    }
    
    @Override
    public void setRenderingHints(final RenderingHints rh) {
        (this.renderingHints = new RenderingHints(null)).add(rh);
        this.rootFilter = null;
        this.rootCR = null;
        this.workingOffScreen = null;
        this.workingRaster = null;
        this.currentOffScreen = null;
        this.currentRaster = null;
    }
    
    @Override
    public RenderingHints getRenderingHints() {
        return this.renderingHints;
    }
    
    @Override
    public void setTransform(final AffineTransform usr2dev) {
        if (this.usr2dev.equals(usr2dev)) {
            return;
        }
        if (usr2dev == null) {
            this.usr2dev = new AffineTransform();
        }
        else {
            this.usr2dev = new AffineTransform(usr2dev);
        }
        this.rootCR = null;
    }
    
    @Override
    public AffineTransform getTransform() {
        return this.usr2dev;
    }
    
    @Override
    public boolean isDoubleBuffered() {
        return this.isDoubleBuffered;
    }
    
    @Override
    public void setDoubleBuffered(final boolean isDoubleBuffered) {
        if (this.isDoubleBuffered == isDoubleBuffered) {
            return;
        }
        this.isDoubleBuffered = isDoubleBuffered;
        if (isDoubleBuffered) {
            this.currentOffScreen = null;
            this.currentBaseRaster = null;
            this.currentRaster = null;
        }
        else {
            this.currentOffScreen = this.workingOffScreen;
            this.currentBaseRaster = this.workingBaseRaster;
            this.currentRaster = this.workingRaster;
        }
    }
    
    @Override
    public void updateOffScreen(final int width, final int height) {
        this.offScreenWidth = width;
        this.offScreenHeight = height;
    }
    
    @Override
    public BufferedImage getOffScreen() {
        if (this.rootGN == null) {
            return null;
        }
        return this.currentOffScreen;
    }
    
    @Override
    public void clearOffScreen() {
        if (this.isDoubleBuffered) {
            return;
        }
        this.updateWorkingBuffers();
        if (this.rootCR == null || this.workingBaseRaster == null) {
            return;
        }
        final ColorModel cm = this.rootCR.getColorModel();
        final WritableRaster syncRaster = this.workingBaseRaster;
        synchronized (syncRaster) {
            final BufferedImage bi = new BufferedImage(cm, this.workingBaseRaster, cm.isAlphaPremultiplied(), null);
            final Graphics2D g2d = bi.createGraphics();
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
            g2d.dispose();
        }
    }
    
    @Override
    public void repaint(final Shape area) {
        if (area == null) {
            return;
        }
        final RectListManager rlm = new RectListManager();
        rlm.add(this.usr2dev.createTransformedShape(area).getBounds());
        this.repaint(rlm);
    }
    
    @Override
    public void repaint(final RectListManager areas) {
        if (areas == null) {
            return;
        }
        this.updateWorkingBuffers();
        if (this.rootCR == null || this.workingBaseRaster == null) {
            return;
        }
        CachableRed cr = this.rootCR;
        final WritableRaster syncRaster = this.workingBaseRaster;
        final WritableRaster copyRaster = this.workingRaster;
        final Rectangle srcR = this.rootCR.getBounds();
        final Rectangle dstR = this.workingRaster.getBounds();
        if (dstR.x < srcR.x || dstR.y < srcR.y || dstR.x + dstR.width > srcR.x + srcR.width || dstR.y + dstR.height > srcR.y + srcR.height) {
            cr = new PadRed(cr, dstR, PadMode.ZERO_PAD, null);
        }
        synchronized (syncRaster) {
            cr.copyData(copyRaster);
        }
        if (!HaltingThread.hasBeenHalted()) {
            final BufferedImage tmpBI = this.workingOffScreen;
            this.workingBaseRaster = this.currentBaseRaster;
            this.workingRaster = this.currentRaster;
            this.workingOffScreen = this.currentOffScreen;
            this.currentRaster = copyRaster;
            this.currentBaseRaster = syncRaster;
            this.currentOffScreen = tmpBI;
        }
    }
    
    @Override
    public void flush() {
        if (this.lastCache == null) {
            return;
        }
        final Object o = this.lastCache.get();
        if (o == null) {
            return;
        }
        final TileCacheRed tcr = (TileCacheRed)o;
        tcr.flushCache(tcr.getBounds());
    }
    
    @Override
    public void flush(final Collection areas) {
        final AffineTransform at = this.getTransform();
        for (final Object area : areas) {
            final Shape s = (Shape)area;
            final Rectangle r = at.createTransformedShape(s).getBounds();
            this.flush(r);
        }
    }
    
    @Override
    public void flush(Rectangle r) {
        if (this.lastCache == null) {
            return;
        }
        final Object o = this.lastCache.get();
        if (o == null) {
            return;
        }
        final TileCacheRed tcr = (TileCacheRed)o;
        final Rectangle rectangle;
        r = (rectangle = (Rectangle)r.clone());
        rectangle.x -= Math.round((float)this.usr2dev.getTranslateX());
        final Rectangle rectangle2 = r;
        rectangle2.y -= Math.round((float)this.usr2dev.getTranslateY());
        tcr.flushCache(r);
    }
    
    protected CachableRed setupCache(CachableRed img) {
        if (this.lastCR == null || img != this.lastCR.get()) {
            this.lastCR = new SoftReference((T)img);
            this.lastCache = null;
        }
        Object o = null;
        if (this.lastCache != null) {
            o = this.lastCache.get();
        }
        if (o != null) {
            return (CachableRed)o;
        }
        img = new TileCacheRed(img);
        this.lastCache = new SoftReference((T)img);
        return img;
    }
    
    protected CachableRed renderGNR() {
        final AffineTransform at = this.usr2dev;
        final AffineTransform rcAT = new AffineTransform(at.getScaleX(), at.getShearY(), at.getShearX(), at.getScaleY(), 0.0, 0.0);
        final RenderContext rc = new RenderContext(rcAT, null, this.renderingHints);
        final RenderedImage ri = this.rootFilter.createRendering(rc);
        if (ri == null) {
            return null;
        }
        CachableRed ret = GraphicsUtil.wrap(ri);
        ret = this.setupCache(ret);
        final int dx = Math.round((float)at.getTranslateX());
        final int dy = Math.round((float)at.getTranslateY());
        ret = new TranslateRed(ret, ret.getMinX() + dx, ret.getMinY() + dy);
        ret = GraphicsUtil.convertTosRGB(ret);
        return ret;
    }
    
    protected void updateWorkingBuffers() {
        if (this.rootFilter == null) {
            this.rootFilter = this.rootGN.getGraphicsNodeRable(true);
            this.rootCR = null;
        }
        this.rootCR = this.renderGNR();
        if (this.rootCR == null) {
            this.workingRaster = null;
            this.workingOffScreen = null;
            this.workingBaseRaster = null;
            this.currentOffScreen = null;
            this.currentBaseRaster = null;
            this.currentRaster = null;
            return;
        }
        SampleModel sm = this.rootCR.getSampleModel();
        int w = this.offScreenWidth;
        int h = this.offScreenHeight;
        final int tw = sm.getWidth();
        final int th = sm.getHeight();
        w = ((w + tw - 1) / tw + 1) * tw;
        h = ((h + th - 1) / th + 1) * th;
        if (this.workingBaseRaster == null || this.workingBaseRaster.getWidth() < w || this.workingBaseRaster.getHeight() < h) {
            sm = sm.createCompatibleSampleModel(w, h);
            this.workingBaseRaster = Raster.createWritableRaster(sm, new Point(0, 0));
        }
        final int tgx = -this.rootCR.getTileGridXOffset();
        final int tgy = -this.rootCR.getTileGridYOffset();
        int xt;
        if (tgx >= 0) {
            xt = tgx / tw;
        }
        else {
            xt = (tgx - tw + 1) / tw;
        }
        int yt;
        if (tgy >= 0) {
            yt = tgy / th;
        }
        else {
            yt = (tgy - th + 1) / th;
        }
        final int xloc = xt * tw - tgx;
        final int yloc = yt * th - tgy;
        this.workingRaster = this.workingBaseRaster.createWritableChild(0, 0, w, h, xloc, yloc, null);
        this.workingOffScreen = new BufferedImage(this.rootCR.getColorModel(), this.workingRaster.createWritableChild(0, 0, this.offScreenWidth, this.offScreenHeight, 0, 0, null), this.rootCR.getColorModel().isAlphaPremultiplied(), null);
        if (!this.isDoubleBuffered) {
            this.currentOffScreen = this.workingOffScreen;
            this.currentBaseRaster = this.workingBaseRaster;
            this.currentRaster = this.workingRaster;
        }
    }
    
    static {
        (StaticRenderer.defaultRenderingHints = new RenderingHints(null)).put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        StaticRenderer.defaultRenderingHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }
}
