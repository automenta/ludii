// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.DirectColorModel;
import java.awt.Graphics2D;
import java.awt.image.RenderedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.Point;
import java.awt.image.BandedSampleModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent;
import java.awt.image.ColorModel;
import java.awt.color.ColorSpace;

public class ProfileRed extends AbstractRed
{
    private static final ColorSpace sRGBCS;
    private static final ColorModel sRGBCM;
    private ICCColorSpaceWithIntent colorSpace;
    
    public ProfileRed(final CachableRed src, final ICCColorSpaceWithIntent colorSpace) {
        this.colorSpace = colorSpace;
        this.init(src, src.getBounds(), ProfileRed.sRGBCM, ProfileRed.sRGBCM.createCompatibleSampleModel(src.getWidth(), src.getHeight()), src.getTileGridXOffset(), src.getTileGridYOffset(), null);
    }
    
    public CachableRed getSource() {
        return this.getSources().get(0);
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster argbWR) {
        try {
            RenderedImage img = this.getSource();
            ColorModel imgCM = img.getColorModel();
            final ColorSpace imgCS = imgCM.getColorSpace();
            final int nImageComponents = imgCS.getNumComponents();
            final int nProfileComponents = this.colorSpace.getNumComponents();
            if (nImageComponents != nProfileComponents) {
                System.err.println("Input image and associated color profile have mismatching number of color components: conversion is not possible");
                return argbWR;
            }
            final int w = argbWR.getWidth();
            final int h = argbWR.getHeight();
            final int minX = argbWR.getMinX();
            final int minY = argbWR.getMinY();
            WritableRaster srcWR = imgCM.createCompatibleWritableRaster(w, h);
            srcWR = srcWR.createWritableTranslatedChild(minX, minY);
            img.copyData(srcWR);
            if (!(imgCM instanceof ComponentColorModel) || !(img.getSampleModel() instanceof BandedSampleModel) || (imgCM.hasAlpha() && imgCM.isAlphaPremultiplied())) {
                final ComponentColorModel imgCompCM = new ComponentColorModel(imgCS, imgCM.getComponentSize(), imgCM.hasAlpha(), false, imgCM.getTransparency(), 0);
                final WritableRaster wr = Raster.createBandedRaster(0, argbWR.getWidth(), argbWR.getHeight(), imgCompCM.getNumComponents(), new Point(0, 0));
                final BufferedImage imgComp = new BufferedImage(imgCompCM, wr, imgCompCM.isAlphaPremultiplied(), null);
                final BufferedImage srcImg = new BufferedImage(imgCM, srcWR.createWritableTranslatedChild(0, 0), imgCM.isAlphaPremultiplied(), null);
                final Graphics2D g = imgComp.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                g.drawImage(srcImg, 0, 0, null);
                img = imgComp;
                imgCM = imgCompCM;
                srcWR = wr.createWritableTranslatedChild(minX, minY);
            }
            final ComponentColorModel newCM = new ComponentColorModel(this.colorSpace, imgCM.getComponentSize(), false, false, 1, 0);
            final DataBufferByte data = (DataBufferByte)srcWR.getDataBuffer();
            srcWR = Raster.createBandedRaster(data, argbWR.getWidth(), argbWR.getHeight(), argbWR.getWidth(), new int[] { 0, 1, 2 }, new int[] { 0, 0, 0 }, new Point(0, 0));
            final BufferedImage newImg = new BufferedImage(newCM, srcWR, newCM.isAlphaPremultiplied(), null);
            ComponentColorModel sRGBCompCM = new ComponentColorModel(ColorSpace.getInstance(1000), new int[] { 8, 8, 8 }, false, false, 1, 0);
            final WritableRaster wr2 = Raster.createBandedRaster(0, argbWR.getWidth(), argbWR.getHeight(), sRGBCompCM.getNumComponents(), new Point(0, 0));
            BufferedImage sRGBImage = new BufferedImage(sRGBCompCM, wr2, false, null);
            final ColorConvertOp colorConvertOp = new ColorConvertOp(null);
            colorConvertOp.filter(newImg, sRGBImage);
            if (imgCM.hasAlpha()) {
                final DataBufferByte rgbData = (DataBufferByte)wr2.getDataBuffer();
                final byte[][] imgBanks = data.getBankData();
                final byte[][] rgbBanks = rgbData.getBankData();
                final byte[][] argbBanks = { rgbBanks[0], rgbBanks[1], rgbBanks[2], imgBanks[3] };
                final DataBufferByte argbData = new DataBufferByte(argbBanks, imgBanks[0].length);
                srcWR = Raster.createBandedRaster(argbData, argbWR.getWidth(), argbWR.getHeight(), argbWR.getWidth(), new int[] { 0, 1, 2, 3 }, new int[] { 0, 0, 0, 0 }, new Point(0, 0));
                sRGBCompCM = new ComponentColorModel(ColorSpace.getInstance(1000), new int[] { 8, 8, 8, 8 }, true, false, 3, 0);
                sRGBImage = new BufferedImage(sRGBCompCM, srcWR, false, null);
            }
            final BufferedImage result = new BufferedImage(ProfileRed.sRGBCM, argbWR.createWritableTranslatedChild(0, 0), false, null);
            final Graphics2D g2 = result.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2.drawImage(sRGBImage, 0, 0, null);
            g2.dispose();
            return argbWR;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
    
    static {
        sRGBCS = ColorSpace.getInstance(1000);
        sRGBCM = new DirectColorModel(ProfileRed.sRGBCS, 32, 16711680, 65280, 255, -16777216, false, 3);
    }
}
