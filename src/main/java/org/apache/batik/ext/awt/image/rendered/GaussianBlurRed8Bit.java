// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.color.ColorSpace;
import java.awt.image.DirectColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.Raster;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.image.WritableRaster;
import java.awt.image.Kernel;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.awt.Rectangle;
import java.util.Map;
import java.awt.image.ConvolveOp;
import java.awt.RenderingHints;

public class GaussianBlurRed8Bit extends AbstractRed
{
    int xinset;
    int yinset;
    double stdDevX;
    double stdDevY;
    RenderingHints hints;
    ConvolveOp[] convOp;
    int dX;
    int dY;
    static final float SQRT2PI;
    static final float DSQRT2PI;
    static final float precision = 0.499f;
    
    public GaussianBlurRed8Bit(final CachableRed src, final double stdDev, final RenderingHints rh) {
        this(src, stdDev, stdDev, rh);
    }
    
    public GaussianBlurRed8Bit(final CachableRed src, final double stdDevX, final double stdDevY, final RenderingHints rh) {
        this.convOp = new ConvolveOp[2];
        this.stdDevX = stdDevX;
        this.stdDevY = stdDevY;
        this.hints = rh;
        this.xinset = surroundPixels(stdDevX, rh);
        this.yinset = surroundPixels(stdDevY, rh);
        final Rectangle bounds;
        final Rectangle myBounds = bounds = src.getBounds();
        bounds.x += this.xinset;
        final Rectangle rectangle = myBounds;
        rectangle.y += this.yinset;
        final Rectangle rectangle2 = myBounds;
        rectangle2.width -= 2 * this.xinset;
        final Rectangle rectangle3 = myBounds;
        rectangle3.height -= 2 * this.yinset;
        if (myBounds.width <= 0 || myBounds.height <= 0) {
            myBounds.width = 0;
            myBounds.height = 0;
        }
        final ColorModel cm = fixColorModel(src);
        SampleModel sm = src.getSampleModel();
        int tw = sm.getWidth();
        int th = sm.getHeight();
        if (tw > myBounds.width) {
            tw = myBounds.width;
        }
        if (th > myBounds.height) {
            th = myBounds.height;
        }
        sm = cm.createCompatibleSampleModel(tw, th);
        this.init(src, myBounds, cm, sm, src.getTileGridXOffset() + this.xinset, src.getTileGridYOffset() + this.yinset, null);
        final boolean highQuality = this.hints != null && RenderingHints.VALUE_RENDER_QUALITY.equals(this.hints.get(RenderingHints.KEY_RENDERING));
        if (this.xinset != 0 && (stdDevX < 2.0 || highQuality)) {
            this.convOp[0] = new ConvolveOp(this.makeQualityKernelX(this.xinset * 2 + 1));
        }
        else {
            this.dX = (int)Math.floor(GaussianBlurRed8Bit.DSQRT2PI * stdDevX + 0.5);
        }
        if (this.yinset != 0 && (stdDevY < 2.0 || highQuality)) {
            this.convOp[1] = new ConvolveOp(this.makeQualityKernelY(this.yinset * 2 + 1));
        }
        else {
            this.dY = (int)Math.floor(GaussianBlurRed8Bit.DSQRT2PI * stdDevY + 0.5);
        }
    }
    
    public static int surroundPixels(final double stdDev) {
        return surroundPixels(stdDev, null);
    }
    
    public static int surroundPixels(final double stdDev, final RenderingHints hints) {
        final boolean highQuality = hints != null && RenderingHints.VALUE_RENDER_QUALITY.equals(hints.get(RenderingHints.KEY_RENDERING));
        if (stdDev < 2.0 || highQuality) {
            float areaSum;
            int i;
            for (areaSum = (float)(0.5 / (stdDev * GaussianBlurRed8Bit.SQRT2PI)), i = 0; areaSum < 0.499f; areaSum += (float)(Math.pow(2.718281828459045, -i * i / (2.0 * stdDev * stdDev)) / (stdDev * GaussianBlurRed8Bit.SQRT2PI)), ++i) {}
            return i;
        }
        final int diam = (int)Math.floor(GaussianBlurRed8Bit.DSQRT2PI * stdDev + 0.5);
        if (diam % 2 == 0) {
            return diam - 1 + diam / 2;
        }
        return diam - 2 + diam / 2;
    }
    
    private float[] computeQualityKernelData(final int len, final double stdDev) {
        final float[] kernelData = new float[len];
        final int mid = len / 2;
        float sum = 0.0f;
        for (int i = 0; i < len; ++i) {
            kernelData[i] = (float)(Math.pow(2.718281828459045, -(i - mid) * (i - mid) / (2.0 * stdDev * stdDev)) / (GaussianBlurRed8Bit.SQRT2PI * stdDev));
            sum += kernelData[i];
        }
        for (int i = 0; i < len; ++i) {
            final float[] array = kernelData;
            final int n = i;
            array[n] /= sum;
        }
        return kernelData;
    }
    
    private Kernel makeQualityKernelX(final int len) {
        return new Kernel(len, 1, this.computeQualityKernelData(len, this.stdDevX));
    }
    
    private Kernel makeQualityKernelY(final int len) {
        return new Kernel(1, len, this.computeQualityKernelData(len, this.stdDevY));
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        final CachableRed src = this.getSources().get(0);
        final Rectangle bounds;
        final Rectangle r = bounds = wr.getBounds();
        bounds.x -= this.xinset;
        final Rectangle rectangle = r;
        rectangle.y -= this.yinset;
        final Rectangle rectangle2 = r;
        rectangle2.width += 2 * this.xinset;
        final Rectangle rectangle3 = r;
        rectangle3.height += 2 * this.yinset;
        final ColorModel srcCM = src.getColorModel();
        WritableRaster tmpR1 = null;
        WritableRaster tmpR2 = null;
        tmpR1 = srcCM.createCompatibleWritableRaster(r.width, r.height);
        final WritableRaster fill = tmpR1.createWritableTranslatedChild(r.x, r.y);
        src.copyData(fill);
        if (srcCM.hasAlpha() && !srcCM.isAlphaPremultiplied()) {
            GraphicsUtil.coerceData(tmpR1, srcCM, true);
        }
        int skipX;
        if (this.xinset == 0) {
            skipX = 0;
        }
        else if (this.convOp[0] != null) {
            tmpR2 = this.getColorModel().createCompatibleWritableRaster(r.width, r.height);
            tmpR2 = this.convOp[0].filter(tmpR1, tmpR2);
            skipX = this.convOp[0].getKernel().getXOrigin();
            final WritableRaster tmp = tmpR1;
            tmpR1 = tmpR2;
            tmpR2 = tmp;
        }
        else if ((this.dX & 0x1) == 0x0) {
            tmpR1 = this.boxFilterH(tmpR1, tmpR1, 0, 0, this.dX, this.dX / 2);
            tmpR1 = this.boxFilterH(tmpR1, tmpR1, this.dX / 2, 0, this.dX, this.dX / 2 - 1);
            tmpR1 = this.boxFilterH(tmpR1, tmpR1, this.dX - 1, 0, this.dX + 1, this.dX / 2);
            skipX = this.dX - 1 + this.dX / 2;
        }
        else {
            tmpR1 = this.boxFilterH(tmpR1, tmpR1, 0, 0, this.dX, this.dX / 2);
            tmpR1 = this.boxFilterH(tmpR1, tmpR1, this.dX / 2, 0, this.dX, this.dX / 2);
            tmpR1 = this.boxFilterH(tmpR1, tmpR1, this.dX - 2, 0, this.dX, this.dX / 2);
            skipX = this.dX - 2 + this.dX / 2;
        }
        if (this.yinset == 0) {
            tmpR2 = tmpR1;
        }
        else if (this.convOp[1] != null) {
            if (tmpR2 == null) {
                tmpR2 = this.getColorModel().createCompatibleWritableRaster(r.width, r.height);
            }
            tmpR2 = this.convOp[1].filter(tmpR1, tmpR2);
        }
        else {
            if ((this.dY & 0x1) == 0x0) {
                tmpR1 = this.boxFilterV(tmpR1, tmpR1, skipX, 0, this.dY, this.dY / 2);
                tmpR1 = this.boxFilterV(tmpR1, tmpR1, skipX, this.dY / 2, this.dY, this.dY / 2 - 1);
                tmpR1 = this.boxFilterV(tmpR1, tmpR1, skipX, this.dY - 1, this.dY + 1, this.dY / 2);
            }
            else {
                tmpR1 = this.boxFilterV(tmpR1, tmpR1, skipX, 0, this.dY, this.dY / 2);
                tmpR1 = this.boxFilterV(tmpR1, tmpR1, skipX, this.dY / 2, this.dY, this.dY / 2);
                tmpR1 = this.boxFilterV(tmpR1, tmpR1, skipX, this.dY - 2, this.dY, this.dY / 2);
            }
            tmpR2 = tmpR1;
        }
        tmpR2 = tmpR2.createWritableTranslatedChild(r.x, r.y);
        GraphicsUtil.copyData(tmpR2, wr);
        return wr;
    }
    
    private WritableRaster boxFilterH(final Raster src, final WritableRaster dest, final int skipX, final int skipY, final int boxSz, final int loc) {
        final int w = src.getWidth();
        final int h = src.getHeight();
        if (w < 2 * skipX + boxSz) {
            return dest;
        }
        if (h < 2 * skipY) {
            return dest;
        }
        final SinglePixelPackedSampleModel srcSPPSM = (SinglePixelPackedSampleModel)src.getSampleModel();
        final SinglePixelPackedSampleModel dstSPPSM = (SinglePixelPackedSampleModel)dest.getSampleModel();
        final int srcScanStride = srcSPPSM.getScanlineStride();
        final int dstScanStride = dstSPPSM.getScanlineStride();
        final DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        final DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
        final int srcOff = srcDB.getOffset() + srcSPPSM.getOffset(src.getMinX() - src.getSampleModelTranslateX(), src.getMinY() - src.getSampleModelTranslateY());
        final int dstOff = dstDB.getOffset() + dstSPPSM.getOffset(dest.getMinX() - dest.getSampleModelTranslateX(), dest.getMinY() - dest.getSampleModelTranslateY());
        final int[] srcPixels = srcDB.getBankData()[0];
        final int[] destPixels = dstDB.getBankData()[0];
        final int[] buffer = new int[boxSz];
        final int scale = 16777216 / boxSz;
        for (int y = skipY; y < h - skipY; ++y) {
            int sp = srcOff + y * srcScanStride;
            int dp = dstOff + y * dstScanStride;
            final int rowEnd = sp + (w - skipX);
            int k = 0;
            int sumA = 0;
            int sumR = 0;
            int sumG = 0;
            int sumB = 0;
            sp += skipX;
            for (int end = sp + boxSz; sp < end; ++sp) {
                final int[] array = buffer;
                final int n = k;
                final int n2 = srcPixels[sp];
                array[n] = n2;
                final int curr = n2;
                sumA += curr >>> 24;
                sumR += (curr >> 16 & 0xFF);
                sumG += (curr >> 8 & 0xFF);
                sumB += (curr & 0xFF);
                ++k;
            }
            dp += skipX + loc;
            final int[] array2 = destPixels;
            final int n3 = dp;
            final int n4 = (sumA * scale & 0xFF000000) | (sumR * scale & 0xFF000000) >>> 8 | (sumG * scale & 0xFF000000) >>> 16 | (sumB * scale & 0xFF000000) >>> 24;
            array2[n3] = n4;
            int prev = n4;
            ++dp;
            k = 0;
            while (sp < rowEnd) {
                int curr = buffer[k];
                if (curr == srcPixels[sp]) {
                    destPixels[dp] = prev;
                }
                else {
                    sumA -= curr >>> 24;
                    sumR -= (curr >> 16 & 0xFF);
                    sumG -= (curr >> 8 & 0xFF);
                    sumB -= (curr & 0xFF);
                    final int[] array3 = buffer;
                    final int n5 = k;
                    final int n6 = srcPixels[sp];
                    array3[n5] = n6;
                    curr = n6;
                    sumA += curr >>> 24;
                    sumR += (curr >> 16 & 0xFF);
                    sumG += (curr >> 8 & 0xFF);
                    sumB += (curr & 0xFF);
                    final int[] array4 = destPixels;
                    final int n7 = dp;
                    final int n8 = (sumA * scale & 0xFF000000) | (sumR * scale & 0xFF000000) >>> 8 | (sumG * scale & 0xFF000000) >>> 16 | (sumB * scale & 0xFF000000) >>> 24;
                    array4[n7] = n8;
                    prev = n8;
                }
                k = (k + 1) % boxSz;
                ++sp;
                ++dp;
            }
        }
        return dest;
    }
    
    private WritableRaster boxFilterV(final Raster src, final WritableRaster dest, final int skipX, final int skipY, final int boxSz, final int loc) {
        final int w = src.getWidth();
        final int h = src.getHeight();
        if (w < 2 * skipX) {
            return dest;
        }
        if (h < 2 * skipY + boxSz) {
            return dest;
        }
        final SinglePixelPackedSampleModel srcSPPSM = (SinglePixelPackedSampleModel)src.getSampleModel();
        final SinglePixelPackedSampleModel dstSPPSM = (SinglePixelPackedSampleModel)dest.getSampleModel();
        final int srcScanStride = srcSPPSM.getScanlineStride();
        final int dstScanStride = dstSPPSM.getScanlineStride();
        final DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        final DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
        final int srcOff = srcDB.getOffset() + srcSPPSM.getOffset(src.getMinX() - src.getSampleModelTranslateX(), src.getMinY() - src.getSampleModelTranslateY());
        final int dstOff = dstDB.getOffset() + dstSPPSM.getOffset(dest.getMinX() - dest.getSampleModelTranslateX(), dest.getMinY() - dest.getSampleModelTranslateY());
        final int[] srcPixels = srcDB.getBankData()[0];
        final int[] destPixels = dstDB.getBankData()[0];
        final int[] buffer = new int[boxSz];
        final int scale = 16777216 / boxSz;
        for (int x = skipX; x < w - skipX; ++x) {
            int sp = srcOff + x;
            int dp = dstOff + x;
            final int colEnd = sp + (h - skipY) * srcScanStride;
            int k = 0;
            int sumA = 0;
            int sumR = 0;
            int sumG = 0;
            int sumB = 0;
            sp += skipY * srcScanStride;
            for (int end = sp + boxSz * srcScanStride; sp < end; sp += srcScanStride) {
                final int[] array = buffer;
                final int n = k;
                final int n2 = srcPixels[sp];
                array[n] = n2;
                final int curr = n2;
                sumA += curr >>> 24;
                sumR += (curr >> 16 & 0xFF);
                sumG += (curr >> 8 & 0xFF);
                sumB += (curr & 0xFF);
                ++k;
            }
            dp += (skipY + loc) * dstScanStride;
            final int[] array2 = destPixels;
            final int n3 = dp;
            final int n4 = (sumA * scale & 0xFF000000) | (sumR * scale & 0xFF000000) >>> 8 | (sumG * scale & 0xFF000000) >>> 16 | (sumB * scale & 0xFF000000) >>> 24;
            array2[n3] = n4;
            int prev = n4;
            dp += dstScanStride;
            k = 0;
            while (sp < colEnd) {
                int curr = buffer[k];
                if (curr == srcPixels[sp]) {
                    destPixels[dp] = prev;
                }
                else {
                    sumA -= curr >>> 24;
                    sumR -= (curr >> 16 & 0xFF);
                    sumG -= (curr >> 8 & 0xFF);
                    sumB -= (curr & 0xFF);
                    final int[] array3 = buffer;
                    final int n5 = k;
                    final int n6 = srcPixels[sp];
                    array3[n5] = n6;
                    curr = n6;
                    sumA += curr >>> 24;
                    sumR += (curr >> 16 & 0xFF);
                    sumG += (curr >> 8 & 0xFF);
                    sumB += (curr & 0xFF);
                    final int[] array4 = destPixels;
                    final int n7 = dp;
                    final int n8 = (sumA * scale & 0xFF000000) | (sumR * scale & 0xFF000000) >>> 8 | (sumG * scale & 0xFF000000) >>> 16 | (sumB * scale & 0xFF000000) >>> 24;
                    array4[n7] = n8;
                    prev = n8;
                }
                k = (k + 1) % boxSz;
                sp += srcScanStride;
                dp += dstScanStride;
            }
        }
        return dest;
    }
    
    protected static ColorModel fixColorModel(final CachableRed src) {
        final ColorModel cm = src.getColorModel();
        final int b = src.getSampleModel().getNumBands();
        final int[] masks = new int[4];
        switch (b) {
            case 1: {
                masks[0] = 255;
                break;
            }
            case 2: {
                masks[0] = 255;
                masks[3] = 65280;
                break;
            }
            case 3: {
                masks[0] = 16711680;
                masks[1] = 65280;
                masks[2] = 255;
                break;
            }
            case 4: {
                masks[0] = 16711680;
                masks[1] = 65280;
                masks[2] = 255;
                masks[3] = -16777216;
                break;
            }
            default: {
                throw new IllegalArgumentException("GaussianBlurRed8Bit only supports one to four band images");
            }
        }
        final ColorSpace cs = cm.getColorSpace();
        return new DirectColorModel(cs, 8 * b, masks[0], masks[1], masks[2], masks[3], true, 3);
    }
    
    static {
        SQRT2PI = (float)Math.sqrt(6.283185307179586);
        DSQRT2PI = GaussianBlurRed8Bit.SQRT2PI * 3.0f / 4.0f;
    }
}
