// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image;

import java.awt.image.DirectColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import org.apache.batik.ext.awt.image.rendered.RenderedImageCachableRed;
import org.apache.batik.ext.awt.image.rendered.Any2sRGBRed;
import org.apache.batik.ext.awt.image.rendered.Any2LsRGBRed;
import java.awt.GraphicsDevice;
import java.awt.GraphicsConfiguration;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import org.apache.batik.ext.awt.image.renderable.PaintRable;
import java.awt.RenderingHints;
import java.util.Map;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.SampleModel;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.Point;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.Composite;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.ext.awt.image.rendered.FormatRed;
import java.awt.image.ImageObserver;
import java.awt.Image;
import org.apache.batik.ext.awt.image.rendered.BufferedImageCachableRed;
import java.awt.color.ColorSpace;
import org.apache.batik.ext.awt.image.rendered.TranslateRed;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import java.awt.image.RenderedImage;
import java.awt.Graphics2D;
import java.awt.image.ColorModel;
import java.awt.geom.AffineTransform;

public class GraphicsUtil
{
    public static AffineTransform IDENTITY;
    public static final boolean WARN_DESTINATION;
    public static final ColorModel Linear_sRGB;
    public static final ColorModel Linear_sRGB_Pre;
    public static final ColorModel Linear_sRGB_Unpre;
    public static final ColorModel sRGB;
    public static final ColorModel sRGB_Pre;
    public static final ColorModel sRGB_Unpre;
    
    public static void drawImage(final Graphics2D g2d, final RenderedImage ri) {
        drawImage(g2d, wrap(ri));
    }
    
    public static void drawImage(final Graphics2D g2d, CachableRed cr) {
        AffineTransform at = null;
        while (true) {
            if (cr instanceof AffineRed) {
                final AffineRed ar = (AffineRed)cr;
                if (at == null) {
                    at = ar.getTransform();
                }
                else {
                    at.concatenate(ar.getTransform());
                }
                cr = ar.getSource();
            }
            else {
                if (!(cr instanceof TranslateRed)) {
                    break;
                }
                final TranslateRed tr = (TranslateRed)cr;
                final int dx = tr.getDeltaX();
                final int dy = tr.getDeltaY();
                if (at == null) {
                    at = AffineTransform.getTranslateInstance(dx, dy);
                }
                else {
                    at.translate(dx, dy);
                }
                cr = tr.getSource();
            }
        }
        final AffineTransform g2dAt = g2d.getTransform();
        if (at == null || at.isIdentity()) {
            at = g2dAt;
        }
        else {
            at.preConcatenate(g2dAt);
        }
        ColorModel srcCM = cr.getColorModel();
        final ColorModel g2dCM = getDestinationColorModel(g2d);
        ColorSpace g2dCS = null;
        if (g2dCM != null) {
            g2dCS = g2dCM.getColorSpace();
        }
        if (g2dCS == null) {
            g2dCS = ColorSpace.getInstance(1000);
        }
        ColorModel drawCM = g2dCM;
        if (g2dCM == null || !g2dCM.hasAlpha()) {
            drawCM = GraphicsUtil.sRGB_Unpre;
        }
        if (cr instanceof BufferedImageCachableRed && g2dCS.equals(srcCM.getColorSpace()) && drawCM.equals(srcCM)) {
            g2d.setTransform(at);
            final BufferedImageCachableRed bicr = (BufferedImageCachableRed)cr;
            g2d.drawImage(bicr.getBufferedImage(), bicr.getMinX(), bicr.getMinY(), null);
            g2d.setTransform(g2dAt);
            return;
        }
        final double determinant = at.getDeterminant();
        if (!at.isIdentity() && determinant <= 1.0) {
            if (at.getType() != 1) {
                cr = new AffineRed(cr, at, g2d.getRenderingHints());
            }
            else {
                final int xloc = cr.getMinX() + (int)at.getTranslateX();
                final int yloc = cr.getMinY() + (int)at.getTranslateY();
                cr = new TranslateRed(cr, xloc, yloc);
            }
        }
        if (g2dCS != srcCM.getColorSpace()) {
            if (g2dCS == ColorSpace.getInstance(1000)) {
                cr = convertTosRGB(cr);
            }
            else if (g2dCS == ColorSpace.getInstance(1004)) {
                cr = convertToLsRGB(cr);
            }
        }
        srcCM = cr.getColorModel();
        if (!drawCM.equals(srcCM)) {
            cr = FormatRed.construct(cr, drawCM);
        }
        if (!at.isIdentity() && determinant > 1.0) {
            cr = new AffineRed(cr, at, g2d.getRenderingHints());
        }
        g2d.setTransform(GraphicsUtil.IDENTITY);
        final Composite g2dComposite = g2d.getComposite();
        if (g2d.getRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING) == "Printing" && SVGComposite.OVER.equals(g2dComposite)) {
            g2d.setComposite(SVGComposite.OVER);
        }
        final Rectangle crR = cr.getBounds();
        Shape clip = g2d.getClip();
        try {
            Rectangle clipR;
            if (clip == null) {
                clip = crR;
                clipR = crR;
            }
            else {
                clipR = clip.getBounds();
                if (!clipR.intersects(crR)) {
                    return;
                }
                clipR = clipR.intersection(crR);
            }
            final Rectangle gcR = getDestinationBounds(g2d);
            if (gcR != null) {
                if (!clipR.intersects(gcR)) {
                    return;
                }
                clipR = clipR.intersection(gcR);
            }
            boolean useDrawRenderedImage = false;
            srcCM = cr.getColorModel();
            final SampleModel srcSM = cr.getSampleModel();
            if (srcSM.getWidth() * srcSM.getHeight() >= clipR.width * clipR.height) {
                useDrawRenderedImage = true;
            }
            final Object atpHint = g2d.getRenderingHint(RenderingHintsKeyExt.KEY_AVOID_TILE_PAINTING);
            if (atpHint == RenderingHintsKeyExt.VALUE_AVOID_TILE_PAINTING_ON) {
                useDrawRenderedImage = true;
            }
            if (atpHint == RenderingHintsKeyExt.VALUE_AVOID_TILE_PAINTING_OFF) {
                useDrawRenderedImage = false;
            }
            if (useDrawRenderedImage) {
                final Raster r = cr.getData(clipR);
                final WritableRaster wr = ((WritableRaster)r).createWritableChild(clipR.x, clipR.y, clipR.width, clipR.height, 0, 0, null);
                final BufferedImage bi = new BufferedImage(srcCM, wr, srcCM.isAlphaPremultiplied(), null);
                g2d.drawImage(bi, clipR.x, clipR.y, null);
            }
            else {
                final WritableRaster wr = Raster.createWritableRaster(srcSM, new Point(0, 0));
                final BufferedImage bi2 = new BufferedImage(srcCM, wr, srcCM.isAlphaPremultiplied(), null);
                int xt0 = cr.getMinTileX();
                final int xt2 = xt0 + cr.getNumXTiles();
                int yt0 = cr.getMinTileY();
                final int yt2 = yt0 + cr.getNumYTiles();
                final int tw = srcSM.getWidth();
                final int th = srcSM.getHeight();
                final Rectangle tR = new Rectangle(0, 0, tw, th);
                final Rectangle iR = new Rectangle(0, 0, 0, 0);
                int yloc2 = yt0 * th + cr.getTileGridYOffset();
                int skip = (clipR.y - yloc2) / th;
                if (skip < 0) {
                    skip = 0;
                }
                yt0 += skip;
                int xloc2 = xt0 * tw + cr.getTileGridXOffset();
                skip = (clipR.x - xloc2) / tw;
                if (skip < 0) {
                    skip = 0;
                }
                xt0 += skip;
                final int endX = clipR.x + clipR.width - 1;
                final int endY = clipR.y + clipR.height - 1;
                yloc2 = yt0 * th + cr.getTileGridYOffset();
                final int minX = xt0 * tw + cr.getTileGridXOffset();
                int xStep = tw;
                xloc2 = minX;
                for (int y = yt0; y < yt2; ++y, yloc2 += th) {
                    if (yloc2 > endY) {
                        break;
                    }
                    for (int x = xt0; x < xt2 && xloc2 >= minX && xloc2 <= endX; ++x, xloc2 += xStep) {
                        tR.x = xloc2;
                        tR.y = yloc2;
                        Rectangle2D.intersect(crR, tR, iR);
                        final WritableRaster twr = wr.createWritableChild(0, 0, iR.width, iR.height, iR.x, iR.y, null);
                        cr.copyData(twr);
                        final BufferedImage subBI = bi2.getSubimage(0, 0, iR.width, iR.height);
                        g2d.drawImage(subBI, iR.x, iR.y, null);
                    }
                    xStep = -xStep;
                    xloc2 += xStep;
                }
            }
        }
        finally {
            g2d.setTransform(g2dAt);
            g2d.setComposite(g2dComposite);
        }
    }
    
    public static void drawImage(final Graphics2D g2d, final RenderableImage filter, final RenderContext rc) {
        final AffineTransform origDev = g2d.getTransform();
        final Shape origClip = g2d.getClip();
        final RenderingHints origRH = g2d.getRenderingHints();
        final Shape clip = rc.getAreaOfInterest();
        if (clip != null) {
            g2d.clip(clip);
        }
        g2d.transform(rc.getTransform());
        g2d.setRenderingHints(rc.getRenderingHints());
        drawImage(g2d, filter);
        g2d.setTransform(origDev);
        g2d.setClip(origClip);
        g2d.setRenderingHints(origRH);
    }
    
    public static void drawImage(final Graphics2D g2d, final RenderableImage filter) {
        if (filter instanceof PaintRable) {
            final PaintRable pr = (PaintRable)filter;
            if (pr.paintRable(g2d)) {
                return;
            }
        }
        final AffineTransform at = g2d.getTransform();
        final RenderedImage ri = filter.createRendering(new RenderContext(at, g2d.getClip(), g2d.getRenderingHints()));
        if (ri == null) {
            return;
        }
        g2d.setTransform(GraphicsUtil.IDENTITY);
        drawImage(g2d, wrap(ri));
        g2d.setTransform(at);
    }
    
    public static Graphics2D createGraphics(final BufferedImage bi, final RenderingHints hints) {
        final Graphics2D g2d = bi.createGraphics();
        if (hints != null) {
            g2d.addRenderingHints(hints);
        }
        g2d.setRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE, new WeakReference(bi));
        g2d.clip(new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
        return g2d;
    }
    
    public static Graphics2D createGraphics(final BufferedImage bi) {
        final Graphics2D g2d = bi.createGraphics();
        g2d.setRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE, new WeakReference(bi));
        g2d.clip(new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
        return g2d;
    }
    
    public static BufferedImage getDestination(final Graphics2D g2d) {
        final Object o = g2d.getRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE);
        if (o != null) {
            return ((Reference)o).get();
        }
        final GraphicsConfiguration gc = g2d.getDeviceConfiguration();
        if (gc == null) {
            return null;
        }
        final GraphicsDevice gd = gc.getDevice();
        if (GraphicsUtil.WARN_DESTINATION && gd.getType() == 2 && g2d.getRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING) != "Printing") {
            System.err.println("Graphics2D from BufferedImage lacks BUFFERED_IMAGE hint");
        }
        return null;
    }
    
    public static ColorModel getDestinationColorModel(final Graphics2D g2d) {
        final BufferedImage bi = getDestination(g2d);
        if (bi != null) {
            return bi.getColorModel();
        }
        final GraphicsConfiguration gc = g2d.getDeviceConfiguration();
        if (gc == null) {
            return null;
        }
        if (gc.getDevice().getType() != 2) {
            return gc.getColorModel();
        }
        if (g2d.getRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING) == "Printing") {
            return GraphicsUtil.sRGB_Unpre;
        }
        return null;
    }
    
    public static ColorSpace getDestinationColorSpace(final Graphics2D g2d) {
        final ColorModel cm = getDestinationColorModel(g2d);
        if (cm != null) {
            return cm.getColorSpace();
        }
        return null;
    }
    
    public static Rectangle getDestinationBounds(final Graphics2D g2d) {
        final BufferedImage bi = getDestination(g2d);
        if (bi != null) {
            return new Rectangle(0, 0, bi.getWidth(), bi.getHeight());
        }
        final GraphicsConfiguration gc = g2d.getDeviceConfiguration();
        if (gc == null) {
            return null;
        }
        if (gc.getDevice().getType() == 2) {
            return null;
        }
        return null;
    }
    
    public static ColorModel makeLinear_sRGBCM(final boolean premult) {
        return premult ? GraphicsUtil.Linear_sRGB_Pre : GraphicsUtil.Linear_sRGB_Unpre;
    }
    
    public static BufferedImage makeLinearBufferedImage(final int width, final int height, final boolean premult) {
        final ColorModel cm = makeLinear_sRGBCM(premult);
        final WritableRaster wr = cm.createCompatibleWritableRaster(width, height);
        return new BufferedImage(cm, wr, premult, null);
    }
    
    public static CachableRed convertToLsRGB(final CachableRed src) {
        final ColorModel cm = src.getColorModel();
        final ColorSpace cs = cm.getColorSpace();
        if (cs == ColorSpace.getInstance(1004)) {
            return src;
        }
        return new Any2LsRGBRed(src);
    }
    
    public static CachableRed convertTosRGB(final CachableRed src) {
        final ColorModel cm = src.getColorModel();
        final ColorSpace cs = cm.getColorSpace();
        if (cs == ColorSpace.getInstance(1000)) {
            return src;
        }
        return new Any2sRGBRed(src);
    }
    
    public static CachableRed wrap(final RenderedImage ri) {
        if (ri instanceof CachableRed) {
            return (CachableRed)ri;
        }
        if (ri instanceof BufferedImage) {
            return new BufferedImageCachableRed((BufferedImage)ri);
        }
        return new RenderedImageCachableRed(ri);
    }
    
    public static void copyData_INT_PACK(final Raster src, final WritableRaster dst) {
        int x0 = dst.getMinX();
        if (x0 < src.getMinX()) {
            x0 = src.getMinX();
        }
        int y0 = dst.getMinY();
        if (y0 < src.getMinY()) {
            y0 = src.getMinY();
        }
        int x2 = dst.getMinX() + dst.getWidth() - 1;
        if (x2 > src.getMinX() + src.getWidth() - 1) {
            x2 = src.getMinX() + src.getWidth() - 1;
        }
        int y2 = dst.getMinY() + dst.getHeight() - 1;
        if (y2 > src.getMinY() + src.getHeight() - 1) {
            y2 = src.getMinY() + src.getHeight() - 1;
        }
        final int width = x2 - x0 + 1;
        final int height = y2 - y0 + 1;
        final SinglePixelPackedSampleModel srcSPPSM = (SinglePixelPackedSampleModel)src.getSampleModel();
        final int srcScanStride = srcSPPSM.getScanlineStride();
        final DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        final int[] srcPixels = srcDB.getBankData()[0];
        final int srcBase = srcDB.getOffset() + srcSPPSM.getOffset(x0 - src.getSampleModelTranslateX(), y0 - src.getSampleModelTranslateY());
        final SinglePixelPackedSampleModel dstSPPSM = (SinglePixelPackedSampleModel)dst.getSampleModel();
        final int dstScanStride = dstSPPSM.getScanlineStride();
        final DataBufferInt dstDB = (DataBufferInt)dst.getDataBuffer();
        final int[] dstPixels = dstDB.getBankData()[0];
        final int dstBase = dstDB.getOffset() + dstSPPSM.getOffset(x0 - dst.getSampleModelTranslateX(), y0 - dst.getSampleModelTranslateY());
        if (srcScanStride == dstScanStride && srcScanStride == width) {
            System.arraycopy(srcPixels, srcBase, dstPixels, dstBase, width * height);
        }
        else if (width > 128) {
            int srcSP = srcBase;
            int dstSP = dstBase;
            for (int y3 = 0; y3 < height; ++y3) {
                System.arraycopy(srcPixels, srcSP, dstPixels, dstSP, width);
                srcSP += srcScanStride;
                dstSP += dstScanStride;
            }
        }
        else {
            for (int y4 = 0; y4 < height; ++y4) {
                int srcSP2 = srcBase + y4 * srcScanStride;
                int dstSP2 = dstBase + y4 * dstScanStride;
                for (int x3 = 0; x3 < width; ++x3) {
                    dstPixels[dstSP2++] = srcPixels[srcSP2++];
                }
            }
        }
    }
    
    public static void copyData_FALLBACK(final Raster src, final WritableRaster dst) {
        int x0 = dst.getMinX();
        if (x0 < src.getMinX()) {
            x0 = src.getMinX();
        }
        int y0 = dst.getMinY();
        if (y0 < src.getMinY()) {
            y0 = src.getMinY();
        }
        int x2 = dst.getMinX() + dst.getWidth() - 1;
        if (x2 > src.getMinX() + src.getWidth() - 1) {
            x2 = src.getMinX() + src.getWidth() - 1;
        }
        int y2 = dst.getMinY() + dst.getHeight() - 1;
        if (y2 > src.getMinY() + src.getHeight() - 1) {
            y2 = src.getMinY() + src.getHeight() - 1;
        }
        final int width = x2 - x0 + 1;
        int[] data = null;
        for (int y3 = y0; y3 <= y2; ++y3) {
            data = src.getPixels(x0, y3, width, 1, data);
            dst.setPixels(x0, y3, width, 1, data);
        }
    }
    
    public static void copyData(final Raster src, final WritableRaster dst) {
        if (is_INT_PACK_Data(src.getSampleModel(), false) && is_INT_PACK_Data(dst.getSampleModel(), false)) {
            copyData_INT_PACK(src, dst);
            return;
        }
        copyData_FALLBACK(src, dst);
    }
    
    public static WritableRaster copyRaster(final Raster ras) {
        return copyRaster(ras, ras.getMinX(), ras.getMinY());
    }
    
    public static WritableRaster copyRaster(final Raster ras, final int minX, final int minY) {
        WritableRaster ret = Raster.createWritableRaster(ras.getSampleModel(), new Point(0, 0));
        ret = ret.createWritableChild(ras.getMinX() - ras.getSampleModelTranslateX(), ras.getMinY() - ras.getSampleModelTranslateY(), ras.getWidth(), ras.getHeight(), minX, minY, null);
        final DataBuffer srcDB = ras.getDataBuffer();
        final DataBuffer retDB = ret.getDataBuffer();
        if (srcDB.getDataType() != retDB.getDataType()) {
            throw new IllegalArgumentException("New DataBuffer doesn't match original");
        }
        final int len = srcDB.getSize();
        final int banks = srcDB.getNumBanks();
        final int[] offsets = srcDB.getOffsets();
        for (int b = 0; b < banks; ++b) {
            switch (srcDB.getDataType()) {
                case 0: {
                    final DataBufferByte srcDBT = (DataBufferByte)srcDB;
                    final DataBufferByte retDBT = (DataBufferByte)retDB;
                    System.arraycopy(srcDBT.getData(b), offsets[b], retDBT.getData(b), offsets[b], len);
                    break;
                }
                case 3: {
                    final DataBufferInt srcDBT2 = (DataBufferInt)srcDB;
                    final DataBufferInt retDBT2 = (DataBufferInt)retDB;
                    System.arraycopy(srcDBT2.getData(b), offsets[b], retDBT2.getData(b), offsets[b], len);
                    break;
                }
                case 2: {
                    final DataBufferShort srcDBT3 = (DataBufferShort)srcDB;
                    final DataBufferShort retDBT3 = (DataBufferShort)retDB;
                    System.arraycopy(srcDBT3.getData(b), offsets[b], retDBT3.getData(b), offsets[b], len);
                    break;
                }
                case 1: {
                    final DataBufferUShort srcDBT4 = (DataBufferUShort)srcDB;
                    final DataBufferUShort retDBT4 = (DataBufferUShort)retDB;
                    System.arraycopy(srcDBT4.getData(b), offsets[b], retDBT4.getData(b), offsets[b], len);
                    break;
                }
            }
        }
        return ret;
    }
    
    public static WritableRaster makeRasterWritable(final Raster ras) {
        return makeRasterWritable(ras, ras.getMinX(), ras.getMinY());
    }
    
    public static WritableRaster makeRasterWritable(final Raster ras, final int minX, final int minY) {
        WritableRaster ret = Raster.createWritableRaster(ras.getSampleModel(), ras.getDataBuffer(), new Point(0, 0));
        ret = ret.createWritableChild(ras.getMinX() - ras.getSampleModelTranslateX(), ras.getMinY() - ras.getSampleModelTranslateY(), ras.getWidth(), ras.getHeight(), minX, minY, null);
        return ret;
    }
    
    public static ColorModel coerceColorModel(final ColorModel cm, final boolean newAlphaPreMult) {
        if (cm.isAlphaPremultiplied() == newAlphaPreMult) {
            return cm;
        }
        final WritableRaster wr = cm.createCompatibleWritableRaster(1, 1);
        return cm.coerceData(wr, newAlphaPreMult);
    }
    
    public static ColorModel coerceData(final WritableRaster wr, final ColorModel cm, final boolean newAlphaPreMult) {
        if (!cm.hasAlpha()) {
            return cm;
        }
        if (cm.isAlphaPremultiplied() == newAlphaPreMult) {
            return cm;
        }
        if (newAlphaPreMult) {
            multiplyAlpha(wr);
        }
        else {
            divideAlpha(wr);
        }
        return coerceColorModel(cm, newAlphaPreMult);
    }
    
    public static void multiplyAlpha(final WritableRaster wr) {
        if (is_BYTE_COMP_Data(wr.getSampleModel())) {
            mult_BYTE_COMP_Data(wr);
        }
        else if (is_INT_PACK_Data(wr.getSampleModel(), true)) {
            mult_INT_PACK_Data(wr);
        }
        else {
            int[] pixel = null;
            final int bands = wr.getNumBands();
            final float norm = 0.003921569f;
            final int x0 = wr.getMinX();
            final int x2 = x0 + wr.getWidth();
            final int y0 = wr.getMinY();
            for (int y2 = y0 + wr.getHeight(), y3 = y0; y3 < y2; ++y3) {
                for (int x3 = x0; x3 < x2; ++x3) {
                    pixel = wr.getPixel(x3, y3, pixel);
                    final int a = pixel[bands - 1];
                    if (a >= 0 && a < 255) {
                        final float alpha = a * norm;
                        for (int b = 0; b < bands - 1; ++b) {
                            pixel[b] = (int)(pixel[b] * alpha + 0.5f);
                        }
                        wr.setPixel(x3, y3, pixel);
                    }
                }
            }
        }
    }
    
    public static void divideAlpha(final WritableRaster wr) {
        if (is_BYTE_COMP_Data(wr.getSampleModel())) {
            divide_BYTE_COMP_Data(wr);
        }
        else if (is_INT_PACK_Data(wr.getSampleModel(), true)) {
            divide_INT_PACK_Data(wr);
        }
        else {
            final int bands = wr.getNumBands();
            int[] pixel = null;
            final int x0 = wr.getMinX();
            final int x2 = x0 + wr.getWidth();
            final int y0 = wr.getMinY();
            for (int y2 = y0 + wr.getHeight(), y3 = y0; y3 < y2; ++y3) {
                for (int x3 = x0; x3 < x2; ++x3) {
                    pixel = wr.getPixel(x3, y3, pixel);
                    final int a = pixel[bands - 1];
                    if (a > 0 && a < 255) {
                        final float ialpha = 255.0f / a;
                        for (int b = 0; b < bands - 1; ++b) {
                            pixel[b] = (int)(pixel[b] * ialpha + 0.5f);
                        }
                        wr.setPixel(x3, y3, pixel);
                    }
                }
            }
        }
    }
    
    public static void copyData(final BufferedImage src, final BufferedImage dst) {
        final Rectangle srcRect = new Rectangle(0, 0, src.getWidth(), src.getHeight());
        copyData(src, srcRect, dst, new Point(0, 0));
    }
    
    public static void copyData(final BufferedImage src, final Rectangle srcRect, final BufferedImage dst, final Point destP) {
        final boolean srcAlpha = src.getColorModel().hasAlpha();
        final boolean dstAlpha = dst.getColorModel().hasAlpha();
        if (srcAlpha == dstAlpha && (!srcAlpha || src.isAlphaPremultiplied() == dst.isAlphaPremultiplied())) {
            copyData(src.getRaster(), dst.getRaster());
            return;
        }
        int[] pixel = null;
        final Raster srcR = src.getRaster();
        final WritableRaster dstR = dst.getRaster();
        final int bands = dstR.getNumBands();
        final int dx = destP.x - srcRect.x;
        final int dy = destP.y - srcRect.y;
        final int w = srcRect.width;
        final int x0 = srcRect.x;
        final int y0 = srcRect.y;
        final int y2 = y0 + srcRect.height - 1;
        if (!srcAlpha) {
            final int[] oPix = new int[bands * w];
            for (int out = w * bands - 1; out >= 0; out -= bands) {
                oPix[out] = 255;
            }
            for (int y3 = y0; y3 <= y2; ++y3) {
                pixel = srcR.getPixels(x0, y3, w, 1, pixel);
                int in = w * (bands - 1) - 1;
                int out = w * bands - 2;
                switch (bands) {
                    case 4: {
                        while (in >= 0) {
                            oPix[out--] = pixel[in--];
                            oPix[out--] = pixel[in--];
                            oPix[out--] = pixel[in--];
                            --out;
                        }
                        break;
                    }
                    default: {
                        while (in >= 0) {
                            for (int b = 0; b < bands - 1; ++b) {
                                oPix[out--] = pixel[in--];
                            }
                            --out;
                        }
                        break;
                    }
                }
                dstR.setPixels(x0 + dx, y3 + dy, w, 1, oPix);
            }
        }
        else if (dstAlpha && dst.isAlphaPremultiplied()) {
            final int fpNorm = 65793;
            final int pt5 = 8388608;
            for (int y4 = y0; y4 <= y2; ++y4) {
                pixel = srcR.getPixels(x0, y4, w, 1, pixel);
                int in = bands * w - 1;
                switch (bands) {
                    case 4: {
                        while (in >= 0) {
                            final int a = pixel[in];
                            if (a == 255) {
                                in -= 4;
                            }
                            else {
                                --in;
                                final int alpha = fpNorm * a;
                                pixel[in] = pixel[in] * alpha + pt5 >>> 24;
                                --in;
                                pixel[in] = pixel[in] * alpha + pt5 >>> 24;
                                --in;
                                pixel[in] = pixel[in] * alpha + pt5 >>> 24;
                                --in;
                            }
                        }
                        break;
                    }
                    default: {
                        while (in >= 0) {
                            final int a = pixel[in];
                            if (a == 255) {
                                in -= bands;
                            }
                            else {
                                --in;
                                final int alpha = fpNorm * a;
                                for (int b2 = 0; b2 < bands - 1; ++b2) {
                                    pixel[in] = pixel[in] * alpha + pt5 >>> 24;
                                    --in;
                                }
                            }
                        }
                        break;
                    }
                }
                dstR.setPixels(x0 + dx, y4 + dy, w, 1, pixel);
            }
        }
        else if (dstAlpha && !dst.isAlphaPremultiplied()) {
            final int fpNorm = 16711680;
            final int pt5 = 32768;
            for (int y4 = y0; y4 <= y2; ++y4) {
                pixel = srcR.getPixels(x0, y4, w, 1, pixel);
                int in = bands * w - 1;
                switch (bands) {
                    case 4: {
                        while (in >= 0) {
                            final int a = pixel[in];
                            if (a <= 0 || a >= 255) {
                                in -= 4;
                            }
                            else {
                                --in;
                                final int ialpha = fpNorm / a;
                                pixel[in] = pixel[in] * ialpha + pt5 >>> 16;
                                --in;
                                pixel[in] = pixel[in] * ialpha + pt5 >>> 16;
                                --in;
                                pixel[in] = pixel[in] * ialpha + pt5 >>> 16;
                                --in;
                            }
                        }
                        break;
                    }
                    default: {
                        while (in >= 0) {
                            final int a = pixel[in];
                            if (a <= 0 || a >= 255) {
                                in -= bands;
                            }
                            else {
                                --in;
                                final int ialpha = fpNorm / a;
                                for (int b2 = 0; b2 < bands - 1; ++b2) {
                                    pixel[in] = pixel[in] * ialpha + pt5 >>> 16;
                                    --in;
                                }
                            }
                        }
                        break;
                    }
                }
                dstR.setPixels(x0 + dx, y4 + dy, w, 1, pixel);
            }
        }
        else if (src.isAlphaPremultiplied()) {
            final int[] oPix = new int[bands * w];
            final int fpNorm2 = 16711680;
            final int pt6 = 32768;
            for (int y5 = y0; y5 <= y2; ++y5) {
                pixel = srcR.getPixels(x0, y5, w, 1, pixel);
                int in2 = (bands + 1) * w - 1;
                int out2 = bands * w - 1;
                while (in2 >= 0) {
                    final int a2 = pixel[in2];
                    --in2;
                    if (a2 > 0) {
                        if (a2 < 255) {
                            final int ialpha2 = fpNorm2 / a2;
                            for (int b = 0; b < bands; ++b) {
                                oPix[out2--] = pixel[in2--] * ialpha2 + pt6 >>> 16;
                            }
                        }
                        else {
                            for (int b = 0; b < bands; ++b) {
                                oPix[out2--] = pixel[in2--];
                            }
                        }
                    }
                    else {
                        in2 -= bands;
                        for (int b = 0; b < bands; ++b) {
                            oPix[out2--] = 255;
                        }
                    }
                }
                dstR.setPixels(x0 + dx, y5 + dy, w, 1, oPix);
            }
        }
        else {
            final Rectangle dstRect = new Rectangle(destP.x, destP.y, srcRect.width, srcRect.height);
            for (int b2 = 0; b2 < bands; ++b2) {
                copyBand(srcR, srcRect, b2, dstR, dstRect, b2);
            }
        }
    }
    
    public static void copyBand(final Raster src, final int srcBand, final WritableRaster dst, final int dstBand) {
        final Rectangle sR = src.getBounds();
        final Rectangle dR = dst.getBounds();
        final Rectangle cpR = sR.intersection(dR);
        copyBand(src, cpR, srcBand, dst, cpR, dstBand);
    }
    
    public static void copyBand(final Raster src, Rectangle sR, final int sBand, final WritableRaster dst, Rectangle dR, final int dBand) {
        final int dy = dR.y - sR.y;
        final int dx = dR.x - sR.x;
        sR = sR.intersection(src.getBounds());
        dR = dR.intersection(dst.getBounds());
        int width;
        if (dR.width < sR.width) {
            width = dR.width;
        }
        else {
            width = sR.width;
        }
        int height;
        if (dR.height < sR.height) {
            height = dR.height;
        }
        else {
            height = sR.height;
        }
        final int x = sR.x + dx;
        int[] samples = null;
        for (int y = sR.y; y < sR.y + height; ++y) {
            samples = src.getSamples(sR.x, y, width, 1, sBand, samples);
            dst.setSamples(x, y + dy, width, 1, dBand, samples);
        }
    }
    
    public static boolean is_INT_PACK_Data(final SampleModel sm, final boolean requireAlpha) {
        if (!(sm instanceof SinglePixelPackedSampleModel)) {
            return false;
        }
        if (sm.getDataType() != 3) {
            return false;
        }
        final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm;
        final int[] masks = sppsm.getBitMasks();
        if (masks.length == 3) {
            if (requireAlpha) {
                return false;
            }
        }
        else if (masks.length != 4) {
            return false;
        }
        return masks[0] == 16711680 && masks[1] == 65280 && masks[2] == 255 && (masks.length != 4 || masks[3] == -16777216);
    }
    
    public static boolean is_BYTE_COMP_Data(final SampleModel sm) {
        return sm instanceof ComponentSampleModel && sm.getDataType() == 0;
    }
    
    protected static void divide_INT_PACK_Data(final WritableRaster wr) {
        final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        final int width = wr.getWidth();
        final int scanStride = sppsm.getScanlineStride();
        final DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        final int base = db.getOffset() + sppsm.getOffset(wr.getMinX() - wr.getSampleModelTranslateX(), wr.getMinY() - wr.getSampleModelTranslateY());
        final int[] pixels = db.getBankData()[0];
        for (int y = 0; y < wr.getHeight(); ++y) {
            for (int sp = base + y * scanStride, end = sp + width; sp < end; ++sp) {
                final int pixel = pixels[sp];
                final int a = pixel >>> 24;
                if (a <= 0) {
                    pixels[sp] = 16777215;
                }
                else if (a < 255) {
                    final int aFP = 16711680 / a;
                    pixels[sp] = (a << 24 | (((pixel & 0xFF0000) >> 16) * aFP & 0xFF0000) | (((pixel & 0xFF00) >> 8) * aFP & 0xFF0000) >> 8 | ((pixel & 0xFF) * aFP & 0xFF0000) >> 16);
                }
            }
        }
    }
    
    protected static void mult_INT_PACK_Data(final WritableRaster wr) {
        final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        final int width = wr.getWidth();
        final int scanStride = sppsm.getScanlineStride();
        final DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        final int base = db.getOffset() + sppsm.getOffset(wr.getMinX() - wr.getSampleModelTranslateX(), wr.getMinY() - wr.getSampleModelTranslateY());
        final int[] pixels = db.getBankData()[0];
        for (int y = 0; y < wr.getHeight(); ++y) {
            for (int sp = base + y * scanStride, end = sp + width; sp < end; ++sp) {
                final int pixel = pixels[sp];
                final int a = pixel >>> 24;
                if (a >= 0 && a < 255) {
                    pixels[sp] = (a << 24 | ((pixel & 0xFF0000) * a >> 8 & 0xFF0000) | ((pixel & 0xFF00) * a >> 8 & 0xFF00) | ((pixel & 0xFF) * a >> 8 & 0xFF));
                }
            }
        }
    }
    
    protected static void divide_BYTE_COMP_Data(final WritableRaster wr) {
        final ComponentSampleModel csm = (ComponentSampleModel)wr.getSampleModel();
        final int width = wr.getWidth();
        final int scanStride = csm.getScanlineStride();
        final int pixStride = csm.getPixelStride();
        final int[] bandOff = csm.getBandOffsets();
        final DataBufferByte db = (DataBufferByte)wr.getDataBuffer();
        final int base = db.getOffset() + csm.getOffset(wr.getMinX() - wr.getSampleModelTranslateX(), wr.getMinY() - wr.getSampleModelTranslateY());
        final int aOff = bandOff[bandOff.length - 1];
        final int bands = bandOff.length - 1;
        final byte[] pixels = db.getBankData()[0];
        for (int y = 0; y < wr.getHeight(); ++y) {
            for (int sp = base + y * scanStride, end = sp + width * pixStride; sp < end; sp += pixStride) {
                final int a = pixels[sp + aOff] & 0xFF;
                if (a == 0) {
                    for (int b = 0; b < bands; ++b) {
                        pixels[sp + bandOff[b]] = -1;
                    }
                }
                else if (a < 255) {
                    final int aFP = 16711680 / a;
                    for (int b2 = 0; b2 < bands; ++b2) {
                        final int i = sp + bandOff[b2];
                        pixels[i] = (byte)((pixels[i] & 0xFF) * aFP >>> 16);
                    }
                }
            }
        }
    }
    
    protected static void mult_BYTE_COMP_Data(final WritableRaster wr) {
        final ComponentSampleModel csm = (ComponentSampleModel)wr.getSampleModel();
        final int width = wr.getWidth();
        final int scanStride = csm.getScanlineStride();
        final int pixStride = csm.getPixelStride();
        final int[] bandOff = csm.getBandOffsets();
        final DataBufferByte db = (DataBufferByte)wr.getDataBuffer();
        final int base = db.getOffset() + csm.getOffset(wr.getMinX() - wr.getSampleModelTranslateX(), wr.getMinY() - wr.getSampleModelTranslateY());
        final int aOff = bandOff[bandOff.length - 1];
        final int bands = bandOff.length - 1;
        final byte[] pixels = db.getBankData()[0];
        for (int y = 0; y < wr.getHeight(); ++y) {
            for (int sp = base + y * scanStride, end = sp + width * pixStride; sp < end; sp += pixStride) {
                final int a = pixels[sp + aOff] & 0xFF;
                if (a != 255) {
                    for (int b = 0; b < bands; ++b) {
                        final int i = sp + bandOff[b];
                        pixels[i] = (byte)((pixels[i] & 0xFF) * a >> 8);
                    }
                }
            }
        }
    }
    
    static {
        GraphicsUtil.IDENTITY = new AffineTransform();
        boolean warn = true;
        try {
            final String s = System.getProperty("org.apache.batik.warn_destination", "true");
            warn = Boolean.valueOf(s);
        }
        catch (SecurityException se) {}
        catch (NumberFormatException nfe) {}
        finally {
            WARN_DESTINATION = warn;
        }
        Linear_sRGB = new DirectColorModel(ColorSpace.getInstance(1004), 24, 16711680, 65280, 255, 0, false, 3);
        Linear_sRGB_Pre = new DirectColorModel(ColorSpace.getInstance(1004), 32, 16711680, 65280, 255, -16777216, true, 3);
        Linear_sRGB_Unpre = new DirectColorModel(ColorSpace.getInstance(1004), 32, 16711680, 65280, 255, -16777216, false, 3);
        sRGB = new DirectColorModel(ColorSpace.getInstance(1000), 24, 16711680, 65280, 255, 0, false, 3);
        sRGB_Pre = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, true, 3);
        sRGB_Unpre = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, false, 3);
    }
}
