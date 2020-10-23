// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.Rectangle;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.awt.image.ComponentSampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.SampleModel;
import java.util.Map;

public class MultiplyAlphaRed extends AbstractRed
{
    public MultiplyAlphaRed(final CachableRed src, final CachableRed alpha) {
        super(makeList(src, alpha), makeBounds(src, alpha), fixColorModel(src), fixSampleModel(src), src.getTileGridXOffset(), src.getTileGridYOffset(), null);
    }
    
    public boolean is_INT_PACK_BYTE_COMP(final SampleModel srcSM, final SampleModel alpSM) {
        if (!(srcSM instanceof SinglePixelPackedSampleModel)) {
            return false;
        }
        if (!(alpSM instanceof ComponentSampleModel)) {
            return false;
        }
        if (srcSM.getDataType() != 3) {
            return false;
        }
        if (alpSM.getDataType() != 0) {
            return false;
        }
        final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)srcSM;
        final int[] masks = sppsm.getBitMasks();
        if (masks.length != 4) {
            return false;
        }
        if (masks[0] != 16711680) {
            return false;
        }
        if (masks[1] != 65280) {
            return false;
        }
        if (masks[2] != 255) {
            return false;
        }
        if (masks[3] != -16777216) {
            return false;
        }
        final ComponentSampleModel csm = (ComponentSampleModel)alpSM;
        return csm.getNumBands() == 1 && csm.getPixelStride() == 1;
    }
    
    public WritableRaster INT_PACK_BYTE_COMP_Impl(final WritableRaster wr) {
        final CachableRed srcRed = this.getSources().get(0);
        final CachableRed alphaRed = this.getSources().get(1);
        srcRed.copyData(wr);
        Rectangle rgn = wr.getBounds();
        rgn = rgn.intersection(alphaRed.getBounds());
        final Raster r = alphaRed.getData(rgn);
        final ComponentSampleModel csm = (ComponentSampleModel)r.getSampleModel();
        final int alpScanStride = csm.getScanlineStride();
        final DataBufferByte alpDB = (DataBufferByte)r.getDataBuffer();
        final int alpBase = alpDB.getOffset() + csm.getOffset(rgn.x - r.getSampleModelTranslateX(), rgn.y - r.getSampleModelTranslateY());
        final byte[] alpPixels = alpDB.getBankData()[0];
        final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        final int srcScanStride = sppsm.getScanlineStride();
        final DataBufferInt srcDB = (DataBufferInt)wr.getDataBuffer();
        final int srcBase = srcDB.getOffset() + sppsm.getOffset(rgn.x - wr.getSampleModelTranslateX(), rgn.y - wr.getSampleModelTranslateY());
        final int[] srcPixels = srcDB.getBankData()[0];
        final ColorModel cm = srcRed.getColorModel();
        if (cm.isAlphaPremultiplied()) {
            for (int y = 0; y < rgn.height; ++y) {
                int sp = srcBase + y * srcScanStride;
                int ap = alpBase + y * alpScanStride;
                for (int end = sp + rgn.width; sp < end; ++sp) {
                    final int a = alpPixels[ap++] & 0xFF;
                    final int pix = srcPixels[sp];
                    srcPixels[sp] = (((pix >>> 24) * a & 0xFF00) << 16 | ((pix >>> 16 & 0xFF) * a & 0xFF00) << 8 | ((pix >>> 8 & 0xFF) * a & 0xFF00) | ((pix & 0xFF) * a & 0xFF00) >> 8);
                }
            }
        }
        else {
            for (int y = 0; y < rgn.height; ++y) {
                int sp = srcBase + y * srcScanStride;
                int ap = alpBase + y * alpScanStride;
                for (int end = sp + rgn.width; sp < end; ++sp) {
                    final int a = alpPixels[ap++] & 0xFF;
                    final int sa = srcPixels[sp] >>> 24;
                    srcPixels[sp] = ((sa * a & 0xFF00) << 16 | (srcPixels[sp] & 0xFFFFFF));
                }
            }
        }
        return wr;
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        final CachableRed srcRed = this.getSources().get(0);
        final CachableRed alphaRed = this.getSources().get(1);
        if (this.is_INT_PACK_BYTE_COMP(srcRed.getSampleModel(), alphaRed.getSampleModel())) {
            return this.INT_PACK_BYTE_COMP_Impl(wr);
        }
        final ColorModel cm = srcRed.getColorModel();
        if (!cm.hasAlpha()) {
            int[] bands = new int[wr.getNumBands() - 1];
            for (int i = 0; i < bands.length; ++i) {
                bands[i] = i;
            }
            WritableRaster subWr = wr.createWritableChild(wr.getMinX(), wr.getMinY(), wr.getWidth(), wr.getHeight(), wr.getMinX(), wr.getMinY(), bands);
            srcRed.copyData(subWr);
            Rectangle rgn = wr.getBounds();
            rgn = rgn.intersection(alphaRed.getBounds());
            bands = new int[] { wr.getNumBands() - 1 };
            subWr = wr.createWritableChild(rgn.x, rgn.y, rgn.width, rgn.height, rgn.x, rgn.y, bands);
            alphaRed.copyData(subWr);
            return wr;
        }
        srcRed.copyData(wr);
        Rectangle rgn2 = wr.getBounds();
        if (rgn2.intersects(alphaRed.getBounds())) {
            rgn2 = rgn2.intersection(alphaRed.getBounds());
            int[] wrData = null;
            int[] alphaData = null;
            final Raster r = alphaRed.getData(rgn2);
            final int w = rgn2.width;
            final int bands2 = wr.getSampleModel().getNumBands();
            if (cm.isAlphaPremultiplied()) {
                for (int y = rgn2.y; y < rgn2.y + rgn2.height; ++y) {
                    wrData = wr.getPixels(rgn2.x, y, w, 1, wrData);
                    alphaData = r.getSamples(rgn2.x, y, w, 1, 0, alphaData);
                    int j = 0;
                    switch (bands2) {
                        case 2: {
                            for (final int anAlphaData2 : alphaData) {
                                final int a = anAlphaData2 & 0xFF;
                                wrData[j] = (wrData[j] & 0xFF) * a >> 8;
                                ++j;
                                wrData[j] = (wrData[j] & 0xFF) * a >> 8;
                                ++j;
                            }
                            break;
                        }
                        case 4: {
                            for (final int anAlphaData3 : alphaData) {
                                final int a = anAlphaData3 & 0xFF;
                                wrData[j] = (wrData[j] & 0xFF) * a >> 8;
                                ++j;
                                wrData[j] = (wrData[j] & 0xFF) * a >> 8;
                                ++j;
                                wrData[j] = (wrData[j] & 0xFF) * a >> 8;
                                ++j;
                                wrData[j] = (wrData[j] & 0xFF) * a >> 8;
                                ++j;
                            }
                            break;
                        }
                        default: {
                            for (final int anAlphaData4 : alphaData) {
                                final int a = anAlphaData4 & 0xFF;
                                for (int b = 0; b < bands2; ++b) {
                                    wrData[j] = (wrData[j] & 0xFF) * a >> 8;
                                    ++j;
                                }
                            }
                            break;
                        }
                    }
                    wr.setPixels(rgn2.x, y, w, 1, wrData);
                }
            }
            else {
                final int b2 = srcRed.getSampleModel().getNumBands() - 1;
                for (int y2 = rgn2.y; y2 < rgn2.y + rgn2.height; ++y2) {
                    wrData = wr.getSamples(rgn2.x, y2, w, 1, b2, wrData);
                    alphaData = r.getSamples(rgn2.x, y2, w, 1, 0, alphaData);
                    for (int k = 0; k < wrData.length; ++k) {
                        wrData[k] = (wrData[k] & 0xFF) * (alphaData[k] & 0xFF) >> 8;
                    }
                    wr.setSamples(rgn2.x, y2, w, 1, b2, wrData);
                }
            }
            return wr;
        }
        return wr;
    }
    
    public static List makeList(final CachableRed src1, final CachableRed src2) {
        final List ret = new ArrayList(2);
        ret.add(src1);
        ret.add(src2);
        return ret;
    }
    
    public static Rectangle makeBounds(final CachableRed src1, final CachableRed src2) {
        final Rectangle r1 = src1.getBounds();
        final Rectangle r2 = src2.getBounds();
        return r1.intersection(r2);
    }
    
    public static SampleModel fixSampleModel(final CachableRed src) {
        final ColorModel cm = src.getColorModel();
        final SampleModel srcSM = src.getSampleModel();
        if (cm.hasAlpha()) {
            return srcSM;
        }
        final int w = srcSM.getWidth();
        final int h = srcSM.getHeight();
        final int b = srcSM.getNumBands() + 1;
        final int[] offsets = new int[b];
        for (int i = 0; i < b; ++i) {
            offsets[i] = i;
        }
        return new PixelInterleavedSampleModel(0, w, h, b, w * b, offsets);
    }
    
    public static ColorModel fixColorModel(final CachableRed src) {
        final ColorModel cm = src.getColorModel();
        if (cm.hasAlpha()) {
            return cm;
        }
        final int b = src.getSampleModel().getNumBands() + 1;
        final int[] bits = new int[b];
        for (int i = 0; i < b; ++i) {
            bits[i] = 8;
        }
        final ColorSpace cs = cm.getColorSpace();
        return new ComponentColorModel(cs, bits, true, false, 3, 0);
    }
}
