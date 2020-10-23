// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.renderer;

import java.util.Iterator;
import org.apache.batik.util.HaltingThread;
import java.awt.image.ImageObserver;
import java.awt.Image;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.Shape;
import java.util.Collection;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.util.Map;
import java.awt.Color;
import org.apache.batik.ext.awt.geom.RectListManager;
import java.awt.image.BufferedImage;
import org.apache.batik.gvt.GraphicsNode;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

public class MacRenderer implements ImageRenderer
{
    static final int COPY_OVERHEAD = 1000;
    static final int COPY_LINE_OVERHEAD = 10;
    static final AffineTransform IDENTITY;
    protected RenderingHints renderingHints;
    protected AffineTransform usr2dev;
    protected GraphicsNode rootGN;
    protected int offScreenWidth;
    protected int offScreenHeight;
    protected boolean isDoubleBuffered;
    protected BufferedImage currImg;
    protected BufferedImage workImg;
    protected RectListManager damagedAreas;
    public static int IMAGE_TYPE;
    public static Color TRANSPARENT_WHITE;
    protected static RenderingHints defaultRenderingHints;
    
    public MacRenderer() {
        (this.renderingHints = new RenderingHints(null)).add(MacRenderer.defaultRenderingHints);
        this.usr2dev = new AffineTransform();
    }
    
    public MacRenderer(final RenderingHints rh, final AffineTransform at) {
        (this.renderingHints = new RenderingHints(null)).add(rh);
        if (at == null) {
            this.usr2dev = new AffineTransform();
        }
        else {
            this.usr2dev = new AffineTransform(at);
        }
    }
    
    @Override
    public void dispose() {
        this.rootGN = null;
        this.currImg = null;
        this.workImg = null;
        this.renderingHints = null;
        this.usr2dev = null;
        if (this.damagedAreas != null) {
            this.damagedAreas.clear();
        }
        this.damagedAreas = null;
    }
    
    @Override
    public void setTree(final GraphicsNode treeRoot) {
        this.rootGN = treeRoot;
    }
    
    @Override
    public GraphicsNode getTree() {
        return this.rootGN;
    }
    
    @Override
    public void setTransform(final AffineTransform usr2dev) {
        if (usr2dev == null) {
            this.usr2dev = new AffineTransform();
        }
        else {
            this.usr2dev = new AffineTransform(usr2dev);
        }
        if (this.workImg == null) {
            return;
        }
        synchronized (this.workImg) {
            final Graphics2D g2d = this.workImg.createGraphics();
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, this.workImg.getWidth(), this.workImg.getHeight());
            g2d.dispose();
        }
        this.damagedAreas = null;
    }
    
    @Override
    public AffineTransform getTransform() {
        return this.usr2dev;
    }
    
    @Override
    public void setRenderingHints(final RenderingHints rh) {
        (this.renderingHints = new RenderingHints(null)).add(rh);
        this.damagedAreas = null;
    }
    
    @Override
    public RenderingHints getRenderingHints() {
        return this.renderingHints;
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
            this.workImg = null;
        }
        else {
            this.workImg = this.currImg;
            this.damagedAreas = null;
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
        return this.currImg;
    }
    
    @Override
    public void clearOffScreen() {
        if (this.isDoubleBuffered) {
            return;
        }
        this.updateWorkingBuffers();
        if (this.workImg == null) {
            return;
        }
        synchronized (this.workImg) {
            final Graphics2D g2d = this.workImg.createGraphics();
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, this.workImg.getWidth(), this.workImg.getHeight());
            g2d.dispose();
        }
        this.damagedAreas = null;
    }
    
    @Override
    public void flush() {
    }
    
    @Override
    public void flush(final Rectangle r) {
    }
    
    @Override
    public void flush(final Collection areas) {
    }
    
    protected void updateWorkingBuffers() {
        if (this.rootGN == null) {
            this.currImg = null;
            this.workImg = null;
            return;
        }
        final int w = this.offScreenWidth;
        final int h = this.offScreenHeight;
        if (this.workImg == null || this.workImg.getWidth() < w || this.workImg.getHeight() < h) {
            this.workImg = new BufferedImage(w, h, MacRenderer.IMAGE_TYPE);
        }
        if (!this.isDoubleBuffered) {
            this.currImg = this.workImg;
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
    public void repaint(final RectListManager devRLM) {
        if (devRLM == null) {
            return;
        }
        this.updateWorkingBuffers();
        if (this.rootGN == null || this.workImg == null) {
            return;
        }
        try {
            synchronized (this.workImg) {
                final Graphics2D g2d = GraphicsUtil.createGraphics(this.workImg, this.renderingHints);
                final Rectangle dr = new Rectangle(0, 0, this.offScreenWidth, this.offScreenHeight);
                if (this.isDoubleBuffered && this.currImg != null && this.damagedAreas != null) {
                    this.damagedAreas.subtract(devRLM, 1000, 10);
                    this.damagedAreas.mergeRects(1000, 10);
                    final Iterator iter = this.damagedAreas.iterator();
                    g2d.setComposite(AlphaComposite.Src);
                    while (iter.hasNext()) {
                        Rectangle r = iter.next();
                        if (!dr.intersects(r)) {
                            continue;
                        }
                        r = dr.intersection(r);
                        g2d.setClip(r.x, r.y, r.width, r.height);
                        g2d.setComposite(AlphaComposite.Clear);
                        g2d.fillRect(r.x, r.y, r.width, r.height);
                        g2d.setComposite(AlphaComposite.SrcOver);
                        g2d.drawImage(this.currImg, 0, 0, null);
                    }
                }
                for (final Object aDevRLM : devRLM) {
                    Rectangle r2 = (Rectangle)aDevRLM;
                    if (!dr.intersects(r2)) {
                        continue;
                    }
                    r2 = dr.intersection(r2);
                    g2d.setTransform(MacRenderer.IDENTITY);
                    g2d.setClip(r2.x, r2.y, r2.width, r2.height);
                    g2d.setComposite(AlphaComposite.Clear);
                    g2d.fillRect(r2.x, r2.y, r2.width, r2.height);
                    g2d.setComposite(AlphaComposite.SrcOver);
                    g2d.transform(this.usr2dev);
                    this.rootGN.paint(g2d);
                }
                g2d.dispose();
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        if (HaltingThread.hasBeenHalted()) {
            return;
        }
        if (this.isDoubleBuffered) {
            final BufferedImage tmpImg = this.workImg;
            this.workImg = this.currImg;
            this.currImg = tmpImg;
            this.damagedAreas = devRLM;
        }
    }
    
    static {
        IDENTITY = new AffineTransform();
        MacRenderer.IMAGE_TYPE = 3;
        MacRenderer.TRANSPARENT_WHITE = new Color(255, 255, 255, 0);
        (MacRenderer.defaultRenderingHints = new RenderingHints(null)).put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        MacRenderer.defaultRenderingHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }
}
