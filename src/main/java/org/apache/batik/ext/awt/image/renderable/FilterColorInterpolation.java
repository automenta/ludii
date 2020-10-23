// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.color.ColorSpace;

public interface FilterColorInterpolation extends Filter
{
    boolean isColorSpaceLinear();
    
    void setColorSpaceLinear(final boolean p0);
    
    ColorSpace getOperationColorSpace();
}
