// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.DataBufferInt;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;

public final class BumpMap
{
    private RenderedImage texture;
    private double surfaceScale;
    private double surfaceScaleX;
    private double surfaceScaleY;
    private double scaleX;
    private double scaleY;
    
    public BumpMap(final RenderedImage texture, final double surfaceScale, final double scaleX, final double scaleY) {
        this.texture = texture;
        this.surfaceScaleX = surfaceScale * scaleX;
        this.surfaceScaleY = surfaceScale * scaleY;
        this.surfaceScale = surfaceScale;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
    
    public double getSurfaceScale() {
        return this.surfaceScale;
    }
    
    public double[][][] getNormalArray(final int x, final int y, final int w, final int h) {
        final double[][][] N = new double[h][w][4];
        Rectangle srcRect = new Rectangle(x - 1, y - 1, w + 2, h + 2);
        final Rectangle srcBound = new Rectangle(this.texture.getMinX(), this.texture.getMinY(), this.texture.getWidth(), this.texture.getHeight());
        if (!srcRect.intersects(srcBound)) {
            return N;
        }
        srcRect = srcRect.intersection(srcBound);
        final Raster r = this.texture.getData(srcRect);
        srcRect = r.getBounds();
        final DataBufferInt db = (DataBufferInt)r.getDataBuffer();
        final int[] pixels = db.getBankData()[0];
        final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)r.getSampleModel();
        final int scanStride = sppsm.getScanlineStride();
        final int scanStridePP = scanStride + 1;
        final int scanStrideMM = scanStride - 1;
        double prpc = 0.0;
        double prcc = 0.0;
        double prnc = 0.0;
        double crpc = 0.0;
        double crcc = 0.0;
        double crnc = 0.0;
        double nrpc = 0.0;
        double nrcc = 0.0;
        double nrnc = 0.0;
        final double quarterSurfaceScaleX = this.surfaceScaleX / 4.0;
        final double quarterSurfaceScaleY = this.surfaceScaleY / 4.0;
        final double halfSurfaceScaleX = this.surfaceScaleX / 2.0;
        final double halfSurfaceScaleY = this.surfaceScaleY / 2.0;
        final double thirdSurfaceScaleX = this.surfaceScaleX / 3.0;
        final double thirdSurfaceScaleY = this.surfaceScaleY / 3.0;
        final double twoThirdSurfaceScaleX = this.surfaceScaleX * 2.0 / 3.0;
        final double twoThirdSurfaceScaleY = this.surfaceScaleY * 2.0 / 3.0;
        final double pixelScale = 0.00392156862745098;
        if (w <= 0) {
            return N;
        }
        if (h <= 0) {
            return N;
        }
        final int xEnd = Math.min(srcRect.x + srcRect.width - 1, x + w);
        final int yEnd = Math.min(srcRect.y + srcRect.height - 1, y + h);
        final int offset = db.getOffset() + sppsm.getOffset(srcRect.x - r.getSampleModelTranslateX(), srcRect.y - r.getSampleModelTranslateY());
        int yloc = y;
        if (yloc < srcRect.y) {
            yloc = srcRect.y;
        }
        if (yloc == srcRect.y) {
            if (yloc == yEnd) {
                final double[][] NRow = N[yloc - y];
                int xloc = x;
                if (xloc < srcRect.x) {
                    xloc = srcRect.x;
                }
                int p = offset + (xloc - srcRect.x) + scanStride * (yloc - srcRect.y);
                crcc = (pixels[p] >>> 24) * 0.00392156862745098;
                if (xloc != srcRect.x) {
                    crpc = (pixels[p - 1] >>> 24) * 0.00392156862745098;
                }
                else if (xloc < xEnd) {
                    crnc = (pixels[p + 1] >>> 24) * 0.00392156862745098;
                    final double[] n = NRow[xloc - x];
                    n[0] = 2.0 * this.surfaceScaleX * (crcc - crnc);
                    final double invNorm = 1.0 / Math.sqrt(n[0] * n[0] + 1.0);
                    final double[] array = n;
                    final int n2 = 0;
                    array[n2] *= invNorm;
                    n[1] = 0.0;
                    n[2] = invNorm;
                    n[3] = crcc * this.surfaceScale;
                    ++p;
                    ++xloc;
                    crpc = crcc;
                    crcc = crnc;
                }
                else {
                    crpc = crcc;
                }
                while (xloc < xEnd) {
                    crnc = (pixels[p + 1] >>> 24) * 0.00392156862745098;
                    final double[] n = NRow[xloc - x];
                    n[0] = this.surfaceScaleX * (crpc - crnc);
                    final double invNorm = 1.0 / Math.sqrt(n[0] * n[0] + 1.0);
                    final double[] array2 = n;
                    final int n3 = 0;
                    array2[n3] *= invNorm;
                    n[1] = 0.0;
                    n[2] = invNorm;
                    n[3] = crcc * this.surfaceScale;
                    ++p;
                    crpc = crcc;
                    crcc = crnc;
                    ++xloc;
                }
                if (xloc < x + w && xloc == srcRect.x + srcRect.width - 1) {
                    final double[] n = NRow[xloc - x];
                    n[0] = 2.0 * this.surfaceScaleX * (crpc - crcc);
                    final double invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                    final double[] array3 = n;
                    final int n4 = 0;
                    array3[n4] *= invNorm;
                    final double[] array4 = n;
                    final int n5 = 1;
                    array4[n5] *= invNorm;
                    n[2] = invNorm;
                    n[3] = crcc * this.surfaceScale;
                }
                return N;
            }
            final double[][] NRow = N[yloc - y];
            int p2 = offset + scanStride * (yloc - srcRect.y);
            int xloc2 = x;
            if (xloc2 < srcRect.x) {
                xloc2 = srcRect.x;
            }
            p2 += xloc2 - srcRect.x;
            crcc = (pixels[p2] >>> 24) * 0.00392156862745098;
            nrcc = (pixels[p2 + scanStride] >>> 24) * 0.00392156862745098;
            if (xloc2 != srcRect.x) {
                crpc = (pixels[p2 - 1] >>> 24) * 0.00392156862745098;
                nrpc = (pixels[p2 + scanStrideMM] >>> 24) * 0.00392156862745098;
            }
            else if (xloc2 < xEnd) {
                crnc = (pixels[p2 + 1] >>> 24) * 0.00392156862745098;
                nrnc = (pixels[p2 + scanStridePP] >>> 24) * 0.00392156862745098;
                final double[] n = NRow[xloc2 - x];
                n[0] = -twoThirdSurfaceScaleX * (2.0 * crnc + nrnc - 2.0 * crcc - nrcc);
                n[1] = -twoThirdSurfaceScaleY * (2.0 * nrcc + nrnc - 2.0 * crcc - crnc);
                final double invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                final double[] array5 = n;
                final int n6 = 0;
                array5[n6] *= invNorm;
                final double[] array6 = n;
                final int n7 = 1;
                array6[n7] *= invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
                ++p2;
                ++xloc2;
                crpc = crcc;
                nrpc = nrcc;
                crcc = crnc;
                nrcc = nrnc;
            }
            else {
                crpc = crcc;
                nrpc = nrcc;
            }
            while (xloc2 < xEnd) {
                crnc = (pixels[p2 + 1] >>> 24) * 0.00392156862745098;
                nrnc = (pixels[p2 + scanStridePP] >>> 24) * 0.00392156862745098;
                final double[] n = NRow[xloc2 - x];
                n[0] = -thirdSurfaceScaleX * (2.0 * crnc + nrnc - (2.0 * crpc + nrpc));
                n[1] = -halfSurfaceScaleY * (nrpc + 2.0 * nrcc + nrnc - (crpc + 2.0 * crcc + crnc));
                final double invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                final double[] array7 = n;
                final int n8 = 0;
                array7[n8] *= invNorm;
                final double[] array8 = n;
                final int n9 = 1;
                array8[n9] *= invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
                ++p2;
                crpc = crcc;
                nrpc = nrcc;
                crcc = crnc;
                nrcc = nrnc;
                ++xloc2;
            }
            if (xloc2 < x + w && xloc2 == srcRect.x + srcRect.width - 1) {
                final double[] n = NRow[xloc2 - x];
                n[0] = -twoThirdSurfaceScaleX * (2.0 * crcc + nrcc - (2.0 * crpc + nrpc));
                n[1] = -twoThirdSurfaceScaleY * (2.0 * nrcc + nrpc - (2.0 * crcc + crpc));
                final double invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                final double[] array9 = n;
                final int n10 = 0;
                array9[n10] *= invNorm;
                final double[] array10 = n;
                final int n11 = 1;
                array10[n11] *= invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
            }
            ++yloc;
        }
        while (yloc < yEnd) {
            final double[][] NRow = N[yloc - y];
            int p2 = offset + scanStride * (yloc - srcRect.y);
            int xloc2 = x;
            if (xloc2 < srcRect.x) {
                xloc2 = srcRect.x;
            }
            p2 += xloc2 - srcRect.x;
            prcc = (pixels[p2 - scanStride] >>> 24) * 0.00392156862745098;
            crcc = (pixels[p2] >>> 24) * 0.00392156862745098;
            nrcc = (pixels[p2 + scanStride] >>> 24) * 0.00392156862745098;
            if (xloc2 != srcRect.x) {
                prpc = (pixels[p2 - scanStridePP] >>> 24) * 0.00392156862745098;
                crpc = (pixels[p2 - 1] >>> 24) * 0.00392156862745098;
                nrpc = (pixels[p2 + scanStrideMM] >>> 24) * 0.00392156862745098;
            }
            else if (xloc2 < xEnd) {
                crnc = (pixels[p2 + 1] >>> 24) * 0.00392156862745098;
                prnc = (pixels[p2 - scanStrideMM] >>> 24) * 0.00392156862745098;
                nrnc = (pixels[p2 + scanStridePP] >>> 24) * 0.00392156862745098;
                final double[] n = NRow[xloc2 - x];
                n[0] = -halfSurfaceScaleX * (prnc + 2.0 * crnc + nrnc - (prcc + 2.0 * crcc + nrcc));
                n[1] = -thirdSurfaceScaleY * (2.0 * prcc + prnc - (2.0 * crcc + crnc));
                final double invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                final double[] array11 = n;
                final int n12 = 0;
                array11[n12] *= invNorm;
                final double[] array12 = n;
                final int n13 = 1;
                array12[n13] *= invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
                ++p2;
                ++xloc2;
                prpc = prcc;
                crpc = crcc;
                nrpc = nrcc;
                prcc = prnc;
                crcc = crnc;
                nrcc = nrnc;
            }
            else {
                prpc = prcc;
                crpc = crcc;
                nrpc = nrcc;
            }
            while (xloc2 < xEnd) {
                prnc = (pixels[p2 - scanStrideMM] >>> 24) * 0.00392156862745098;
                crnc = (pixels[p2 + 1] >>> 24) * 0.00392156862745098;
                nrnc = (pixels[p2 + scanStridePP] >>> 24) * 0.00392156862745098;
                final double[] n = NRow[xloc2 - x];
                n[0] = -quarterSurfaceScaleX * (prnc + 2.0 * crnc + nrnc - (prpc + 2.0 * crpc + nrpc));
                n[1] = -quarterSurfaceScaleY * (nrpc + 2.0 * nrcc + nrnc - (prpc + 2.0 * prcc + prnc));
                final double invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                final double[] array13 = n;
                final int n14 = 0;
                array13[n14] *= invNorm;
                final double[] array14 = n;
                final int n15 = 1;
                array14[n15] *= invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
                ++p2;
                prpc = prcc;
                crpc = crcc;
                nrpc = nrcc;
                prcc = prnc;
                crcc = crnc;
                nrcc = nrnc;
                ++xloc2;
            }
            if (xloc2 < x + w && xloc2 == srcRect.x + srcRect.width - 1) {
                final double[] n = NRow[xloc2 - x];
                n[0] = -halfSurfaceScaleX * (prcc + 2.0 * crcc + nrcc - (prpc + 2.0 * crpc + nrpc));
                n[1] = -thirdSurfaceScaleY * (nrpc + 2.0 * nrcc - (prpc + 2.0 * prcc));
                final double invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                final double[] array15 = n;
                final int n16 = 0;
                array15[n16] *= invNorm;
                final double[] array16 = n;
                final int n17 = 1;
                array16[n17] *= invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
            }
            ++yloc;
        }
        if (yloc < y + h && yloc == srcRect.y + srcRect.height - 1) {
            final double[][] NRow = N[yloc - y];
            int p2 = offset + scanStride * (yloc - srcRect.y);
            int xloc2 = x;
            if (xloc2 < srcRect.x) {
                xloc2 = srcRect.x;
            }
            p2 += xloc2 - srcRect.x;
            crcc = (pixels[p2] >>> 24) * 0.00392156862745098;
            prcc = (pixels[p2 - scanStride] >>> 24) * 0.00392156862745098;
            if (xloc2 != srcRect.x) {
                prpc = (pixels[p2 - scanStridePP] >>> 24) * 0.00392156862745098;
                crpc = (pixels[p2 - 1] >>> 24) * 0.00392156862745098;
            }
            else if (xloc2 < xEnd) {
                crnc = (pixels[p2 + 1] >>> 24) * 0.00392156862745098;
                prnc = (pixels[p2 - scanStrideMM] >>> 24) * 0.00392156862745098;
                final double[] n = NRow[xloc2 - x];
                n[0] = -twoThirdSurfaceScaleX * (2.0 * crnc + prnc - 2.0 * crcc - prcc);
                n[1] = -twoThirdSurfaceScaleY * (2.0 * crcc + crnc - 2.0 * prcc - prnc);
                final double invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                final double[] array17 = n;
                final int n18 = 0;
                array17[n18] *= invNorm;
                final double[] array18 = n;
                final int n19 = 1;
                array18[n19] *= invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
                ++p2;
                ++xloc2;
                crpc = crcc;
                prpc = prcc;
                crcc = crnc;
                prcc = prnc;
            }
            else {
                crpc = crcc;
                prpc = prcc;
            }
            while (xloc2 < xEnd) {
                crnc = (pixels[p2 + 1] >>> 24) * 0.00392156862745098;
                prnc = (pixels[p2 - scanStrideMM] >>> 24) * 0.00392156862745098;
                final double[] n = NRow[xloc2 - x];
                n[0] = -thirdSurfaceScaleX * (2.0 * crnc + prnc - (2.0 * crpc + prpc));
                n[1] = -halfSurfaceScaleY * (crpc + 2.0 * crcc + crnc - (prpc + 2.0 * prcc + prnc));
                final double invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                final double[] array19 = n;
                final int n20 = 0;
                array19[n20] *= invNorm;
                final double[] array20 = n;
                final int n21 = 1;
                array20[n21] *= invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
                ++p2;
                crpc = crcc;
                prpc = prcc;
                crcc = crnc;
                prcc = prnc;
                ++xloc2;
            }
            if (xloc2 < x + w && xloc2 == srcRect.x + srcRect.width - 1) {
                final double[] n = NRow[xloc2 - x];
                n[0] = -twoThirdSurfaceScaleX * (2.0 * crcc + prcc - (2.0 * crpc + prpc));
                n[1] = -twoThirdSurfaceScaleY * (2.0 * crcc + crpc - (2.0 * prcc + prpc));
                final double invNorm = 1.0 / Math.sqrt(n[0] * n[0] + n[1] * n[1] + 1.0);
                final double[] array21 = n;
                final int n22 = 0;
                array21[n22] *= invNorm;
                final double[] array22 = n;
                final int n23 = 1;
                array22[n23] *= invNorm;
                n[2] = invNorm;
                n[3] = crcc * this.surfaceScale;
            }
        }
        return N;
    }
}
