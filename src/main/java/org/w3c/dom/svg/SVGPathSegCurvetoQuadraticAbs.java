// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGPathSegCurvetoQuadraticAbs extends SVGPathSeg
{
    float getX();
    
    void setX(final float p0) throws DOMException;
    
    float getY();
    
    void setY(final float p0) throws DOMException;
    
    float getX1();
    
    void setX1(final float p0) throws DOMException;
    
    float getY1();
    
    void setY1(final float p0) throws DOMException;
}
