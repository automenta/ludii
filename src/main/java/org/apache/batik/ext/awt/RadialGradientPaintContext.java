// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt;

import java.awt.geom.NoninvertibleTransformException;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.image.ColorModel;

final class RadialGradientPaintContext extends MultipleGradientPaintContext
{
    private boolean isSimpleFocus;
    private boolean isNonCyclic;
    private float radius;
    private float centerX;
    private float centerY;
    private float focusX;
    private float focusY;
    private float radiusSq;
    private float constA;
    private float constB;
    private float trivial;
    private static final int FIXED_POINT_IMPL = 1;
    private static final int DEFAULT_IMPL = 2;
    private static final int ANTI_ALIAS_IMPL = 3;
    private int fillMethod;
    private static final float SCALEBACK = 0.999f;
    private float invSqStepFloat;
    private static final int MAX_PRECISION = 256;
    private int[] sqrtLutFixed;
    
    public RadialGradientPaintContext(final ColorModel cm, final Rectangle deviceBounds, final Rectangle2D userBounds, final AffineTransform t, final RenderingHints hints, final float cx, final float cy, final float r, final float fx, final float fy, final float[] fractions, final Color[] colors, final MultipleGradientPaint.CycleMethodEnum cycleMethod, final MultipleGradientPaint.ColorSpaceEnum colorSpace) throws NoninvertibleTransformException {
        super(cm, deviceBounds, userBounds, t, hints, fractions, colors, cycleMethod, colorSpace);
        this.isSimpleFocus = false;
        this.isNonCyclic = false;
        this.sqrtLutFixed = new int[256];
        this.centerX = cx;
        this.centerY = cy;
        this.focusX = fx;
        this.focusY = fy;
        this.radius = r;
        this.isSimpleFocus = (this.focusX == this.centerX && this.focusY == this.centerY);
        this.isNonCyclic = (cycleMethod == RadialGradientPaint.NO_CYCLE);
        this.radiusSq = this.radius * this.radius;
        float dX = this.focusX - this.centerX;
        final float dY = this.focusY - this.centerY;
        final double dist = Math.sqrt(dX * dX + dY * dY);
        if (dist > this.radius * 0.999f) {
            final double angle = Math.atan2(dY, dX);
            this.focusX = (float)(0.999f * this.radius * Math.cos(angle)) + this.centerX;
            this.focusY = (float)(0.999f * this.radius * Math.sin(angle)) + this.centerY;
        }
        dX = this.focusX - this.centerX;
        this.trivial = (float)Math.sqrt(this.radiusSq - dX * dX);
        this.constA = this.a02 - this.centerX;
        this.constB = this.a12 - this.centerY;
        final Object colorRend = hints.get(RenderingHints.KEY_COLOR_RENDERING);
        final Object rend = hints.get(RenderingHints.KEY_RENDERING);
        this.fillMethod = 0;
        if (rend == RenderingHints.VALUE_RENDER_QUALITY || colorRend == RenderingHints.VALUE_COLOR_RENDER_QUALITY) {
            this.fillMethod = 3;
        }
        if (rend == RenderingHints.VALUE_RENDER_SPEED || colorRend == RenderingHints.VALUE_COLOR_RENDER_SPEED) {
            this.fillMethod = 2;
        }
        if (this.fillMethod == 0) {
            this.fillMethod = 2;
        }
        if (this.fillMethod == 2 && this.isSimpleFocus && this.isNonCyclic && this.isSimpleLookup) {
            this.calculateFixedPointSqrtLookupTable();
            this.fillMethod = 1;
        }
    }
    
    @Override
    protected void fillRaster(final int[] pixels, final int off, final int adjust, final int x, final int y, final int w, final int h) {
        switch (this.fillMethod) {
            case 1: {
                this.fixedPointSimplestCaseNonCyclicFillRaster(pixels, off, adjust, x, y, w, h);
                break;
            }
            case 3: {
                this.antiAliasFillRaster(pixels, off, adjust, x, y, w, h);
                break;
            }
            default: {
                this.cyclicCircularGradientFillRaster(pixels, off, adjust, x, y, w, h);
                break;
            }
        }
    }
    
    private void fixedPointSimplestCaseNonCyclicFillRaster(final int[] pixels, final int off, final int adjust, final int x, final int y, final int w, final int h) {
        float iSq = 0.0f;
        final float indexFactor = this.fastGradientArraySize / this.radius;
        final float constX = this.a00 * x + this.a01 * y + this.constA;
        final float constY = this.a10 * x + this.a11 * y + this.constB;
        final float deltaX = indexFactor * this.a00;
        final float deltaY = indexFactor * this.a10;
        final int fixedArraySizeSq = this.fastGradientArraySize * this.fastGradientArraySize;
        int indexer = off;
        final float temp = deltaX * deltaX + deltaY * deltaY;
        final float gDeltaDelta = temp * 2.0f;
        if (temp > fixedArraySizeSq) {
            final int val = this.gradientOverflow;
            for (int j = 0; j < h; ++j) {
                for (int end = indexer + w; indexer < end; ++indexer) {
                    pixels[indexer] = val;
                }
                indexer += adjust;
            }
            return;
        }
        for (int j = 0; j < h; ++j) {
            final float dX = indexFactor * (this.a01 * j + constX);
            final float dY = indexFactor * (this.a11 * j + constY);
            float g = dY * dY + dX * dX;
            float gDelta = (deltaY * dY + deltaX * dX) * 2.0f + temp;
            for (int end = indexer + w; indexer < end; ++indexer) {
                if (g >= fixedArraySizeSq) {
                    pixels[indexer] = this.gradientOverflow;
                }
                else {
                    iSq = g * this.invSqStepFloat;
                    final int iSqInt = (int)iSq;
                    iSq -= iSqInt;
                    int gIndex = this.sqrtLutFixed[iSqInt];
                    gIndex += (int)(iSq * (this.sqrtLutFixed[iSqInt + 1] - gIndex));
                    pixels[indexer] = this.gradient[gIndex];
                }
                g += gDelta;
                gDelta += gDeltaDelta;
            }
            indexer += adjust;
        }
    }
    
    private void calculateFixedPointSqrtLookupTable() {
        final float sqStepFloat = this.fastGradientArraySize * this.fastGradientArraySize / 254.0f;
        final int[] workTbl = this.sqrtLutFixed;
        int i;
        for (i = 0; i < 255; ++i) {
            workTbl[i] = (int)Math.sqrt(i * sqStepFloat);
        }
        workTbl[i] = workTbl[i - 1];
        this.invSqStepFloat = 1.0f / sqStepFloat;
    }
    
    private void cyclicCircularGradientFillRaster(final int[] pixels, final int off, final int adjust, final int x, final int y, final int w, final int h) {
        final double constC = -this.radiusSq + this.centerX * this.centerX + this.centerY * this.centerY;
        final float constX = this.a00 * x + this.a01 * y + this.a02;
        final float constY = this.a10 * x + this.a11 * y + this.a12;
        final float precalc2 = 2.0f * this.centerY;
        final float precalc3 = -2.0f * this.centerX;
        int indexer = off;
        final int pixInc = w + adjust;
        for (int j = 0; j < h; ++j) {
            float X = this.a01 * j + constX;
            float Y = this.a11 * j + constY;
            for (int i = 0; i < w; ++i) {
                double solutionX;
                double solutionY;
                if (X - this.focusX > -1.0E-6f && X - this.focusX < 1.0E-6f) {
                    solutionX = this.focusX;
                    solutionY = this.centerY;
                    solutionY += ((Y > this.focusY) ? this.trivial : ((double)(-this.trivial)));
                }
                else {
                    final double slope = (Y - this.focusY) / (X - this.focusX);
                    final double yintcpt = Y - slope * X;
                    final double A = slope * slope + 1.0;
                    final double B = precalc3 + -2.0 * slope * (this.centerY - yintcpt);
                    final double C = constC + yintcpt * (yintcpt - precalc2);
                    final float det = (float)Math.sqrt(B * B - 4.0 * A * C);
                    solutionX = -B;
                    solutionX += ((X < this.focusX) ? (-det) : ((double)det));
                    solutionX /= 2.0 * A;
                    solutionY = slope * solutionX + yintcpt;
                }
                float deltaXSq = (float)solutionX - this.focusX;
                deltaXSq *= deltaXSq;
                float deltaYSq = (float)solutionY - this.focusY;
                deltaYSq *= deltaYSq;
                final float intersectToFocusSq = deltaXSq + deltaYSq;
                deltaXSq = X - this.focusX;
                deltaXSq *= deltaXSq;
                deltaYSq = Y - this.focusY;
                deltaYSq *= deltaYSq;
                final float currentToFocusSq = deltaXSq + deltaYSq;
                final float g = (float)Math.sqrt(currentToFocusSq / intersectToFocusSq);
                pixels[indexer + i] = this.indexIntoGradientsArrays(g);
                X += this.a00;
                Y += this.a10;
            }
            indexer += pixInc;
        }
    }
    
    private void antiAliasFillRaster(final int[] pixels, final int off, final int adjust, final int x, final int y, final int w, final int h) {
        final double constC = -this.radiusSq + this.centerX * this.centerX + this.centerY * this.centerY;
        final float precalc2 = 2.0f * this.centerY;
        final float precalc3 = -2.0f * this.centerX;
        final float constX = this.a00 * (x - 0.5f) + this.a01 * (y + 0.5f) + this.a02;
        final float constY = this.a10 * (x - 0.5f) + this.a11 * (y + 0.5f) + this.a12;
        int indexer = off - 1;
        final double[] prevGs = new double[w + 1];
        float X = constX - this.a01;
        float Y = constY - this.a11;
        for (int i = 0; i <= w; ++i) {
            final float dx = X - this.focusX;
            double solutionX;
            double solutionY;
            if (dx > -1.0E-6f && dx < 1.0E-6f) {
                solutionX = this.focusX;
                solutionY = this.centerY;
                solutionY += ((Y > this.focusY) ? this.trivial : ((double)(-this.trivial)));
            }
            else {
                final double slope = (Y - this.focusY) / (X - this.focusX);
                final double yintcpt = Y - slope * X;
                final double A = slope * slope + 1.0;
                final double B = precalc3 + -2.0 * slope * (this.centerY - yintcpt);
                final double C = constC + yintcpt * (yintcpt - precalc2);
                final double det = Math.sqrt(B * B - 4.0 * A * C);
                solutionX = -B;
                solutionX += ((X < this.focusX) ? (-det) : det);
                solutionX /= 2.0 * A;
                solutionY = slope * solutionX + yintcpt;
            }
            double deltaXSq = solutionX - this.focusX;
            deltaXSq *= deltaXSq;
            double deltaYSq = solutionY - this.focusY;
            deltaYSq *= deltaYSq;
            final double intersectToFocusSq = deltaXSq + deltaYSq;
            deltaXSq = X - this.focusX;
            deltaXSq *= deltaXSq;
            deltaYSq = Y - this.focusY;
            deltaYSq *= deltaYSq;
            final double currentToFocusSq = deltaXSq + deltaYSq;
            prevGs[i] = Math.sqrt(currentToFocusSq / intersectToFocusSq);
            X += this.a00;
            Y += this.a10;
        }
        for (int j = 0; j < h; ++j) {
            X = this.a01 * j + constX;
            Y = this.a11 * j + constY;
            double g10 = prevGs[0];
            float dx = X - this.focusX;
            double solutionX;
            double solutionY;
            if (dx > -1.0E-6f && dx < 1.0E-6f) {
                solutionX = this.focusX;
                solutionY = this.centerY;
                solutionY += ((Y > this.focusY) ? this.trivial : ((double)(-this.trivial)));
            }
            else {
                final double slope = (Y - this.focusY) / (X - this.focusX);
                final double yintcpt = Y - slope * X;
                final double A = slope * slope + 1.0;
                final double B = precalc3 + -2.0 * slope * (this.centerY - yintcpt);
                final double C = constC + yintcpt * (yintcpt - precalc2);
                final double det = Math.sqrt(B * B - 4.0 * A * C);
                solutionX = -B;
                solutionX += ((X < this.focusX) ? (-det) : det);
                solutionX /= 2.0 * A;
                solutionY = slope * solutionX + yintcpt;
            }
            double deltaXSq = solutionX - this.focusX;
            deltaXSq *= deltaXSq;
            double deltaYSq = solutionY - this.focusY;
            deltaYSq *= deltaYSq;
            double intersectToFocusSq = deltaXSq + deltaYSq;
            deltaXSq = X - this.focusX;
            deltaXSq *= deltaXSq;
            deltaYSq = Y - this.focusY;
            deltaYSq *= deltaYSq;
            double currentToFocusSq = deltaXSq + deltaYSq;
            double g11 = Math.sqrt(currentToFocusSq / intersectToFocusSq);
            prevGs[0] = g11;
            X += this.a00;
            Y += this.a10;
            for (int i = 1; i <= w; ++i) {
                final double g12 = g10;
                final double g13 = g11;
                g10 = prevGs[i];
                dx = X - this.focusX;
                if (dx > -1.0E-6f && dx < 1.0E-6f) {
                    solutionX = this.focusX;
                    solutionY = this.centerY;
                    solutionY += ((Y > this.focusY) ? this.trivial : ((double)(-this.trivial)));
                }
                else {
                    final double slope = (Y - this.focusY) / (X - this.focusX);
                    final double yintcpt = Y - slope * X;
                    final double A = slope * slope + 1.0;
                    final double B = precalc3 + -2.0 * slope * (this.centerY - yintcpt);
                    final double C = constC + yintcpt * (yintcpt - precalc2);
                    final double det = Math.sqrt(B * B - 4.0 * A * C);
                    solutionX = -B;
                    solutionX += ((X < this.focusX) ? (-det) : det);
                    solutionX /= 2.0 * A;
                    solutionY = slope * solutionX + yintcpt;
                }
                deltaXSq = solutionX - this.focusX;
                deltaXSq *= deltaXSq;
                deltaYSq = solutionY - this.focusY;
                deltaYSq *= deltaYSq;
                intersectToFocusSq = deltaXSq + deltaYSq;
                deltaXSq = X - this.focusX;
                deltaXSq *= deltaXSq;
                deltaYSq = Y - this.focusY;
                deltaYSq *= deltaYSq;
                currentToFocusSq = deltaXSq + deltaYSq;
                g11 = Math.sqrt(currentToFocusSq / intersectToFocusSq);
                prevGs[i] = g11;
                pixels[indexer + i] = this.indexGradientAntiAlias((float)((g12 + g13 + g10 + g11) / 4.0), (float)Math.max(Math.abs(g11 - g12), Math.abs(g10 - g13)));
                X += this.a00;
                Y += this.a10;
            }
            indexer += w + adjust;
        }
    }
}
