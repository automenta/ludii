// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.AffineTransform;

public interface AffineRable extends Filter
{
    Filter getSource();
    
    void setSource(final Filter p0);
    
    void setAffine(final AffineTransform p0);
    
    AffineTransform getAffine();
}
