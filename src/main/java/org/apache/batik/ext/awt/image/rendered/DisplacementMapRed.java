// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.Rectangle;
import java.util.Map;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.PadMode;
import java.awt.RenderingHints;
import org.apache.batik.ext.awt.image.ARGBChannel;

public class DisplacementMapRed extends AbstractRed
{
    private static final boolean TIME = false;
    private static final boolean USE_NN = false;
    private float scaleX;
    private float scaleY;
    private ARGBChannel xChannel;
    private ARGBChannel yChannel;
    CachableRed image;
    CachableRed offsets;
    int maxOffX;
    int maxOffY;
    RenderingHints hints;
    TileOffsets[] xOffsets;
    TileOffsets[] yOffsets;
    
    public DisplacementMapRed(CachableRed image, final CachableRed offsets, final ARGBChannel xChannel, final ARGBChannel yChannel, final float scaleX, final float scaleY, final RenderingHints rh) {
        if (xChannel == null) {
            throw new IllegalArgumentException("Must provide xChannel");
        }
        if (yChannel == null) {
            throw new IllegalArgumentException("Must provide yChannel");
        }
        this.offsets = offsets;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.xChannel = xChannel;
        this.yChannel = yChannel;
        this.hints = rh;
        this.maxOffX = (int)Math.ceil(scaleX / 2.0f);
        this.maxOffY = (int)Math.ceil(scaleY / 2.0f);
        final Rectangle rect = image.getBounds();
        final Rectangle bounds;
        final Rectangle r = bounds = image.getBounds();
        bounds.x -= this.maxOffX;
        final Rectangle rectangle = r;
        rectangle.width += 2 * this.maxOffX;
        final Rectangle rectangle2 = r;
        rectangle2.y -= this.maxOffY;
        final Rectangle rectangle3 = r;
        rectangle3.height += 2 * this.maxOffY;
        image = new PadRed(image, r, PadMode.ZERO_PAD, null);
        image = new TileCacheRed(image);
        this.image = image;
        ColorModel cm = image.getColorModel();
        cm = GraphicsUtil.coerceColorModel(cm, true);
        this.init(image, rect, cm, image.getSampleModel(), rect.x, rect.y, null);
        this.xOffsets = new TileOffsets[this.getNumXTiles()];
        this.yOffsets = new TileOffsets[this.getNumYTiles()];
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        this.copyToRaster(wr);
        return wr;
    }
    
    @Override
    public Raster getTile(final int tileX, final int tileY) {
        final WritableRaster dest = this.makeTile(tileX, tileY);
        final Rectangle srcR = dest.getBounds();
        final Raster mapRas = this.offsets.getData(srcR);
        final ColorModel mapCM = this.offsets.getColorModel();
        GraphicsUtil.coerceData((WritableRaster)mapRas, mapCM, false);
        final TileOffsets xinfo = this.getXOffsets(tileX);
        final TileOffsets yinfo = this.getYOffsets(tileY);
        if (this.image.getColorModel().isAlphaPremultiplied()) {
            this.filterBL(mapRas, dest, xinfo.tile, xinfo.off, yinfo.tile, yinfo.off);
        }
        else {
            this.filterBLPre(mapRas, dest, xinfo.tile, xinfo.off, yinfo.tile, yinfo.off);
        }
        return dest;
    }
    
    public TileOffsets getXOffsets(final int xTile) {
        TileOffsets ret = this.xOffsets[xTile - this.getMinTileX()];
        if (ret != null) {
            return ret;
        }
        final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)this.getSampleModel();
        final int base = sppsm.getOffset(0, 0);
        final int tw = sppsm.getWidth();
        final int width = tw + 2 * this.maxOffX;
        final int x0 = this.getTileGridXOffset() + xTile * tw - this.maxOffX - this.image.getTileGridXOffset();
        final int x2 = x0 + width - 1;
        final int tile = (int)Math.floor(x0 / (double)tw);
        final int endTile = (int)Math.floor(x2 / (double)tw);
        final int loc = x0 - tile * tw;
        final int endLoc = tw;
        final int slop = (endTile + 1) * tw - 1 - x2;
        ret = new TileOffsets(width, base, 1, loc, endLoc, slop, tile, endTile);
        return this.xOffsets[xTile - this.getMinTileX()] = ret;
    }
    
    public TileOffsets getYOffsets(final int yTile) {
        TileOffsets ret = this.yOffsets[yTile - this.getMinTileY()];
        if (ret != null) {
            return ret;
        }
        final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)this.getSampleModel();
        final int stride = sppsm.getScanlineStride();
        final int th = sppsm.getHeight();
        final int height = th + 2 * this.maxOffY;
        final int y0 = this.getTileGridYOffset() + yTile * th - this.maxOffY - this.image.getTileGridYOffset();
        final int y2 = y0 + height - 1;
        final int tile = (int)Math.floor(y0 / (double)th);
        final int endTile = (int)Math.floor(y2 / (double)th);
        final int loc = y0 - tile * th;
        final int endLoc = th;
        final int slop = (endTile + 1) * th - 1 - y2;
        ret = new TileOffsets(height, 0, stride, loc, endLoc, slop, tile, endTile);
        return this.yOffsets[yTile - this.getMinTileY()] = ret;
    }
    
    public void filterBL(final Raster off, final WritableRaster dst, final int[] xTile, final int[] xOff, final int[] yTile, final int[] yOff) {
        final int w = dst.getWidth();
        final int h = dst.getHeight();
        final int xStart = this.maxOffX;
        final int yStart = this.maxOffY;
        final int xEnd = xStart + w;
        final int yEnd = yStart + h;
        final DataBufferInt dstDB = (DataBufferInt)dst.getDataBuffer();
        final DataBufferInt offDB = (DataBufferInt)off.getDataBuffer();
        final SinglePixelPackedSampleModel dstSPPSM = (SinglePixelPackedSampleModel)dst.getSampleModel();
        final int dstOff = dstDB.getOffset() + dstSPPSM.getOffset(dst.getMinX() - dst.getSampleModelTranslateX(), dst.getMinY() - dst.getSampleModelTranslateY());
        final SinglePixelPackedSampleModel offSPPSM = (SinglePixelPackedSampleModel)off.getSampleModel();
        final int offOff = offDB.getOffset() + offSPPSM.getOffset(dst.getMinX() - off.getSampleModelTranslateX(), dst.getMinY() - off.getSampleModelTranslateY());
        final int dstScanStride = dstSPPSM.getScanlineStride();
        final int offScanStride = offSPPSM.getScanlineStride();
        final int dstAdjust = dstScanStride - w;
        final int offAdjust = offScanStride - w;
        final int[] dstPixels = dstDB.getBankData()[0];
        final int[] offPixels = offDB.getBankData()[0];
        final int xShift = this.xChannel.toInt() * 8;
        final int yShift = this.yChannel.toInt() * 8;
        int dp = dstOff;
        int ip = offOff;
        final int fpScaleX = (int)(this.scaleX / 255.0 * 32768.0 + 0.5);
        final int fpAdjX = (int)(-127.5 * fpScaleX - 0.5);
        final int fpScaleY = (int)(this.scaleY / 255.0 * 32768.0 + 0.5);
        final int fpAdjY = (int)(-127.5 * fpScaleY - 0.5);
        final long start = System.currentTimeMillis();
        int xt = xTile[0] - 1;
        int yt = yTile[0] - 1;
        int[] imgPix = null;
        for (int y = yStart; y < yEnd; ++y) {
            for (int x = xStart; x < xEnd; ++x, ++dp, ++ip) {
                final int dPel = offPixels[ip];
                final int xDisplace = fpScaleX * (dPel >> xShift & 0xFF) + fpAdjX;
                final int yDisplace = fpScaleY * (dPel >> yShift & 0xFF) + fpAdjY;
                final int x2 = x + (xDisplace >> 15);
                final int y2 = y + (yDisplace >> 15);
                if (xt != xTile[x2] || yt != yTile[y2]) {
                    xt = xTile[x2];
                    yt = yTile[y2];
                    imgPix = ((DataBufferInt)this.image.getTile(xt, yt).getDataBuffer()).getBankData()[0];
                }
                final int pel00 = imgPix[xOff[x2] + yOff[y2]];
                final int xt2 = xTile[x2 + 1];
                final int yt2 = yTile[y2 + 1];
                int pel2;
                int pel3;
                int pel4;
                if (yt == yt2) {
                    if (xt == xt2) {
                        pel2 = imgPix[xOff[x2 + 1] + yOff[y2]];
                        pel3 = imgPix[xOff[x2] + yOff[y2 + 1]];
                        pel4 = imgPix[xOff[x2 + 1] + yOff[y2 + 1]];
                    }
                    else {
                        pel3 = imgPix[xOff[x2] + yOff[y2 + 1]];
                        imgPix = ((DataBufferInt)this.image.getTile(xt2, yt).getDataBuffer()).getBankData()[0];
                        pel2 = imgPix[xOff[x2 + 1] + yOff[y2]];
                        pel4 = imgPix[xOff[x2 + 1] + yOff[y2 + 1]];
                        xt = xt2;
                    }
                }
                else if (xt == xt2) {
                    pel2 = imgPix[xOff[x2 + 1] + yOff[y2]];
                    imgPix = ((DataBufferInt)this.image.getTile(xt, yt2).getDataBuffer()).getBankData()[0];
                    pel3 = imgPix[xOff[x2] + yOff[y2 + 1]];
                    pel4 = imgPix[xOff[x2 + 1] + yOff[y2 + 1]];
                    yt = yt2;
                }
                else {
                    imgPix = ((DataBufferInt)this.image.getTile(xt, yt2).getDataBuffer()).getBankData()[0];
                    pel3 = imgPix[xOff[x2] + yOff[y2 + 1]];
                    imgPix = ((DataBufferInt)this.image.getTile(xt2, yt2).getDataBuffer()).getBankData()[0];
                    pel4 = imgPix[xOff[x2 + 1] + yOff[y2 + 1]];
                    imgPix = ((DataBufferInt)this.image.getTile(xt2, yt).getDataBuffer()).getBankData()[0];
                    pel2 = imgPix[xOff[x2 + 1] + yOff[y2]];
                    xt = xt2;
                }
                final int xFrac = xDisplace & 0x7FFF;
                final int yFrac = yDisplace & 0x7FFF;
                int sp0 = pel00 >>> 16 & 0xFF00;
                int sp2 = pel2 >>> 16 & 0xFF00;
                int pel5 = sp0 + ((sp2 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                sp0 = (pel3 >>> 16 & 0xFF00);
                sp2 = (pel4 >>> 16 & 0xFF00);
                int pel6 = sp0 + ((sp2 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                int newPel = ((pel5 << 15) + (pel6 - pel5) * yFrac + 4194304 & 0x7F800000) << 1;
                sp0 = (pel00 >> 8 & 0xFF00);
                sp2 = (pel2 >> 8 & 0xFF00);
                pel5 = (sp0 + ((sp2 - sp0) * xFrac + 16384 >> 15) & 0xFFFF);
                sp0 = (pel3 >> 8 & 0xFF00);
                sp2 = (pel4 >> 8 & 0xFF00);
                pel6 = (sp0 + ((sp2 - sp0) * xFrac + 16384 >> 15) & 0xFFFF);
                newPel |= ((pel5 << 15) + (pel6 - pel5) * yFrac + 4194304 & 0x7F800000) >>> 7;
                sp0 = (pel00 & 0xFF00);
                sp2 = (pel2 & 0xFF00);
                pel5 = (sp0 + ((sp2 - sp0) * xFrac + 16384 >> 15) & 0xFFFF);
                sp0 = (pel3 & 0xFF00);
                sp2 = (pel4 & 0xFF00);
                pel6 = (sp0 + ((sp2 - sp0) * xFrac + 16384 >> 15) & 0xFFFF);
                newPel |= ((pel5 << 15) + (pel6 - pel5) * yFrac + 4194304 & 0x7F800000) >>> 15;
                sp0 = (pel00 << 8 & 0xFF00);
                sp2 = (pel2 << 8 & 0xFF00);
                pel5 = (sp0 + ((sp2 - sp0) * xFrac + 16384 >> 15) & 0xFFFF);
                sp0 = (pel3 << 8 & 0xFF00);
                sp2 = (pel4 << 8 & 0xFF00);
                pel6 = (sp0 + ((sp2 - sp0) * xFrac + 16384 >> 15) & 0xFFFF);
                newPel |= ((pel5 << 15) + (pel6 - pel5) * yFrac + 4194304 & 0x7F800000) >>> 23;
                dstPixels[dp] = newPel;
            }
            dp += dstAdjust;
            ip += offAdjust;
        }
    }
    
    public void filterBLPre(final Raster off, final WritableRaster dst, final int[] xTile, final int[] xOff, final int[] yTile, final int[] yOff) {
        final int w = dst.getWidth();
        final int h = dst.getHeight();
        final int xStart = this.maxOffX;
        final int yStart = this.maxOffY;
        final int xEnd = xStart + w;
        final int yEnd = yStart + h;
        final DataBufferInt dstDB = (DataBufferInt)dst.getDataBuffer();
        final DataBufferInt offDB = (DataBufferInt)off.getDataBuffer();
        final SinglePixelPackedSampleModel dstSPPSM = (SinglePixelPackedSampleModel)dst.getSampleModel();
        final int dstOff = dstDB.getOffset() + dstSPPSM.getOffset(dst.getMinX() - dst.getSampleModelTranslateX(), dst.getMinY() - dst.getSampleModelTranslateY());
        final SinglePixelPackedSampleModel offSPPSM = (SinglePixelPackedSampleModel)off.getSampleModel();
        final int offOff = offDB.getOffset() + offSPPSM.getOffset(dst.getMinX() - off.getSampleModelTranslateX(), dst.getMinY() - off.getSampleModelTranslateY());
        final int dstScanStride = dstSPPSM.getScanlineStride();
        final int offScanStride = offSPPSM.getScanlineStride();
        final int dstAdjust = dstScanStride - w;
        final int offAdjust = offScanStride - w;
        final int[] dstPixels = dstDB.getBankData()[0];
        final int[] offPixels = offDB.getBankData()[0];
        final int xShift = this.xChannel.toInt() * 8;
        final int yShift = this.yChannel.toInt() * 8;
        int dp = dstOff;
        int ip = offOff;
        final int fpScaleX = (int)(this.scaleX / 255.0 * 32768.0 + 0.5);
        final int fpAdjX = (int)(-127.5 * fpScaleX - 0.5);
        final int fpScaleY = (int)(this.scaleY / 255.0 * 32768.0 + 0.5);
        final int fpAdjY = (int)(-127.5 * fpScaleY - 0.5);
        final long start = System.currentTimeMillis();
        final int norm = 65793;
        int xt = xTile[0] - 1;
        int yt = yTile[0] - 1;
        int[] imgPix = null;
        for (int y = yStart; y < yEnd; ++y) {
            for (int x = xStart; x < xEnd; ++x, ++dp, ++ip) {
                final int dPel = offPixels[ip];
                final int xDisplace = fpScaleX * (dPel >> xShift & 0xFF) + fpAdjX;
                final int yDisplace = fpScaleY * (dPel >> yShift & 0xFF) + fpAdjY;
                final int x2 = x + (xDisplace >> 15);
                final int y2 = y + (yDisplace >> 15);
                if (xt != xTile[x2] || yt != yTile[y2]) {
                    xt = xTile[x2];
                    yt = yTile[y2];
                    imgPix = ((DataBufferInt)this.image.getTile(xt, yt).getDataBuffer()).getBankData()[0];
                }
                final int pel00 = imgPix[xOff[x2] + yOff[y2]];
                final int xt2 = xTile[x2 + 1];
                final int yt2 = yTile[y2 + 1];
                int pel2;
                int pel3;
                int pel4;
                if (yt == yt2) {
                    if (xt == xt2) {
                        pel2 = imgPix[xOff[x2 + 1] + yOff[y2]];
                        pel3 = imgPix[xOff[x2] + yOff[y2 + 1]];
                        pel4 = imgPix[xOff[x2 + 1] + yOff[y2 + 1]];
                    }
                    else {
                        pel3 = imgPix[xOff[x2] + yOff[y2 + 1]];
                        imgPix = ((DataBufferInt)this.image.getTile(xt2, yt).getDataBuffer()).getBankData()[0];
                        pel2 = imgPix[xOff[x2 + 1] + yOff[y2]];
                        pel4 = imgPix[xOff[x2 + 1] + yOff[y2 + 1]];
                        xt = xt2;
                    }
                }
                else if (xt == xt2) {
                    pel2 = imgPix[xOff[x2 + 1] + yOff[y2]];
                    imgPix = ((DataBufferInt)this.image.getTile(xt, yt2).getDataBuffer()).getBankData()[0];
                    pel3 = imgPix[xOff[x2] + yOff[y2 + 1]];
                    pel4 = imgPix[xOff[x2 + 1] + yOff[y2 + 1]];
                    yt = yt2;
                }
                else {
                    imgPix = ((DataBufferInt)this.image.getTile(xt, yt2).getDataBuffer()).getBankData()[0];
                    pel3 = imgPix[xOff[x2] + yOff[y2 + 1]];
                    imgPix = ((DataBufferInt)this.image.getTile(xt2, yt2).getDataBuffer()).getBankData()[0];
                    pel4 = imgPix[xOff[x2 + 1] + yOff[y2 + 1]];
                    imgPix = ((DataBufferInt)this.image.getTile(xt2, yt).getDataBuffer()).getBankData()[0];
                    pel2 = imgPix[xOff[x2 + 1] + yOff[y2]];
                    xt = xt2;
                }
                final int xFrac = xDisplace & 0x7FFF;
                final int yFrac = yDisplace & 0x7FFF;
                int sp0 = pel00 >>> 16 & 0xFF00;
                int sp2 = pel2 >>> 16 & 0xFF00;
                int pel5 = sp0 + ((sp2 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                final int a00 = (sp0 >> 8) * 65793 + 128 >> 8;
                final int a2 = (sp2 >> 8) * 65793 + 128 >> 8;
                sp0 = (pel3 >>> 16 & 0xFF00);
                sp2 = (pel4 >>> 16 & 0xFF00);
                int pel6 = sp0 + ((sp2 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                final int a3 = (sp0 >> 8) * 65793 + 128 >> 8;
                final int a4 = (sp2 >> 8) * 65793 + 128 >> 8;
                int newPel = ((pel5 << 15) + (pel6 - pel5) * yFrac + 4194304 & 0x7F800000) << 1;
                sp0 = (pel00 >> 16 & 0xFF) * a00 + 128 >> 8;
                sp2 = (pel2 >> 16 & 0xFF) * a2 + 128 >> 8;
                pel5 = (sp0 + ((sp2 - sp0) * xFrac + 16384 >> 15) & 0xFFFF);
                sp0 = (pel3 >> 16 & 0xFF) * a3 + 128 >> 8;
                sp2 = (pel4 >> 16 & 0xFF) * a4 + 128 >> 8;
                pel6 = (sp0 + ((sp2 - sp0) * xFrac + 16384 >> 15) & 0xFFFF);
                newPel |= ((pel5 << 15) + (pel6 - pel5) * yFrac + 4194304 & 0x7F800000) >>> 7;
                sp0 = (pel00 >> 8 & 0xFF) * a00 + 128 >> 8;
                sp2 = (pel2 >> 8 & 0xFF) * a2 + 128 >> 8;
                pel5 = (sp0 + ((sp2 - sp0) * xFrac + 16384 >> 15) & 0xFFFF);
                sp0 = (pel3 >> 8 & 0xFF) * a3 + 128 >> 8;
                sp2 = (pel4 >> 8 & 0xFF) * a4 + 128 >> 8;
                pel6 = (sp0 + ((sp2 - sp0) * xFrac + 16384 >> 15) & 0xFFFF);
                newPel |= ((pel5 << 15) + (pel6 - pel5) * yFrac + 4194304 & 0x7F800000) >>> 15;
                sp0 = (pel00 & 0xFF) * a00 + 128 >> 8;
                sp2 = (pel2 & 0xFF) * a2 + 128 >> 8;
                pel5 = (sp0 + ((sp2 - sp0) * xFrac + 16384 >> 15) & 0xFFFF);
                sp0 = (pel3 & 0xFF) * a3 + 128 >> 8;
                sp2 = (pel4 & 0xFF) * a4 + 128 >> 8;
                pel6 = (sp0 + ((sp2 - sp0) * xFrac + 16384 >> 15) & 0xFFFF);
                newPel |= ((pel5 << 15) + (pel6 - pel5) * yFrac + 4194304 & 0x7F800000) >>> 23;
                dstPixels[dp] = newPel;
            }
            dp += dstAdjust;
            ip += offAdjust;
        }
    }
    
    public void filterNN(final Raster off, final WritableRaster dst, final int[] xTile, final int[] xOff, final int[] yTile, final int[] yOff) {
        final int w = dst.getWidth();
        final int h = dst.getHeight();
        final int xStart = this.maxOffX;
        final int yStart = this.maxOffY;
        final int xEnd = xStart + w;
        final int yEnd = yStart + h;
        final DataBufferInt dstDB = (DataBufferInt)dst.getDataBuffer();
        final DataBufferInt offDB = (DataBufferInt)off.getDataBuffer();
        final SinglePixelPackedSampleModel dstSPPSM = (SinglePixelPackedSampleModel)dst.getSampleModel();
        final int dstOff = dstDB.getOffset() + dstSPPSM.getOffset(dst.getMinX() - dst.getSampleModelTranslateX(), dst.getMinY() - dst.getSampleModelTranslateY());
        final SinglePixelPackedSampleModel offSPPSM = (SinglePixelPackedSampleModel)off.getSampleModel();
        final int offOff = offDB.getOffset() + offSPPSM.getOffset(off.getMinX() - off.getSampleModelTranslateX(), off.getMinY() - off.getSampleModelTranslateY());
        final int dstScanStride = dstSPPSM.getScanlineStride();
        final int offScanStride = offSPPSM.getScanlineStride();
        final int dstAdjust = dstScanStride - w;
        final int offAdjust = offScanStride - w;
        final int[] dstPixels = dstDB.getBankData()[0];
        final int[] offPixels = offDB.getBankData()[0];
        final int xShift = this.xChannel.toInt() * 8;
        final int yShift = this.yChannel.toInt() * 8;
        final int fpScaleX = (int)(this.scaleX / 255.0 * 32768.0 + 0.5);
        final int fpScaleY = (int)(this.scaleY / 255.0 * 32768.0 + 0.5);
        final int fpAdjX = (int)(-127.5 * fpScaleX - 0.5) + 16384;
        final int fpAdjY = (int)(-127.5 * fpScaleY - 0.5) + 16384;
        int dp = dstOff;
        int ip = offOff;
        final long start = System.currentTimeMillis();
        int y = yStart;
        int xt = xTile[0] - 1;
        int yt = yTile[0] - 1;
        int[] imgPix = null;
        while (y < yEnd) {
            for (int x = xStart; x < xEnd; ++x) {
                final int dPel = offPixels[ip];
                final int xDisplace = fpScaleX * (dPel >> xShift & 0xFF) + fpAdjX;
                final int yDisplace = fpScaleY * (dPel >> yShift & 0xFF) + fpAdjY;
                final int x2 = x + (xDisplace >> 15);
                final int y2 = y + (yDisplace >> 15);
                if (xt != xTile[x2] || yt != yTile[y2]) {
                    xt = xTile[x2];
                    yt = yTile[y2];
                    imgPix = ((DataBufferInt)this.image.getTile(xt, yt).getDataBuffer()).getBankData()[0];
                }
                dstPixels[dp] = imgPix[xOff[x2] + yOff[y2]];
                ++dp;
                ++ip;
            }
            dp += dstAdjust;
            ip += offAdjust;
            ++y;
        }
    }
    
    static class TileOffsets
    {
        int[] tile;
        int[] off;
        
        TileOffsets(final int len, final int base, final int stride, int loc, int endLoc, final int slop, int tile, final int endTile) {
            this.tile = new int[len + 1];
            this.off = new int[len + 1];
            if (tile == endTile) {
                endLoc -= slop;
            }
            for (int i = 0; i < len; ++i) {
                this.tile[i] = tile;
                this.off[i] = base + loc * stride;
                if (++loc == endLoc) {
                    loc = 0;
                    if (++tile == endTile) {
                        endLoc -= slop;
                    }
                }
            }
            this.tile[len] = this.tile[len - 1];
            this.off[len] = this.off[len - 1];
        }
    }
}
