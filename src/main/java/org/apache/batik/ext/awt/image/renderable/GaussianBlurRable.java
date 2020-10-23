// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

public interface GaussianBlurRable extends FilterColorInterpolation
{
    Filter getSource();
    
    void setSource(final Filter p0);
    
    void setStdDeviationX(final double p0);
    
    void setStdDeviationY(final double p0);
    
    double getStdDeviationX();
    
    double getStdDeviationY();
}
