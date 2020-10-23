// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt;

import java.awt.geom.NoninvertibleTransformException;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.image.ColorModel;

final class LinearGradientPaintContext extends MultipleGradientPaintContext
{
    private float dgdX;
    private float dgdY;
    private float gc;
    private float pixSz;
    private static final int DEFAULT_IMPL = 1;
    private static final int ANTI_ALIAS_IMPL = 3;
    private int fillMethod;
    
    public LinearGradientPaintContext(final ColorModel cm, final Rectangle deviceBounds, final Rectangle2D userBounds, final AffineTransform t, final RenderingHints hints, final Point2D dStart, final Point2D dEnd, final float[] fractions, final Color[] colors, final MultipleGradientPaint.CycleMethodEnum cycleMethod, final MultipleGradientPaint.ColorSpaceEnum colorSpace) throws NoninvertibleTransformException {
        super(cm, deviceBounds, userBounds, t, hints, fractions, colors, cycleMethod, colorSpace);
        final Point2D.Float start = new Point2D.Float((float)dStart.getX(), (float)dStart.getY());
        final Point2D.Float end = new Point2D.Float((float)dEnd.getX(), (float)dEnd.getY());
        final float dx = end.x - start.x;
        final float dy = end.y - start.y;
        final float dSq = dx * dx + dy * dy;
        final float constX = dx / dSq;
        final float constY = dy / dSq;
        this.dgdX = this.a00 * constX + this.a10 * constY;
        this.dgdY = this.a01 * constX + this.a11 * constY;
        final float dgdXAbs = Math.abs(this.dgdX);
        final float dgdYAbs = Math.abs(this.dgdY);
        if (dgdXAbs > dgdYAbs) {
            this.pixSz = dgdXAbs;
        }
        else {
            this.pixSz = dgdYAbs;
        }
        this.gc = (this.a02 - start.x) * constX + (this.a12 - start.y) * constY;
        final Object colorRend = hints.get(RenderingHints.KEY_COLOR_RENDERING);
        final Object rend = hints.get(RenderingHints.KEY_RENDERING);
        this.fillMethod = 1;
        if (cycleMethod == MultipleGradientPaint.REPEAT || this.hasDiscontinuity) {
            if (rend == RenderingHints.VALUE_RENDER_QUALITY) {
                this.fillMethod = 3;
            }
            if (colorRend == RenderingHints.VALUE_COLOR_RENDER_SPEED) {
                this.fillMethod = 1;
            }
            else if (colorRend == RenderingHints.VALUE_COLOR_RENDER_QUALITY) {
                this.fillMethod = 3;
            }
        }
    }
    
    protected void fillHardNoCycle(final int[] pixels, int off, final int adjust, final int x, final int y, final int w, final int h) {
        final float initConst = this.dgdX * x + this.gc;
        for (int i = 0; i < h; ++i) {
            float g = initConst + this.dgdY * (y + i);
            final int rowLimit = off + w;
            if (this.dgdX == 0.0f) {
                int val;
                if (g <= 0.0f) {
                    val = this.gradientUnderflow;
                }
                else if (g >= 1.0f) {
                    val = this.gradientOverflow;
                }
                else {
                    int gradIdx;
                    for (gradIdx = 0; gradIdx < this.gradientsLength - 1 && g >= this.fractions[gradIdx + 1]; ++gradIdx) {}
                    final float delta = g - this.fractions[gradIdx];
                    final float idx = delta * 255.0f / this.normalizedIntervals[gradIdx] + 0.5f;
                    val = this.gradients[gradIdx][(int)idx];
                }
                while (off < rowLimit) {
                    pixels[off++] = val;
                }
            }
            else {
                float gradStepsF;
                float preGradStepsF;
                int preVal;
                int postVal;
                if (this.dgdX >= 0.0f) {
                    gradStepsF = (1.0f - g) / this.dgdX;
                    preGradStepsF = (float)Math.ceil((0.0f - g) / this.dgdX);
                    preVal = this.gradientUnderflow;
                    postVal = this.gradientOverflow;
                }
                else {
                    gradStepsF = (0.0f - g) / this.dgdX;
                    preGradStepsF = (float)Math.ceil((1.0f - g) / this.dgdX);
                    preVal = this.gradientOverflow;
                    postVal = this.gradientUnderflow;
                }
                int gradSteps;
                if (gradStepsF > w) {
                    gradSteps = w;
                }
                else {
                    gradSteps = (int)gradStepsF;
                }
                int preGradSteps;
                if (preGradStepsF > w) {
                    preGradSteps = w;
                }
                else {
                    preGradSteps = (int)preGradStepsF;
                }
                final int gradLimit = off + gradSteps;
                if (preGradSteps > 0) {
                    for (int preGradLimit = off + preGradSteps; off < preGradLimit; pixels[off++] = preVal) {}
                    g += this.dgdX * preGradSteps;
                }
                if (this.dgdX > 0.0f) {
                    int gradIdx2;
                    for (gradIdx2 = 0; gradIdx2 < this.gradientsLength - 1; ++gradIdx2) {
                        if (g < this.fractions[gradIdx2 + 1]) {
                            break;
                        }
                    }
                    while (off < gradLimit) {
                        final float delta2 = g - this.fractions[gradIdx2];
                        final int[] grad = this.gradients[gradIdx2];
                        final double stepsD = Math.ceil((this.fractions[gradIdx2 + 1] - g) / this.dgdX);
                        int steps;
                        if (stepsD > w) {
                            steps = w;
                        }
                        else {
                            steps = (int)stepsD;
                        }
                        int subGradLimit = off + steps;
                        if (subGradLimit > gradLimit) {
                            subGradLimit = gradLimit;
                        }
                        for (int idx2 = (int)(delta2 * 255.0f / this.normalizedIntervals[gradIdx2] * 65536.0f) + 32768, step = (int)(this.dgdX * 255.0f / this.normalizedIntervals[gradIdx2] * 65536.0f); off < subGradLimit; pixels[off++] = grad[idx2 >> 16], idx2 += step) {}
                        g += (float)(this.dgdX * stepsD);
                        ++gradIdx2;
                    }
                }
                else {
                    int gradIdx2;
                    for (gradIdx2 = this.gradientsLength - 1; gradIdx2 > 0; --gradIdx2) {
                        if (g > this.fractions[gradIdx2]) {
                            break;
                        }
                    }
                    while (off < gradLimit) {
                        final float delta2 = g - this.fractions[gradIdx2];
                        final int[] grad = this.gradients[gradIdx2];
                        final double stepsD = Math.ceil(delta2 / -this.dgdX);
                        int steps;
                        if (stepsD > w) {
                            steps = w;
                        }
                        else {
                            steps = (int)stepsD;
                        }
                        int subGradLimit = off + steps;
                        if (subGradLimit > gradLimit) {
                            subGradLimit = gradLimit;
                        }
                        for (int idx2 = (int)(delta2 * 255.0f / this.normalizedIntervals[gradIdx2] * 65536.0f) + 32768, step = (int)(this.dgdX * 255.0f / this.normalizedIntervals[gradIdx2] * 65536.0f); off < subGradLimit; pixels[off++] = grad[idx2 >> 16], idx2 += step) {}
                        g += (float)(this.dgdX * stepsD);
                        --gradIdx2;
                    }
                }
                while (off < rowLimit) {
                    pixels[off++] = postVal;
                }
            }
            off += adjust;
        }
    }
    
    protected void fillSimpleNoCycle(final int[] pixels, int off, final int adjust, final int x, final int y, final int w, final int h) {
        final float initConst = this.dgdX * x + this.gc;
        final float step = this.dgdX * this.fastGradientArraySize;
        final int fpStep = (int)(step * 65536.0f);
        final int[] grad = this.gradient;
        for (int i = 0; i < h; ++i) {
            float g = initConst + this.dgdY * (y + i);
            g *= this.fastGradientArraySize;
            g += 0.5;
            final int rowLimit = off + w;
            float check = this.dgdX * this.fastGradientArraySize * w;
            if (check < 0.0f) {
                check = -check;
            }
            if (check < 0.3) {
                int val;
                if (g <= 0.0f) {
                    val = this.gradientUnderflow;
                }
                else if (g >= this.fastGradientArraySize) {
                    val = this.gradientOverflow;
                }
                else {
                    val = grad[(int)g];
                }
                while (off < rowLimit) {
                    pixels[off++] = val;
                }
            }
            else {
                int gradSteps;
                int preGradSteps;
                int preVal;
                int postVal;
                if (this.dgdX > 0.0f) {
                    gradSteps = (int)((this.fastGradientArraySize - g) / step);
                    preGradSteps = (int)Math.ceil(0.0f - g / step);
                    preVal = this.gradientUnderflow;
                    postVal = this.gradientOverflow;
                }
                else {
                    gradSteps = (int)((0.0f - g) / step);
                    preGradSteps = (int)Math.ceil((this.fastGradientArraySize - g) / step);
                    preVal = this.gradientOverflow;
                    postVal = this.gradientUnderflow;
                }
                if (gradSteps > w) {
                    gradSteps = w;
                }
                final int gradLimit = off + gradSteps;
                if (preGradSteps > 0) {
                    if (preGradSteps > w) {
                        preGradSteps = w;
                    }
                    for (int preGradLimit = off + preGradSteps; off < preGradLimit; pixels[off++] = preVal) {}
                    g += step * preGradSteps;
                }
                for (int fpG = (int)(g * 65536.0f); off < gradLimit; pixels[off++] = grad[fpG >> 16], fpG += fpStep) {}
                while (off < rowLimit) {
                    pixels[off++] = postVal;
                }
            }
            off += adjust;
        }
    }
    
    protected void fillSimpleRepeat(final int[] pixels, int off, final int adjust, final int x, final int y, final int w, final int h) {
        final float initConst = this.dgdX * x + this.gc;
        float step = (this.dgdX - (int)this.dgdX) * this.fastGradientArraySize;
        if (step < 0.0f) {
            step += this.fastGradientArraySize;
        }
        final int[] grad = this.gradient;
        for (int i = 0; i < h; ++i) {
            float g = initConst + this.dgdY * (y + i);
            g -= (int)g;
            if (g < 0.0f) {
                ++g;
            }
            g *= this.fastGradientArraySize;
            g += 0.5;
            int idx;
            for (int rowLimit = off + w; off < rowLimit; pixels[off++] = grad[idx], g += step) {
                idx = (int)g;
                if (idx >= this.fastGradientArraySize) {
                    g -= this.fastGradientArraySize;
                    idx -= this.fastGradientArraySize;
                }
            }
            off += adjust;
        }
    }
    
    protected void fillSimpleReflect(final int[] pixels, int off, final int adjust, final int x, final int y, final int w, final int h) {
        final float initConst = this.dgdX * x + this.gc;
        final int[] grad = this.gradient;
        for (int i = 0; i < h; ++i) {
            float g = initConst + this.dgdY * (y + i);
            g -= 2 * (int)(g / 2.0f);
            float step = this.dgdX;
            if (g < 0.0f) {
                g = -g;
                step = -step;
            }
            step -= 2.0f * ((int)step / 2.0f);
            if (step < 0.0f) {
                step += 2.0;
            }
            final int reflectMax = 2 * this.fastGradientArraySize;
            g *= this.fastGradientArraySize;
            g += 0.5;
            step *= this.fastGradientArraySize;
            final int rowLimit = off + w;
            while (off < rowLimit) {
                int idx = (int)g;
                if (idx >= reflectMax) {
                    g -= reflectMax;
                    idx -= reflectMax;
                }
                if (idx <= this.fastGradientArraySize) {
                    pixels[off++] = grad[idx];
                }
                else {
                    pixels[off++] = grad[reflectMax - idx];
                }
                g += step;
            }
            off += adjust;
        }
    }
    
    @Override
    protected void fillRaster(final int[] pixels, int off, final int adjust, final int x, final int y, final int w, final int h) {
        final float initConst = this.dgdX * x + this.gc;
        if (this.fillMethod == 3) {
            for (int i = 0; i < h; ++i) {
                float g = initConst + this.dgdY * (y + i);
                for (int rowLimit = off + w; off < rowLimit; pixels[off++] = this.indexGradientAntiAlias(g, this.pixSz), g += this.dgdX) {}
                off += adjust;
            }
        }
        else if (!this.isSimpleLookup) {
            if (this.cycleMethod == MultipleGradientPaint.NO_CYCLE) {
                this.fillHardNoCycle(pixels, off, adjust, x, y, w, h);
            }
            else {
                for (int i = 0; i < h; ++i) {
                    float g = initConst + this.dgdY * (y + i);
                    for (int rowLimit = off + w; off < rowLimit; pixels[off++] = this.indexIntoGradientsArrays(g), g += this.dgdX) {}
                    off += adjust;
                }
            }
        }
        else if (this.cycleMethod == MultipleGradientPaint.NO_CYCLE) {
            this.fillSimpleNoCycle(pixels, off, adjust, x, y, w, h);
        }
        else if (this.cycleMethod == MultipleGradientPaint.REPEAT) {
            this.fillSimpleRepeat(pixels, off, adjust, x, y, w, h);
        }
        else {
            this.fillSimpleReflect(pixels, off, adjust, x, y, w, h);
        }
    }
}
