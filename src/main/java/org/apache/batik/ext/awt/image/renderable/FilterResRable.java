// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

public interface FilterResRable extends Filter
{
    Filter getSource();
    
    void setSource(final Filter p0);
    
    int getFilterResolutionX();
    
    void setFilterResolutionX(final int p0);
    
    int getFilterResolutionY();
    
    void setFilterResolutionY(final int p0);
}
