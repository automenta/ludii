// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.ComponentTransferFunction;

public interface ComponentTransferRable extends FilterColorInterpolation
{
    Filter getSource();
    
    void setSource(final Filter p0);
    
    ComponentTransferFunction getAlphaFunction();
    
    void setAlphaFunction(final ComponentTransferFunction p0);
    
    ComponentTransferFunction getRedFunction();
    
    void setRedFunction(final ComponentTransferFunction p0);
    
    ComponentTransferFunction getGreenFunction();
    
    void setGreenFunction(final ComponentTransferFunction p0);
    
    ComponentTransferFunction getBlueFunction();
    
    void setBlueFunction(final ComponentTransferFunction p0);
}
