// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.util.Map;
import java.awt.color.ColorSpace;
import org.apache.batik.ext.awt.image.GraphicsUtil;

public class ColorMatrixRed extends AbstractRed
{
    private float[][] matrix;
    
    public float[][] getMatrix() {
        return this.copyMatrix(this.matrix);
    }
    
    public void setMatrix(final float[][] matrix) {
        final float[][] tmp = this.copyMatrix(matrix);
        if (tmp == null) {
            throw new IllegalArgumentException();
        }
        if (tmp.length != 4) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < 4; ++i) {
            if (tmp[i].length != 5) {
                throw new IllegalArgumentException(String.valueOf(i) + " : " + tmp[i].length);
            }
        }
        this.matrix = matrix;
    }
    
    private float[][] copyMatrix(final float[][] m) {
        if (m == null) {
            return null;
        }
        final float[][] cm = new float[m.length][];
        for (int i = 0; i < m.length; ++i) {
            if (m[i] != null) {
                cm[i] = new float[m[i].length];
                System.arraycopy(m[i], 0, cm[i], 0, m[i].length);
            }
        }
        return cm;
    }
    
    public ColorMatrixRed(final CachableRed src, final float[][] matrix) {
        this.setMatrix(matrix);
        final ColorModel srcCM = src.getColorModel();
        ColorSpace srcCS = null;
        if (srcCM != null) {
            srcCS = srcCM.getColorSpace();
        }
        ColorModel cm;
        if (srcCS == null) {
            cm = GraphicsUtil.Linear_sRGB_Unpre;
        }
        else if (srcCS == ColorSpace.getInstance(1004)) {
            cm = GraphicsUtil.Linear_sRGB_Unpre;
        }
        else {
            cm = GraphicsUtil.sRGB_Unpre;
        }
        final SampleModel sm = cm.createCompatibleSampleModel(src.getWidth(), src.getHeight());
        this.init(src, src.getBounds(), cm, sm, src.getTileGridXOffset(), src.getTileGridYOffset(), null);
    }
    
    @Override
    public WritableRaster copyData(WritableRaster wr) {
        final CachableRed src = this.getSources().get(0);
        wr = src.copyData(wr);
        final ColorModel cm = src.getColorModel();
        GraphicsUtil.coerceData(wr, cm, false);
        final int minX = wr.getMinX();
        final int minY = wr.getMinY();
        final int w = wr.getWidth();
        final int h = wr.getHeight();
        final DataBufferInt dbf = (DataBufferInt)wr.getDataBuffer();
        final int[] pixels = dbf.getBankData()[0];
        final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        final int offset = dbf.getOffset() + sppsm.getOffset(minX - wr.getSampleModelTranslateX(), minY - wr.getSampleModelTranslateY());
        final int scanStride = ((SinglePixelPackedSampleModel)wr.getSampleModel()).getScanlineStride();
        final int adjust = scanStride - w;
        int p = offset;
        int i = 0;
        int j = 0;
        final float a00 = this.matrix[0][0] / 255.0f;
        final float a2 = this.matrix[0][1] / 255.0f;
        final float a3 = this.matrix[0][2] / 255.0f;
        final float a4 = this.matrix[0][3] / 255.0f;
        final float a5 = this.matrix[0][4] / 255.0f;
        final float a6 = this.matrix[1][0] / 255.0f;
        final float a7 = this.matrix[1][1] / 255.0f;
        final float a8 = this.matrix[1][2] / 255.0f;
        final float a9 = this.matrix[1][3] / 255.0f;
        final float a10 = this.matrix[1][4] / 255.0f;
        final float a11 = this.matrix[2][0] / 255.0f;
        final float a12 = this.matrix[2][1] / 255.0f;
        final float a13 = this.matrix[2][2] / 255.0f;
        final float a14 = this.matrix[2][3] / 255.0f;
        final float a15 = this.matrix[2][4] / 255.0f;
        final float a16 = this.matrix[3][0] / 255.0f;
        final float a17 = this.matrix[3][1] / 255.0f;
        final float a18 = this.matrix[3][2] / 255.0f;
        final float a19 = this.matrix[3][3] / 255.0f;
        final float a20 = this.matrix[3][4] / 255.0f;
        for (i = 0; i < h; ++i) {
            for (j = 0; j < w; ++j) {
                final int pel = pixels[p];
                final int a21 = pel >>> 24;
                final int r = pel >> 16 & 0xFF;
                final int g = pel >> 8 & 0xFF;
                final int b = pel & 0xFF;
                int dr = (int)((a00 * r + a2 * g + a3 * b + a4 * a21 + a5) * 255.0f);
                int dg = (int)((a6 * r + a7 * g + a8 * b + a9 * a21 + a10) * 255.0f);
                int db = (int)((a11 * r + a12 * g + a13 * b + a14 * a21 + a15) * 255.0f);
                int da = (int)((a16 * r + a17 * g + a18 * b + a19 * a21 + a20) * 255.0f);
                if ((dr & 0xFFFFFF00) != 0x0) {
                    dr = (((dr & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
                if ((dg & 0xFFFFFF00) != 0x0) {
                    dg = (((dg & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
                if ((db & 0xFFFFFF00) != 0x0) {
                    db = (((db & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
                if ((da & 0xFFFFFF00) != 0x0) {
                    da = (((da & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                }
                pixels[p++] = (da << 24 | dr << 16 | dg << 8 | db);
            }
            p += adjust;
        }
        return wr;
    }
}
