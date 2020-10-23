// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

public interface MorphologyRable extends Filter
{
    Filter getSource();
    
    void setSource(final Filter p0);
    
    void setRadiusX(final double p0);
    
    void setRadiusY(final double p0);
    
    void setDoDilation(final boolean p0);
    
    boolean getDoDilation();
    
    double getRadiusX();
    
    double getRadiusY();
}
