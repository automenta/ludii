// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import org.apache.batik.util.CleanerThread;
import java.lang.ref.SoftReference;
import org.apache.batik.util.HaltingThread;
import java.awt.Point;
import java.awt.image.Raster;
import java.util.HashMap;

public class TileMap implements TileStore
{
    private static final boolean DEBUG = false;
    private static final boolean COUNT = false;
    private HashMap rasters;
    private TileGenerator source;
    private LRUCache cache;
    static int requests;
    static int misses;
    
    public TileMap(final TileGenerator source, final LRUCache cache) {
        this.rasters = new HashMap();
        this.source = null;
        this.cache = null;
        this.cache = cache;
        this.source = source;
    }
    
    @Override
    public void setTile(final int x, final int y, final Raster ras) {
        final Point pt = new Point(x, y);
        if (ras == null) {
            final Object o = this.rasters.remove(pt);
            if (o != null) {
                this.cache.remove((LRUCache.LRUObj)o);
            }
            return;
        }
        final Object o = this.rasters.get(pt);
        TileMapLRUMember item;
        if (o == null) {
            item = new TileMapLRUMember(this, pt, ras);
            this.rasters.put(pt, item);
        }
        else {
            item = (TileMapLRUMember)o;
            item.setRaster(ras);
        }
        this.cache.add(item);
    }
    
    @Override
    public Raster getTileNoCompute(final int x, final int y) {
        final Point pt = new Point(x, y);
        final Object o = this.rasters.get(pt);
        if (o == null) {
            return null;
        }
        final TileMapLRUMember item = (TileMapLRUMember)o;
        final Raster ret = item.retrieveRaster();
        if (ret != null) {
            this.cache.add(item);
        }
        return ret;
    }
    
    @Override
    public Raster getTile(final int x, final int y) {
        Raster ras = null;
        final Point pt = new Point(x, y);
        final Object o = this.rasters.get(pt);
        TileMapLRUMember item = null;
        if (o != null) {
            item = (TileMapLRUMember)o;
            ras = item.retrieveRaster();
        }
        if (ras == null) {
            ras = this.source.genTile(x, y);
            if (HaltingThread.hasBeenHalted()) {
                return ras;
            }
            if (item != null) {
                item.setRaster(ras);
            }
            else {
                item = new TileMapLRUMember(this, pt, ras);
                this.rasters.put(pt, item);
            }
        }
        this.cache.add(item);
        return ras;
    }
    
    static class TileMapLRUMember extends TileLRUMember
    {
        public Point pt;
        public SoftReference parent;
        
        TileMapLRUMember(final TileMap parent, final Point pt, final Raster ras) {
            super(ras);
            this.parent = new SoftReference((T)parent);
            this.pt = pt;
        }
        
        @Override
        public void setRaster(final Raster ras) {
            this.hRaster = ras;
            this.wRaster = new RasterSoftRef(ras);
        }
        
        class RasterSoftRef extends CleanerThread.SoftReferenceCleared
        {
            RasterSoftRef(final Object o) {
                super(o);
            }
            
            @Override
            public void cleared() {
                final TileMap tm = TileMapLRUMember.this.parent.get();
                if (tm != null) {
                    tm.rasters.remove(TileMapLRUMember.this.pt);
                }
            }
        }
    }
}
