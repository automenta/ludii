// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.Raster;

public interface TileStore
{
    void setTile(final int p0, final int p1, final Raster p2);
    
    Raster getTile(final int p0, final int p1);
    
    Raster getTileNoCompute(final int p0, final int p1);
}
