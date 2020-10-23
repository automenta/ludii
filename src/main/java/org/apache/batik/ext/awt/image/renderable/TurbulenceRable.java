// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.Rectangle2D;

public interface TurbulenceRable extends FilterColorInterpolation
{
    void setTurbulenceRegion(final Rectangle2D p0);
    
    Rectangle2D getTurbulenceRegion();
    
    int getSeed();
    
    double getBaseFrequencyX();
    
    double getBaseFrequencyY();
    
    int getNumOctaves();
    
    boolean isStitched();
    
    boolean isFractalNoise();
    
    void setSeed(final int p0);
    
    void setBaseFrequencyX(final double p0);
    
    void setBaseFrequencyY(final double p0);
    
    void setNumOctaves(final int p0);
    
    void setStitched(final boolean p0);
    
    void setFractalNoise(final boolean p0);
}
