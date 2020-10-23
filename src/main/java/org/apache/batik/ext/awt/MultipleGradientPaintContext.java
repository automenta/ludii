// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt;

import java.awt.image.DirectColorModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.color.ColorSpace;
import java.awt.geom.NoninvertibleTransformException;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.image.WritableRaster;
import java.lang.ref.WeakReference;
import java.awt.image.ColorModel;
import java.awt.PaintContext;

abstract class MultipleGradientPaintContext implements PaintContext
{
    protected static final boolean DEBUG = false;
    protected ColorModel dataModel;
    protected ColorModel model;
    private static ColorModel lrgbmodel_NA;
    private static ColorModel srgbmodel_NA;
    private static ColorModel lrgbmodel_A;
    private static ColorModel srgbmodel_A;
    protected static ColorModel cachedModel;
    protected static WeakReference cached;
    protected WritableRaster saved;
    protected MultipleGradientPaint.CycleMethodEnum cycleMethod;
    protected MultipleGradientPaint.ColorSpaceEnum colorSpace;
    protected float a00;
    protected float a01;
    protected float a10;
    protected float a11;
    protected float a02;
    protected float a12;
    protected boolean isSimpleLookup;
    protected boolean hasDiscontinuity;
    protected int fastGradientArraySize;
    protected int[] gradient;
    protected int[][] gradients;
    protected int gradientAverage;
    protected int gradientUnderflow;
    protected int gradientOverflow;
    protected int gradientsLength;
    protected float[] normalizedIntervals;
    protected float[] fractions;
    private int transparencyTest;
    private static final int[] SRGBtoLinearRGB;
    private static final int[] LinearRGBtoSRGB;
    protected static final int GRADIENT_SIZE = 256;
    protected static final int GRADIENT_SIZE_INDEX = 255;
    private static final int MAX_GRADIENT_ARRAY_SIZE = 5000;
    
    protected MultipleGradientPaintContext(final ColorModel cm, final Rectangle deviceBounds, final Rectangle2D userBounds, final AffineTransform t, final RenderingHints hints, final float[] fractions, final Color[] colors, final MultipleGradientPaint.CycleMethodEnum cycleMethod, final MultipleGradientPaint.ColorSpaceEnum colorSpace) throws NoninvertibleTransformException {
        this.isSimpleLookup = true;
        this.hasDiscontinuity = false;
        boolean fixFirst = false;
        boolean fixLast = false;
        int len = fractions.length;
        if (fractions[0] != 0.0f) {
            fixFirst = true;
            ++len;
        }
        if (fractions[fractions.length - 1] != 1.0f) {
            fixLast = true;
            ++len;
        }
        for (int i = 0; i < fractions.length - 1; ++i) {
            if (fractions[i] == fractions[i + 1]) {
                --len;
            }
        }
        this.fractions = new float[len];
        final Color[] loColors = new Color[len - 1];
        final Color[] hiColors = new Color[len - 1];
        this.normalizedIntervals = new float[len - 1];
        this.gradientUnderflow = colors[0].getRGB();
        this.gradientOverflow = colors[colors.length - 1].getRGB();
        int idx = 0;
        if (fixFirst) {
            this.fractions[0] = 0.0f;
            loColors[0] = colors[0];
            hiColors[0] = colors[0];
            this.normalizedIntervals[0] = fractions[0];
            ++idx;
        }
        for (int j = 0; j < fractions.length - 1; ++j) {
            if (fractions[j] == fractions[j + 1]) {
                if (!colors[j].equals(colors[j + 1])) {
                    this.hasDiscontinuity = true;
                }
            }
            else {
                this.fractions[idx] = fractions[j];
                loColors[idx] = colors[j];
                hiColors[idx] = colors[j + 1];
                this.normalizedIntervals[idx] = fractions[j + 1] - fractions[j];
                ++idx;
            }
        }
        this.fractions[idx] = fractions[fractions.length - 1];
        if (fixLast) {
            loColors[idx] = (hiColors[idx] = colors[colors.length - 1]);
            this.normalizedIntervals[idx] = 1.0f - fractions[fractions.length - 1];
            ++idx;
            this.fractions[idx] = 1.0f;
        }
        final AffineTransform tInv = t.createInverse();
        final double[] m = new double[6];
        tInv.getMatrix(m);
        this.a00 = (float)m[0];
        this.a10 = (float)m[1];
        this.a01 = (float)m[2];
        this.a11 = (float)m[3];
        this.a02 = (float)m[4];
        this.a12 = (float)m[5];
        this.cycleMethod = cycleMethod;
        this.colorSpace = colorSpace;
        if (cm.getColorSpace() == MultipleGradientPaintContext.lrgbmodel_A.getColorSpace()) {
            this.dataModel = MultipleGradientPaintContext.lrgbmodel_A;
        }
        else {
            if (cm.getColorSpace() != MultipleGradientPaintContext.srgbmodel_A.getColorSpace()) {
                throw new IllegalArgumentException("Unsupported ColorSpace for interpolation");
            }
            this.dataModel = MultipleGradientPaintContext.srgbmodel_A;
        }
        this.calculateGradientFractions(loColors, hiColors);
        this.model = GraphicsUtil.coerceColorModel(this.dataModel, cm.isAlphaPremultiplied());
    }
    
    protected final void calculateGradientFractions(final Color[] loColors, final Color[] hiColors) {
        if (this.colorSpace == LinearGradientPaint.LINEAR_RGB) {
            final int[] workTbl = MultipleGradientPaintContext.SRGBtoLinearRGB;
            for (int i = 0; i < loColors.length; ++i) {
                loColors[i] = interpolateColor(workTbl, loColors[i]);
                hiColors[i] = interpolateColor(workTbl, hiColors[i]);
            }
        }
        this.transparencyTest = -16777216;
        if (this.cycleMethod == MultipleGradientPaint.NO_CYCLE) {
            this.transparencyTest &= this.gradientUnderflow;
            this.transparencyTest &= this.gradientOverflow;
        }
        this.gradients = new int[this.fractions.length - 1][];
        this.gradientsLength = this.gradients.length;
        final int n = this.normalizedIntervals.length;
        float Imin = 1.0f;
        final float[] workTbl2 = this.normalizedIntervals;
        for (int j = 0; j < n; ++j) {
            Imin = ((Imin > workTbl2[j]) ? workTbl2[j] : Imin);
        }
        int estimatedSize = 0;
        if (Imin == 0.0f) {
            estimatedSize = Integer.MAX_VALUE;
            this.hasDiscontinuity = true;
        }
        else {
            for (final float aWorkTbl : workTbl2) {
                estimatedSize += (int)(aWorkTbl / Imin * 256.0f);
            }
        }
        if (estimatedSize > 5000) {
            this.calculateMultipleArrayGradient(loColors, hiColors);
            if (this.cycleMethod == MultipleGradientPaint.REPEAT && this.gradients[0][0] != this.gradients[this.gradients.length - 1][255]) {
                this.hasDiscontinuity = true;
            }
        }
        else {
            this.calculateSingleArrayGradient(loColors, hiColors, Imin);
            if (this.cycleMethod == MultipleGradientPaint.REPEAT && this.gradient[0] != this.gradient[this.fastGradientArraySize]) {
                this.hasDiscontinuity = true;
            }
        }
        if (this.transparencyTest >>> 24 == 255) {
            if (this.dataModel.getColorSpace() == MultipleGradientPaintContext.lrgbmodel_NA.getColorSpace()) {
                this.dataModel = MultipleGradientPaintContext.lrgbmodel_NA;
            }
            else if (this.dataModel.getColorSpace() == MultipleGradientPaintContext.srgbmodel_NA.getColorSpace()) {
                this.dataModel = MultipleGradientPaintContext.srgbmodel_NA;
            }
            this.model = this.dataModel;
        }
    }
    
    private static Color interpolateColor(final int[] workTbl, final Color inColor) {
        final int oldColor = inColor.getRGB();
        final int newColorValue = (workTbl[oldColor >> 24 & 0xFF] & 0xFF) << 24 | (workTbl[oldColor >> 16 & 0xFF] & 0xFF) << 16 | (workTbl[oldColor >> 8 & 0xFF] & 0xFF) << 8 | (workTbl[oldColor & 0xFF] & 0xFF);
        return new Color(newColorValue, true);
    }
    
    private void calculateSingleArrayGradient(final Color[] loColors, final Color[] hiColors, final float Imin) {
        this.isSimpleLookup = true;
        int gradientsTot = 1;
        int aveA = 32768;
        int aveR = 32768;
        int aveG = 32768;
        int aveB = 32768;
        for (int i = 0; i < this.gradients.length; ++i) {
            final int nGradients = (int)(this.normalizedIntervals[i] / Imin * 255.0f);
            gradientsTot += nGradients;
            this.gradients[i] = new int[nGradients];
            final int rgb1 = loColors[i].getRGB();
            final int rgb2 = hiColors[i].getRGB();
            this.interpolate(rgb1, rgb2, this.gradients[i]);
            final int argb = this.gradients[i][128];
            final float norm = this.normalizedIntervals[i];
            aveA += (int)((argb >> 8 & 0xFF0000) * norm);
            aveR += (int)((argb & 0xFF0000) * norm);
            aveG += (int)((argb << 8 & 0xFF0000) * norm);
            aveB += (int)((argb << 16 & 0xFF0000) * norm);
            this.transparencyTest &= (rgb1 & rgb2);
        }
        this.gradientAverage = ((aveA & 0xFF0000) << 8 | (aveR & 0xFF0000) | (aveG & 0xFF0000) >> 8 | (aveB & 0xFF0000) >> 16);
        this.gradient = new int[gradientsTot];
        int curOffset = 0;
        for (final int[] gradient1 : this.gradients) {
            System.arraycopy(gradient1, 0, this.gradient, curOffset, gradient1.length);
            curOffset += gradient1.length;
        }
        this.gradient[this.gradient.length - 1] = hiColors[hiColors.length - 1].getRGB();
        if (this.colorSpace == LinearGradientPaint.LINEAR_RGB) {
            if (this.dataModel.getColorSpace() == ColorSpace.getInstance(1000)) {
                for (int j = 0; j < this.gradient.length; ++j) {
                    this.gradient[j] = convertEntireColorLinearRGBtoSRGB(this.gradient[j]);
                }
                this.gradientAverage = convertEntireColorLinearRGBtoSRGB(this.gradientAverage);
            }
        }
        else if (this.dataModel.getColorSpace() == ColorSpace.getInstance(1004)) {
            for (int j = 0; j < this.gradient.length; ++j) {
                this.gradient[j] = convertEntireColorSRGBtoLinearRGB(this.gradient[j]);
            }
            this.gradientAverage = convertEntireColorSRGBtoLinearRGB(this.gradientAverage);
        }
        this.fastGradientArraySize = this.gradient.length - 1;
    }
    
    private void calculateMultipleArrayGradient(final Color[] loColors, final Color[] hiColors) {
        this.isSimpleLookup = false;
        int aveA = 32768;
        int aveR = 32768;
        int aveG = 32768;
        int aveB = 32768;
        for (int i = 0; i < this.gradients.length; ++i) {
            if (this.normalizedIntervals[i] != 0.0f) {
                this.gradients[i] = new int[256];
                final int rgb1 = loColors[i].getRGB();
                final int rgb2 = hiColors[i].getRGB();
                this.interpolate(rgb1, rgb2, this.gradients[i]);
                final int argb = this.gradients[i][128];
                final float norm = this.normalizedIntervals[i];
                aveA += (int)((argb >> 8 & 0xFF0000) * norm);
                aveR += (int)((argb & 0xFF0000) * norm);
                aveG += (int)((argb << 8 & 0xFF0000) * norm);
                aveB += (int)((argb << 16 & 0xFF0000) * norm);
                this.transparencyTest &= rgb1;
                this.transparencyTest &= rgb2;
            }
        }
        this.gradientAverage = ((aveA & 0xFF0000) << 8 | (aveR & 0xFF0000) | (aveG & 0xFF0000) >> 8 | (aveB & 0xFF0000) >> 16);
        if (this.colorSpace == LinearGradientPaint.LINEAR_RGB) {
            if (this.dataModel.getColorSpace() == ColorSpace.getInstance(1000)) {
                for (int j = 0; j < this.gradients.length; ++j) {
                    for (int k = 0; k < this.gradients[j].length; ++k) {
                        this.gradients[j][k] = convertEntireColorLinearRGBtoSRGB(this.gradients[j][k]);
                    }
                }
                this.gradientAverage = convertEntireColorLinearRGBtoSRGB(this.gradientAverage);
            }
        }
        else if (this.dataModel.getColorSpace() == ColorSpace.getInstance(1004)) {
            for (int j = 0; j < this.gradients.length; ++j) {
                for (int k = 0; k < this.gradients[j].length; ++k) {
                    this.gradients[j][k] = convertEntireColorSRGBtoLinearRGB(this.gradients[j][k]);
                }
            }
            this.gradientAverage = convertEntireColorSRGBtoLinearRGB(this.gradientAverage);
        }
    }
    
    private void interpolate(final int rgb1, final int rgb2, final int[] output) {
        int nSteps = output.length;
        final float stepSize = 1.0f / nSteps;
        final int a1 = rgb1 >> 24 & 0xFF;
        final int r1 = rgb1 >> 16 & 0xFF;
        final int g1 = rgb1 >> 8 & 0xFF;
        final int b1 = rgb1 & 0xFF;
        final int da = (rgb2 >> 24 & 0xFF) - a1;
        final int dr = (rgb2 >> 16 & 0xFF) - r1;
        final int dg = (rgb2 >> 8 & 0xFF) - g1;
        final int db = (rgb2 & 0xFF) - b1;
        final float tempA = 2.0f * da * stepSize;
        final float tempR = 2.0f * dr * stepSize;
        final float tempG = 2.0f * dg * stepSize;
        final float tempB = 2.0f * db * stepSize;
        output[0] = rgb1;
        --nSteps;
        output[nSteps] = rgb2;
        for (int i = 1; i < nSteps; ++i) {
            final float fI = (float)i;
            output[i] = ((a1 + ((int)(fI * tempA) + 1 >> 1) & 0xFF) << 24 | (r1 + ((int)(fI * tempR) + 1 >> 1) & 0xFF) << 16 | (g1 + ((int)(fI * tempG) + 1 >> 1) & 0xFF) << 8 | (b1 + ((int)(fI * tempB) + 1 >> 1) & 0xFF));
        }
    }
    
    private static int convertEntireColorLinearRGBtoSRGB(final int rgb) {
        final int a1 = rgb >> 24 & 0xFF;
        int r1 = rgb >> 16 & 0xFF;
        int g1 = rgb >> 8 & 0xFF;
        int b1 = rgb & 0xFF;
        final int[] workTbl = MultipleGradientPaintContext.LinearRGBtoSRGB;
        r1 = workTbl[r1];
        g1 = workTbl[g1];
        b1 = workTbl[b1];
        return a1 << 24 | r1 << 16 | g1 << 8 | b1;
    }
    
    private static int convertEntireColorSRGBtoLinearRGB(final int rgb) {
        final int a1 = rgb >> 24 & 0xFF;
        int r1 = rgb >> 16 & 0xFF;
        int g1 = rgb >> 8 & 0xFF;
        int b1 = rgb & 0xFF;
        final int[] workTbl = MultipleGradientPaintContext.SRGBtoLinearRGB;
        r1 = workTbl[r1];
        g1 = workTbl[g1];
        b1 = workTbl[b1];
        return a1 << 24 | r1 << 16 | g1 << 8 | b1;
    }
    
    protected final int indexIntoGradientsArrays(float position) {
        if (this.cycleMethod == MultipleGradientPaint.NO_CYCLE) {
            if (position >= 1.0f) {
                return this.gradientOverflow;
            }
            if (position <= 0.0f) {
                return this.gradientUnderflow;
            }
        }
        else {
            if (this.cycleMethod == MultipleGradientPaint.REPEAT) {
                position -= (int)position;
                if (position < 0.0f) {
                    ++position;
                }
                int w = 0;
                int c1 = 0;
                int c2 = 0;
                if (this.isSimpleLookup) {
                    position *= this.gradient.length;
                    final int idx1 = (int)position;
                    if (idx1 + 1 < this.gradient.length) {
                        return this.gradient[idx1];
                    }
                    w = (int)((position - idx1) * 65536.0f);
                    c1 = this.gradient[idx1];
                    c2 = this.gradient[0];
                }
                else {
                    int i = 0;
                    while (i < this.gradientsLength) {
                        if (position < this.fractions[i + 1]) {
                            float delta = position - this.fractions[i];
                            delta = delta / this.normalizedIntervals[i] * 256.0f;
                            final int index = (int)delta;
                            if (index + 1 < this.gradients[i].length || i + 1 < this.gradientsLength) {
                                return this.gradients[i][index];
                            }
                            w = (int)((delta - index) * 65536.0f);
                            c1 = this.gradients[i][index];
                            c2 = this.gradients[0][0];
                            break;
                        }
                        else {
                            ++i;
                        }
                    }
                }
                return ((c1 >> 8 & 0xFF0000) + ((c2 >>> 24) - (c1 >>> 24)) * w & 0xFF0000) << 8 | ((c1 & 0xFF0000) + ((c2 >> 16 & 0xFF) - (c1 >> 16 & 0xFF)) * w & 0xFF0000) | ((c1 << 8 & 0xFF0000) + ((c2 >> 8 & 0xFF) - (c1 >> 8 & 0xFF)) * w & 0xFF0000) >> 8 | ((c1 << 16 & 0xFF0000) + ((c2 & 0xFF) - (c1 & 0xFF)) * w & 0xFF0000) >> 16;
            }
            if (position < 0.0f) {
                position = -position;
            }
            final int part = (int)position;
            position -= part;
            if ((part & 0x1) == 0x1) {
                position = 1.0f - position;
            }
        }
        if (this.isSimpleLookup) {
            return this.gradient[(int)(position * this.fastGradientArraySize)];
        }
        for (int j = 0; j < this.gradientsLength; ++j) {
            if (position < this.fractions[j + 1]) {
                final float delta2 = position - this.fractions[j];
                final int index2 = (int)(delta2 / this.normalizedIntervals[j] * 255.0f);
                return this.gradients[j][index2];
            }
        }
        return this.gradientOverflow;
    }
    
    protected final int indexGradientAntiAlias(final float position, float sz) {
        if (this.cycleMethod == MultipleGradientPaint.NO_CYCLE) {
            final float p1 = position - sz / 2.0f;
            final float p2 = position + sz / 2.0f;
            if (p1 >= 1.0f) {
                return this.gradientOverflow;
            }
            if (p2 <= 0.0f) {
                return this.gradientUnderflow;
            }
            float top_weight = 0.0f;
            float bottom_weight = 0.0f;
            float frac;
            int interior;
            if (p2 >= 1.0f) {
                top_weight = (p2 - 1.0f) / sz;
                if (p1 <= 0.0f) {
                    bottom_weight = -p1 / sz;
                    frac = 1.0f;
                    interior = this.gradientAverage;
                }
                else {
                    frac = 1.0f - p1;
                    interior = this.getAntiAlias(p1, true, 1.0f, false, 1.0f - p1, 1.0f);
                }
            }
            else {
                if (p1 > 0.0f) {
                    return this.getAntiAlias(p1, true, p2, false, sz, 1.0f);
                }
                bottom_weight = -p1 / sz;
                frac = p2;
                interior = this.getAntiAlias(0.0f, true, p2, false, p2, 1.0f);
            }
            int norm = (int)(65536.0f * frac / sz);
            int pA = (interior >>> 20 & 0xFF0) * norm >> 16;
            int pR = (interior >> 12 & 0xFF0) * norm >> 16;
            int pG = (interior >> 4 & 0xFF0) * norm >> 16;
            int pB = (interior << 4 & 0xFF0) * norm >> 16;
            if (bottom_weight != 0.0f) {
                final int bPix = this.gradientUnderflow;
                norm = (int)(65536.0f * bottom_weight);
                pA += (bPix >>> 20 & 0xFF0) * norm >> 16;
                pR += (bPix >> 12 & 0xFF0) * norm >> 16;
                pG += (bPix >> 4 & 0xFF0) * norm >> 16;
                pB += (bPix << 4 & 0xFF0) * norm >> 16;
            }
            if (top_weight != 0.0f) {
                final int tPix = this.gradientOverflow;
                norm = (int)(65536.0f * top_weight);
                pA += (tPix >>> 20 & 0xFF0) * norm >> 16;
                pR += (tPix >> 12 & 0xFF0) * norm >> 16;
                pG += (tPix >> 4 & 0xFF0) * norm >> 16;
                pB += (tPix << 4 & 0xFF0) * norm >> 16;
            }
            return (pA & 0xFF0) << 20 | (pR & 0xFF0) << 12 | (pG & 0xFF0) << 4 | (pB & 0xFF0) >> 4;
        }
        else {
            final int intSz = (int)sz;
            float weight = 1.0f;
            if (intSz != 0) {
                sz -= intSz;
                weight = sz / (intSz + sz);
                if (weight < 0.1) {
                    return this.gradientAverage;
                }
            }
            if (sz > 0.99) {
                return this.gradientAverage;
            }
            float p3 = position - sz / 2.0f;
            float p4 = position + sz / 2.0f;
            boolean p1_up = true;
            boolean p2_up = false;
            if (this.cycleMethod == MultipleGradientPaint.REPEAT) {
                p3 -= (int)p3;
                p4 -= (int)p4;
                if (p3 < 0.0f) {
                    ++p3;
                }
                if (p4 < 0.0f) {
                    ++p4;
                }
            }
            else {
                if (p4 < 0.0f) {
                    p3 = -p3;
                    p1_up = !p1_up;
                    p4 = -p4;
                    p2_up = !p2_up;
                }
                else if (p3 < 0.0f) {
                    p3 = -p3;
                    p1_up = !p1_up;
                }
                final int part1 = (int)p3;
                p3 -= part1;
                final int part2 = (int)p4;
                p4 -= part2;
                if ((part1 & 0x1) == 0x1) {
                    p3 = 1.0f - p3;
                    p1_up = !p1_up;
                }
                if ((part2 & 0x1) == 0x1) {
                    p4 = 1.0f - p4;
                    p2_up = !p2_up;
                }
                if (p3 > p4 && !p1_up && p2_up) {
                    final float t = p3;
                    p3 = p4;
                    p4 = t;
                    p1_up = true;
                    p2_up = false;
                }
            }
            return this.getAntiAlias(p3, p1_up, p4, p2_up, sz, weight);
        }
    }
    
    private final int getAntiAlias(float p1, final boolean p1_up, float p2, final boolean p2_up, final float sz, final float weight) {
        int ach = 0;
        int rch = 0;
        int gch = 0;
        int bch = 0;
        if (this.isSimpleLookup) {
            p1 *= this.fastGradientArraySize;
            p2 *= this.fastGradientArraySize;
            final int idx1 = (int)p1;
            final int idx2 = (int)p2;
            if (p1_up && !p2_up && idx1 <= idx2) {
                if (idx1 == idx2) {
                    return this.gradient[idx1];
                }
                for (int i = idx1 + 1; i < idx2; ++i) {
                    final int pix = this.gradient[i];
                    ach += (pix >>> 20 & 0xFF0);
                    rch += (pix >>> 12 & 0xFF0);
                    gch += (pix >>> 4 & 0xFF0);
                    bch += (pix << 4 & 0xFF0);
                }
            }
            else {
                int iStart;
                int iEnd;
                if (p1_up) {
                    iStart = idx1 + 1;
                    iEnd = this.fastGradientArraySize;
                }
                else {
                    iStart = 0;
                    iEnd = idx1;
                }
                for (int i = iStart; i < iEnd; ++i) {
                    final int pix = this.gradient[i];
                    ach += (pix >>> 20 & 0xFF0);
                    rch += (pix >>> 12 & 0xFF0);
                    gch += (pix >>> 4 & 0xFF0);
                    bch += (pix << 4 & 0xFF0);
                }
                if (p2_up) {
                    iStart = idx2 + 1;
                    iEnd = this.fastGradientArraySize;
                }
                else {
                    iStart = 0;
                    iEnd = idx2;
                }
                for (int i = iStart; i < iEnd; ++i) {
                    final int pix = this.gradient[i];
                    ach += (pix >>> 20 & 0xFF0);
                    rch += (pix >>> 12 & 0xFF0);
                    gch += (pix >>> 4 & 0xFF0);
                    bch += (pix << 4 & 0xFF0);
                }
            }
            final int isz = (int)(65536.0f / (sz * this.fastGradientArraySize));
            ach = ach * isz >> 16;
            rch = rch * isz >> 16;
            gch = gch * isz >> 16;
            bch = bch * isz >> 16;
            int norm;
            if (p1_up) {
                norm = (int)((1.0f - (p1 - idx1)) * isz);
            }
            else {
                norm = (int)((p1 - idx1) * isz);
            }
            int pix = this.gradient[idx1];
            ach += (pix >>> 20 & 0xFF0) * norm >> 16;
            rch += (pix >>> 12 & 0xFF0) * norm >> 16;
            gch += (pix >>> 4 & 0xFF0) * norm >> 16;
            bch += (pix << 4 & 0xFF0) * norm >> 16;
            if (p2_up) {
                norm = (int)((1.0f - (p2 - idx2)) * isz);
            }
            else {
                norm = (int)((p2 - idx2) * isz);
            }
            pix = this.gradient[idx2];
            ach += (pix >>> 20 & 0xFF0) * norm >> 16;
            rch += (pix >>> 12 & 0xFF0) * norm >> 16;
            gch += (pix >>> 4 & 0xFF0) * norm >> 16;
            bch += (pix << 4 & 0xFF0) * norm >> 16;
            ach = ach + 8 >> 4;
            rch = rch + 8 >> 4;
            gch = gch + 8 >> 4;
            bch = bch + 8 >> 4;
        }
        else {
            int idx1 = 0;
            int idx2 = 0;
            int i2 = -1;
            int i3 = -1;
            float f1 = 0.0f;
            float f2 = 0.0f;
            for (int j = 0; j < this.gradientsLength; ++j) {
                if (p1 < this.fractions[j + 1] && i2 == -1) {
                    i2 = j;
                    f1 = p1 - this.fractions[j];
                    f1 = f1 / this.normalizedIntervals[j] * 255.0f;
                    idx1 = (int)f1;
                    if (i3 != -1) {
                        break;
                    }
                }
                if (p2 < this.fractions[j + 1] && i3 == -1) {
                    i3 = j;
                    f2 = p2 - this.fractions[j];
                    f2 = f2 / this.normalizedIntervals[j] * 255.0f;
                    idx2 = (int)f2;
                    if (i2 != -1) {
                        break;
                    }
                }
            }
            if (i2 == -1) {
                i2 = this.gradients.length - 1;
                f1 = (float)(idx1 = 255);
            }
            if (i3 == -1) {
                i3 = this.gradients.length - 1;
                f2 = (float)(idx2 = 255);
            }
            if (i2 == i3 && idx1 <= idx2 && p1_up && !p2_up) {
                return this.gradients[i2][idx1 + idx2 + 1 >> 1];
            }
            final int base = (int)(65536.0f / sz);
            if (i2 < i3 && p1_up && !p2_up) {
                int norm2 = (int)(base * this.normalizedIntervals[i2] * (255.0f - f1) / 255.0f);
                int pix2 = this.gradients[i2][idx1 + 256 >> 1];
                ach += (pix2 >>> 20 & 0xFF0) * norm2 >> 16;
                rch += (pix2 >>> 12 & 0xFF0) * norm2 >> 16;
                gch += (pix2 >>> 4 & 0xFF0) * norm2 >> 16;
                bch += (pix2 << 4 & 0xFF0) * norm2 >> 16;
                for (int k = i2 + 1; k < i3; ++k) {
                    norm2 = (int)(base * this.normalizedIntervals[k]);
                    pix2 = this.gradients[k][128];
                    ach += (pix2 >>> 20 & 0xFF0) * norm2 >> 16;
                    rch += (pix2 >>> 12 & 0xFF0) * norm2 >> 16;
                    gch += (pix2 >>> 4 & 0xFF0) * norm2 >> 16;
                    bch += (pix2 << 4 & 0xFF0) * norm2 >> 16;
                }
                norm2 = (int)(base * this.normalizedIntervals[i3] * f2 / 255.0f);
                pix2 = this.gradients[i3][idx2 + 1 >> 1];
                ach += (pix2 >>> 20 & 0xFF0) * norm2 >> 16;
                rch += (pix2 >>> 12 & 0xFF0) * norm2 >> 16;
                gch += (pix2 >>> 4 & 0xFF0) * norm2 >> 16;
                bch += (pix2 << 4 & 0xFF0) * norm2 >> 16;
            }
            else {
                int norm2;
                int pix2;
                if (p1_up) {
                    norm2 = (int)(base * this.normalizedIntervals[i2] * (255.0f - f1) / 255.0f);
                    pix2 = this.gradients[i2][idx1 + 256 >> 1];
                }
                else {
                    norm2 = (int)(base * this.normalizedIntervals[i2] * f1 / 255.0f);
                    pix2 = this.gradients[i2][idx1 + 1 >> 1];
                }
                ach += (pix2 >>> 20 & 0xFF0) * norm2 >> 16;
                rch += (pix2 >>> 12 & 0xFF0) * norm2 >> 16;
                gch += (pix2 >>> 4 & 0xFF0) * norm2 >> 16;
                bch += (pix2 << 4 & 0xFF0) * norm2 >> 16;
                if (p2_up) {
                    norm2 = (int)(base * this.normalizedIntervals[i3] * (255.0f - f2) / 255.0f);
                    pix2 = this.gradients[i3][idx2 + 256 >> 1];
                }
                else {
                    norm2 = (int)(base * this.normalizedIntervals[i3] * f2 / 255.0f);
                    pix2 = this.gradients[i3][idx2 + 1 >> 1];
                }
                ach += (pix2 >>> 20 & 0xFF0) * norm2 >> 16;
                rch += (pix2 >>> 12 & 0xFF0) * norm2 >> 16;
                gch += (pix2 >>> 4 & 0xFF0) * norm2 >> 16;
                bch += (pix2 << 4 & 0xFF0) * norm2 >> 16;
                int iStart2;
                int iEnd2;
                if (p1_up) {
                    iStart2 = i2 + 1;
                    iEnd2 = this.gradientsLength;
                }
                else {
                    iStart2 = 0;
                    iEnd2 = i2;
                }
                for (int l = iStart2; l < iEnd2; ++l) {
                    norm2 = (int)(base * this.normalizedIntervals[l]);
                    pix2 = this.gradients[l][128];
                    ach += (pix2 >>> 20 & 0xFF0) * norm2 >> 16;
                    rch += (pix2 >>> 12 & 0xFF0) * norm2 >> 16;
                    gch += (pix2 >>> 4 & 0xFF0) * norm2 >> 16;
                    bch += (pix2 << 4 & 0xFF0) * norm2 >> 16;
                }
                if (p2_up) {
                    iStart2 = i3 + 1;
                    iEnd2 = this.gradientsLength;
                }
                else {
                    iStart2 = 0;
                    iEnd2 = i3;
                }
                for (int l = iStart2; l < iEnd2; ++l) {
                    norm2 = (int)(base * this.normalizedIntervals[l]);
                    pix2 = this.gradients[l][128];
                    ach += (pix2 >>> 20 & 0xFF0) * norm2 >> 16;
                    rch += (pix2 >>> 12 & 0xFF0) * norm2 >> 16;
                    gch += (pix2 >>> 4 & 0xFF0) * norm2 >> 16;
                    bch += (pix2 << 4 & 0xFF0) * norm2 >> 16;
                }
            }
            ach = ach + 8 >> 4;
            rch = rch + 8 >> 4;
            gch = gch + 8 >> 4;
            bch = bch + 8 >> 4;
        }
        if (weight != 1.0f) {
            final int aveW = (int)(65536.0f * (1.0f - weight));
            final int aveA = (this.gradientAverage >>> 24 & 0xFF) * aveW;
            final int aveR = (this.gradientAverage >> 16 & 0xFF) * aveW;
            final int aveG = (this.gradientAverage >> 8 & 0xFF) * aveW;
            final int aveB = (this.gradientAverage & 0xFF) * aveW;
            final int iw = (int)(weight * 65536.0f);
            ach = ach * iw + aveA >> 16;
            rch = rch * iw + aveR >> 16;
            gch = gch * iw + aveG >> 16;
            bch = bch * iw + aveB >> 16;
        }
        return ach << 24 | rch << 16 | gch << 8 | bch;
    }
    
    private static int convertSRGBtoLinearRGB(final int color) {
        final float input = color / 255.0f;
        float output;
        if (input <= 0.04045f) {
            output = input / 12.92f;
        }
        else {
            output = (float)Math.pow((input + 0.055) / 1.055, 2.4);
        }
        final int o = Math.round(output * 255.0f);
        return o;
    }
    
    private static int convertLinearRGBtoSRGB(final int color) {
        final float input = color / 255.0f;
        float output;
        if (input <= 0.0031308f) {
            output = input * 12.92f;
        }
        else {
            output = 1.055f * (float)Math.pow(input, 0.4166666666666667) - 0.055f;
        }
        final int o = Math.round(output * 255.0f);
        return o;
    }
    
    @Override
    public final Raster getRaster(final int x, final int y, final int w, final int h) {
        if (w == 0 || h == 0) {
            return null;
        }
        WritableRaster raster = this.saved;
        if (raster == null || raster.getWidth() < w || raster.getHeight() < h) {
            raster = getCachedRaster(this.dataModel, w, h);
            this.saved = raster;
            raster = raster.createWritableChild(raster.getMinX(), raster.getMinY(), w, h, 0, 0, null);
        }
        final DataBufferInt rasterDB = (DataBufferInt)raster.getDataBuffer();
        final int[] pixels = rasterDB.getBankData()[0];
        final int off = rasterDB.getOffset();
        final int scanlineStride = ((SinglePixelPackedSampleModel)raster.getSampleModel()).getScanlineStride();
        final int adjust = scanlineStride - w;
        this.fillRaster(pixels, off, adjust, x, y, w, h);
        GraphicsUtil.coerceData(raster, this.dataModel, this.model.isAlphaPremultiplied());
        return raster;
    }
    
    protected abstract void fillRaster(final int[] p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6);
    
    protected static final synchronized WritableRaster getCachedRaster(final ColorModel cm, int w, int h) {
        if (cm == MultipleGradientPaintContext.cachedModel && MultipleGradientPaintContext.cached != null) {
            final WritableRaster ras = (WritableRaster)MultipleGradientPaintContext.cached.get();
            if (ras != null && ras.getWidth() >= w && ras.getHeight() >= h) {
                MultipleGradientPaintContext.cached = null;
                return ras;
            }
        }
        if (w < 32) {
            w = 32;
        }
        if (h < 32) {
            h = 32;
        }
        return cm.createCompatibleWritableRaster(w, h);
    }
    
    protected static final synchronized void putCachedRaster(final ColorModel cm, final WritableRaster ras) {
        if (MultipleGradientPaintContext.cached != null) {
            final WritableRaster cras = (WritableRaster)MultipleGradientPaintContext.cached.get();
            if (cras != null) {
                final int cw = cras.getWidth();
                final int ch = cras.getHeight();
                final int iw = ras.getWidth();
                final int ih = ras.getHeight();
                if (cw >= iw && ch >= ih) {
                    return;
                }
                if (cw * ch >= iw * ih) {
                    return;
                }
            }
        }
        MultipleGradientPaintContext.cachedModel = cm;
        MultipleGradientPaintContext.cached = new WeakReference((T)ras);
    }
    
    @Override
    public final void dispose() {
        if (this.saved != null) {
            putCachedRaster(this.model, this.saved);
            this.saved = null;
        }
    }
    
    @Override
    public final ColorModel getColorModel() {
        return this.model;
    }
    
    static {
        MultipleGradientPaintContext.lrgbmodel_NA = new DirectColorModel(ColorSpace.getInstance(1004), 24, 16711680, 65280, 255, 0, false, 3);
        MultipleGradientPaintContext.srgbmodel_NA = new DirectColorModel(ColorSpace.getInstance(1000), 24, 16711680, 65280, 255, 0, false, 3);
        MultipleGradientPaintContext.lrgbmodel_A = new DirectColorModel(ColorSpace.getInstance(1004), 32, 16711680, 65280, 255, -16777216, false, 3);
        MultipleGradientPaintContext.srgbmodel_A = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, false, 3);
        SRGBtoLinearRGB = new int[256];
        LinearRGBtoSRGB = new int[256];
        for (int k = 0; k < 256; ++k) {
            MultipleGradientPaintContext.SRGBtoLinearRGB[k] = convertSRGBtoLinearRGB(k);
            MultipleGradientPaintContext.LinearRGBtoSRGB[k] = convertLinearRGBtoSRGB(k);
        }
    }
}
