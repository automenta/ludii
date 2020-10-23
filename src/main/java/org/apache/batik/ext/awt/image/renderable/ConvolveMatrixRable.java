// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.PadMode;
import java.awt.Point;
import java.awt.image.Kernel;

public interface ConvolveMatrixRable extends FilterColorInterpolation
{
    Filter getSource();
    
    void setSource(final Filter p0);
    
    Kernel getKernel();
    
    void setKernel(final Kernel p0);
    
    Point getTarget();
    
    void setTarget(final Point p0);
    
    double getBias();
    
    void setBias(final double p0);
    
    PadMode getEdgeMode();
    
    void setEdgeMode(final PadMode p0);
    
    double[] getKernelUnitLength();
    
    void setKernelUnitLength(final double[] p0);
    
    boolean getPreserveAlpha();
    
    void setPreserveAlpha(final boolean p0);
}
