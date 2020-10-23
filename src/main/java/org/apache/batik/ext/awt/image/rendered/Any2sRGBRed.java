// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.Raster;
import java.awt.image.ColorConvertOp;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.awt.image.BandCombineOp;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.awt.color.ColorSpace;
import java.util.Map;

public class Any2sRGBRed extends AbstractRed
{
    boolean srcIsLsRGB;
    private static final double GAMMA = 2.4;
    private static final int[] linearToSRGBLut;
    
    public Any2sRGBRed(final CachableRed src) {
        super(src, src.getBounds(), fixColorModel(src), fixSampleModel(src), src.getTileGridXOffset(), src.getTileGridYOffset(), null);
        this.srcIsLsRGB = false;
        final ColorModel srcCM = src.getColorModel();
        if (srcCM == null) {
            return;
        }
        final ColorSpace srcCS = srcCM.getColorSpace();
        if (srcCS == ColorSpace.getInstance(1004)) {
            this.srcIsLsRGB = true;
        }
    }
    
    public static boolean is_INT_PACK_COMP(final SampleModel sm) {
        if (!(sm instanceof SinglePixelPackedSampleModel)) {
            return false;
        }
        if (sm.getDataType() != 3) {
            return false;
        }
        final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm;
        final int[] masks = sppsm.getBitMasks();
        return (masks.length == 3 || masks.length == 4) && masks[0] == 16711680 && masks[1] == 65280 && masks[2] == 255 && (masks.length != 4 || masks[3] == -16777216);
    }
    
    public static WritableRaster applyLut_INT(final WritableRaster wr, final int[] lut) {
        final SinglePixelPackedSampleModel sm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        final DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        final int srcBase = db.getOffset() + sm.getOffset(wr.getMinX() - wr.getSampleModelTranslateX(), wr.getMinY() - wr.getSampleModelTranslateY());
        final int[] pixels = db.getBankData()[0];
        final int width = wr.getWidth();
        final int height = wr.getHeight();
        final int scanStride = sm.getScanlineStride();
        for (int y = 0; y < height; ++y) {
            for (int sp = srcBase + y * scanStride, end = sp + width; sp < end; ++sp) {
                final int pix = pixels[sp];
                pixels[sp] = ((pix & 0xFF000000) | lut[pix >>> 16 & 0xFF] << 16 | lut[pix >>> 8 & 0xFF] << 8 | lut[pix & 0xFF]);
            }
        }
        return wr;
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        final CachableRed src = this.getSources().get(0);
        final ColorModel srcCM = src.getColorModel();
        final SampleModel srcSM = src.getSampleModel();
        if (this.srcIsLsRGB && is_INT_PACK_COMP(wr.getSampleModel())) {
            src.copyData(wr);
            if (srcCM.hasAlpha()) {
                GraphicsUtil.coerceData(wr, srcCM, false);
            }
            applyLut_INT(wr, Any2sRGBRed.linearToSRGBLut);
            return wr;
        }
        if (srcCM == null) {
            float[][] matrix = null;
            switch (srcSM.getNumBands()) {
                case 1: {
                    matrix = new float[3][1];
                    matrix[0][0] = 1.0f;
                    matrix[1][0] = 1.0f;
                    matrix[2][0] = 1.0f;
                    break;
                }
                case 2: {
                    matrix = new float[4][2];
                    matrix[0][0] = 1.0f;
                    matrix[1][0] = 1.0f;
                    matrix[2][0] = 1.0f;
                    matrix[3][1] = 1.0f;
                    break;
                }
                case 3: {
                    matrix = new float[3][3];
                    matrix[0][0] = 1.0f;
                    matrix[1][1] = 1.0f;
                    matrix[2][2] = 1.0f;
                    break;
                }
                default: {
                    matrix = new float[4][srcSM.getNumBands()];
                    matrix[0][0] = 1.0f;
                    matrix[1][1] = 1.0f;
                    matrix[2][2] = 1.0f;
                    matrix[3][3] = 1.0f;
                    break;
                }
            }
            final Raster srcRas = src.getData(wr.getBounds());
            final BandCombineOp op = new BandCombineOp(matrix, null);
            op.filter(srcRas, wr);
            return wr;
        }
        if (srcCM.getColorSpace() == ColorSpace.getInstance(1003)) {
            try {
                float[][] matrix = null;
                switch (srcSM.getNumBands()) {
                    case 1: {
                        matrix = new float[3][1];
                        matrix[0][0] = 1.0f;
                        matrix[1][0] = 1.0f;
                        matrix[2][0] = 1.0f;
                        break;
                    }
                    default: {
                        matrix = new float[4][2];
                        matrix[0][0] = 1.0f;
                        matrix[1][0] = 1.0f;
                        matrix[2][0] = 1.0f;
                        matrix[3][1] = 1.0f;
                        break;
                    }
                }
                final Raster srcRas = src.getData(wr.getBounds());
                final BandCombineOp op = new BandCombineOp(matrix, null);
                op.filter(srcRas, wr);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
            return wr;
        }
        final ColorModel dstCM = this.getColorModel();
        if (srcCM.getColorSpace() == dstCM.getColorSpace()) {
            if (is_INT_PACK_COMP(srcSM)) {
                src.copyData(wr);
            }
            else {
                GraphicsUtil.copyData(src.getData(wr.getBounds()), wr);
            }
            return wr;
        }
        final Raster srcRas = src.getData(wr.getBounds());
        final WritableRaster srcWr = (WritableRaster)srcRas;
        ColorModel srcBICM = srcCM;
        if (srcCM.hasAlpha()) {
            srcBICM = GraphicsUtil.coerceData(srcWr, srcCM, false);
        }
        final BufferedImage srcBI = new BufferedImage(srcBICM, srcWr.createWritableTranslatedChild(0, 0), false, null);
        final ColorConvertOp op2 = new ColorConvertOp(dstCM.getColorSpace(), null);
        final BufferedImage dstBI = op2.filter(srcBI, null);
        final WritableRaster wr2 = wr.createWritableTranslatedChild(0, 0);
        for (int i = 0; i < dstCM.getColorSpace().getNumComponents(); ++i) {
            AbstractRed.copyBand(dstBI.getRaster(), i, wr2, i);
        }
        if (dstCM.hasAlpha()) {
            AbstractRed.copyBand(srcWr, srcSM.getNumBands() - 1, wr, this.getSampleModel().getNumBands() - 1);
        }
        return wr;
    }
    
    protected static ColorModel fixColorModel(final CachableRed src) {
        final ColorModel cm = src.getColorModel();
        if (cm != null) {
            if (cm.hasAlpha()) {
                return GraphicsUtil.sRGB_Unpre;
            }
            return GraphicsUtil.sRGB;
        }
        else {
            final SampleModel sm = src.getSampleModel();
            switch (sm.getNumBands()) {
                case 1: {
                    return GraphicsUtil.sRGB;
                }
                case 2: {
                    return GraphicsUtil.sRGB_Unpre;
                }
                case 3: {
                    return GraphicsUtil.sRGB;
                }
                default: {
                    return GraphicsUtil.sRGB_Unpre;
                }
            }
        }
    }
    
    protected static SampleModel fixSampleModel(final CachableRed src) {
        final SampleModel sm = src.getSampleModel();
        final ColorModel cm = src.getColorModel();
        boolean alpha = false;
        if (cm != null) {
            alpha = cm.hasAlpha();
        }
        else {
            switch (sm.getNumBands()) {
                case 1:
                case 3: {
                    alpha = false;
                    break;
                }
                default: {
                    alpha = true;
                    break;
                }
            }
        }
        if (alpha) {
            return new SinglePixelPackedSampleModel(3, sm.getWidth(), sm.getHeight(), new int[] { 16711680, 65280, 255, -16777216 });
        }
        return new SinglePixelPackedSampleModel(3, sm.getWidth(), sm.getHeight(), new int[] { 16711680, 65280, 255 });
    }
    
    static {
        linearToSRGBLut = new int[256];
        final double scale = 0.00392156862745098;
        final double exp = 0.4166666666666667;
        for (int i = 0; i < 256; ++i) {
            double value = i * 0.00392156862745098;
            if (value <= 0.0031308) {
                value *= 12.92;
            }
            else {
                value = 1.055 * Math.pow(value, 0.4166666666666667) - 0.055;
            }
            Any2sRGBRed.linearToSRGBLut[i] = (int)Math.round(value * 255.0);
        }
    }
}
