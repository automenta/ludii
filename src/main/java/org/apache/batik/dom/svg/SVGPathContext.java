// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import java.awt.geom.Point2D;

public interface SVGPathContext extends SVGContext
{
    float getTotalLength();
    
    Point2D getPointAtLength(final float p0);
    
    int getPathSegAtLength(final float p0);
}
