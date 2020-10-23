// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.renderer;

import java.util.Iterator;
import java.awt.Graphics2D;
import java.awt.image.WritableRaster;
import org.apache.batik.util.HaltingThread;
import java.awt.Shape;
import java.awt.Paint;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.Color;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.PadMode;
import java.awt.image.SampleModel;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.Point;
import java.util.Collection;
import java.awt.Rectangle;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import org.apache.batik.ext.awt.geom.RectListManager;

public class DynamicRenderer extends StaticRenderer
{
    static final int COPY_OVERHEAD = 1000;
    static final int COPY_LINE_OVERHEAD = 10;
    RectListManager damagedAreas;
    
    public DynamicRenderer() {
    }
    
    public DynamicRenderer(final RenderingHints rh, final AffineTransform at) {
        super(rh, at);
    }
    
    @Override
    protected CachableRed setupCache(final CachableRed img) {
        return img;
    }
    
    @Override
    public void flush(final Rectangle r) {
    }
    
    @Override
    public void flush(final Collection areas) {
    }
    
    @Override
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
        final int w = this.offScreenWidth;
        final int h = this.offScreenHeight;
        if (this.workingBaseRaster == null || this.workingBaseRaster.getWidth() < w || this.workingBaseRaster.getHeight() < h) {
            sm = sm.createCompatibleSampleModel(w, h);
            this.workingBaseRaster = Raster.createWritableRaster(sm, new Point(0, 0));
            this.workingRaster = this.workingBaseRaster.createWritableChild(0, 0, w, h, 0, 0, null);
            this.workingOffScreen = new BufferedImage(this.rootCR.getColorModel(), this.workingRaster, this.rootCR.getColorModel().isAlphaPremultiplied(), null);
        }
        if (!this.isDoubleBuffered) {
            this.currentOffScreen = this.workingOffScreen;
            this.currentBaseRaster = this.workingBaseRaster;
            this.currentRaster = this.workingRaster;
        }
    }
    
    @Override
    public void repaint(final RectListManager devRLM) {
        if (devRLM == null) {
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
        final boolean repaintAll = false;
        final Rectangle dr = copyRaster.getBounds();
        Rectangle sr = null;
        if (this.currentRaster != null) {
            sr = this.currentRaster.getBounds();
        }
        synchronized (syncRaster) {
            if (repaintAll) {
                cr.copyData(copyRaster);
            }
            else {
                final Graphics2D g2d = null;
                if (this.isDoubleBuffered && this.currentRaster != null && this.damagedAreas != null) {
                    this.damagedAreas.subtract(devRLM, 1000, 10);
                    this.damagedAreas.mergeRects(1000, 10);
                    final Color fillColor = new Color(0, 0, 255, 50);
                    final Color borderColor = new Color(0, 0, 0, 50);
                    for (final Object damagedArea : this.damagedAreas) {
                        Rectangle r = (Rectangle)damagedArea;
                        if (!dr.intersects(r)) {
                            continue;
                        }
                        r = dr.intersection(r);
                        if (sr != null && !sr.intersects(r)) {
                            continue;
                        }
                        r = sr.intersection(r);
                        final Raster src = this.currentRaster.createWritableChild(r.x, r.y, r.width, r.height, r.x, r.y, null);
                        GraphicsUtil.copyData(src, copyRaster);
                        if (g2d == null) {
                            continue;
                        }
                        g2d.setPaint(fillColor);
                        g2d.fill(r);
                        g2d.setPaint(borderColor);
                        g2d.draw(r);
                    }
                }
                final Color fillColor = new Color(255, 0, 0, 50);
                final Color borderColor = new Color(0, 0, 0, 50);
                for (final Object aDevRLM : devRLM) {
                    Rectangle r = (Rectangle)aDevRLM;
                    if (!dr.intersects(r)) {
                        continue;
                    }
                    r = dr.intersection(r);
                    final WritableRaster dst = copyRaster.createWritableChild(r.x, r.y, r.width, r.height, r.x, r.y, null);
                    cr.copyData(dst);
                    if (g2d == null) {
                        continue;
                    }
                    g2d.setPaint(fillColor);
                    g2d.fill(r);
                    g2d.setPaint(borderColor);
                    g2d.draw(r);
                }
            }
        }
        if (HaltingThread.hasBeenHalted()) {
            return;
        }
        final BufferedImage tmpBI = this.workingOffScreen;
        this.workingBaseRaster = this.currentBaseRaster;
        this.workingRaster = this.currentRaster;
        this.workingOffScreen = this.currentOffScreen;
        this.currentRaster = copyRaster;
        this.currentBaseRaster = syncRaster;
        this.currentOffScreen = tmpBI;
        this.damagedAreas = devRLM;
    }
}
