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
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.Rectangle;
import org.apache.batik.ext.awt.image.Light;

public class DiffuseLightingRed extends AbstractRed
{
    private double kd;
    private Light light;
    private BumpMap bumpMap;
    private double scaleX;
    private double scaleY;
    private Rectangle litRegion;
    private boolean linear;
    
    public DiffuseLightingRed(final double kd, final Light light, final BumpMap bumpMap, final Rectangle litRegion, final double scaleX, final double scaleY, final boolean linear) {
        this.kd = kd;
        this.light = light;
        this.bumpMap = bumpMap;
        this.litRegion = litRegion;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.linear = linear;
        ColorModel cm;
        if (linear) {
            cm = GraphicsUtil.Linear_sRGB_Pre;
        }
        else {
            cm = GraphicsUtil.sRGB_Pre;
        }
        final SampleModel sm = cm.createCompatibleSampleModel(litRegion.width, litRegion.height);
        this.init((CachableRed)null, litRegion, cm, sm, litRegion.x, litRegion.y, null);
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        final double[] lightColor = this.light.getColor(this.linear);
        final int w = wr.getWidth();
        final int h = wr.getHeight();
        final int minX = wr.getMinX();
        final int minY = wr.getMinY();
        final DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        final int[] pixels = db.getBankData()[0];
        final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        final int offset = db.getOffset() + sppsm.getOffset(minX - wr.getSampleModelTranslateX(), minY - wr.getSampleModelTranslateY());
        final int scanStride = sppsm.getScanlineStride();
        final int adjust = scanStride - w;
        int p = offset;
        int r = 0;
        int g = 0;
        int b = 0;
        int i = 0;
        int j = 0;
        final double x = this.scaleX * minX;
        final double y = this.scaleY * minY;
        double NL = 0.0;
        final double[][][] NA = this.bumpMap.getNormalArray(minX, minY, w, h);
        if (!this.light.isConstant()) {
            final double[][] LA = new double[w][3];
            for (i = 0; i < h; ++i) {
                final double[][] NR = NA[i];
                this.light.getLightRow(x, y + i * this.scaleY, this.scaleX, w, NR, LA);
                for (j = 0; j < w; ++j) {
                    final double[] N = NR[j];
                    final double[] L = LA[j];
                    NL = 255.0 * this.kd * (N[0] * L[0] + N[1] * L[1] + N[2] * L[2]);
                    r = (int)(NL * lightColor[0]);
                    g = (int)(NL * lightColor[1]);
                    b = (int)(NL * lightColor[2]);
                    if ((r & 0xFFFFFF00) != 0x0) {
                        r = (((r & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    }
                    if ((g & 0xFFFFFF00) != 0x0) {
                        g = (((g & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    }
                    if ((b & 0xFFFFFF00) != 0x0) {
                        b = (((b & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    }
                    pixels[p++] = (0xFF000000 | r << 16 | g << 8 | b);
                }
                p += adjust;
            }
        }
        else {
            final double[] L2 = new double[3];
            this.light.getLight(0.0, 0.0, 0.0, L2);
            for (i = 0; i < h; ++i) {
                final double[][] NR = NA[i];
                for (j = 0; j < w; ++j) {
                    final double[] N = NR[j];
                    NL = 255.0 * this.kd * (N[0] * L2[0] + N[1] * L2[1] + N[2] * L2[2]);
                    r = (int)(NL * lightColor[0]);
                    g = (int)(NL * lightColor[1]);
                    b = (int)(NL * lightColor[2]);
                    if ((r & 0xFFFFFF00) != 0x0) {
                        r = (((r & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    }
                    if ((g & 0xFFFFFF00) != 0x0) {
                        g = (((g & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    }
                    if ((b & 0xFFFFFF00) != 0x0) {
                        b = (((b & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    }
                    pixels[p++] = (0xFF000000 | r << 16 | g << 8 | b);
                }
                p += adjust;
            }
        }
        return wr;
    }
}
