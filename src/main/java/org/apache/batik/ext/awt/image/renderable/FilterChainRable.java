// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.Rectangle2D;

public interface FilterChainRable extends Filter
{
    int getFilterResolutionX();
    
    void setFilterResolutionX(final int p0);
    
    int getFilterResolutionY();
    
    void setFilterResolutionY(final int p0);
    
    void setFilterRegion(final Rectangle2D p0);
    
    Rectangle2D getFilterRegion();
    
    void setSource(final Filter p0);
    
    Filter getSource();
}
