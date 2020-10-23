// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.color.ColorSpace;
import java.awt.image.DirectColorModel;
import java.awt.Graphics2D;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.Rectangle;
import java.util.Iterator;
import java.awt.image.ColorModel;
import java.util.Map;
import org.apache.batik.ext.awt.image.PadMode;
import java.util.ArrayList;
import java.awt.RenderingHints;
import org.apache.batik.ext.awt.image.SVGComposite;
import java.util.List;
import java.awt.CompositeContext;
import org.apache.batik.ext.awt.image.CompositeRule;

public class CompositeRed extends AbstractRed
{
    CompositeRule rule;
    CompositeContext[] contexts;
    
    public CompositeRed(List srcs, final CompositeRule rule) {
        final CachableRed src = srcs.get(0);
        final ColorModel cm = fixColorModel(src);
        this.rule = rule;
        final SVGComposite comp = new SVGComposite(rule);
        this.contexts = new CompositeContext[srcs.size()];
        int idx = 0;
        Iterator i = srcs.iterator();
        Rectangle myBounds = null;
        while (i.hasNext()) {
            final CachableRed cr = i.next();
            this.contexts[idx++] = comp.createContext(cr.getColorModel(), cm, null);
            final Rectangle newBound = cr.getBounds();
            if (myBounds == null) {
                myBounds = newBound;
            }
            else {
                switch (rule.getRule()) {
                    case 2: {
                        if (myBounds.intersects(newBound)) {
                            myBounds = myBounds.intersection(newBound);
                            continue;
                        }
                        myBounds.width = 0;
                        myBounds.height = 0;
                        continue;
                    }
                    case 3: {
                        myBounds = newBound;
                        continue;
                    }
                    default: {
                        myBounds.add(newBound);
                        continue;
                    }
                }
            }
        }
        if (myBounds == null) {
            throw new IllegalArgumentException("Composite Operation Must have some source!");
        }
        if (rule.getRule() == 6) {
            final List vec = new ArrayList(srcs.size());
            i = srcs.iterator();
            while (i.hasNext()) {
                CachableRed cr2 = i.next();
                final Rectangle r = cr2.getBounds();
                if (r.x != myBounds.x || r.y != myBounds.y || r.width != myBounds.width || r.height != myBounds.height) {
                    cr2 = new PadRed(cr2, myBounds, PadMode.ZERO_PAD, null);
                }
                vec.add(cr2);
            }
            srcs = vec;
        }
        final SampleModel sm = fixSampleModel(src, cm, myBounds);
        final int defSz = AbstractTiledRed.getDefaultTileSize();
        final int tgX = defSz * (int)Math.floor(myBounds.x / defSz);
        final int tgY = defSz * (int)Math.floor(myBounds.y / defSz);
        this.init(srcs, myBounds, cm, sm, tgX, tgY, null);
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        this.genRect(wr);
        return wr;
    }
    
    @Override
    public Raster getTile(final int x, final int y) {
        final int tx = this.tileGridXOff + x * this.tileWidth;
        final int ty = this.tileGridYOff + y * this.tileHeight;
        final Point pt = new Point(tx, ty);
        final WritableRaster wr = Raster.createWritableRaster(this.sm, pt);
        this.genRect(wr);
        return wr;
    }
    
    public void emptyRect(final WritableRaster wr) {
        final PadRed.ZeroRecter zr = PadRed.ZeroRecter.getZeroRecter(wr);
        zr.zeroRect(new Rectangle(wr.getMinX(), wr.getMinY(), wr.getWidth(), wr.getHeight()));
    }
    
    public void genRect(final WritableRaster wr) {
        final Rectangle r = wr.getBounds();
        int idx = 0;
        final Iterator i = this.srcs.iterator();
        boolean first = true;
        while (i.hasNext()) {
            final CachableRed cr = i.next();
            if (first) {
                final Rectangle crR = cr.getBounds();
                if (r.x < crR.x || r.y < crR.y || r.x + r.width > crR.x + crR.width || r.y + r.height > crR.y + crR.height) {
                    this.emptyRect(wr);
                }
                cr.copyData(wr);
                if (!cr.getColorModel().isAlphaPremultiplied()) {
                    GraphicsUtil.coerceData(wr, cr.getColorModel(), true);
                }
                first = false;
            }
            else {
                final Rectangle crR = cr.getBounds();
                if (crR.intersects(r)) {
                    final Rectangle smR = crR.intersection(r);
                    final Raster ras = cr.getData(smR);
                    final WritableRaster smWR = wr.createWritableChild(smR.x, smR.y, smR.width, smR.height, smR.x, smR.y, null);
                    this.contexts[idx].compose(ras, smWR, smWR);
                }
            }
            ++idx;
        }
    }
    
    public void genRect_OVER(final WritableRaster wr) {
        final Rectangle r = wr.getBounds();
        final ColorModel cm = this.getColorModel();
        final BufferedImage bi = new BufferedImage(cm, wr.createWritableTranslatedChild(0, 0), cm.isAlphaPremultiplied(), null);
        final Graphics2D g2d = GraphicsUtil.createGraphics(bi);
        g2d.translate(-r.x, -r.y);
        final Iterator i = this.srcs.iterator();
        boolean first = true;
        while (i.hasNext()) {
            final CachableRed cr = i.next();
            if (first) {
                final Rectangle crR = cr.getBounds();
                if (r.x < crR.x || r.y < crR.y || r.x + r.width > crR.x + crR.width || r.y + r.height > crR.y + crR.height) {
                    this.emptyRect(wr);
                }
                cr.copyData(wr);
                GraphicsUtil.coerceData(wr, cr.getColorModel(), cm.isAlphaPremultiplied());
                first = false;
            }
            else {
                GraphicsUtil.drawImage(g2d, cr);
            }
        }
    }
    
    protected static SampleModel fixSampleModel(final CachableRed src, final ColorModel cm, final Rectangle bounds) {
        final int defSz = AbstractTiledRed.getDefaultTileSize();
        final int tgX = defSz * (int)Math.floor(bounds.x / defSz);
        final int tgY = defSz * (int)Math.floor(bounds.y / defSz);
        final int tw = bounds.x + bounds.width - tgX;
        final int th = bounds.y + bounds.height - tgY;
        final SampleModel sm = src.getSampleModel();
        int w = sm.getWidth();
        if (w < defSz) {
            w = defSz;
        }
        if (w > tw) {
            w = tw;
        }
        int h = sm.getHeight();
        if (h < defSz) {
            h = defSz;
        }
        if (h > th) {
            h = th;
        }
        if (w <= 0 || h <= 0) {
            w = 1;
            h = 1;
        }
        return cm.createCompatibleSampleModel(w, h);
    }
    
    protected static ColorModel fixColorModel(final CachableRed src) {
        ColorModel cm = src.getColorModel();
        if (cm.hasAlpha()) {
            if (!cm.isAlphaPremultiplied()) {
                cm = GraphicsUtil.coerceColorModel(cm, true);
            }
            return cm;
        }
        final int b = src.getSampleModel().getNumBands() + 1;
        if (b > 4) {
            throw new IllegalArgumentException("CompositeRed can only handle up to three band images");
        }
        final int[] masks = new int[4];
        for (int i = 0; i < b - 1; ++i) {
            masks[i] = 16711680 >> 8 * i;
        }
        masks[3] = 255 << 8 * (b - 1);
        final ColorSpace cs = cm.getColorSpace();
        return new DirectColorModel(cs, 8 * b, masks[0], masks[1], masks[2], masks[3], true, 3);
    }
}
