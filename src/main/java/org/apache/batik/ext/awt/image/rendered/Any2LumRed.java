// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.awt.image.ColorConvertOp;
import java.awt.image.ComponentColorModel;
import java.awt.color.ColorSpace;
import java.awt.image.Raster;
import java.awt.Point;
import java.awt.image.PixelInterleavedSampleModel;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.RenderingHints;
import java.awt.image.BandCombineOp;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.ColorSpaceHintKey;
import java.util.Map;

public class Any2LumRed extends AbstractRed
{
    boolean isColorConvertOpAplhaSupported;
    
    public Any2LumRed(final CachableRed src) {
        super(src, src.getBounds(), fixColorModel(src), fixSampleModel(src), src.getTileGridXOffset(), src.getTileGridYOffset(), null);
        this.isColorConvertOpAplhaSupported = getColorConvertOpAplhaSupported();
        this.props.put("org.apache.batik.gvt.filter.Colorspace", ColorSpaceHintKey.VALUE_COLORSPACE_GREY);
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        final CachableRed src = this.getSources().get(0);
        final SampleModel sm = src.getSampleModel();
        final ColorModel srcCM = src.getColorModel();
        final Raster srcRas = src.getData(wr.getBounds());
        if (srcCM == null) {
            float[][] matrix = null;
            if (sm.getNumBands() == 2) {
                matrix = new float[2][2];
                matrix[0][0] = 1.0f;
                matrix[1][1] = 1.0f;
            }
            else {
                matrix = new float[sm.getNumBands()][1];
                matrix[0][0] = 1.0f;
            }
            final BandCombineOp op = new BandCombineOp(matrix, null);
            op.filter(srcRas, wr);
        }
        else {
            final WritableRaster srcWr = (WritableRaster)srcRas;
            if (srcCM.hasAlpha()) {
                GraphicsUtil.coerceData(srcWr, srcCM, false);
            }
            final BufferedImage srcBI = new BufferedImage(srcCM, srcWr.createWritableTranslatedChild(0, 0), false, null);
            final ColorModel dstCM = this.getColorModel();
            BufferedImage dstBI;
            if (dstCM.hasAlpha() && !this.isColorConvertOpAplhaSupported) {
                final PixelInterleavedSampleModel dstSM = (PixelInterleavedSampleModel)wr.getSampleModel();
                final SampleModel smna = new PixelInterleavedSampleModel(dstSM.getDataType(), dstSM.getWidth(), dstSM.getHeight(), dstSM.getPixelStride(), dstSM.getScanlineStride(), new int[] { 0 });
                WritableRaster dstWr = Raster.createWritableRaster(smna, wr.getDataBuffer(), new Point(0, 0));
                dstWr = dstWr.createWritableChild(wr.getMinX() - wr.getSampleModelTranslateX(), wr.getMinY() - wr.getSampleModelTranslateY(), wr.getWidth(), wr.getHeight(), 0, 0, null);
                final ColorModel cmna = new ComponentColorModel(ColorSpace.getInstance(1003), new int[] { 8 }, false, false, 1, 0);
                dstBI = new BufferedImage(cmna, dstWr, false, null);
            }
            else {
                dstBI = new BufferedImage(dstCM, wr.createWritableTranslatedChild(0, 0), dstCM.isAlphaPremultiplied(), null);
            }
            final ColorConvertOp op2 = new ColorConvertOp(null);
            op2.filter(srcBI, dstBI);
            if (dstCM.hasAlpha()) {
                AbstractRed.copyBand(srcWr, sm.getNumBands() - 1, wr, this.getSampleModel().getNumBands() - 1);
                if (dstCM.isAlphaPremultiplied()) {
                    GraphicsUtil.multiplyAlpha(wr);
                }
            }
        }
        return wr;
    }
    
    protected static ColorModel fixColorModel(final CachableRed src) {
        final ColorModel cm = src.getColorModel();
        if (cm != null) {
            if (cm.hasAlpha()) {
                return new ComponentColorModel(ColorSpace.getInstance(1003), new int[] { 8, 8 }, true, cm.isAlphaPremultiplied(), 3, 0);
            }
            return new ComponentColorModel(ColorSpace.getInstance(1003), new int[] { 8 }, false, false, 1, 0);
        }
        else {
            final SampleModel sm = src.getSampleModel();
            if (sm.getNumBands() == 2) {
                return new ComponentColorModel(ColorSpace.getInstance(1003), new int[] { 8, 8 }, true, true, 3, 0);
            }
            return new ComponentColorModel(ColorSpace.getInstance(1003), new int[] { 8 }, false, false, 1, 0);
        }
    }
    
    protected static SampleModel fixSampleModel(final CachableRed src) {
        final SampleModel sm = src.getSampleModel();
        final int width = sm.getWidth();
        final int height = sm.getHeight();
        final ColorModel cm = src.getColorModel();
        if (cm != null) {
            if (cm.hasAlpha()) {
                return new PixelInterleavedSampleModel(0, width, height, 2, 2 * width, new int[] { 0, 1 });
            }
            return new PixelInterleavedSampleModel(0, width, height, 1, width, new int[] { 0 });
        }
        else {
            if (sm.getNumBands() == 2) {
                return new PixelInterleavedSampleModel(0, width, height, 2, 2 * width, new int[] { 0, 1 });
            }
            return new PixelInterleavedSampleModel(0, width, height, 1, width, new int[] { 0 });
        }
    }
    
    protected static boolean getColorConvertOpAplhaSupported() {
        final int size = 50;
        final BufferedImage srcImage = new BufferedImage(size, size, 2);
        final Graphics2D srcGraphics = srcImage.createGraphics();
        srcGraphics.setColor(Color.red);
        srcGraphics.fillRect(0, 0, size, size);
        srcGraphics.dispose();
        final BufferedImage dstImage = new BufferedImage(size, size, 2);
        final Graphics2D dstGraphics = dstImage.createGraphics();
        dstGraphics.setComposite(AlphaComposite.Clear);
        dstGraphics.fillRect(0, 0, size, size);
        dstGraphics.dispose();
        final ColorSpace grayColorSpace = ColorSpace.getInstance(1003);
        final ColorConvertOp op = new ColorConvertOp(grayColorSpace, null);
        op.filter(srcImage, dstImage);
        return getAlpha(srcImage) == getAlpha(dstImage);
    }
    
    protected static int getAlpha(final BufferedImage bufferedImage) {
        final int x = bufferedImage.getWidth() / 2;
        final int y = bufferedImage.getHeight() / 2;
        return 0xFF & bufferedImage.getRGB(x, y) >> 24;
    }
}
