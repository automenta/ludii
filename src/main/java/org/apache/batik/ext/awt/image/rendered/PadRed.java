// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import org.apache.batik.ext.awt.image.PadMode;

public class PadRed extends AbstractRed
{
    static final boolean DEBUG = false;
    PadMode padMode;
    RenderingHints hints;
    
    public PadRed(final CachableRed src, final Rectangle bounds, final PadMode padMode, final RenderingHints hints) {
        super(src, bounds, src.getColorModel(), fixSampleModel(src, bounds), bounds.x, bounds.y, null);
        this.padMode = padMode;
        this.hints = hints;
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        final CachableRed src = this.getSources().get(0);
        final Rectangle srcR = src.getBounds();
        final Rectangle wrR = wr.getBounds();
        if (wrR.intersects(srcR)) {
            final Rectangle r = wrR.intersection(srcR);
            final WritableRaster srcWR = wr.createWritableChild(r.x, r.y, r.width, r.height, r.x, r.y, null);
            src.copyData(srcWR);
        }
        if (this.padMode == PadMode.ZERO_PAD) {
            this.handleZero(wr);
        }
        else if (this.padMode == PadMode.REPLICATE) {
            this.handleReplicate(wr);
        }
        else if (this.padMode == PadMode.WRAP) {
            this.handleWrap(wr);
        }
        return wr;
    }
    
    protected void handleZero(final WritableRaster wr) {
        final CachableRed src = this.getSources().get(0);
        final Rectangle srcR = src.getBounds();
        final Rectangle wrR = wr.getBounds();
        final ZeroRecter zr = ZeroRecter.getZeroRecter(wr);
        final Rectangle ar = new Rectangle(wrR.x, wrR.y, wrR.width, wrR.height);
        final Rectangle dr = new Rectangle(wrR.x, wrR.y, wrR.width, wrR.height);
        if (ar.x < srcR.x) {
            int w = srcR.x - ar.x;
            if (w > ar.width) {
                w = ar.width;
            }
            dr.width = w;
            zr.zeroRect(dr);
            final Rectangle rectangle = ar;
            rectangle.x += w;
            final Rectangle rectangle2 = ar;
            rectangle2.width -= w;
        }
        if (ar.y < srcR.y) {
            int h = srcR.y - ar.y;
            if (h > ar.height) {
                h = ar.height;
            }
            dr.x = ar.x;
            dr.y = ar.y;
            dr.width = ar.width;
            dr.height = h;
            zr.zeroRect(dr);
            final Rectangle rectangle3 = ar;
            rectangle3.y += h;
            final Rectangle rectangle4 = ar;
            rectangle4.height -= h;
        }
        if (ar.y + ar.height > srcR.y + srcR.height) {
            int h = ar.y + ar.height - (srcR.y + srcR.height);
            if (h > ar.height) {
                h = ar.height;
            }
            final int y0 = ar.y + ar.height - h;
            dr.x = ar.x;
            dr.y = y0;
            dr.width = ar.width;
            dr.height = h;
            zr.zeroRect(dr);
            final Rectangle rectangle5 = ar;
            rectangle5.height -= h;
        }
        if (ar.x + ar.width > srcR.x + srcR.width) {
            int w = ar.x + ar.width - (srcR.x + srcR.width);
            if (w > ar.width) {
                w = ar.width;
            }
            final int x0 = ar.x + ar.width - w;
            dr.x = x0;
            dr.y = ar.y;
            dr.width = w;
            dr.height = ar.height;
            zr.zeroRect(dr);
            final Rectangle rectangle6 = ar;
            rectangle6.width -= w;
        }
    }
    
    protected void handleReplicate(final WritableRaster wr) {
        final CachableRed src = this.getSources().get(0);
        final Rectangle srcR = src.getBounds();
        final Rectangle wrR = wr.getBounds();
        final int x = wrR.x;
        final int y = wrR.y;
        final int width = wrR.width;
        final int height = wrR.height;
        final int minX = (srcR.x > x) ? srcR.x : x;
        final int maxX = (srcR.x + srcR.width - 1 < x + width - 1) ? (srcR.x + srcR.width - 1) : (x + width - 1);
        final int minY = (srcR.y > y) ? srcR.y : y;
        final int maxY = (srcR.y + srcR.height - 1 < y + height - 1) ? (srcR.y + srcR.height - 1) : (y + height - 1);
        int x2 = minX;
        int w = maxX - minX + 1;
        int y2 = minY;
        int h = maxY - minY + 1;
        if (w < 0) {
            x2 = 0;
            w = 0;
        }
        if (h < 0) {
            y2 = 0;
            h = 0;
        }
        final Rectangle r = new Rectangle(x2, y2, w, h);
        if (y < srcR.y) {
            int repW = r.width;
            int repX = r.x;
            int wrX = r.x;
            int wrY = y;
            if (x + width - 1 <= srcR.x) {
                repW = 1;
                repX = srcR.x;
                wrX = x + width - 1;
            }
            else if (x >= srcR.x + srcR.width) {
                repW = 1;
                repX = srcR.x + srcR.width - 1;
                wrX = x;
            }
            final WritableRaster wr2 = wr.createWritableChild(wrX, wrY, repW, 1, repX, srcR.y, null);
            src.copyData(wr2);
            ++wrY;
            int endY = srcR.y;
            if (y + height < endY) {
                endY = y + height;
            }
            if (wrY < endY) {
                final int[] pixels = wr.getPixels(wrX, wrY - 1, repW, 1, (int[])null);
                while (wrY < srcR.y) {
                    wr.setPixels(wrX, wrY, repW, 1, pixels);
                    ++wrY;
                }
            }
        }
        if (y + height > srcR.y + srcR.height) {
            int repW = r.width;
            int repX = r.x;
            final int repY = srcR.y + srcR.height - 1;
            int wrX2 = r.x;
            int wrY2 = srcR.y + srcR.height;
            if (wrY2 < y) {
                wrY2 = y;
            }
            if (x + width <= srcR.x) {
                repW = 1;
                repX = srcR.x;
                wrX2 = x + width - 1;
            }
            else if (x >= srcR.x + srcR.width) {
                repW = 1;
                repX = srcR.x + srcR.width - 1;
                wrX2 = x;
            }
            final WritableRaster wr3 = wr.createWritableChild(wrX2, wrY2, repW, 1, repX, repY, null);
            src.copyData(wr3);
            ++wrY2;
            final int endY2 = y + height;
            if (wrY2 < endY2) {
                final int[] pixels2 = wr.getPixels(wrX2, wrY2 - 1, repW, 1, (int[])null);
                while (wrY2 < endY2) {
                    wr.setPixels(wrX2, wrY2, repW, 1, pixels2);
                    ++wrY2;
                }
            }
        }
        if (x < srcR.x) {
            int wrX3 = srcR.x;
            if (x + width <= srcR.x) {
                wrX3 = x + width - 1;
            }
            int xLoc = x;
            final int[] pixels3 = wr.getPixels(wrX3, y, 1, height, (int[])null);
            while (xLoc < wrX3) {
                wr.setPixels(xLoc, y, 1, height, pixels3);
                ++xLoc;
            }
        }
        if (x + width > srcR.x + srcR.width) {
            int wrX3 = srcR.x + srcR.width - 1;
            if (x >= srcR.x + srcR.width) {
                wrX3 = x;
            }
            int xLoc = wrX3 + 1;
            final int endX = x + width - 1;
            final int[] pixels4 = wr.getPixels(wrX3, y, 1, height, (int[])null);
            while (xLoc < endX) {
                wr.setPixels(xLoc, y, 1, height, pixels4);
                ++xLoc;
            }
        }
    }
    
    protected void handleWrap(final WritableRaster wr) {
        this.handleZero(wr);
    }
    
    protected static SampleModel fixSampleModel(final CachableRed src, final Rectangle bounds) {
        final int defSz = AbstractTiledRed.getDefaultTileSize();
        final SampleModel sm = src.getSampleModel();
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
        return sm.createCompatibleSampleModel(w, h);
    }
    
    protected static class ZeroRecter
    {
        WritableRaster wr;
        int bands;
        static int[] zeros;
        
        public ZeroRecter(final WritableRaster wr) {
            this.wr = wr;
            this.bands = wr.getSampleModel().getNumBands();
        }
        
        public void zeroRect(final Rectangle r) {
            synchronized (this) {
                if (ZeroRecter.zeros == null || ZeroRecter.zeros.length < r.width * this.bands) {
                    ZeroRecter.zeros = new int[r.width * this.bands];
                }
            }
            for (int y = 0; y < r.height; ++y) {
                this.wr.setPixels(r.x, r.y + y, r.width, 1, ZeroRecter.zeros);
            }
        }
        
        public static ZeroRecter getZeroRecter(final WritableRaster wr) {
            if (GraphicsUtil.is_INT_PACK_Data(wr.getSampleModel(), false)) {
                return new ZeroRecter_INT_PACK(wr);
            }
            return new ZeroRecter(wr);
        }
        
        public static void zeroRect(final WritableRaster wr) {
            final ZeroRecter zr = getZeroRecter(wr);
            zr.zeroRect(wr.getBounds());
        }
        
        static {
            ZeroRecter.zeros = null;
        }
    }
    
    protected static class ZeroRecter_INT_PACK extends ZeroRecter
    {
        final int base;
        final int scanStride;
        final int[] pixels;
        final int[] zeros;
        final int x0;
        final int y0;
        
        public ZeroRecter_INT_PACK(final WritableRaster wr) {
            super(wr);
            final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
            this.scanStride = sppsm.getScanlineStride();
            final DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
            this.x0 = wr.getMinY();
            this.y0 = wr.getMinX();
            this.base = db.getOffset() + sppsm.getOffset(this.x0 - wr.getSampleModelTranslateX(), this.y0 - wr.getSampleModelTranslateY());
            this.pixels = db.getBankData()[0];
            if (wr.getWidth() > 10) {
                this.zeros = new int[wr.getWidth()];
            }
            else {
                this.zeros = null;
            }
        }
        
        @Override
        public void zeroRect(final Rectangle r) {
            final int rbase = this.base + (r.x - this.x0) + (r.y - this.y0) * this.scanStride;
            if (r.width > 10) {
                for (int y = 0; y < r.height; ++y) {
                    final int sp = rbase + y * this.scanStride;
                    System.arraycopy(this.zeros, 0, this.pixels, sp, r.width);
                }
            }
            else {
                int sp2 = rbase;
                int end = sp2 + r.width;
                final int adj = this.scanStride - r.width;
                for (int y2 = 0; y2 < r.height; ++y2) {
                    while (sp2 < end) {
                        this.pixels[sp2++] = 0;
                    }
                    sp2 += adj;
                    end += this.scanStride;
                }
            }
        }
    }
}
