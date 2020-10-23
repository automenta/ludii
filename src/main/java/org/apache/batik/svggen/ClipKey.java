// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

class ClipKey
{
    int hashCodeValue;
    
    public ClipKey(final GeneralPath proxiedPath, final SVGGeneratorContext gc) {
        this.hashCodeValue = 0;
        final String pathData = SVGPath.toSVGPathData(proxiedPath, gc);
        this.hashCodeValue = pathData.hashCode();
    }
    
    @Override
    public int hashCode() {
        return this.hashCodeValue;
    }
    
    @Override
    public boolean equals(final Object clipKey) {
        return clipKey instanceof ClipKey && this.hashCodeValue == ((ClipKey)clipKey).hashCodeValue;
    }
}
