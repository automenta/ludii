// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import org.apache.batik.util.HaltingThread;
import java.awt.image.Raster;

public class TileGrid implements TileStore
{
    private static final boolean DEBUG = false;
    private static final boolean COUNT = false;
    private int xSz;
    private int ySz;
    private int minTileX;
    private int minTileY;
    private TileLRUMember[][] rasters;
    private TileGenerator source;
    private LRUCache cache;
    static int requests;
    static int misses;
    
    public TileGrid(final int minTileX, final int minTileY, final int xSz, final int ySz, final TileGenerator source, final LRUCache cache) {
        this.rasters = null;
        this.source = null;
        this.cache = null;
        this.cache = cache;
        this.source = source;
        this.minTileX = minTileX;
        this.minTileY = minTileY;
        this.xSz = xSz;
        this.ySz = ySz;
        this.rasters = new TileLRUMember[ySz][];
    }
    
    @Override
    public void setTile(int x, int y, final Raster ras) {
        x -= this.minTileX;
        y -= this.minTileY;
        if (x < 0 || x >= this.xSz) {
            return;
        }
        if (y < 0 || y >= this.ySz) {
            return;
        }
        TileLRUMember[] row = this.rasters[y];
        if (ras != null) {
            TileLRUMember item;
            if (row != null) {
                item = row[x];
                if (item == null) {
                    item = new TileLRUMember();
                    row[x] = item;
                }
            }
            else {
                row = new TileLRUMember[this.xSz];
                item = new TileLRUMember();
                row[x] = item;
                this.rasters[y] = row;
            }
            item.setRaster(ras);
            this.cache.add(item);
            return;
        }
        if (row == null) {
            return;
        }
        TileLRUMember item = row[x];
        if (item == null) {
            return;
        }
        row[x] = null;
        this.cache.remove(item);
    }
    
    @Override
    public Raster getTileNoCompute(int x, int y) {
        x -= this.minTileX;
        y -= this.minTileY;
        if (x < 0 || x >= this.xSz) {
            return null;
        }
        if (y < 0 || y >= this.ySz) {
            return null;
        }
        final TileLRUMember[] row = this.rasters[y];
        if (row == null) {
            return null;
        }
        final TileLRUMember item = row[x];
        if (item == null) {
            return null;
        }
        final Raster ret = item.retrieveRaster();
        if (ret != null) {
            this.cache.add(item);
        }
        return ret;
    }
    
    @Override
    public Raster getTile(int x, int y) {
        x -= this.minTileX;
        y -= this.minTileY;
        if (x < 0 || x >= this.xSz) {
            return null;
        }
        if (y < 0 || y >= this.ySz) {
            return null;
        }
        Raster ras = null;
        TileLRUMember[] row = this.rasters[y];
        TileLRUMember item = null;
        if (row != null) {
            item = row[x];
            if (item != null) {
                ras = item.retrieveRaster();
            }
            else {
                item = new TileLRUMember();
                row[x] = item;
            }
        }
        else {
            row = new TileLRUMember[this.xSz];
            this.rasters[y] = row;
            item = new TileLRUMember();
            row[x] = item;
        }
        if (ras == null) {
            ras = this.source.genTile(x + this.minTileX, y + this.minTileY);
            if (HaltingThread.hasBeenHalted()) {
                return ras;
            }
            item.setRaster(ras);
        }
        this.cache.add(item);
        return ras;
    }
}
