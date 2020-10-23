// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.util.Map;

public class TileCacheRed extends AbstractTiledRed
{
    public TileCacheRed(final CachableRed cr) {
        super(cr, null);
    }
    
    public TileCacheRed(final CachableRed cr, int tileWidth, int tileHeight) {
        final ColorModel cm = cr.getColorModel();
        final Rectangle bounds = cr.getBounds();
        if (tileWidth > bounds.width) {
            tileWidth = bounds.width;
        }
        if (tileHeight > bounds.height) {
            tileHeight = bounds.height;
        }
        final SampleModel sm = cm.createCompatibleSampleModel(tileWidth, tileHeight);
        this.init(cr, bounds, cm, sm, cr.getTileGridXOffset(), cr.getTileGridYOffset(), null);
    }
    
    @Override
    public void genRect(final WritableRaster wr) {
        final CachableRed src = this.getSources().get(0);
        src.copyData(wr);
    }
    
    public void flushCache(final Rectangle rect) {
        int tx0 = this.getXTile(rect.x);
        int ty0 = this.getYTile(rect.y);
        int tx2 = this.getXTile(rect.x + rect.width - 1);
        int ty2 = this.getYTile(rect.y + rect.height - 1);
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
        final TileStore store = this.getTileStore();
        for (int y = ty0; y <= ty2; ++y) {
            for (int x = tx0; x <= tx2; ++x) {
                store.setTile(x, y, null);
            }
        }
    }
}
