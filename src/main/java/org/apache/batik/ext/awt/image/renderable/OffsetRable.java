// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

public interface OffsetRable extends Filter
{
    Filter getSource();
    
    void setSource(final Filter p0);
    
    void setXoffset(final double p0);
    
    double getXoffset();
    
    void setYoffset(final double p0);
    
    double getYoffset();
}
