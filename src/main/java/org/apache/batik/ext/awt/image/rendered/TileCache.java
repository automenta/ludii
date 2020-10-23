// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.RenderedImage;

public class TileCache
{
    private static LRUCache cache;
    
    public static void setSize(final int sz) {
        TileCache.cache.setSize(sz);
    }
    
    public static TileStore getTileGrid(final int minTileX, final int minTileY, final int xSz, final int ySz, final TileGenerator src) {
        return new TileGrid(minTileX, minTileY, xSz, ySz, src, TileCache.cache);
    }
    
    public static TileStore getTileGrid(final RenderedImage img, final TileGenerator src) {
        return new TileGrid(img.getMinTileX(), img.getMinTileY(), img.getNumXTiles(), img.getNumYTiles(), src, TileCache.cache);
    }
    
    public static TileStore getTileMap(final TileGenerator src) {
        return new TileMap(src, TileCache.cache);
    }
    
    static {
        TileCache.cache = new LRUCache(50);
    }
}
