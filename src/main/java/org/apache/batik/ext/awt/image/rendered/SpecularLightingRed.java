// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import org.apache.batik.ext.awt.image.SpotLight;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.util.Map;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.Rectangle;
import org.apache.batik.ext.awt.image.Light;

public class SpecularLightingRed extends AbstractTiledRed
{
    private double ks;
    private double specularExponent;
    private Light light;
    private BumpMap bumpMap;
    private double scaleX;
    private double scaleY;
    private Rectangle litRegion;
    private boolean linear;
    
    public SpecularLightingRed(final double ks, final double specularExponent, final Light light, final BumpMap bumpMap, final Rectangle litRegion, final double scaleX, final double scaleY, final boolean linear) {
        this.ks = ks;
        this.specularExponent = specularExponent;
        this.light = light;
        this.bumpMap = bumpMap;
        this.litRegion = litRegion;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.linear = linear;
        ColorModel cm;
        if (linear) {
            cm = GraphicsUtil.Linear_sRGB_Unpre;
        }
        else {
            cm = GraphicsUtil.sRGB_Unpre;
        }
        int tw = litRegion.width;
        int th = litRegion.height;
        final int defSz = AbstractTiledRed.getDefaultTileSize();
        if (tw > defSz) {
            tw = defSz;
        }
        if (th > defSz) {
            th = defSz;
        }
        final SampleModel sm = cm.createCompatibleSampleModel(tw, th);
        this.init((CachableRed)null, litRegion, cm, sm, litRegion.x, litRegion.y, null);
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        this.copyToRaster(wr);
        return wr;
    }
    
    @Override
    public void genRect(final WritableRaster wr) {
        final double scaleX = this.scaleX;
        final double scaleY = this.scaleY;
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
        int a = 0;
        int i = 0;
        int j = 0;
        final double x = scaleX * minX;
        final double y = scaleY * minY;
        double norm = 0.0;
        int pixel = 0;
        double mult = (lightColor[0] > lightColor[1]) ? lightColor[0] : lightColor[1];
        mult = ((mult > lightColor[2]) ? mult : lightColor[2]);
        final double scale = 255.0 / mult;
        pixel = (int)(lightColor[0] * scale + 0.5);
        int tmp = (int)(lightColor[1] * scale + 0.5);
        pixel = (pixel << 8 | tmp);
        tmp = (int)(lightColor[2] * scale + 0.5);
        pixel = (pixel << 8 | tmp);
        mult *= 255.0 * this.ks;
        final double[][][] NA = this.bumpMap.getNormalArray(minX, minY, w, h);
        if (this.light instanceof SpotLight) {
            final SpotLight slight = (SpotLight)this.light;
            final double[][] LA = new double[w][4];
            for (i = 0; i < h; ++i) {
                final double[][] NR = NA[i];
                slight.getLightRow4(x, y + i * scaleY, scaleX, w, NR, LA);
                for (j = 0; j < w; ++j) {
                    final double[] N = NR[j];
                    final double[] L = LA[j];
                    double vs = L[3];
                    if (vs == 0.0) {
                        a = 0;
                    }
                    else {
                        final double[] array = L;
                        final int n = 2;
                        ++array[n];
                        norm = L[0] * L[0] + L[1] * L[1] + L[2] * L[2];
                        norm = Math.sqrt(norm);
                        final double dot = N[0] * L[0] + N[1] * L[1] + N[2] * L[2];
                        vs *= Math.pow(dot / norm, this.specularExponent);
                        a = (int)(mult * vs + 0.5);
                        if ((a & 0xFFFFFF00) != 0x0) {
                            a = (((a & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                        }
                    }
                    pixels[p++] = (a << 24 | pixel);
                }
                p += adjust;
            }
        }
        else if (!this.light.isConstant()) {
            final double[][] LA2 = new double[w][4];
            for (i = 0; i < h; ++i) {
                final double[][] NR2 = NA[i];
                this.light.getLightRow(x, y + i * scaleY, scaleX, w, NR2, LA2);
                for (j = 0; j < w; ++j) {
                    final double[] N2 = NR2[j];
                    final double[] array2;
                    final double[] L2 = array2 = LA2[j];
                    final int n2 = 2;
                    ++array2[n2];
                    norm = L2[0] * L2[0] + L2[1] * L2[1] + L2[2] * L2[2];
                    norm = Math.sqrt(norm);
                    final double dot2 = N2[0] * L2[0] + N2[1] * L2[1] + N2[2] * L2[2];
                    norm = Math.pow(dot2 / norm, this.specularExponent);
                    a = (int)(mult * norm + 0.5);
                    if ((a & 0xFFFFFF00) != 0x0) {
                        a = (((a & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    }
                    pixels[p++] = (a << 24 | pixel);
                }
                p += adjust;
            }
        }
        else {
            final double[] L3 = new double[3];
            this.light.getLight(0.0, 0.0, 0.0, L3);
            final double[] array3 = L3;
            final int n3 = 2;
            ++array3[n3];
            norm = Math.sqrt(L3[0] * L3[0] + L3[1] * L3[1] + L3[2] * L3[2]);
            if (norm > 0.0) {
                final double[] array4 = L3;
                final int n4 = 0;
                array4[n4] /= norm;
                final double[] array5 = L3;
                final int n5 = 1;
                array5[n5] /= norm;
                final double[] array6 = L3;
                final int n6 = 2;
                array6[n6] /= norm;
            }
            for (i = 0; i < h; ++i) {
                final double[][] NR2 = NA[i];
                for (j = 0; j < w; ++j) {
                    final double[] N2 = NR2[j];
                    a = (int)(mult * Math.pow(N2[0] * L3[0] + N2[1] * L3[1] + N2[2] * L3[2], this.specularExponent) + 0.5);
                    if ((a & 0xFFFFFF00) != 0x0) {
                        a = (((a & Integer.MIN_VALUE) != 0x0) ? 0 : 255);
                    }
                    pixels[p++] = (a << 24 | pixel);
                }
                p += adjust;
            }
        }
    }
}
