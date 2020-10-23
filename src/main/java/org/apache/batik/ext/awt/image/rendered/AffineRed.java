// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.DirectColorModel;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.image.AffineTransformOp;
import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.awt.Rectangle;
import java.util.Map;
import java.awt.geom.Point2D;
import java.awt.Shape;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;

public class AffineRed extends AbstractRed
{
    RenderingHints hints;
    AffineTransform src2me;
    AffineTransform me2src;
    
    public AffineTransform getTransform() {
        return (AffineTransform)this.src2me.clone();
    }
    
    public CachableRed getSource() {
        return this.getSources().get(0);
    }
    
    public AffineRed(final CachableRed src, final AffineTransform src2me, final RenderingHints hints) {
        this.src2me = src2me;
        this.hints = hints;
        try {
            this.me2src = src2me.createInverse();
        }
        catch (NoninvertibleTransformException nite) {
            this.me2src = null;
        }
        final Rectangle srcBounds = src.getBounds();
        final Rectangle myBounds = src2me.createTransformedShape(srcBounds).getBounds();
        final ColorModel cm = fixColorModel(src);
        final SampleModel sm = this.fixSampleModel(src, cm, myBounds);
        Point2D pt = new Point2D.Float((float)src.getTileGridXOffset(), (float)src.getTileGridYOffset());
        pt = src2me.transform(pt, null);
        this.init(src, myBounds, cm, sm, (int)pt.getX(), (int)pt.getY(), null);
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        final PadRed.ZeroRecter zr = PadRed.ZeroRecter.getZeroRecter(wr);
        zr.zeroRect(new Rectangle(wr.getMinX(), wr.getMinY(), wr.getWidth(), wr.getHeight()));
        this.genRect(wr);
        return wr;
    }
    
    @Override
    public Raster getTile(final int x, final int y) {
        if (this.me2src == null) {
            return null;
        }
        final int tx = this.tileGridXOff + x * this.tileWidth;
        final int ty = this.tileGridYOff + y * this.tileHeight;
        final Point pt = new Point(tx, ty);
        final WritableRaster wr = Raster.createWritableRaster(this.sm, pt);
        this.genRect(wr);
        return wr;
    }
    
    public void genRect(final WritableRaster wr) {
        if (this.me2src == null) {
            return;
        }
        final Rectangle srcR = this.me2src.createTransformedShape(wr.getBounds()).getBounds();
        srcR.setBounds(srcR.x - 1, srcR.y - 1, srcR.width + 2, srcR.height + 2);
        final CachableRed src = this.getSources().get(0);
        if (!srcR.intersects(src.getBounds())) {
            return;
        }
        final Raster srcRas = src.getData(srcR.intersection(src.getBounds()));
        if (srcRas == null) {
            return;
        }
        final AffineTransform aff = (AffineTransform)this.src2me.clone();
        aff.concatenate(AffineTransform.getTranslateInstance(srcRas.getMinX(), srcRas.getMinY()));
        Point2D srcPt = new Point2D.Float((float)wr.getMinX(), (float)wr.getMinY());
        srcPt = this.me2src.transform(srcPt, null);
        Point2D destPt = new Point2D.Double(srcPt.getX() - srcRas.getMinX(), srcPt.getY() - srcRas.getMinY());
        destPt = aff.transform(destPt, null);
        aff.preConcatenate(AffineTransform.getTranslateInstance(-destPt.getX(), -destPt.getY()));
        final AffineTransformOp op = new AffineTransformOp(aff, this.hints);
        ColorModel srcCM = src.getColorModel();
        final ColorModel myCM = this.getColorModel();
        final WritableRaster srcWR = (WritableRaster)srcRas;
        srcCM = GraphicsUtil.coerceData(srcWR, srcCM, true);
        final BufferedImage srcBI = new BufferedImage(srcCM, srcWR.createWritableTranslatedChild(0, 0), srcCM.isAlphaPremultiplied(), null);
        final BufferedImage myBI = new BufferedImage(myCM, wr.createWritableTranslatedChild(0, 0), myCM.isAlphaPremultiplied(), null);
        op.filter(srcBI.getRaster(), myBI.getRaster());
    }
    
    protected static ColorModel fixColorModel(final CachableRed src) {
        ColorModel cm = src.getColorModel();
        if (cm.hasAlpha()) {
            if (!cm.isAlphaPremultiplied()) {
                cm = GraphicsUtil.coerceColorModel(cm, true);
            }
            return cm;
        }
        final ColorSpace cs = cm.getColorSpace();
        final int b = src.getSampleModel().getNumBands() + 1;
        if (b == 4) {
            final int[] masks = new int[4];
            for (int i = 0; i < b - 1; ++i) {
                masks[i] = 16711680 >> 8 * i;
            }
            masks[3] = 255 << 8 * (b - 1);
            return new DirectColorModel(cs, 8 * b, masks[0], masks[1], masks[2], masks[3], true, 3);
        }
        final int[] bits = new int[b];
        for (int i = 0; i < b; ++i) {
            bits[i] = 8;
        }
        return new ComponentColorModel(cs, bits, true, true, 3, 3);
    }
    
    protected SampleModel fixSampleModel(final CachableRed src, final ColorModel cm, final Rectangle bounds) {
        final SampleModel sm = src.getSampleModel();
        final int defSz = AbstractTiledRed.getDefaultTileSize();
        int w = sm.getWidth();
        if (w < defSz) {
            w = defSz;
        }
        if (w > bounds.width) {
            w = bounds.width;
        }
        int h = sm.getHeight();
        if (h < defSz) {
            h = defSz;
        }
        if (h > bounds.height) {
            h = bounds.height;
        }
        if (w <= 0 || h <= 0) {
            w = 1;
            h = 1;
        }
        return cm.createCompatibleSampleModel(w, h);
    }
}
