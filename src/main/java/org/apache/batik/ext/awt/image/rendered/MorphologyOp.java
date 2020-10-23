// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.image.DataBufferInt;
import java.util.Hashtable;
import java.awt.image.WritableRaster;
import java.awt.RenderingHints;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.DirectColorModel;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.Raster;
import java.awt.color.ColorSpace;
import java.awt.image.RasterOp;
import java.awt.image.BufferedImageOp;

public class MorphologyOp implements BufferedImageOp, RasterOp
{
    private int radiusX;
    private int radiusY;
    private boolean doDilation;
    private final int rangeX;
    private final int rangeY;
    private final ColorSpace sRGB;
    private final ColorSpace lRGB;
    
    public MorphologyOp(final int radiusX, final int radiusY, final boolean doDilation) {
        this.sRGB = ColorSpace.getInstance(1000);
        this.lRGB = ColorSpace.getInstance(1004);
        if (radiusX <= 0 || radiusY <= 0) {
            throw new IllegalArgumentException("The radius of X-axis or Y-axis should not be Zero or Negatives.");
        }
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.doDilation = doDilation;
        this.rangeX = 2 * radiusX + 1;
        this.rangeY = 2 * radiusY + 1;
    }
    
    @Override
    public Rectangle2D getBounds2D(final Raster src) {
        this.checkCompatible(src.getSampleModel());
        return new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
    }
    
    @Override
    public Rectangle2D getBounds2D(final BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }
    
    @Override
    public Point2D getPoint2D(final Point2D srcPt, Point2D destPt) {
        if (destPt == null) {
            destPt = new Point2D.Float();
        }
        destPt.setLocation(srcPt.getX(), srcPt.getY());
        return destPt;
    }
    
    private void checkCompatible(final ColorModel colorModel, final SampleModel sampleModel) {
        final ColorSpace cs = colorModel.getColorSpace();
        if (!cs.equals(this.sRGB) && !cs.equals(this.lRGB)) {
            throw new IllegalArgumentException("Expected CS_sRGB or CS_LINEAR_RGB color model");
        }
        if (!(colorModel instanceof DirectColorModel)) {
            throw new IllegalArgumentException("colorModel should be an instance of DirectColorModel");
        }
        if (sampleModel.getDataType() != 3) {
            throw new IllegalArgumentException("colorModel's transferType should be DataBuffer.TYPE_INT");
        }
        final DirectColorModel dcm = (DirectColorModel)colorModel;
        if (dcm.getRedMask() != 16711680) {
            throw new IllegalArgumentException("red mask in source should be 0x00ff0000");
        }
        if (dcm.getGreenMask() != 65280) {
            throw new IllegalArgumentException("green mask in source should be 0x0000ff00");
        }
        if (dcm.getBlueMask() != 255) {
            throw new IllegalArgumentException("blue mask in source should be 0x000000ff");
        }
        if (dcm.getAlphaMask() != -16777216) {
            throw new IllegalArgumentException("alpha mask in source should be 0xff000000");
        }
    }
    
    private boolean isCompatible(final ColorModel colorModel, final SampleModel sampleModel) {
        final ColorSpace cs = colorModel.getColorSpace();
        if (cs != ColorSpace.getInstance(1000) && cs != ColorSpace.getInstance(1004)) {
            return false;
        }
        if (!(colorModel instanceof DirectColorModel)) {
            return false;
        }
        if (sampleModel.getDataType() != 3) {
            return false;
        }
        final DirectColorModel dcm = (DirectColorModel)colorModel;
        return dcm.getRedMask() == 16711680 && dcm.getGreenMask() == 65280 && dcm.getBlueMask() == 255 && dcm.getAlphaMask() == -16777216;
    }
    
    private void checkCompatible(final SampleModel model) {
        if (!(model instanceof SinglePixelPackedSampleModel)) {
            throw new IllegalArgumentException("MorphologyOp only works with Rasters using SinglePixelPackedSampleModels");
        }
        final int nBands = model.getNumBands();
        if (nBands != 4) {
            throw new IllegalArgumentException("MorphologyOp only words with Rasters having 4 bands");
        }
        if (model.getDataType() != 3) {
            throw new IllegalArgumentException("MorphologyOp only works with Rasters using DataBufferInt");
        }
        final int[] bitOffsets = ((SinglePixelPackedSampleModel)model).getBitOffsets();
        for (int i = 0; i < bitOffsets.length; ++i) {
            if (bitOffsets[i] % 8 != 0) {
                throw new IllegalArgumentException("MorphologyOp only works with Rasters using 8 bits per band : " + i + " : " + bitOffsets[i]);
            }
        }
    }
    
    @Override
    public RenderingHints getRenderingHints() {
        return null;
    }
    
    @Override
    public WritableRaster createCompatibleDestRaster(final Raster src) {
        this.checkCompatible(src.getSampleModel());
        return src.createCompatibleWritableRaster();
    }
    
    @Override
    public BufferedImage createCompatibleDestImage(final BufferedImage src, ColorModel destCM) {
        BufferedImage dest = null;
        if (destCM == null) {
            destCM = src.getColorModel();
        }
        final WritableRaster wr = destCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight());
        this.checkCompatible(destCM, wr.getSampleModel());
        dest = new BufferedImage(destCM, wr, destCM.isAlphaPremultiplied(), null);
        return dest;
    }
    
    static final boolean isBetter(final int v1, final int v2, final boolean doDilation) {
        if (v1 > v2) {
            return doDilation;
        }
        return v1 >= v2 || !doDilation;
    }
    
    private void specialProcessRow(final Raster src, final WritableRaster dest) {
        final int w = src.getWidth();
        final int h = src.getHeight();
        final DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        final DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)src.getSampleModel();
        final int srcOff = srcDB.getOffset() + sppsm.getOffset(src.getMinX() - src.getSampleModelTranslateX(), src.getMinY() - src.getSampleModelTranslateY());
        sppsm = (SinglePixelPackedSampleModel)dest.getSampleModel();
        final int dstOff = dstDB.getOffset() + sppsm.getOffset(dest.getMinX() - dest.getSampleModelTranslateX(), dest.getMinY() - dest.getSampleModelTranslateY());
        final int srcScanStride = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
        final int dstScanStride = ((SinglePixelPackedSampleModel)dest.getSampleModel()).getScanlineStride();
        final int[] srcPixels = srcDB.getBankData()[0];
        final int[] destPixels = dstDB.getBankData()[0];
        if (w <= this.radiusX) {
            for (int i = 0; i < h; ++i) {
                int sp = srcOff + i * srcScanStride;
                int dp = dstOff + i * dstScanStride;
                final int pel = srcPixels[sp++];
                int a = pel >>> 24;
                int r = pel & 0xFF0000;
                int g = pel & 0xFF00;
                int b = pel & 0xFF;
                for (int k = 1; k < w; ++k) {
                    final int currentPixel = srcPixels[sp++];
                    final int a2 = currentPixel >>> 24;
                    final int r2 = currentPixel & 0xFF0000;
                    final int g2 = currentPixel & 0xFF00;
                    final int b2 = currentPixel & 0xFF;
                    if (isBetter(a2, a, this.doDilation)) {
                        a = a2;
                    }
                    if (isBetter(r2, r, this.doDilation)) {
                        r = r2;
                    }
                    if (isBetter(g2, g, this.doDilation)) {
                        g = g2;
                    }
                    if (isBetter(b2, b, this.doDilation)) {
                        b = b2;
                    }
                }
                for (int k = 0; k < w; ++k) {
                    destPixels[dp++] = (a << 24 | r | g | b);
                }
            }
        }
        else {
            final int[] bufferA = new int[w];
            final int[] bufferR = new int[w];
            final int[] bufferG = new int[w];
            final int[] bufferB = new int[w];
            for (int j = 0; j < h; ++j) {
                int sp = srcOff + j * srcScanStride;
                int dp = dstOff + j * dstScanStride;
                int bufferHead = 0;
                int maxIndexA = 0;
                int maxIndexR = 0;
                int maxIndexG = 0;
                int maxIndexB = 0;
                final int pel = srcPixels[sp++];
                int a = pel >>> 24;
                int r = pel & 0xFF0000;
                int g = pel & 0xFF00;
                int b = pel & 0xFF;
                bufferA[0] = a;
                bufferR[0] = r;
                bufferG[0] = g;
                bufferB[0] = b;
                for (int l = 1; l <= this.radiusX; ++l) {
                    final int currentPixel = srcPixels[sp++];
                    final int a2 = currentPixel >>> 24;
                    final int r2 = currentPixel & 0xFF0000;
                    final int g2 = currentPixel & 0xFF00;
                    final int b2 = currentPixel & 0xFF;
                    bufferA[l] = a2;
                    bufferR[l] = r2;
                    bufferG[l] = g2;
                    bufferB[l] = b2;
                    if (isBetter(a2, a, this.doDilation)) {
                        a = a2;
                        maxIndexA = l;
                    }
                    if (isBetter(r2, r, this.doDilation)) {
                        r = r2;
                        maxIndexR = l;
                    }
                    if (isBetter(g2, g, this.doDilation)) {
                        g = g2;
                        maxIndexG = l;
                    }
                    if (isBetter(b2, b, this.doDilation)) {
                        b = b2;
                        maxIndexB = l;
                    }
                }
                destPixels[dp++] = (a << 24 | r | g | b);
                for (int m = 1; m <= w - this.radiusX - 1; ++m) {
                    final int lastPixel = srcPixels[sp++];
                    a = bufferA[maxIndexA];
                    final int a2 = lastPixel >>> 24;
                    bufferA[m + this.radiusX] = a2;
                    if (isBetter(a2, a, this.doDilation)) {
                        a = a2;
                        maxIndexA = m + this.radiusX;
                    }
                    r = bufferR[maxIndexR];
                    final int r2 = lastPixel & 0xFF0000;
                    bufferR[m + this.radiusX] = r2;
                    if (isBetter(r2, r, this.doDilation)) {
                        r = r2;
                        maxIndexR = m + this.radiusX;
                    }
                    g = bufferG[maxIndexG];
                    final int g2 = lastPixel & 0xFF00;
                    bufferG[m + this.radiusX] = g2;
                    if (isBetter(g2, g, this.doDilation)) {
                        g = g2;
                        maxIndexG = m + this.radiusX;
                    }
                    b = bufferB[maxIndexB];
                    final int b2 = lastPixel & 0xFF;
                    bufferB[m + this.radiusX] = b2;
                    if (isBetter(b2, b, this.doDilation)) {
                        b = b2;
                        maxIndexB = m + this.radiusX;
                    }
                    destPixels[dp++] = (a << 24 | r | g | b);
                }
                for (int m = w - this.radiusX; m <= this.radiusX; ++m) {
                    destPixels[dp] = destPixels[dp - 1];
                    ++dp;
                }
                for (int m = this.radiusX + 1; m < w; ++m) {
                    if (maxIndexA == bufferHead) {
                        a = bufferA[bufferHead + 1];
                        maxIndexA = bufferHead + 1;
                        for (int m2 = bufferHead + 2; m2 < w; ++m2) {
                            final int a2 = bufferA[m2];
                            if (isBetter(a2, a, this.doDilation)) {
                                a = a2;
                                maxIndexA = m2;
                            }
                        }
                    }
                    else {
                        a = bufferA[maxIndexA];
                    }
                    if (maxIndexR == bufferHead) {
                        r = bufferR[bufferHead + 1];
                        maxIndexR = bufferHead + 1;
                        for (int m2 = bufferHead + 2; m2 < w; ++m2) {
                            final int r2 = bufferR[m2];
                            if (isBetter(r2, r, this.doDilation)) {
                                r = r2;
                                maxIndexR = m2;
                            }
                        }
                    }
                    else {
                        r = bufferR[maxIndexR];
                    }
                    if (maxIndexG == bufferHead) {
                        g = bufferG[bufferHead + 1];
                        maxIndexG = bufferHead + 1;
                        for (int m2 = bufferHead + 2; m2 < w; ++m2) {
                            final int g2 = bufferG[m2];
                            if (isBetter(g2, g, this.doDilation)) {
                                g = g2;
                                maxIndexG = m2;
                            }
                        }
                    }
                    else {
                        g = bufferG[maxIndexG];
                    }
                    if (maxIndexB == bufferHead) {
                        b = bufferB[bufferHead + 1];
                        maxIndexB = bufferHead + 1;
                        for (int m2 = bufferHead + 2; m2 < w; ++m2) {
                            final int b2 = bufferB[m2];
                            if (isBetter(b2, b, this.doDilation)) {
                                b = b2;
                                maxIndexB = m2;
                            }
                        }
                    }
                    else {
                        b = bufferB[maxIndexB];
                    }
                    ++bufferHead;
                    destPixels[dp++] = (a << 24 | r | g | b);
                }
            }
        }
    }
    
    private void specialProcessColumn(final Raster src, final WritableRaster dest) {
        final int w = src.getWidth();
        final int h = src.getHeight();
        final DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
        final int dstOff = dstDB.getOffset();
        final int dstScanStride = ((SinglePixelPackedSampleModel)dest.getSampleModel()).getScanlineStride();
        final int[] destPixels = dstDB.getBankData()[0];
        if (h <= this.radiusY) {
            for (int j = 0; j < w; ++j) {
                int dp = dstOff + j;
                int cp = dstOff + j;
                final int pel = destPixels[cp];
                cp += dstScanStride;
                int a = pel >>> 24;
                int r = pel & 0xFF0000;
                int g = pel & 0xFF00;
                int b = pel & 0xFF;
                for (int k = 1; k < h; ++k) {
                    final int currentPixel = destPixels[cp];
                    cp += dstScanStride;
                    final int a2 = currentPixel >>> 24;
                    final int r2 = currentPixel & 0xFF0000;
                    final int g2 = currentPixel & 0xFF00;
                    final int b2 = currentPixel & 0xFF;
                    if (isBetter(a2, a, this.doDilation)) {
                        a = a2;
                    }
                    if (isBetter(r2, r, this.doDilation)) {
                        r = r2;
                    }
                    if (isBetter(g2, g, this.doDilation)) {
                        g = g2;
                    }
                    if (isBetter(b2, b, this.doDilation)) {
                        b = b2;
                    }
                }
                for (int k = 0; k < h; ++k) {
                    destPixels[dp] = (a << 24 | r | g | b);
                    dp += dstScanStride;
                }
            }
        }
        else {
            final int[] bufferA = new int[h];
            final int[] bufferR = new int[h];
            final int[] bufferG = new int[h];
            final int[] bufferB = new int[h];
            for (int i = 0; i < w; ++i) {
                int dp = dstOff + i;
                int cp = dstOff + i;
                int bufferHead = 0;
                int maxIndexA = 0;
                int maxIndexR = 0;
                int maxIndexG = 0;
                int maxIndexB = 0;
                final int pel = destPixels[cp];
                cp += dstScanStride;
                int a = pel >>> 24;
                int r = pel & 0xFF0000;
                int g = pel & 0xFF00;
                int b = pel & 0xFF;
                bufferA[0] = a;
                bufferR[0] = r;
                bufferG[0] = g;
                bufferB[0] = b;
                for (int l = 1; l <= this.radiusY; ++l) {
                    final int currentPixel = destPixels[cp];
                    cp += dstScanStride;
                    final int a2 = currentPixel >>> 24;
                    final int r2 = currentPixel & 0xFF0000;
                    final int g2 = currentPixel & 0xFF00;
                    final int b2 = currentPixel & 0xFF;
                    bufferA[l] = a2;
                    bufferR[l] = r2;
                    bufferG[l] = g2;
                    bufferB[l] = b2;
                    if (isBetter(a2, a, this.doDilation)) {
                        a = a2;
                        maxIndexA = l;
                    }
                    if (isBetter(r2, r, this.doDilation)) {
                        r = r2;
                        maxIndexR = l;
                    }
                    if (isBetter(g2, g, this.doDilation)) {
                        g = g2;
                        maxIndexG = l;
                    }
                    if (isBetter(b2, b, this.doDilation)) {
                        b = b2;
                        maxIndexB = l;
                    }
                }
                destPixels[dp] = (a << 24 | r | g | b);
                dp += dstScanStride;
                for (int m = 1; m <= h - this.radiusY - 1; ++m) {
                    final int lastPixel = destPixels[cp];
                    cp += dstScanStride;
                    a = bufferA[maxIndexA];
                    final int a2 = lastPixel >>> 24;
                    bufferA[m + this.radiusY] = a2;
                    if (isBetter(a2, a, this.doDilation)) {
                        a = a2;
                        maxIndexA = m + this.radiusY;
                    }
                    r = bufferR[maxIndexR];
                    final int r2 = lastPixel & 0xFF0000;
                    bufferR[m + this.radiusY] = r2;
                    if (isBetter(r2, r, this.doDilation)) {
                        r = r2;
                        maxIndexR = m + this.radiusY;
                    }
                    g = bufferG[maxIndexG];
                    final int g2 = lastPixel & 0xFF00;
                    bufferG[m + this.radiusY] = g2;
                    if (isBetter(g2, g, this.doDilation)) {
                        g = g2;
                        maxIndexG = m + this.radiusY;
                    }
                    b = bufferB[maxIndexB];
                    final int b2 = lastPixel & 0xFF;
                    bufferB[m + this.radiusY] = b2;
                    if (isBetter(b2, b, this.doDilation)) {
                        b = b2;
                        maxIndexB = m + this.radiusY;
                    }
                    destPixels[dp] = (a << 24 | r | g | b);
                    dp += dstScanStride;
                }
                for (int m = h - this.radiusY; m <= this.radiusY; ++m) {
                    destPixels[dp] = destPixels[dp - dstScanStride];
                    dp += dstScanStride;
                }
                for (int m = this.radiusY + 1; m < h; ++m) {
                    if (maxIndexA == bufferHead) {
                        a = bufferA[bufferHead + 1];
                        maxIndexA = bufferHead + 1;
                        for (int m2 = bufferHead + 2; m2 < h; ++m2) {
                            final int a2 = bufferA[m2];
                            if (isBetter(a2, a, this.doDilation)) {
                                a = a2;
                                maxIndexA = m2;
                            }
                        }
                    }
                    else {
                        a = bufferA[maxIndexA];
                    }
                    if (maxIndexR == bufferHead) {
                        r = bufferR[bufferHead + 1];
                        maxIndexR = bufferHead + 1;
                        for (int m2 = bufferHead + 2; m2 < h; ++m2) {
                            final int r2 = bufferR[m2];
                            if (isBetter(r2, r, this.doDilation)) {
                                r = r2;
                                maxIndexR = m2;
                            }
                        }
                    }
                    else {
                        r = bufferR[maxIndexR];
                    }
                    if (maxIndexG == bufferHead) {
                        g = bufferG[bufferHead + 1];
                        maxIndexG = bufferHead + 1;
                        for (int m2 = bufferHead + 2; m2 < h; ++m2) {
                            final int g2 = bufferG[m2];
                            if (isBetter(g2, g, this.doDilation)) {
                                g = g2;
                                maxIndexG = m2;
                            }
                        }
                    }
                    else {
                        g = bufferG[maxIndexG];
                    }
                    if (maxIndexB == bufferHead) {
                        b = bufferB[bufferHead + 1];
                        maxIndexB = bufferHead + 1;
                        for (int m2 = bufferHead + 2; m2 < h; ++m2) {
                            final int b2 = bufferB[m2];
                            if (isBetter(b2, b, this.doDilation)) {
                                b = b2;
                                maxIndexB = m2;
                            }
                        }
                    }
                    else {
                        b = bufferB[maxIndexB];
                    }
                    ++bufferHead;
                    destPixels[dp] = (a << 24 | r | g | b);
                    dp += dstScanStride;
                }
            }
        }
    }
    
    @Override
    public WritableRaster filter(final Raster src, WritableRaster dest) {
        if (dest != null) {
            this.checkCompatible(dest.getSampleModel());
        }
        else {
            if (src == null) {
                throw new IllegalArgumentException("src should not be null when dest is null");
            }
            dest = this.createCompatibleDestRaster(src);
        }
        final int w = src.getWidth();
        final int h = src.getHeight();
        final DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        final DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
        final int srcOff = srcDB.getOffset();
        final int dstOff = dstDB.getOffset();
        final int srcScanStride = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
        final int dstScanStride = ((SinglePixelPackedSampleModel)dest.getSampleModel()).getScanlineStride();
        final int[] srcPixels = srcDB.getBankData()[0];
        final int[] destPixels = dstDB.getBankData()[0];
        if (w <= 2 * this.radiusX) {
            this.specialProcessRow(src, dest);
        }
        else {
            final int[] bufferA = new int[this.rangeX];
            final int[] bufferR = new int[this.rangeX];
            final int[] bufferG = new int[this.rangeX];
            final int[] bufferB = new int[this.rangeX];
            for (int i = 0; i < h; ++i) {
                int sp = srcOff + i * srcScanStride;
                int dp = dstOff + i * dstScanStride;
                int bufferHead = 0;
                int maxIndexA = 0;
                int maxIndexR = 0;
                int maxIndexG = 0;
                int maxIndexB = 0;
                final int pel = srcPixels[sp++];
                int a = pel >>> 24;
                int r = pel & 0xFF0000;
                int g = pel & 0xFF00;
                int b = pel & 0xFF;
                bufferA[0] = a;
                bufferR[0] = r;
                bufferG[0] = g;
                bufferB[0] = b;
                for (int k = 1; k <= this.radiusX; ++k) {
                    final int currentPixel = srcPixels[sp++];
                    final int a2 = currentPixel >>> 24;
                    final int r2 = currentPixel & 0xFF0000;
                    final int g2 = currentPixel & 0xFF00;
                    final int b2 = currentPixel & 0xFF;
                    bufferA[k] = a2;
                    bufferR[k] = r2;
                    bufferG[k] = g2;
                    bufferB[k] = b2;
                    if (isBetter(a2, a, this.doDilation)) {
                        a = a2;
                        maxIndexA = k;
                    }
                    if (isBetter(r2, r, this.doDilation)) {
                        r = r2;
                        maxIndexR = k;
                    }
                    if (isBetter(g2, g, this.doDilation)) {
                        g = g2;
                        maxIndexG = k;
                    }
                    if (isBetter(b2, b, this.doDilation)) {
                        b = b2;
                        maxIndexB = k;
                    }
                }
                destPixels[dp++] = (a << 24 | r | g | b);
                for (int j = 1; j <= this.radiusX; ++j) {
                    final int lastPixel = srcPixels[sp++];
                    a = bufferA[maxIndexA];
                    final int a2 = lastPixel >>> 24;
                    bufferA[j + this.radiusX] = a2;
                    if (isBetter(a2, a, this.doDilation)) {
                        a = a2;
                        maxIndexA = j + this.radiusX;
                    }
                    r = bufferR[maxIndexR];
                    final int r2 = lastPixel & 0xFF0000;
                    bufferR[j + this.radiusX] = r2;
                    if (isBetter(r2, r, this.doDilation)) {
                        r = r2;
                        maxIndexR = j + this.radiusX;
                    }
                    g = bufferG[maxIndexG];
                    final int g2 = lastPixel & 0xFF00;
                    bufferG[j + this.radiusX] = g2;
                    if (isBetter(g2, g, this.doDilation)) {
                        g = g2;
                        maxIndexG = j + this.radiusX;
                    }
                    b = bufferB[maxIndexB];
                    final int b2 = lastPixel & 0xFF;
                    bufferB[j + this.radiusX] = b2;
                    if (isBetter(b2, b, this.doDilation)) {
                        b = b2;
                        maxIndexB = j + this.radiusX;
                    }
                    destPixels[dp++] = (a << 24 | r | g | b);
                }
                for (int j = this.radiusX + 1; j <= w - 1 - this.radiusX; ++j) {
                    final int lastPixel = srcPixels[sp++];
                    int a2 = lastPixel >>> 24;
                    int r2 = lastPixel & 0xFF0000;
                    int g2 = lastPixel & 0xFF00;
                    int b2 = lastPixel & 0xFF;
                    bufferA[bufferHead] = a2;
                    bufferR[bufferHead] = r2;
                    bufferG[bufferHead] = g2;
                    bufferB[bufferHead] = b2;
                    if (maxIndexA == bufferHead) {
                        a = bufferA[0];
                        maxIndexA = 0;
                        for (int m = 1; m < this.rangeX; ++m) {
                            a2 = bufferA[m];
                            if (isBetter(a2, a, this.doDilation)) {
                                a = a2;
                                maxIndexA = m;
                            }
                        }
                    }
                    else {
                        a = bufferA[maxIndexA];
                        if (isBetter(a2, a, this.doDilation)) {
                            a = a2;
                            maxIndexA = bufferHead;
                        }
                    }
                    if (maxIndexR == bufferHead) {
                        r = bufferR[0];
                        maxIndexR = 0;
                        for (int m = 1; m < this.rangeX; ++m) {
                            r2 = bufferR[m];
                            if (isBetter(r2, r, this.doDilation)) {
                                r = r2;
                                maxIndexR = m;
                            }
                        }
                    }
                    else {
                        r = bufferR[maxIndexR];
                        if (isBetter(r2, r, this.doDilation)) {
                            r = r2;
                            maxIndexR = bufferHead;
                        }
                    }
                    if (maxIndexG == bufferHead) {
                        g = bufferG[0];
                        maxIndexG = 0;
                        for (int m = 1; m < this.rangeX; ++m) {
                            g2 = bufferG[m];
                            if (isBetter(g2, g, this.doDilation)) {
                                g = g2;
                                maxIndexG = m;
                            }
                        }
                    }
                    else {
                        g = bufferG[maxIndexG];
                        if (isBetter(g2, g, this.doDilation)) {
                            g = g2;
                            maxIndexG = bufferHead;
                        }
                    }
                    if (maxIndexB == bufferHead) {
                        b = bufferB[0];
                        maxIndexB = 0;
                        for (int m = 1; m < this.rangeX; ++m) {
                            b2 = bufferB[m];
                            if (isBetter(b2, b, this.doDilation)) {
                                b = b2;
                                maxIndexB = m;
                            }
                        }
                    }
                    else {
                        b = bufferB[maxIndexB];
                        if (isBetter(b2, b, this.doDilation)) {
                            b = b2;
                            maxIndexB = bufferHead;
                        }
                    }
                    destPixels[dp++] = (a << 24 | r | g | b);
                    bufferHead = (bufferHead + 1) % this.rangeX;
                }
                final int tail = (bufferHead == 0) ? (this.rangeX - 1) : (bufferHead - 1);
                int count = this.rangeX - 1;
                for (int l = w - this.radiusX; l < w; ++l) {
                    final int head = (bufferHead + 1) % this.rangeX;
                    if (maxIndexA == bufferHead) {
                        a = bufferA[tail];
                        int hd = head;
                        for (int m2 = 1; m2 < count; ++m2) {
                            final int a2 = bufferA[hd];
                            if (isBetter(a2, a, this.doDilation)) {
                                a = a2;
                                maxIndexA = hd;
                            }
                            hd = (hd + 1) % this.rangeX;
                        }
                    }
                    if (maxIndexR == bufferHead) {
                        r = bufferR[tail];
                        int hd = head;
                        for (int m2 = 1; m2 < count; ++m2) {
                            final int r2 = bufferR[hd];
                            if (isBetter(r2, r, this.doDilation)) {
                                r = r2;
                                maxIndexR = hd;
                            }
                            hd = (hd + 1) % this.rangeX;
                        }
                    }
                    if (maxIndexG == bufferHead) {
                        g = bufferG[tail];
                        int hd = head;
                        for (int m2 = 1; m2 < count; ++m2) {
                            final int g2 = bufferG[hd];
                            if (isBetter(g2, g, this.doDilation)) {
                                g = g2;
                                maxIndexG = hd;
                            }
                            hd = (hd + 1) % this.rangeX;
                        }
                    }
                    if (maxIndexB == bufferHead) {
                        b = bufferB[tail];
                        int hd = head;
                        for (int m2 = 1; m2 < count; ++m2) {
                            final int b2 = bufferB[hd];
                            if (isBetter(b2, b, this.doDilation)) {
                                b = b2;
                                maxIndexB = hd;
                            }
                            hd = (hd + 1) % this.rangeX;
                        }
                    }
                    destPixels[dp++] = (a << 24 | r | g | b);
                    bufferHead = (bufferHead + 1) % this.rangeX;
                    --count;
                }
            }
        }
        if (h <= 2 * this.radiusY) {
            this.specialProcessColumn(src, dest);
        }
        else {
            final int[] bufferA = new int[this.rangeY];
            final int[] bufferR = new int[this.rangeY];
            final int[] bufferG = new int[this.rangeY];
            final int[] bufferB = new int[this.rangeY];
            for (int j2 = 0; j2 < w; ++j2) {
                int dp = dstOff + j2;
                int cp = dstOff + j2;
                int bufferHead = 0;
                int maxIndexA = 0;
                int maxIndexR = 0;
                int maxIndexG = 0;
                int maxIndexB = 0;
                final int pel = destPixels[cp];
                cp += dstScanStride;
                int a = pel >>> 24;
                int r = pel & 0xFF0000;
                int g = pel & 0xFF00;
                int b = pel & 0xFF;
                bufferA[0] = a;
                bufferR[0] = r;
                bufferG[0] = g;
                bufferB[0] = b;
                for (int k = 1; k <= this.radiusY; ++k) {
                    final int currentPixel = destPixels[cp];
                    cp += dstScanStride;
                    final int a2 = currentPixel >>> 24;
                    final int r2 = currentPixel & 0xFF0000;
                    final int g2 = currentPixel & 0xFF00;
                    final int b2 = currentPixel & 0xFF;
                    bufferA[k] = a2;
                    bufferR[k] = r2;
                    bufferG[k] = g2;
                    bufferB[k] = b2;
                    if (isBetter(a2, a, this.doDilation)) {
                        a = a2;
                        maxIndexA = k;
                    }
                    if (isBetter(r2, r, this.doDilation)) {
                        r = r2;
                        maxIndexR = k;
                    }
                    if (isBetter(g2, g, this.doDilation)) {
                        g = g2;
                        maxIndexG = k;
                    }
                    if (isBetter(b2, b, this.doDilation)) {
                        b = b2;
                        maxIndexB = k;
                    }
                }
                destPixels[dp] = (a << 24 | r | g | b);
                dp += dstScanStride;
                for (int i2 = 1; i2 <= this.radiusY; ++i2) {
                    final int maxI = i2 + this.radiusY;
                    final int lastPixel = destPixels[cp];
                    cp += dstScanStride;
                    a = bufferA[maxIndexA];
                    final int a2 = lastPixel >>> 24;
                    bufferA[maxI] = a2;
                    if (isBetter(a2, a, this.doDilation)) {
                        a = a2;
                        maxIndexA = maxI;
                    }
                    r = bufferR[maxIndexR];
                    final int r2 = lastPixel & 0xFF0000;
                    bufferR[maxI] = r2;
                    if (isBetter(r2, r, this.doDilation)) {
                        r = r2;
                        maxIndexR = maxI;
                    }
                    g = bufferG[maxIndexG];
                    final int g2 = lastPixel & 0xFF00;
                    bufferG[maxI] = g2;
                    if (isBetter(g2, g, this.doDilation)) {
                        g = g2;
                        maxIndexG = maxI;
                    }
                    b = bufferB[maxIndexB];
                    final int b2 = lastPixel & 0xFF;
                    bufferB[maxI] = b2;
                    if (isBetter(b2, b, this.doDilation)) {
                        b = b2;
                        maxIndexB = maxI;
                    }
                    destPixels[dp] = (a << 24 | r | g | b);
                    dp += dstScanStride;
                }
                for (int i2 = this.radiusY + 1; i2 <= h - 1 - this.radiusY; ++i2) {
                    final int lastPixel = destPixels[cp];
                    cp += dstScanStride;
                    int a2 = lastPixel >>> 24;
                    int r2 = lastPixel & 0xFF0000;
                    int g2 = lastPixel & 0xFF00;
                    int b2 = lastPixel & 0xFF;
                    bufferA[bufferHead] = a2;
                    bufferR[bufferHead] = r2;
                    bufferG[bufferHead] = g2;
                    bufferB[bufferHead] = b2;
                    if (maxIndexA == bufferHead) {
                        a = bufferA[0];
                        maxIndexA = 0;
                        for (int m = 1; m <= 2 * this.radiusY; ++m) {
                            a2 = bufferA[m];
                            if (isBetter(a2, a, this.doDilation)) {
                                a = a2;
                                maxIndexA = m;
                            }
                        }
                    }
                    else {
                        a = bufferA[maxIndexA];
                        if (isBetter(a2, a, this.doDilation)) {
                            a = a2;
                            maxIndexA = bufferHead;
                        }
                    }
                    if (maxIndexR == bufferHead) {
                        r = bufferR[0];
                        maxIndexR = 0;
                        for (int m = 1; m <= 2 * this.radiusY; ++m) {
                            r2 = bufferR[m];
                            if (isBetter(r2, r, this.doDilation)) {
                                r = r2;
                                maxIndexR = m;
                            }
                        }
                    }
                    else {
                        r = bufferR[maxIndexR];
                        if (isBetter(r2, r, this.doDilation)) {
                            r = r2;
                            maxIndexR = bufferHead;
                        }
                    }
                    if (maxIndexG == bufferHead) {
                        g = bufferG[0];
                        maxIndexG = 0;
                        for (int m = 1; m <= 2 * this.radiusY; ++m) {
                            g2 = bufferG[m];
                            if (isBetter(g2, g, this.doDilation)) {
                                g = g2;
                                maxIndexG = m;
                            }
                        }
                    }
                    else {
                        g = bufferG[maxIndexG];
                        if (isBetter(g2, g, this.doDilation)) {
                            g = g2;
                            maxIndexG = bufferHead;
                        }
                    }
                    if (maxIndexB == bufferHead) {
                        b = bufferB[0];
                        maxIndexB = 0;
                        for (int m = 1; m <= 2 * this.radiusY; ++m) {
                            b2 = bufferB[m];
                            if (isBetter(b2, b, this.doDilation)) {
                                b = b2;
                                maxIndexB = m;
                            }
                        }
                    }
                    else {
                        b = bufferB[maxIndexB];
                        if (isBetter(b2, b, this.doDilation)) {
                            b = b2;
                            maxIndexB = bufferHead;
                        }
                    }
                    destPixels[dp] = (a << 24 | r | g | b);
                    dp += dstScanStride;
                    bufferHead = (bufferHead + 1) % this.rangeY;
                }
                final int tail = (bufferHead == 0) ? (2 * this.radiusY) : (bufferHead - 1);
                int count = this.rangeY - 1;
                for (int i3 = h - this.radiusY; i3 < h - 1; ++i3) {
                    final int head = (bufferHead + 1) % this.rangeY;
                    if (maxIndexA == bufferHead) {
                        a = bufferA[tail];
                        int hd = head;
                        for (int m2 = 1; m2 < count; ++m2) {
                            final int a2 = bufferA[hd];
                            if (isBetter(a2, a, this.doDilation)) {
                                a = a2;
                                maxIndexA = hd;
                            }
                            hd = (hd + 1) % this.rangeY;
                        }
                    }
                    if (maxIndexR == bufferHead) {
                        r = bufferR[tail];
                        int hd = head;
                        for (int m2 = 1; m2 < count; ++m2) {
                            final int r2 = bufferR[hd];
                            if (isBetter(r2, r, this.doDilation)) {
                                r = r2;
                                maxIndexR = hd;
                            }
                            hd = (hd + 1) % this.rangeY;
                        }
                    }
                    if (maxIndexG == bufferHead) {
                        g = bufferG[tail];
                        int hd = head;
                        for (int m2 = 1; m2 < count; ++m2) {
                            final int g2 = bufferG[hd];
                            if (isBetter(g2, g, this.doDilation)) {
                                g = g2;
                                maxIndexG = hd;
                            }
                            hd = (hd + 1) % this.rangeY;
                        }
                    }
                    if (maxIndexB == bufferHead) {
                        b = bufferB[tail];
                        int hd = head;
                        for (int m2 = 1; m2 < count; ++m2) {
                            final int b2 = bufferB[hd];
                            if (isBetter(b2, b, this.doDilation)) {
                                b = b2;
                                maxIndexB = hd;
                            }
                            hd = (hd + 1) % this.rangeY;
                        }
                    }
                    destPixels[dp] = (a << 24 | r | g | b);
                    dp += dstScanStride;
                    bufferHead = (bufferHead + 1) % this.rangeY;
                    --count;
                }
            }
        }
        return dest;
    }
    
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (src == null) {
            throw new NullPointerException("Source image should not be null");
        }
        final BufferedImage origSrc = src;
        BufferedImage finalDest = dest;
        if (!this.isCompatible(src.getColorModel(), src.getSampleModel())) {
            src = new BufferedImage(src.getWidth(), src.getHeight(), 3);
            GraphicsUtil.copyData(origSrc, src);
        }
        else if (!src.isAlphaPremultiplied()) {
            final ColorModel srcCM = src.getColorModel();
            final ColorModel srcCMPre = GraphicsUtil.coerceColorModel(srcCM, true);
            src = new BufferedImage(srcCMPre, src.getRaster(), true, null);
            GraphicsUtil.copyData(origSrc, src);
        }
        if (dest == null) {
            dest = (finalDest = this.createCompatibleDestImage(src, null));
        }
        else if (!this.isCompatible(dest.getColorModel(), dest.getSampleModel())) {
            dest = this.createCompatibleDestImage(src, null);
        }
        else if (!dest.isAlphaPremultiplied()) {
            final ColorModel dstCM = dest.getColorModel();
            final ColorModel dstCMPre = GraphicsUtil.coerceColorModel(dstCM, true);
            dest = new BufferedImage(dstCMPre, finalDest.getRaster(), true, null);
        }
        this.filter(src.getRaster(), dest.getRaster());
        if (src.getRaster() == origSrc.getRaster() && src.isAlphaPremultiplied() != origSrc.isAlphaPremultiplied()) {
            GraphicsUtil.copyData(src, origSrc);
        }
        if (dest.getRaster() != finalDest.getRaster() || dest.isAlphaPremultiplied() != finalDest.isAlphaPremultiplied()) {
            GraphicsUtil.copyData(dest, finalDest);
        }
        return finalDest;
    }
}
