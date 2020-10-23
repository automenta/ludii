// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.Graphics2D;
import org.apache.batik.util.HaltingThread;
import java.awt.Color;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.util.Map;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.image.Raster;
import java.awt.Point;
import java.awt.image.WritableRaster;
import java.awt.image.RenderedImage;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class TileRed extends AbstractRed implements TileGenerator
{
    static final AffineTransform IDENTITY;
    Rectangle tiledRegion;
    int xStep;
    int yStep;
    TileStore tiles;
    private RenderingHints hints;
    final boolean is_INT_PACK;
    RenderedImage tile;
    WritableRaster raster;
    
    public TileRed(final RenderedImage tile, final Rectangle tiledRegion) {
        this(tile, tiledRegion, tile.getWidth(), tile.getHeight(), null);
    }
    
    public TileRed(final RenderedImage tile, final Rectangle tiledRegion, final RenderingHints hints) {
        this(tile, tiledRegion, tile.getWidth(), tile.getHeight(), hints);
    }
    
    public TileRed(final RenderedImage tile, final Rectangle tiledRegion, final int xStep, final int yStep) {
        this(tile, tiledRegion, xStep, yStep, null);
    }
    
    public TileRed(final RenderedImage tile, final Rectangle tiledRegion, final int xStep, final int yStep, final RenderingHints hints) {
        this.tile = null;
        this.raster = null;
        if (tiledRegion == null) {
            throw new IllegalArgumentException();
        }
        if (tile == null) {
            throw new IllegalArgumentException();
        }
        this.tiledRegion = tiledRegion;
        this.xStep = xStep;
        this.yStep = yStep;
        this.hints = hints;
        SampleModel sm = fixSampleModel(tile, xStep, yStep, tiledRegion.width, tiledRegion.height);
        final ColorModel cm = tile.getColorModel();
        double smSz = AbstractTiledRed.getDefaultTileSize();
        smSz *= smSz;
        final double stepSz = xStep * (double)yStep;
        if (16.1 * smSz > stepSz) {
            int xSz = xStep;
            int ySz = yStep;
            if (4.0 * stepSz <= smSz) {
                final int mult = (int)Math.ceil(Math.sqrt(smSz / stepSz));
                xSz *= mult;
                ySz *= mult;
            }
            sm = sm.createCompatibleSampleModel(xSz, ySz);
            this.raster = Raster.createWritableRaster(sm, new Point(tile.getMinX(), tile.getMinY()));
        }
        this.is_INT_PACK = GraphicsUtil.is_INT_PACK_Data(sm, false);
        this.init((CachableRed)null, tiledRegion, cm, sm, tile.getMinX(), tile.getMinY(), null);
        if (this.raster != null) {
            final WritableRaster fromRaster = this.raster.createWritableChild(tile.getMinX(), tile.getMinY(), xStep, yStep, tile.getMinX(), tile.getMinY(), null);
            this.fillRasterFrom(fromRaster, tile);
            this.fillOutRaster(this.raster);
        }
        else {
            this.tile = new TileCacheRed(GraphicsUtil.wrap(tile));
        }
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        final int xOff = (int)Math.floor(wr.getMinX() / this.xStep) * this.xStep;
        final int yOff = (int)Math.floor(wr.getMinY() / this.yStep) * this.yStep;
        final int x0 = wr.getMinX() - xOff;
        final int y0 = wr.getMinY() - yOff;
        final int tx0 = this.getXTile(x0);
        final int ty0 = this.getYTile(y0);
        final int tx2 = this.getXTile(x0 + wr.getWidth() - 1);
        for (int ty2 = this.getYTile(y0 + wr.getHeight() - 1), y2 = ty0; y2 <= ty2; ++y2) {
            for (int x2 = tx0; x2 <= tx2; ++x2) {
                Raster r = this.getTile(x2, y2);
                r = r.createChild(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight(), r.getMinX() + xOff, r.getMinY() + yOff, null);
                if (this.is_INT_PACK) {
                    GraphicsUtil.copyData_INT_PACK(r, wr);
                }
                else {
                    GraphicsUtil.copyData_FALLBACK(r, wr);
                }
            }
        }
        return wr;
    }
    
    @Override
    public Raster getTile(final int x, final int y) {
        if (this.raster != null) {
            final int tx = this.tileGridXOff + x * this.tileWidth;
            final int ty = this.tileGridYOff + y * this.tileHeight;
            return this.raster.createTranslatedChild(tx, ty);
        }
        return this.genTile(x, y);
    }
    
    @Override
    public Raster genTile(final int x, final int y) {
        final int tx = this.tileGridXOff + x * this.tileWidth;
        final int ty = this.tileGridYOff + y * this.tileHeight;
        if (this.raster != null) {
            return this.raster.createTranslatedChild(tx, ty);
        }
        final Point pt = new Point(tx, ty);
        final WritableRaster wr = Raster.createWritableRaster(this.sm, pt);
        this.fillRasterFrom(wr, this.tile);
        return wr;
    }
    
    public WritableRaster fillRasterFrom(final WritableRaster wr, final RenderedImage src) {
        final ColorModel cm = this.getColorModel();
        final BufferedImage bi = new BufferedImage(cm, wr.createWritableTranslatedChild(0, 0), cm.isAlphaPremultiplied(), null);
        final Graphics2D g = GraphicsUtil.createGraphics(bi, this.hints);
        int minX = wr.getMinX();
        final int minY = wr.getMinY();
        final int maxX = wr.getWidth();
        final int maxY = wr.getHeight();
        g.setComposite(AlphaComposite.Clear);
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, maxX, maxY);
        g.setComposite(AlphaComposite.SrcOver);
        g.translate(-minX, -minY);
        final int x1 = src.getMinX() + src.getWidth() - 1;
        final int y1 = src.getMinY() + src.getHeight() - 1;
        final int tileTx = (int)Math.ceil((minX - x1) / this.xStep) * this.xStep;
        final int tileTy = (int)Math.ceil((minY - y1) / this.yStep) * this.yStep;
        g.translate(tileTx, tileTy);
        int curX = tileTx - wr.getMinX() + src.getMinX();
        int curY = tileTy - wr.getMinY() + src.getMinY();
        minX = curX;
        while (curY < maxY) {
            if (HaltingThread.hasBeenHalted()) {
                return wr;
            }
            while (curX < maxX) {
                GraphicsUtil.drawImage(g, src);
                curX += this.xStep;
                g.translate(this.xStep, 0);
            }
            curY += this.yStep;
            g.translate(minX - curX, this.yStep);
            curX = minX;
        }
        return wr;
    }
    
    protected void fillOutRaster(final WritableRaster wr) {
        if (this.is_INT_PACK) {
            this.fillOutRaster_INT_PACK(wr);
        }
        else {
            this.fillOutRaster_FALLBACK(wr);
        }
    }
    
    protected void fillOutRaster_INT_PACK(final WritableRaster wr) {
        final int x0 = wr.getMinX();
        final int y0 = wr.getMinY();
        final int width = wr.getWidth();
        final int height = wr.getHeight();
        final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        final int scanStride = sppsm.getScanlineStride();
        final DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        final int[] pixels = db.getBankData()[0];
        final int base = db.getOffset() + sppsm.getOffset(x0 - wr.getSampleModelTranslateX(), y0 - wr.getSampleModelTranslateY());
        for (int step = this.xStep, x2 = this.xStep; x2 < width; x2 += step, step *= 2) {
            int w = step;
            if (x2 + w > width) {
                w = width - x2;
            }
            if (w >= 128) {
                int srcSP = base;
                int dstSP = base + x2;
                for (int y2 = 0; y2 < this.yStep; ++y2) {
                    System.arraycopy(pixels, srcSP, pixels, dstSP, w);
                    srcSP += scanStride;
                    dstSP += scanStride;
                }
            }
            else {
                int srcSP = base;
                int dstSP = base + x2;
                for (int y2 = 0; y2 < this.yStep; ++y2) {
                    int end;
                    for (end = srcSP, srcSP += w - 1, dstSP += w - 1; srcSP >= end; pixels[dstSP--] = pixels[srcSP--]) {}
                    srcSP += scanStride + 1;
                    dstSP += scanStride + 1;
                }
            }
        }
        for (int step = this.yStep, y3 = this.yStep; y3 < height; y3 += step, step *= 2) {
            int h = step;
            if (y3 + h > height) {
                h = height - y3;
            }
            final int dstSP2 = base + y3 * scanStride;
            System.arraycopy(pixels, base, pixels, dstSP2, h * scanStride);
        }
    }
    
    protected void fillOutRaster_FALLBACK(final WritableRaster wr) {
        final int width = wr.getWidth();
        final int height = wr.getHeight();
        Object data = null;
        for (int step = this.xStep, x = this.xStep; x < width; x += step, step *= 4) {
            int w = step;
            if (x + w > width) {
                w = width - x;
            }
            data = wr.getDataElements(0, 0, w, this.yStep, data);
            wr.setDataElements(x, 0, w, this.yStep, data);
            x += w;
            if (x >= width) {
                break;
            }
            if (x + w > width) {
                w = width - x;
            }
            wr.setDataElements(x, 0, w, this.yStep, data);
            x += w;
            if (x >= width) {
                break;
            }
            if (x + w > width) {
                w = width - x;
            }
            wr.setDataElements(x, 0, w, this.yStep, data);
        }
        int h;
        for (int step = this.yStep, y = this.yStep; y < height; y += h, y += step, step *= 4) {
            h = step;
            if (y + h > height) {
                h = height - y;
            }
            data = wr.getDataElements(0, 0, width, h, data);
            wr.setDataElements(0, y, width, h, data);
            y += h;
            if (h >= height) {
                break;
            }
            if (y + h > height) {
                h = height - y;
            }
            wr.setDataElements(0, y, width, h, data);
            y += h;
            if (h >= height) {
                break;
            }
            if (y + h > height) {
                h = height - y;
            }
            wr.setDataElements(0, y, width, h, data);
        }
    }
    
    protected static SampleModel fixSampleModel(final RenderedImage src, final int stepX, final int stepY, final int width, final int height) {
        final int defSz = AbstractTiledRed.getDefaultTileSize();
        final SampleModel sm = src.getSampleModel();
        int w = sm.getWidth();
        if (w < defSz) {
            w = defSz;
        }
        if (w > stepX) {
            w = stepX;
        }
        int h = sm.getHeight();
        if (h < defSz) {
            h = defSz;
        }
        if (h > stepY) {
            h = stepY;
        }
        return sm.createCompatibleSampleModel(w, h);
    }
    
    static {
        IDENTITY = new AffineTransform();
    }
}
