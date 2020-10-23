// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.DataBuffer;
import java.awt.Point;
import java.awt.image.DataBufferInt;
import org.apache.batik.util.HaltingThread;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.List;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.util.Map;
import java.awt.Rectangle;

public abstract class AbstractTiledRed extends AbstractRed implements TileGenerator
{
    private TileStore tiles;
    private static int defaultTileSize;
    
    public static int getDefaultTileSize() {
        return AbstractTiledRed.defaultTileSize;
    }
    
    protected AbstractTiledRed() {
    }
    
    protected AbstractTiledRed(final Rectangle bounds, final Map props) {
        super(bounds, props);
    }
    
    protected AbstractTiledRed(final CachableRed src, final Map props) {
        super(src, props);
    }
    
    protected AbstractTiledRed(final CachableRed src, final Rectangle bounds, final Map props) {
        super(src, bounds, props);
    }
    
    protected AbstractTiledRed(final CachableRed src, final Rectangle bounds, final ColorModel cm, final SampleModel sm, final Map props) {
        super(src, bounds, cm, sm, props);
    }
    
    protected AbstractTiledRed(final CachableRed src, final Rectangle bounds, final ColorModel cm, final SampleModel sm, final int tileGridXOff, final int tileGridYOff, final Map props) {
        super(src, bounds, cm, sm, tileGridXOff, tileGridYOff, props);
    }
    
    @Override
    protected void init(final CachableRed src, final Rectangle bounds, final ColorModel cm, final SampleModel sm, final int tileGridXOff, final int tileGridYOff, final Map props) {
        this.init(src, bounds, cm, sm, tileGridXOff, tileGridYOff, null, props);
    }
    
    protected void init(final CachableRed src, final Rectangle bounds, final ColorModel cm, final SampleModel sm, final int tileGridXOff, final int tileGridYOff, final TileStore tiles, final Map props) {
        super.init(src, bounds, cm, sm, tileGridXOff, tileGridYOff, props);
        this.tiles = tiles;
        if (this.tiles == null) {
            this.tiles = this.createTileStore();
        }
    }
    
    protected AbstractTiledRed(final List srcs, final Rectangle bounds, final Map props) {
        super(srcs, bounds, props);
    }
    
    protected AbstractTiledRed(final List srcs, final Rectangle bounds, final ColorModel cm, final SampleModel sm, final Map props) {
        super(srcs, bounds, cm, sm, props);
    }
    
    protected AbstractTiledRed(final List srcs, final Rectangle bounds, final ColorModel cm, final SampleModel sm, final int tileGridXOff, final int tileGridYOff, final Map props) {
        super(srcs, bounds, cm, sm, tileGridXOff, tileGridYOff, props);
    }
    
    @Override
    protected void init(final List srcs, final Rectangle bounds, final ColorModel cm, final SampleModel sm, final int tileGridXOff, final int tileGridYOff, final Map props) {
        super.init(srcs, bounds, cm, sm, tileGridXOff, tileGridYOff, props);
        this.tiles = this.createTileStore();
    }
    
    public TileStore getTileStore() {
        return this.tiles;
    }
    
    protected void setTileStore(final TileStore tiles) {
        this.tiles = tiles;
    }
    
    protected TileStore createTileStore() {
        return TileCache.getTileMap(this);
    }
    
    @Override
    public WritableRaster copyData(final WritableRaster wr) {
        this.copyToRasterByBlocks(wr);
        return wr;
    }
    
    @Override
    public Raster getData(final Rectangle rect) {
        final int xt0 = this.getXTile(rect.x);
        final int xt2 = this.getXTile(rect.x + rect.width - 1);
        final int yt0 = this.getYTile(rect.y);
        final int yt2 = this.getYTile(rect.y + rect.height - 1);
        if (xt0 == xt2 && yt0 == yt2) {
            final Raster r = this.getTile(xt0, yt0);
            return r.createChild(rect.x, rect.y, rect.width, rect.height, rect.x, rect.y, null);
        }
        return super.getData(rect);
    }
    
    @Override
    public Raster getTile(final int x, final int y) {
        return this.tiles.getTile(x, y);
    }
    
    @Override
    public Raster genTile(final int x, final int y) {
        final WritableRaster wr = this.makeTile(x, y);
        this.genRect(wr);
        return wr;
    }
    
    public abstract void genRect(final WritableRaster p0);
    
    public void setTile(final int x, final int y, final Raster ras) {
        this.tiles.setTile(x, y, ras);
    }
    
    public void copyToRasterByBlocks(final WritableRaster wr) {
        final boolean is_INT_PACK = GraphicsUtil.is_INT_PACK_Data(this.getSampleModel(), false);
        final Rectangle bounds = this.getBounds();
        final Rectangle wrR = wr.getBounds();
        int tx0 = this.getXTile(wrR.x);
        int ty0 = this.getYTile(wrR.y);
        int tx2 = this.getXTile(wrR.x + wrR.width - 1);
        int ty2 = this.getYTile(wrR.y + wrR.height - 1);
        if (tx0 < this.minTileX) {
            tx0 = this.minTileX;
        }
        if (ty0 < this.minTileY) {
            ty0 = this.minTileY;
        }
        if (tx2 >= this.minTileX + this.numXTiles) {
            tx2 = this.minTileX + this.numXTiles - 1;
        }
        if (ty2 >= this.minTileY + this.numYTiles) {
            ty2 = this.minTileY + this.numYTiles - 1;
        }
        if (tx2 < tx0 || ty2 < ty0) {
            return;
        }
        int insideTx0 = tx0;
        int insideTx2 = tx2;
        int insideTy0 = ty0;
        int insideTy2 = ty2;
        int tx3 = tx0 * this.tileWidth + this.tileGridXOff;
        if (tx3 < wrR.x && bounds.x != wrR.x) {
            ++insideTx0;
        }
        int ty3 = ty0 * this.tileHeight + this.tileGridYOff;
        if (ty3 < wrR.y && bounds.y != wrR.y) {
            ++insideTy0;
        }
        tx3 = (tx2 + 1) * this.tileWidth + this.tileGridXOff - 1;
        if (tx3 >= wrR.x + wrR.width && bounds.x + bounds.width != wrR.x + wrR.width) {
            --insideTx2;
        }
        ty3 = (ty2 + 1) * this.tileHeight + this.tileGridYOff - 1;
        if (ty3 >= wrR.y + wrR.height && bounds.y + bounds.height != wrR.y + wrR.height) {
            --insideTy2;
        }
        final int xtiles = insideTx2 - insideTx0 + 1;
        final int ytiles = insideTy2 - insideTy0 + 1;
        boolean[] occupied = null;
        if (xtiles > 0 && ytiles > 0) {
            occupied = new boolean[xtiles * ytiles];
        }
        final boolean[] got = new boolean[2 * (tx2 - tx0 + 1) + 2 * (ty2 - ty0 + 1)];
        int idx = 0;
        int numFound = 0;
        for (int y = ty0; y <= ty2; ++y) {
            for (int x = tx0; x <= tx2; ++x) {
                final Raster ras = this.tiles.getTileNoCompute(x, y);
                final boolean found = ras != null;
                if (y >= insideTy0 && y <= insideTy2 && x >= insideTx0 && x <= insideTx2) {
                    occupied[x - insideTx0 + (y - insideTy0) * xtiles] = found;
                }
                else {
                    got[idx++] = found;
                }
                if (found) {
                    ++numFound;
                    if (is_INT_PACK) {
                        GraphicsUtil.copyData_INT_PACK(ras, wr);
                    }
                    else {
                        GraphicsUtil.copyData_FALLBACK(ras, wr);
                    }
                }
            }
        }
        if (xtiles > 0 && ytiles > 0) {
            final TileBlock block = new TileBlock(insideTx0, insideTy0, xtiles, ytiles, occupied, 0, 0, xtiles, ytiles);
            this.drawBlock(block, wr);
        }
        final Thread currentThread = Thread.currentThread();
        if (HaltingThread.hasBeenHalted()) {
            return;
        }
        idx = 0;
        for (ty3 = ty0; ty3 <= ty2; ++ty3) {
            for (tx3 = tx0; tx3 <= tx2; ++tx3) {
                Raster ras2 = this.tiles.getTileNoCompute(tx3, ty3);
                if (ty3 >= insideTy0 && ty3 <= insideTy2 && tx3 >= insideTx0 && tx3 <= insideTx2) {
                    if (ras2 == null) {
                        final WritableRaster tile = this.makeTile(tx3, ty3);
                        if (is_INT_PACK) {
                            GraphicsUtil.copyData_INT_PACK(wr, tile);
                        }
                        else {
                            GraphicsUtil.copyData_FALLBACK(wr, tile);
                        }
                        this.tiles.setTile(tx3, ty3, tile);
                    }
                }
                else if (!got[idx++]) {
                    ras2 = this.getTile(tx3, ty3);
                    if (HaltingThread.hasBeenHalted(currentThread)) {
                        return;
                    }
                    if (is_INT_PACK) {
                        GraphicsUtil.copyData_INT_PACK(ras2, wr);
                    }
                    else {
                        GraphicsUtil.copyData_FALLBACK(ras2, wr);
                    }
                }
            }
        }
    }
    
    @Override
    public void copyToRaster(final WritableRaster wr) {
        final Rectangle wrR = wr.getBounds();
        int tx0 = this.getXTile(wrR.x);
        int ty0 = this.getYTile(wrR.y);
        int tx2 = this.getXTile(wrR.x + wrR.width - 1);
        int ty2 = this.getYTile(wrR.y + wrR.height - 1);
        if (tx0 < this.minTileX) {
            tx0 = this.minTileX;
        }
        if (ty0 < this.minTileY) {
            ty0 = this.minTileY;
        }
        if (tx2 >= this.minTileX + this.numXTiles) {
            tx2 = this.minTileX + this.numXTiles - 1;
        }
        if (ty2 >= this.minTileY + this.numYTiles) {
            ty2 = this.minTileY + this.numYTiles - 1;
        }
        final boolean is_INT_PACK = GraphicsUtil.is_INT_PACK_Data(this.getSampleModel(), false);
        final int xtiles = tx2 - tx0 + 1;
        final boolean[] got = new boolean[xtiles * (ty2 - ty0 + 1)];
        for (int y = ty0; y <= ty2; ++y) {
            for (int x = tx0; x <= tx2; ++x) {
                final Raster r = this.tiles.getTileNoCompute(x, y);
                if (r != null) {
                    got[x - tx0 + (y - ty0) * xtiles] = true;
                    if (is_INT_PACK) {
                        GraphicsUtil.copyData_INT_PACK(r, wr);
                    }
                    else {
                        GraphicsUtil.copyData_FALLBACK(r, wr);
                    }
                }
            }
        }
        for (int y = ty0; y <= ty2; ++y) {
            for (int x = tx0; x <= tx2; ++x) {
                if (!got[x - tx0 + (y - ty0) * xtiles]) {
                    final Raster r = this.getTile(x, y);
                    if (is_INT_PACK) {
                        GraphicsUtil.copyData_INT_PACK(r, wr);
                    }
                    else {
                        GraphicsUtil.copyData_FALLBACK(r, wr);
                    }
                }
            }
        }
    }
    
    protected void drawBlock(final TileBlock block, final WritableRaster wr) {
        final TileBlock[] blocks = block.getBestSplit();
        if (blocks == null) {
            return;
        }
        this.drawBlockInPlace(blocks, wr);
    }
    
    protected void drawBlockAndCopy(final TileBlock[] blocks, final WritableRaster wr) {
        if (blocks.length == 1) {
            final TileBlock curr = blocks[0];
            final int xloc = curr.getXLoc() * this.tileWidth + this.tileGridXOff;
            final int yloc = curr.getYLoc() * this.tileHeight + this.tileGridYOff;
            if (xloc == wr.getMinX() && yloc == wr.getMinY()) {
                this.drawBlockInPlace(blocks, wr);
                return;
            }
        }
        final int workTileWidth = this.tileWidth;
        final int workTileHeight = this.tileHeight;
        int maxTileSize = 0;
        for (final TileBlock curr2 : blocks) {
            final int sz = curr2.getWidth() * workTileWidth * (curr2.getHeight() * workTileHeight);
            if (sz > maxTileSize) {
                maxTileSize = sz;
            }
        }
        final DataBufferInt dbi = new DataBufferInt(maxTileSize);
        final int[] masks = { 16711680, 65280, 255, -16777216 };
        final boolean use_INT_PACK = GraphicsUtil.is_INT_PACK_Data(wr.getSampleModel(), false);
        final Thread currentThread = Thread.currentThread();
        for (final TileBlock curr3 : blocks) {
            final int xloc2 = curr3.getXLoc() * workTileWidth + this.tileGridXOff;
            final int yloc2 = curr3.getYLoc() * workTileHeight + this.tileGridYOff;
            Rectangle tb = new Rectangle(xloc2, yloc2, curr3.getWidth() * workTileWidth, curr3.getHeight() * workTileHeight);
            tb = tb.intersection(this.bounds);
            final Point loc = new Point(tb.x, tb.y);
            final WritableRaster child = Raster.createPackedRaster(dbi, tb.width, tb.height, tb.width, masks, loc);
            this.genRect(child);
            if (use_INT_PACK) {
                GraphicsUtil.copyData_INT_PACK(child, wr);
            }
            else {
                GraphicsUtil.copyData_FALLBACK(child, wr);
            }
            if (HaltingThread.hasBeenHalted(currentThread)) {
                return;
            }
        }
    }
    
    protected void drawBlockInPlace(final TileBlock[] blocks, final WritableRaster wr) {
        final Thread currentThread = Thread.currentThread();
        final int workTileWidth = this.tileWidth;
        final int workTileHeight = this.tileHeight;
        for (final TileBlock curr : blocks) {
            final int xloc = curr.getXLoc() * workTileWidth + this.tileGridXOff;
            final int yloc = curr.getYLoc() * workTileHeight + this.tileGridYOff;
            Rectangle tb = new Rectangle(xloc, yloc, curr.getWidth() * workTileWidth, curr.getHeight() * workTileHeight);
            tb = tb.intersection(this.bounds);
            final WritableRaster child = wr.createWritableChild(tb.x, tb.y, tb.width, tb.height, tb.x, tb.y, null);
            this.genRect(child);
            if (HaltingThread.hasBeenHalted(currentThread)) {
                return;
            }
        }
    }
    
    static {
        AbstractTiledRed.defaultTileSize = 128;
    }
}
