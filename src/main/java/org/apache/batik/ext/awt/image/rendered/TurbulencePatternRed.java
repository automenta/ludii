// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.ColorModel;
import java.util.Map;
import java.awt.image.DirectColorModel;
import java.awt.color.ColorSpace;
import java.awt.Rectangle;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

public final class TurbulencePatternRed extends AbstractRed
{
    private StitchInfo stitchInfo;
    private static final AffineTransform IDENTITY;
    private double baseFrequencyX;
    private double baseFrequencyY;
    private int numOctaves;
    private int seed;
    private Rectangle2D tile;
    private AffineTransform txf;
    private boolean isFractalNoise;
    private int[] channels;
    double[] tx;
    double[] ty;
    private static final int RAND_m = Integer.MAX_VALUE;
    private static final int RAND_a = 16807;
    private static final int RAND_q = 127773;
    private static final int RAND_r = 2836;
    private static final int BSize = 256;
    private static final int BM = 255;
    private static final double PerlinN = 4096.0;
    private final int[] latticeSelector;
    private final double[] gradient;
    
    public double getBaseFrequencyX() {
        return this.baseFrequencyX;
    }
    
    public double getBaseFrequencyY() {
        return this.baseFrequencyY;
    }
    
    public int getNumOctaves() {
        return this.numOctaves;
    }
    
    public int getSeed() {
        return this.seed;
    }
    
    public Rectangle2D getTile() {
        return (Rectangle2D)this.tile.clone();
    }
    
    public boolean isFractalNoise() {
        return this.isFractalNoise;
    }
    
    public boolean[] getChannels() {
        final boolean[] channels = new boolean[4];
        for (final int channel : this.channels) {
            channels[channel] = true;
        }
        return channels;
    }
    
    public final int setupSeed(int seed) {
        if (seed <= 0) {
            seed = -(seed % 2147483646) + 1;
        }
        if (seed > 2147483646) {
            seed = 2147483646;
        }
        return seed;
    }
    
    public final int random(final int seed) {
        int result = 16807 * (seed % 127773) - 2836 * (seed / 127773);
        if (result <= 0) {
            result += Integer.MAX_VALUE;
        }
        return result;
    }
    
    private void initLattice(int seed) {
        seed = this.setupSeed(seed);
        for (int k = 0; k < 4; ++k) {
            for (int i = 0; i < 256; ++i) {
                final double u = (seed = this.random(seed)) % 512 - 256;
                final double v = (seed = this.random(seed)) % 512 - 256;
                final double s = 1.0 / Math.sqrt(u * u + v * v);
                this.gradient[i * 8 + k * 2] = u * s;
                this.gradient[i * 8 + k * 2 + 1] = v * s;
            }
        }
        int i;
        for (i = 0; i < 256; ++i) {
            this.latticeSelector[i] = i;
        }
        while (--i > 0) {
            final int k = this.latticeSelector[i];
            int j = (seed = this.random(seed)) % 256;
            this.latticeSelector[i] = this.latticeSelector[j];
            this.latticeSelector[j] = k;
            final int s2 = i << 3;
            final int s3 = j << 3;
            for (j = 0; j < 8; ++j) {
                final double s = this.gradient[s2 + j];
                this.gradient[s2 + j] = this.gradient[s3 + j];
                this.gradient[s3 + j] = s;
            }
        }
        this.latticeSelector[256] = this.latticeSelector[0];
        for (int j = 0; j < 8; ++j) {
            this.gradient[2048 + j] = this.gradient[j];
        }
    }
    
    private static final double s_curve(final double t) {
        return t * t * (3.0 - 2.0 * t);
    }
    
    private static final double lerp(final double t, final double a, final double b) {
        return a + t * (b - a);
    }
    
    private final void noise2(final double[] noise, double vec0, double vec1) {
        vec0 += 4096.0;
        int b0 = (int)vec0 & 0xFF;
        final int i = this.latticeSelector[b0];
        final int j = this.latticeSelector[b0 + 1];
        final double rx0 = vec0 - (int)vec0;
        final double rx2 = rx0 - 1.0;
        final double sx = s_curve(rx0);
        vec1 += 4096.0;
        b0 = (int)vec1;
        final int b2 = (j + b0 & 0xFF) << 3;
        b0 = (i + b0 & 0xFF) << 3;
        final double ry0 = vec1 - (int)vec1;
        final double ry2 = ry0 - 1.0;
        final double sy = s_curve(ry0);
        switch (this.channels.length) {
            case 4: {
                noise[3] = lerp(sy, lerp(sx, rx0 * this.gradient[b0 + 6] + ry0 * this.gradient[b0 + 7], rx2 * this.gradient[b2 + 6] + ry0 * this.gradient[b2 + 7]), lerp(sx, rx0 * this.gradient[b0 + 8 + 6] + ry2 * this.gradient[b0 + 8 + 7], rx2 * this.gradient[b2 + 8 + 6] + ry2 * this.gradient[b2 + 8 + 7]));
            }
            case 3: {
                noise[2] = lerp(sy, lerp(sx, rx0 * this.gradient[b0 + 4] + ry0 * this.gradient[b0 + 5], rx2 * this.gradient[b2 + 4] + ry0 * this.gradient[b2 + 5]), lerp(sx, rx0 * this.gradient[b0 + 8 + 4] + ry2 * this.gradient[b0 + 8 + 5], rx2 * this.gradient[b2 + 8 + 4] + ry2 * this.gradient[b2 + 8 + 5]));
            }
            case 2: {
                noise[1] = lerp(sy, lerp(sx, rx0 * this.gradient[b0 + 2] + ry0 * this.gradient[b0 + 3], rx2 * this.gradient[b2 + 2] + ry0 * this.gradient[b2 + 3]), lerp(sx, rx0 * this.gradient[b0 + 8 + 2] + ry2 * this.gradient[b0 + 8 + 3], rx2 * this.gradient[b2 + 8 + 2] + ry2 * this.gradient[b2 + 8 + 3]));
            }
            case 1: {
                noise[0] = lerp(sy, lerp(sx, rx0 * this.gradient[b0 + 0] + ry0 * this.gradient[b0 + 1], rx2 * this.gradient[b2 + 0] + ry0 * this.gradient[b2 + 1]), lerp(sx, rx0 * this.gradient[b0 + 8 + 0] + ry2 * this.gradient[b0 + 8 + 1], rx2 * this.gradient[b2 + 8 + 0] + ry2 * this.gradient[b2 + 8 + 1]));
                break;
            }
        }
    }
    
    private final void noise2Stitch(final double[] noise, final double vec0, final double vec1, final StitchInfo stitchInfo) {
        double t = vec0 + 4096.0;
        int b0 = (int)t;
        int b2 = b0 + 1;
        if (b2 >= stitchInfo.wrapX) {
            if (b0 >= stitchInfo.wrapX) {
                b0 -= stitchInfo.width;
                b2 -= stitchInfo.width;
            }
            else {
                b2 -= stitchInfo.width;
            }
        }
        final int i = this.latticeSelector[b0 & 0xFF];
        final int j = this.latticeSelector[b2 & 0xFF];
        final double rx0 = t - (int)t;
        final double rx2 = rx0 - 1.0;
        final double sx = s_curve(rx0);
        t = vec1 + 4096.0;
        b0 = (int)t;
        b2 = b0 + 1;
        if (b2 >= stitchInfo.wrapY) {
            if (b0 >= stitchInfo.wrapY) {
                b0 -= stitchInfo.height;
                b2 -= stitchInfo.height;
            }
            else {
                b2 -= stitchInfo.height;
            }
        }
        final int b3 = (i + b0 & 0xFF) << 3;
        final int b4 = (j + b0 & 0xFF) << 3;
        final int b5 = (i + b2 & 0xFF) << 3;
        final int b6 = (j + b2 & 0xFF) << 3;
        final double ry0 = t - (int)t;
        final double ry2 = ry0 - 1.0;
        final double sy = s_curve(ry0);
        switch (this.channels.length) {
            case 4: {
                noise[3] = lerp(sy, lerp(sx, rx0 * this.gradient[b3 + 6] + ry0 * this.gradient[b3 + 7], rx2 * this.gradient[b4 + 6] + ry0 * this.gradient[b4 + 7]), lerp(sx, rx0 * this.gradient[b5 + 6] + ry2 * this.gradient[b5 + 7], rx2 * this.gradient[b6 + 6] + ry2 * this.gradient[b6 + 7]));
            }
            case 3: {
                noise[2] = lerp(sy, lerp(sx, rx0 * this.gradient[b3 + 4] + ry0 * this.gradient[b3 + 5], rx2 * this.gradient[b4 + 4] + ry0 * this.gradient[b4 + 5]), lerp(sx, rx0 * this.gradient[b5 + 4] + ry2 * this.gradient[b5 + 5], rx2 * this.gradient[b6 + 4] + ry2 * this.gradient[b6 + 5]));
            }
            case 2: {
                noise[1] = lerp(sy, lerp(sx, rx0 * this.gradient[b3 + 2] + ry0 * this.gradient[b3 + 3], rx2 * this.gradient[b4 + 2] + ry0 * this.gradient[b4 + 3]), lerp(sx, rx0 * this.gradient[b5 + 2] + ry2 * this.gradient[b5 + 3], rx2 * this.gradient[b6 + 2] + ry2 * this.gradient[b6 + 3]));
            }
            case 1: {
                noise[0] = lerp(sy, lerp(sx, rx0 * this.gradient[b3 + 0] + ry0 * this.gradient[b3 + 1], rx2 * this.gradient[b4 + 0] + ry0 * this.gradient[b4 + 1]), lerp(sx, rx0 * this.gradient[b5 + 0] + ry2 * this.gradient[b5 + 1], rx2 * this.gradient[b6 + 0] + ry2 * this.gradient[b6 + 1]));
                break;
            }
        }
    }
    
    private final int turbulence_4(double pointX, double pointY, final double[] fSum) {
        double ratio = 255.0;
        pointX *= this.baseFrequencyX;
        pointY *= this.baseFrequencyY;
        final int n2 = 0;
        final int n3 = 1;
        final int n4 = 2;
        final int n5 = 3;
        final double n6 = 0.0;
        fSum[n4] = (fSum[n5] = n6);
        fSum[n2] = (fSum[n3] = n6);
        for (int nOctave = this.numOctaves; nOctave > 0; --nOctave) {
            final double px = pointX + 4096.0;
            int b0 = (int)px & 0xFF;
            final int i = this.latticeSelector[b0];
            final int j = this.latticeSelector[b0 + 1];
            final double rx0 = px - (int)px;
            final double rx2 = rx0 - 1.0;
            final double sx = s_curve(rx0);
            final double py = pointY + 4096.0;
            b0 = ((int)py & 0xFF);
            int b2 = b0 + 1 & 0xFF;
            b2 = (j + b0 & 0xFF) << 3;
            b0 = (i + b0 & 0xFF) << 3;
            final double ry0 = py - (int)py;
            final double ry2 = ry0 - 1.0;
            final double sy = s_curve(ry0);
            double n = lerp(sy, lerp(sx, rx0 * this.gradient[b0 + 0] + ry0 * this.gradient[b0 + 1], rx2 * this.gradient[b2 + 0] + ry0 * this.gradient[b2 + 1]), lerp(sx, rx0 * this.gradient[b0 + 8 + 0] + ry2 * this.gradient[b0 + 8 + 1], rx2 * this.gradient[b2 + 8 + 0] + ry2 * this.gradient[b2 + 8 + 1]));
            if (n < 0.0) {
                final int n7 = 0;
                fSum[n7] -= n * ratio;
            }
            else {
                final int n8 = 0;
                fSum[n8] += n * ratio;
            }
            n = lerp(sy, lerp(sx, rx0 * this.gradient[b0 + 2] + ry0 * this.gradient[b0 + 3], rx2 * this.gradient[b2 + 2] + ry0 * this.gradient[b2 + 3]), lerp(sx, rx0 * this.gradient[b0 + 8 + 2] + ry2 * this.gradient[b0 + 8 + 3], rx2 * this.gradient[b2 + 8 + 2] + ry2 * this.gradient[b2 + 8 + 3]));
            if (n < 0.0) {
                final int n9 = 1;
                fSum[n9] -= n * ratio;
            }
            else {
                final int n10 = 1;
                fSum[n10] += n * ratio;
            }
            n = lerp(sy, lerp(sx, rx0 * this.gradient[b0 + 4] + ry0 * this.gradient[b0 + 5], rx2 * this.gradient[b2 + 4] + ry0 * this.gradient[b2 + 5]), lerp(sx, rx0 * this.gradient[b0 + 8 + 4] + ry2 * this.gradient[b0 + 8 + 5], rx2 * this.gradient[b2 + 8 + 4] + ry2 * this.gradient[b2 + 8 + 5]));
            if (n < 0.0) {
                final int n11 = 2;
                fSum[n11] -= n * ratio;
            }
            else {
                final int n12 = 2;
                fSum[n12] += n * ratio;
            }
            n = lerp(sy, lerp(sx, rx0 * this.gradient[b0 + 6] + ry0 * this.gradient[b0 + 7], rx2 * this.gradient[b2 + 6] + ry0 * this.gradient[b2 + 7]), lerp(sx, rx0 * this.gradient[b0 + 8 + 6] + ry2 * this.gradient[b0 + 8 + 7], rx2 * this.gradient[b2 + 8 + 6] + ry2 * this.gradient[b2 + 8 + 7]));
            if (n < 0.0) {
                final int n13 = 3;
                fSum[n13] -= n * ratio;
            }
            else {
                final int n14 = 3;
                fSum[n14] += n * ratio;
            }
            ratio *= 0.5;
            pointX *= 2.0;
            pointY *= 2.0;
        }
        int i = (int)fSum[0];
        int j;
        if ((i & 0xFFFFFF00) == 0x0) {
            j = i << 16;
        }
        else {
            j = (((i & Integer.MIN_VALUE) != 0x0) ? 0 : 16711680);
        }
        i = (int)fSum[1];
        if ((i & 0xFFFFFF00) == 0x0) {
            j |= i << 8;
        }
        else {
            j |= (((i & Integer.MIN_VALUE) != 0x0) ? 0 : 65280);
        }
        i = (int)fSum[2];
        if ((i & 0xFFFFFF00) == 0x0) {
            j |= i;
        }
        else {
            j |= (((i & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
        }
        i = (int)fSum[3];
        if ((i & 0xFFFFFF00) == 0x0) {
            j |= i << 24;
        }
        else {
            j |= (((i & Integer.MIN_VALUE) != 0x0) ? 0 : -16777216);
        }
        return j;
    }
    
    private final void turbulence(final int[] rgb, double pointX, double pointY, final double[] fSum, final double[] noise) {
        final int n = 0;
        final int n2 = 1;
        final int n3 = 2;
        final int n4 = 3;
        final double n5 = 0.0;
        fSum[n3] = (fSum[n4] = n5);
        fSum[n] = (fSum[n2] = n5);
        double ratio = 255.0;
        pointX *= this.baseFrequencyX;
        pointY *= this.baseFrequencyY;
        switch (this.channels.length) {
            case 4: {
                for (int nOctave = 0; nOctave < this.numOctaves; ++nOctave) {
                    this.noise2(noise, pointX, pointY);
                    if (noise[0] < 0.0) {
                        final int n6 = 0;
                        fSum[n6] -= noise[0] * ratio;
                    }
                    else {
                        final int n7 = 0;
                        fSum[n7] += noise[0] * ratio;
                    }
                    if (noise[1] < 0.0) {
                        final int n8 = 1;
                        fSum[n8] -= noise[1] * ratio;
                    }
                    else {
                        final int n9 = 1;
                        fSum[n9] += noise[1] * ratio;
                    }
                    if (noise[2] < 0.0) {
                        final int n10 = 2;
                        fSum[n10] -= noise[2] * ratio;
                    }
                    else {
                        final int n11 = 2;
                        fSum[n11] += noise[2] * ratio;
                    }
                    if (noise[3] < 0.0) {
                        final int n12 = 3;
                        fSum[n12] -= noise[3] * ratio;
                    }
                    else {
                        final int n13 = 3;
                        fSum[n13] += noise[3] * ratio;
                    }
                    ratio *= 0.5;
                    pointX *= 2.0;
                    pointY *= 2.0;
                }
                rgb[0] = (int)fSum[0];
                if ((rgb[0] & 0xFFFFFF00) != 0x0) {
                    rgb[0] = (((rgb[0] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
                rgb[1] = (int)fSum[1];
                if ((rgb[1] & 0xFFFFFF00) != 0x0) {
                    rgb[1] = (((rgb[1] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
                rgb[2] = (int)fSum[2];
                if ((rgb[2] & 0xFFFFFF00) != 0x0) {
                    rgb[2] = (((rgb[2] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
                rgb[3] = (int)fSum[3];
                if ((rgb[3] & 0xFFFFFF00) != 0x0) {
                    rgb[3] = (((rgb[3] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    break;
                }
                break;
            }
            case 3: {
                for (int nOctave = 0; nOctave < this.numOctaves; ++nOctave) {
                    this.noise2(noise, pointX, pointY);
                    if (noise[2] < 0.0) {
                        final int n14 = 2;
                        fSum[n14] -= noise[2] * ratio;
                    }
                    else {
                        final int n15 = 2;
                        fSum[n15] += noise[2] * ratio;
                    }
                    if (noise[1] < 0.0) {
                        final int n16 = 1;
                        fSum[n16] -= noise[1] * ratio;
                    }
                    else {
                        final int n17 = 1;
                        fSum[n17] += noise[1] * ratio;
                    }
                    if (noise[0] < 0.0) {
                        final int n18 = 0;
                        fSum[n18] -= noise[0] * ratio;
                    }
                    else {
                        final int n19 = 0;
                        fSum[n19] += noise[0] * ratio;
                    }
                    ratio *= 0.5;
                    pointX *= 2.0;
                    pointY *= 2.0;
                }
                rgb[2] = (int)fSum[2];
                if ((rgb[2] & 0xFFFFFF00) != 0x0) {
                    rgb[2] = (((rgb[2] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
                rgb[1] = (int)fSum[1];
                if ((rgb[1] & 0xFFFFFF00) != 0x0) {
                    rgb[1] = (((rgb[1] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
                rgb[0] = (int)fSum[0];
                if ((rgb[0] & 0xFFFFFF00) != 0x0) {
                    rgb[0] = (((rgb[0] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    break;
                }
                break;
            }
            case 2: {
                for (int nOctave = 0; nOctave < this.numOctaves; ++nOctave) {
                    this.noise2(noise, pointX, pointY);
                    if (noise[1] < 0.0) {
                        final int n20 = 1;
                        fSum[n20] -= noise[1] * ratio;
                    }
                    else {
                        final int n21 = 1;
                        fSum[n21] += noise[1] * ratio;
                    }
                    if (noise[0] < 0.0) {
                        final int n22 = 0;
                        fSum[n22] -= noise[0] * ratio;
                    }
                    else {
                        final int n23 = 0;
                        fSum[n23] += noise[0] * ratio;
                    }
                    ratio *= 0.5;
                    pointX *= 2.0;
                    pointY *= 2.0;
                }
                rgb[1] = (int)fSum[1];
                if ((rgb[1] & 0xFFFFFF00) != 0x0) {
                    rgb[1] = (((rgb[1] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
                rgb[0] = (int)fSum[0];
                if ((rgb[0] & 0xFFFFFF00) != 0x0) {
                    rgb[0] = (((rgb[0] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    break;
                }
                break;
            }
            case 1: {
                for (int nOctave = 0; nOctave < this.numOctaves; ++nOctave) {
                    this.noise2(noise, pointX, pointY);
                    if (noise[0] < 0.0) {
                        final int n24 = 0;
                        fSum[n24] -= noise[0] * ratio;
                    }
                    else {
                        final int n25 = 0;
                        fSum[n25] += noise[0] * ratio;
                    }
                    ratio *= 0.5;
                    pointX *= 2.0;
                    pointY *= 2.0;
                }
                rgb[0] = (int)fSum[0];
                if ((rgb[0] & 0xFFFFFF00) != 0x0) {
                    rgb[0] = (((rgb[0] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    break;
                }
                break;
            }
        }
    }
    
    private final void turbulenceStitch(final int[] rgb, double pointX, double pointY, final double[] fSum, final double[] noise, final StitchInfo stitchInfo) {
        double ratio = 1.0;
        pointX *= this.baseFrequencyX;
        pointY *= this.baseFrequencyY;
        final int n = 0;
        final int n2 = 1;
        final int n3 = 2;
        final int n4 = 3;
        final double n5 = 0.0;
        fSum[n3] = (fSum[n4] = n5);
        fSum[n] = (fSum[n2] = n5);
        switch (this.channels.length) {
            case 4: {
                for (int nOctave = 0; nOctave < this.numOctaves; ++nOctave) {
                    this.noise2Stitch(noise, pointX, pointY, stitchInfo);
                    if (noise[3] < 0.0) {
                        final int n6 = 3;
                        fSum[n6] -= noise[3] * ratio;
                    }
                    else {
                        final int n7 = 3;
                        fSum[n7] += noise[3] * ratio;
                    }
                    if (noise[2] < 0.0) {
                        final int n8 = 2;
                        fSum[n8] -= noise[2] * ratio;
                    }
                    else {
                        final int n9 = 2;
                        fSum[n9] += noise[2] * ratio;
                    }
                    if (noise[1] < 0.0) {
                        final int n10 = 1;
                        fSum[n10] -= noise[1] * ratio;
                    }
                    else {
                        final int n11 = 1;
                        fSum[n11] += noise[1] * ratio;
                    }
                    if (noise[0] < 0.0) {
                        final int n12 = 0;
                        fSum[n12] -= noise[0] * ratio;
                    }
                    else {
                        final int n13 = 0;
                        fSum[n13] += noise[0] * ratio;
                    }
                    ratio *= 0.5;
                    pointX *= 2.0;
                    pointY *= 2.0;
                    stitchInfo.doubleFrequency();
                }
                rgb[3] = (int)(fSum[3] * 255.0);
                if ((rgb[3] & 0xFFFFFF00) != 0x0) {
                    rgb[3] = (((rgb[3] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
                rgb[2] = (int)(fSum[2] * 255.0);
                if ((rgb[2] & 0xFFFFFF00) != 0x0) {
                    rgb[2] = (((rgb[2] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
                rgb[1] = (int)(fSum[1] * 255.0);
                if ((rgb[1] & 0xFFFFFF00) != 0x0) {
                    rgb[1] = (((rgb[1] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
                rgb[0] = (int)(fSum[0] * 255.0);
                if ((rgb[0] & 0xFFFFFF00) != 0x0) {
                    rgb[0] = (((rgb[0] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    break;
                }
                break;
            }
            case 3: {
                for (int nOctave = 0; nOctave < this.numOctaves; ++nOctave) {
                    this.noise2Stitch(noise, pointX, pointY, stitchInfo);
                    if (noise[2] < 0.0) {
                        final int n14 = 2;
                        fSum[n14] -= noise[2] * ratio;
                    }
                    else {
                        final int n15 = 2;
                        fSum[n15] += noise[2] * ratio;
                    }
                    if (noise[1] < 0.0) {
                        final int n16 = 1;
                        fSum[n16] -= noise[1] * ratio;
                    }
                    else {
                        final int n17 = 1;
                        fSum[n17] += noise[1] * ratio;
                    }
                    if (noise[0] < 0.0) {
                        final int n18 = 0;
                        fSum[n18] -= noise[0] * ratio;
                    }
                    else {
                        final int n19 = 0;
                        fSum[n19] += noise[0] * ratio;
                    }
                    ratio *= 0.5;
                    pointX *= 2.0;
                    pointY *= 2.0;
                    stitchInfo.doubleFrequency();
                }
                rgb[2] = (int)(fSum[2] * 255.0);
                if ((rgb[2] & 0xFFFFFF00) != 0x0) {
                    rgb[2] = (((rgb[2] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
                rgb[1] = (int)(fSum[1] * 255.0);
                if ((rgb[1] & 0xFFFFFF00) != 0x0) {
                    rgb[1] = (((rgb[1] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
                rgb[0] = (int)(fSum[0] * 255.0);
                if ((rgb[0] & 0xFFFFFF00) != 0x0) {
                    rgb[0] = (((rgb[0] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    break;
                }
                break;
            }
            case 2: {
                for (int nOctave = 0; nOctave < this.numOctaves; ++nOctave) {
                    this.noise2Stitch(noise, pointX, pointY, stitchInfo);
                    if (noise[1] < 0.0) {
                        final int n20 = 1;
                        fSum[n20] -= noise[1] * ratio;
                    }
                    else {
                        final int n21 = 1;
                        fSum[n21] += noise[1] * ratio;
                    }
                    if (noise[0] < 0.0) {
                        final int n22 = 0;
                        fSum[n22] -= noise[0] * ratio;
                    }
                    else {
                        final int n23 = 0;
                        fSum[n23] += noise[0] * ratio;
                    }
                    ratio *= 0.5;
                    pointX *= 2.0;
                    pointY *= 2.0;
                    stitchInfo.doubleFrequency();
                }
                rgb[1] = (int)(fSum[1] * 255.0);
                if ((rgb[1] & 0xFFFFFF00) != 0x0) {
                    rgb[1] = (((rgb[1] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
                rgb[0] = (int)(fSum[0] * 255.0);
                if ((rgb[0] & 0xFFFFFF00) != 0x0) {
                    rgb[0] = (((rgb[0] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    break;
                }
                break;
            }
            case 1: {
                for (int nOctave = 0; nOctave < this.numOctaves; ++nOctave) {
                    this.noise2Stitch(noise, pointX, pointY, stitchInfo);
                    if (noise[0] < 0.0) {
                        final int n24 = 0;
                        fSum[n24] -= noise[0] * ratio;
                    }
                    else {
                        final int n25 = 0;
                        fSum[n25] += noise[0] * ratio;
                    }
                    ratio *= 0.5;
                    pointX *= 2.0;
                    pointY *= 2.0;
                    stitchInfo.doubleFrequency();
                }
                rgb[0] = (int)(fSum[0] * 255.0);
                if ((rgb[0] & 0xFFFFFF00) != 0x0) {
                    rgb[0] = (((rgb[0] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    break;
                }
                break;
            }
        }
    }
    
    private final int turbulenceFractal_4(double pointX, double pointY, final double[] fSum) {
        double ratio = 127.5;
        pointX *= this.baseFrequencyX;
        pointY *= this.baseFrequencyY;
        final int n = 0;
        final int n2 = 1;
        final int n3 = 2;
        final int n4 = 3;
        final double n5 = 127.5;
        fSum[n3] = (fSum[n4] = n5);
        fSum[n] = (fSum[n2] = n5);
        for (int nOctave = this.numOctaves; nOctave > 0; --nOctave) {
            final double px = pointX + 4096.0;
            int b0 = (int)px & 0xFF;
            final int i = this.latticeSelector[b0];
            final int j = this.latticeSelector[b0 + 1];
            final double rx0 = px - (int)px;
            final double rx2 = rx0 - 1.0;
            final double sx = s_curve(rx0);
            final double py = pointY + 4096.0;
            b0 = ((int)py & 0xFF);
            int b2 = b0 + 1 & 0xFF;
            b2 = (j + b0 & 0xFF) << 3;
            b0 = (i + b0 & 0xFF) << 3;
            final double ry0 = py - (int)py;
            final double ry2 = ry0 - 1.0;
            final double sy = s_curve(ry0);
            final int n6 = 0;
            fSum[n6] += lerp(sy, lerp(sx, rx0 * this.gradient[b0 + 0] + ry0 * this.gradient[b0 + 1], rx2 * this.gradient[b2 + 0] + ry0 * this.gradient[b2 + 1]), lerp(sx, rx0 * this.gradient[b0 + 8 + 0] + ry2 * this.gradient[b0 + 8 + 1], rx2 * this.gradient[b2 + 8 + 0] + ry2 * this.gradient[b2 + 8 + 1])) * ratio;
            final int n7 = 1;
            fSum[n7] += lerp(sy, lerp(sx, rx0 * this.gradient[b0 + 2] + ry0 * this.gradient[b0 + 3], rx2 * this.gradient[b2 + 2] + ry0 * this.gradient[b2 + 3]), lerp(sx, rx0 * this.gradient[b0 + 8 + 2] + ry2 * this.gradient[b0 + 8 + 3], rx2 * this.gradient[b2 + 8 + 2] + ry2 * this.gradient[b2 + 8 + 3])) * ratio;
            final int n8 = 2;
            fSum[n8] += lerp(sy, lerp(sx, rx0 * this.gradient[b0 + 4] + ry0 * this.gradient[b0 + 5], rx2 * this.gradient[b2 + 4] + ry0 * this.gradient[b2 + 5]), lerp(sx, rx0 * this.gradient[b0 + 8 + 4] + ry2 * this.gradient[b0 + 8 + 5], rx2 * this.gradient[b2 + 8 + 4] + ry2 * this.gradient[b2 + 8 + 5])) * ratio;
            final int n9 = 3;
            fSum[n9] += lerp(sy, lerp(sx, rx0 * this.gradient[b0 + 6] + ry0 * this.gradient[b0 + 7], rx2 * this.gradient[b2 + 6] + ry0 * this.gradient[b2 + 7]), lerp(sx, rx0 * this.gradient[b0 + 8 + 6] + ry2 * this.gradient[b0 + 8 + 7], rx2 * this.gradient[b2 + 8 + 6] + ry2 * this.gradient[b2 + 8 + 7])) * ratio;
            ratio *= 0.5;
            pointX *= 2.0;
            pointY *= 2.0;
        }
        int i = (int)fSum[0];
        int j;
        if ((i & 0xFFFFFF00) == 0x0) {
            j = i << 16;
        }
        else {
            j = (((i & Integer.MIN_VALUE) != 0x0) ? 0 : 16711680);
        }
        i = (int)fSum[1];
        if ((i & 0xFFFFFF00) == 0x0) {
            j |= i << 8;
        }
        else {
            j |= (((i & Integer.MIN_VALUE) != 0x0) ? 0 : 65280);
        }
        i = (int)fSum[2];
        if ((i & 0xFFFFFF00) == 0x0) {
            j |= i;
        }
        else {
            j |= (((i & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
        }
        i = (int)fSum[3];
        if ((i & 0xFFFFFF00) == 0x0) {
            j |= i << 24;
        }
        else {
            j |= (((i & Integer.MIN_VALUE) != 0x0) ? 0 : -16777216);
        }
        return j;
    }
    
    private final void turbulenceFractal(final int[] rgb, double pointX, double pointY, final double[] fSum, final double[] noise) {
        double ratio = 127.5;
        final int n = 0;
        final int n2 = 1;
        final int n3 = 2;
        final int n4 = 3;
        final double n5 = 127.5;
        fSum[n3] = (fSum[n4] = n5);
        fSum[n] = (fSum[n2] = n5);
        pointX *= this.baseFrequencyX;
        pointY *= this.baseFrequencyY;
        for (int nOctave = this.numOctaves; nOctave > 0; --nOctave) {
            this.noise2(noise, pointX, pointY);
            switch (this.channels.length) {
                case 4: {
                    final int n6 = 3;
                    fSum[n6] += noise[3] * ratio;
                }
                case 3: {
                    final int n7 = 2;
                    fSum[n7] += noise[2] * ratio;
                }
                case 2: {
                    final int n8 = 1;
                    fSum[n8] += noise[1] * ratio;
                }
                case 1: {
                    final int n9 = 0;
                    fSum[n9] += noise[0] * ratio;
                    break;
                }
            }
            ratio *= 0.5;
            pointX *= 2.0;
            pointY *= 2.0;
        }
        switch (this.channels.length) {
            case 4: {
                rgb[3] = (int)fSum[3];
                if ((rgb[3] & 0xFFFFFF00) != 0x0) {
                    rgb[3] = (((rgb[3] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
            }
            case 3: {
                rgb[2] = (int)fSum[2];
                if ((rgb[2] & 0xFFFFFF00) != 0x0) {
                    rgb[2] = (((rgb[2] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
            }
            case 2: {
                rgb[1] = (int)fSum[1];
                if ((rgb[1] & 0xFFFFFF00) != 0x0) {
                    rgb[1] = (((rgb[1] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
            }
            case 1: {
                rgb[0] = (int)fSum[0];
                if ((rgb[0] & 0xFFFFFF00) != 0x0) {
                    rgb[0] = (((rgb[0] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    break;
                }
                break;
            }
        }
    }
    
    private final void turbulenceFractalStitch(final int[] rgb, double pointX, double pointY, final double[] fSum, final double[] noise, final StitchInfo stitchInfo) {
        double ratio = 127.5;
        final int n = 0;
        final int n2 = 1;
        final int n3 = 2;
        final int n4 = 3;
        final double n5 = 127.5;
        fSum[n3] = (fSum[n4] = n5);
        fSum[n] = (fSum[n2] = n5);
        pointX *= this.baseFrequencyX;
        pointY *= this.baseFrequencyY;
        for (int nOctave = this.numOctaves; nOctave > 0; --nOctave) {
            this.noise2Stitch(noise, pointX, pointY, stitchInfo);
            switch (this.channels.length) {
                case 4: {
                    final int n6 = 3;
                    fSum[n6] += noise[3] * ratio;
                }
                case 3: {
                    final int n7 = 2;
                    fSum[n7] += noise[2] * ratio;
                }
                case 2: {
                    final int n8 = 1;
                    fSum[n8] += noise[1] * ratio;
                }
                case 1: {
                    final int n9 = 0;
                    fSum[n9] += noise[0] * ratio;
                    break;
                }
            }
            ratio *= 0.5;
            pointX *= 2.0;
            pointY *= 2.0;
            stitchInfo.doubleFrequency();
        }
        switch (this.channels.length) {
            case 4: {
                rgb[3] = (int)fSum[3];
                if ((rgb[3] & 0xFFFFFF00) != 0x0) {
                    rgb[3] = (((rgb[3] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
            }
            case 3: {
                rgb[2] = (int)fSum[2];
                if ((rgb[2] & 0xFFFFFF00) != 0x0) {
                    rgb[2] = (((rgb[2] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
            }
            case 2: {
                rgb[1] = (int)fSum[1];
                if ((rgb[1] & 0xFFFFFF00) != 0x0) {
                    rgb[1] = (((rgb[1] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
            }
            case 1: {
                rgb[0] = (int)fSum[0];
                if ((rgb[0] & 0xFFFFFF00) != 0x0) {
                    rgb[0] = (((rgb[0] & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    break;
                }
                break;
            }
        }
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster dest) {
        if (dest == null) {
            throw new IllegalArgumentException("Cannot generate a noise pattern into a null raster");
        }
        final int w = dest.getWidth();
        final int h = dest.getHeight();
        final DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
        final int minX = dest.getMinX();
        final int minY = dest.getMinY();
        final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)dest.getSampleModel();
        final int dstOff = dstDB.getOffset() + sppsm.getOffset(minX - dest.getSampleModelTranslateX(), minY - dest.getSampleModelTranslateY());
        final int[] destPixels = dstDB.getBankData()[0];
        final int dstAdjust = sppsm.getScanlineStride() - w;
        int dp = dstOff;
        final int[] rgb = new int[4];
        final double[] fSum = { 0.0, 0.0, 0.0, 0.0 };
        final double[] noise = { 0.0, 0.0, 0.0, 0.0 };
        final double tx0 = this.tx[0];
        final double tx2 = this.tx[1];
        final double ty0 = this.ty[0] - w * tx0;
        final double ty2 = this.ty[1] - w * tx2;
        final double[] p = { minX, minY };
        this.txf.transform(p, 0, p, 0, 1);
        double point_0 = p[0];
        double point_2 = p[1];
        if (this.isFractalNoise) {
            if (this.stitchInfo == null) {
                if (this.channels.length == 4) {
                    for (int i = 0; i < h; ++i) {
                        for (int end = dp + w; dp < end; ++dp) {
                            destPixels[dp] = this.turbulenceFractal_4(point_0, point_2, fSum);
                            point_0 += tx0;
                            point_2 += tx2;
                        }
                        point_0 += ty0;
                        point_2 += ty2;
                        dp += dstAdjust;
                    }
                }
                else {
                    for (int i = 0; i < h; ++i) {
                        for (int end = dp + w; dp < end; ++dp) {
                            this.turbulenceFractal(rgb, point_0, point_2, fSum, noise);
                            destPixels[dp] = (rgb[3] << 24 | rgb[0] << 16 | rgb[1] << 8 | rgb[2]);
                            point_0 += tx0;
                            point_2 += tx2;
                        }
                        point_0 += ty0;
                        point_2 += ty2;
                        dp += dstAdjust;
                    }
                }
            }
            else {
                final StitchInfo si = new StitchInfo();
                for (int i = 0; i < h; ++i) {
                    for (int end = dp + w; dp < end; ++dp) {
                        si.assign(this.stitchInfo);
                        this.turbulenceFractalStitch(rgb, point_0, point_2, fSum, noise, si);
                        destPixels[dp] = (rgb[3] << 24 | rgb[0] << 16 | rgb[1] << 8 | rgb[2]);
                        point_0 += tx0;
                        point_2 += tx2;
                    }
                    point_0 += ty0;
                    point_2 += ty2;
                    dp += dstAdjust;
                }
            }
        }
        else if (this.stitchInfo == null) {
            if (this.channels.length == 4) {
                for (int i = 0; i < h; ++i) {
                    for (int end = dp + w; dp < end; ++dp) {
                        destPixels[dp] = this.turbulence_4(point_0, point_2, fSum);
                        point_0 += tx0;
                        point_2 += tx2;
                    }
                    point_0 += ty0;
                    point_2 += ty2;
                    dp += dstAdjust;
                }
            }
            else {
                for (int i = 0; i < h; ++i) {
                    for (int end = dp + w; dp < end; ++dp) {
                        this.turbulence(rgb, point_0, point_2, fSum, noise);
                        destPixels[dp] = (rgb[3] << 24 | rgb[0] << 16 | rgb[1] << 8 | rgb[2]);
                        point_0 += tx0;
                        point_2 += tx2;
                    }
                    point_0 += ty0;
                    point_2 += ty2;
                    dp += dstAdjust;
                }
            }
        }
        else {
            final StitchInfo si = new StitchInfo();
            for (int i = 0; i < h; ++i) {
                for (int end = dp + w; dp < end; ++dp) {
                    si.assign(this.stitchInfo);
                    this.turbulenceStitch(rgb, point_0, point_2, fSum, noise, si);
                    destPixels[dp] = (rgb[3] << 24 | rgb[0] << 16 | rgb[1] << 8 | rgb[2]);
                    point_0 += tx0;
                    point_2 += tx2;
                }
                point_0 += ty0;
                point_2 += ty2;
                dp += dstAdjust;
            }
        }
        return dest;
    }
    
    public TurbulencePatternRed(final double baseFrequencyX, final double baseFrequencyY, final int numOctaves, final int seed, final boolean isFractalNoise, final Rectangle2D tile, final AffineTransform txf, final Rectangle devRect, final ColorSpace cs, final boolean alpha) {
        this.stitchInfo = null;
        this.tx = new double[] { 1.0, 0.0 };
        this.ty = new double[] { 0.0, 1.0 };
        this.latticeSelector = new int[257];
        this.gradient = new double[2056];
        this.baseFrequencyX = baseFrequencyX;
        this.baseFrequencyY = baseFrequencyY;
        this.seed = seed;
        this.isFractalNoise = isFractalNoise;
        this.tile = tile;
        this.txf = txf;
        if (this.txf == null) {
            this.txf = TurbulencePatternRed.IDENTITY;
        }
        int nChannels = cs.getNumComponents();
        if (alpha) {
            ++nChannels;
        }
        this.channels = new int[nChannels];
        for (int i = 0; i < this.channels.length; ++i) {
            this.channels[i] = i;
        }
        txf.deltaTransform(this.tx, 0, this.tx, 0, 1);
        txf.deltaTransform(this.ty, 0, this.ty, 0, 1);
        final double[] vecX = { 0.5, 0.0 };
        final double[] vecY = { 0.0, 0.5 };
        txf.deltaTransform(vecX, 0, vecX, 0, 1);
        txf.deltaTransform(vecY, 0, vecY, 0, 1);
        final double dx = Math.max(Math.abs(vecX[0]), Math.abs(vecY[0]));
        final int maxX = -(int)Math.round((Math.log(dx) + Math.log(baseFrequencyX)) / Math.log(2.0));
        final double dy = Math.max(Math.abs(vecX[1]), Math.abs(vecY[1]));
        final int maxY = -(int)Math.round((Math.log(dy) + Math.log(baseFrequencyY)) / Math.log(2.0));
        this.numOctaves = ((numOctaves > maxX) ? maxX : numOctaves);
        this.numOctaves = ((this.numOctaves > maxY) ? maxY : this.numOctaves);
        if (this.numOctaves < 1 && numOctaves > 1) {
            this.numOctaves = 1;
        }
        if (this.numOctaves > 8) {
            this.numOctaves = 8;
        }
        if (tile != null) {
            double lowFreq = Math.floor(tile.getWidth() * baseFrequencyX) / tile.getWidth();
            double highFreq = Math.ceil(tile.getWidth() * baseFrequencyX) / tile.getWidth();
            if (baseFrequencyX / lowFreq < highFreq / baseFrequencyX) {
                this.baseFrequencyX = lowFreq;
            }
            else {
                this.baseFrequencyX = highFreq;
            }
            lowFreq = Math.floor(tile.getHeight() * baseFrequencyY) / tile.getHeight();
            highFreq = Math.ceil(tile.getHeight() * baseFrequencyY) / tile.getHeight();
            if (baseFrequencyY / lowFreq < highFreq / baseFrequencyY) {
                this.baseFrequencyY = lowFreq;
            }
            else {
                this.baseFrequencyY = highFreq;
            }
            this.stitchInfo = new StitchInfo();
            this.stitchInfo.width = (int)(tile.getWidth() * this.baseFrequencyX);
            this.stitchInfo.height = (int)(tile.getHeight() * this.baseFrequencyY);
            this.stitchInfo.wrapX = (int)(tile.getX() * this.baseFrequencyX + 4096.0 + this.stitchInfo.width);
            this.stitchInfo.wrapY = (int)(tile.getY() * this.baseFrequencyY + 4096.0 + this.stitchInfo.height);
            if (this.stitchInfo.width == 0) {
                this.stitchInfo.width = 1;
            }
            if (this.stitchInfo.height == 0) {
                this.stitchInfo.height = 1;
            }
        }
        this.initLattice(seed);
        ColorModel cm;
        if (alpha) {
            cm = new DirectColorModel(cs, 32, 16711680, 65280, 255, -16777216, false, 3);
        }
        else {
            cm = new DirectColorModel(cs, 24, 16711680, 65280, 255, 0, false, 3);
        }
        final int tileSize = AbstractTiledRed.getDefaultTileSize();
        this.init((CachableRed)null, devRect, cm, cm.createCompatibleSampleModel(tileSize, tileSize), 0, 0, null);
    }
    
    static {
        IDENTITY = new AffineTransform();
    }
    
    static final class StitchInfo
    {
        int width;
        int height;
        int wrapX;
        int wrapY;
        
        StitchInfo() {
        }
        
        StitchInfo(final StitchInfo stitchInfo) {
            this.width = stitchInfo.width;
            this.height = stitchInfo.height;
            this.wrapX = stitchInfo.wrapX;
            this.wrapY = stitchInfo.wrapY;
        }
        
        final void assign(final StitchInfo stitchInfo) {
            this.width = stitchInfo.width;
            this.height = stitchInfo.height;
            this.wrapX = stitchInfo.wrapX;
            this.wrapY = stitchInfo.wrapY;
        }
        
        final void doubleFrequency() {
            this.width *= 2;
            this.height *= 2;
            this.wrapX *= 2;
            this.wrapY *= 2;
            this.wrapX -= (int)4096.0;
            this.wrapY -= (int)4096.0;
        }
    }
}
