// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.Rectangle2D;

public interface TileRable extends FilterColorInterpolation
{
    Rectangle2D getTileRegion();
    
    void setTileRegion(final Rectangle2D p0);
    
    Rectangle2D getTiledRegion();
    
    void setTiledRegion(final Rectangle2D p0);
    
    boolean isOverflow();
    
    void setOverflow(final boolean p0);
    
    void setSource(final Filter p0);
    
    Filter getSource();
}
