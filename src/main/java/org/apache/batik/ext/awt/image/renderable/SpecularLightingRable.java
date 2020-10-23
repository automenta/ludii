// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.Light;

public interface SpecularLightingRable extends FilterColorInterpolation
{
    Filter getSource();
    
    void setSource(final Filter p0);
    
    Light getLight();
    
    void setLight(final Light p0);
    
    double getSurfaceScale();
    
    void setSurfaceScale(final double p0);
    
    double getKs();
    
    void setKs(final double p0);
    
    double getSpecularExponent();
    
    void setSpecularExponent(final double p0);
    
    Rectangle2D getLitRegion();
    
    void setLitRegion(final Rectangle2D p0);
    
    double[] getKernelUnitLength();
    
    void setKernelUnitLength(final double[] p0);
}
