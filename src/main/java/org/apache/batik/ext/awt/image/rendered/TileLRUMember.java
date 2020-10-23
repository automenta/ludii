// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.lang.ref.SoftReference;
import java.awt.image.Raster;
import java.lang.ref.Reference;

public class TileLRUMember implements LRUCache.LRUObj
{
    private static final boolean DEBUG = false;
    protected LRUCache.LRUNode myNode;
    protected Reference wRaster;
    protected Raster hRaster;
    
    public TileLRUMember() {
        this.myNode = null;
        this.wRaster = null;
        this.hRaster = null;
    }
    
    public TileLRUMember(final Raster ras) {
        this.myNode = null;
        this.wRaster = null;
        this.hRaster = null;
        this.setRaster(ras);
    }
    
    public void setRaster(final Raster ras) {
        this.hRaster = ras;
        this.wRaster = new SoftReference(ras);
    }
    
    public boolean checkRaster() {
        return this.hRaster != null || (this.wRaster != null && this.wRaster.get() != null);
    }
    
    public Raster retrieveRaster() {
        if (this.hRaster != null) {
            return this.hRaster;
        }
        if (this.wRaster == null) {
            return null;
        }
        this.hRaster = this.wRaster.get();
        if (this.hRaster == null) {
            this.wRaster = null;
        }
        return this.hRaster;
    }
    
    @Override
    public LRUCache.LRUNode lruGet() {
        return this.myNode;
    }
    
    @Override
    public void lruSet(final LRUCache.LRUNode nde) {
        this.myNode = nde;
    }
    
    @Override
    public void lruRemove() {
        this.myNode = null;
        this.hRaster = null;
    }
}
