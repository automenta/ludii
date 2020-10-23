// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.color.ColorSpace;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.ComponentSampleModel;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.Point;
import java.awt.image.WritableRaster;
import java.util.Map;
import java.awt.image.SampleModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ColorModel;

public class FormatRed extends AbstractRed
{
    public static CachableRed construct(final CachableRed src, final ColorModel cm) {
        final ColorModel srcCM = src.getColorModel();
        if (cm.hasAlpha() != srcCM.hasAlpha() || cm.isAlphaPremultiplied() != srcCM.isAlphaPremultiplied()) {
            return new FormatRed(src, cm);
        }
        if (cm.getNumComponents() != srcCM.getNumComponents()) {
            throw new IllegalArgumentException("Incompatible ColorModel given");
        }
        if (srcCM instanceof ComponentColorModel && cm instanceof ComponentColorModel) {
            return src;
        }
        if (srcCM instanceof DirectColorModel && cm instanceof DirectColorModel) {
            return src;
        }
        return new FormatRed(src, cm);
    }
    
    public FormatRed(final CachableRed cr, final SampleModel sm) {
        super(cr, cr.getBounds(), makeColorModel(cr, sm), sm, cr.getTileGridXOffset(), cr.getTileGridYOffset(), null);
    }
    
    public FormatRed(final CachableRed cr, final ColorModel cm) {
        super(cr, cr.getBounds(), cm, makeSampleModel(cr, cm), cr.getTileGridXOffset(), cr.getTileGridYOffset(), null);
    }
    
    public CachableRed getSource() {
        return this.getSources().get(0);
    }
    
    @Override
    public Object getProperty(final String name) {
        return this.getSource().getProperty(name);
    }
    
    @Override
    public String[] getPropertyNames() {
        return this.getSource().getPropertyNames();
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        final ColorModel cm = this.getColorModel();
        final CachableRed cr = this.getSource();
        final ColorModel srcCM = cr.getColorModel();
        SampleModel srcSM = cr.getSampleModel();
        srcSM = srcSM.createCompatibleSampleModel(wr.getWidth(), wr.getHeight());
        final WritableRaster srcWR = Raster.createWritableRaster(srcSM, new Point(wr.getMinX(), wr.getMinY()));
        this.getSource().copyData(srcWR);
        final BufferedImage srcBI = new BufferedImage(srcCM, srcWR.createWritableTranslatedChild(0, 0), srcCM.isAlphaPremultiplied(), null);
        final BufferedImage dstBI = new BufferedImage(cm, wr.createWritableTranslatedChild(0, 0), cm.isAlphaPremultiplied(), null);
        GraphicsUtil.copyData(srcBI, dstBI);
        return wr;
    }
    
    public static SampleModel makeSampleModel(final CachableRed cr, final ColorModel cm) {
        final SampleModel srcSM = cr.getSampleModel();
        return cm.createCompatibleSampleModel(srcSM.getWidth(), srcSM.getHeight());
    }
    
    public static ColorModel makeColorModel(final CachableRed cr, final SampleModel sm) {
        final ColorModel srcCM = cr.getColorModel();
        final ColorSpace cs = srcCM.getColorSpace();
        final int bands = sm.getNumBands();
        final int dt = sm.getDataType();
        int bits = 0;
        switch (dt) {
            case 0: {
                bits = 8;
                break;
            }
            case 2: {
                bits = 16;
                break;
            }
            case 1: {
                bits = 16;
                break;
            }
            case 3: {
                bits = 32;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported DataBuffer type: " + dt);
            }
        }
        boolean hasAlpha = srcCM.hasAlpha();
        if (hasAlpha) {
            if (bands == srcCM.getNumComponents() - 1) {
                hasAlpha = false;
            }
            else if (bands != srcCM.getNumComponents()) {
                throw new IllegalArgumentException("Incompatible number of bands in and out");
            }
        }
        else if (bands == srcCM.getNumComponents() + 1) {
            hasAlpha = true;
        }
        else if (bands != srcCM.getNumComponents()) {
            throw new IllegalArgumentException("Incompatible number of bands in and out");
        }
        boolean preMult = srcCM.isAlphaPremultiplied();
        if (!hasAlpha) {
            preMult = false;
        }
        if (sm instanceof ComponentSampleModel) {
            final int[] bitsPer = new int[bands];
            for (int i = 0; i < bands; ++i) {
                bitsPer[i] = bits;
            }
            return new ComponentColorModel(cs, bitsPer, hasAlpha, preMult, hasAlpha ? 3 : 1, dt);
        }
        if (!(sm instanceof SinglePixelPackedSampleModel)) {
            throw new IllegalArgumentException("Unsupported SampleModel Type");
        }
        final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm;
        final int[] masks = sppsm.getBitMasks();
        if (bands == 4) {
            return new DirectColorModel(cs, bits, masks[0], masks[1], masks[2], masks[3], preMult, dt);
        }
        if (bands == 3) {
            return new DirectColorModel(cs, bits, masks[0], masks[1], masks[2], 0, preMult, dt);
        }
        throw new IllegalArgumentException("Incompatible number of bands out for ColorModel");
    }
}
