// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.rendered.ColorMatrixRed;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.util.Map;

public final class ColorMatrixRable8Bit extends AbstractColorInterpolationRable implements ColorMatrixRable
{
    private static float[][] MATRIX_LUMINANCE_TO_ALPHA;
    private int type;
    private float[][] matrix;
    
    @Override
    public void setSource(final Filter src) {
        this.init(src, null);
    }
    
    @Override
    public Filter getSource() {
        return this.getSources().get(0);
    }
    
    @Override
    public int getType() {
        return this.type;
    }
    
    @Override
    public float[][] getMatrix() {
        return this.matrix;
    }
    
    private ColorMatrixRable8Bit() {
    }
    
    public static ColorMatrixRable buildMatrix(final float[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException();
        }
        if (matrix.length != 4) {
            throw new IllegalArgumentException();
        }
        final float[][] newMatrix = new float[4][];
        for (int i = 0; i < 4; ++i) {
            final float[] m = matrix[i];
            if (m == null) {
                throw new IllegalArgumentException();
            }
            if (m.length != 5) {
                throw new IllegalArgumentException();
            }
            newMatrix[i] = new float[5];
            for (int j = 0; j < 5; ++j) {
                newMatrix[i][j] = m[j];
            }
        }
        final ColorMatrixRable8Bit filter = new ColorMatrixRable8Bit();
        filter.type = 0;
        filter.matrix = newMatrix;
        return filter;
    }
    
    public static ColorMatrixRable buildSaturate(final float s) {
        final ColorMatrixRable8Bit filter = new ColorMatrixRable8Bit();
        filter.type = 1;
        filter.matrix = new float[][] { { 0.213f + 0.787f * s, 0.715f - 0.715f * s, 0.072f - 0.072f * s, 0.0f, 0.0f }, { 0.213f - 0.213f * s, 0.715f + 0.285f * s, 0.072f - 0.072f * s, 0.0f, 0.0f }, { 0.213f - 0.213f * s, 0.715f - 0.715f * s, 0.072f + 0.928f * s, 0.0f, 0.0f }, { 0.0f, 0.0f, 0.0f, 1.0f, 0.0f } };
        return filter;
    }
    
    public static ColorMatrixRable buildHueRotate(final float a) {
        final ColorMatrixRable8Bit filter = new ColorMatrixRable8Bit();
        filter.type = 2;
        final float cos = (float)Math.cos(a);
        final float sin = (float)Math.sin(a);
        final float a2 = 0.213f + cos * 0.787f - sin * 0.213f;
        final float a3 = 0.213f - cos * 0.212f + sin * 0.143f;
        final float a4 = 0.213f - cos * 0.213f - sin * 0.787f;
        final float a5 = 0.715f - cos * 0.715f - sin * 0.715f;
        final float a6 = 0.715f + cos * 0.285f + sin * 0.14f;
        final float a7 = 0.715f - cos * 0.715f + sin * 0.715f;
        final float a8 = 0.072f - cos * 0.072f + sin * 0.928f;
        final float a9 = 0.072f - cos * 0.072f - sin * 0.283f;
        final float a10 = 0.072f + cos * 0.928f + sin * 0.072f;
        filter.matrix = new float[][] { { a2, a5, a8, 0.0f, 0.0f }, { a3, a6, a9, 0.0f, 0.0f }, { a4, a7, a10, 0.0f, 0.0f }, { 0.0f, 0.0f, 0.0f, 1.0f, 0.0f } };
        return filter;
    }
    
    public static ColorMatrixRable buildLuminanceToAlpha() {
        final ColorMatrixRable8Bit filter = new ColorMatrixRable8Bit();
        filter.type = 3;
        filter.matrix = ColorMatrixRable8Bit.MATRIX_LUMINANCE_TO_ALPHA;
        return filter;
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext rc) {
        final RenderedImage srcRI = this.getSource().createRendering(rc);
        if (srcRI == null) {
            return null;
        }
        return new ColorMatrixRed(this.convertSourceCS(srcRI), this.matrix);
    }
    
    static {
        ColorMatrixRable8Bit.MATRIX_LUMINANCE_TO_ALPHA = new float[][] { { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f }, { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f }, { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f }, { 0.2125f, 0.7154f, 0.0721f, 0.0f, 0.0f } };
    }
}
