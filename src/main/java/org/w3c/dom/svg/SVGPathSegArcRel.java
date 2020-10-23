// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGPathSegArcRel extends SVGPathSeg
{
    float getX();
    
    void setX(final float p0) throws DOMException;
    
    float getY();
    
    void setY(final float p0) throws DOMException;
    
    float getR1();
    
    void setR1(final float p0) throws DOMException;
    
    float getR2();
    
    void setR2(final float p0) throws DOMException;
    
    float getAngle();
    
    void setAngle(final float p0) throws DOMException;
    
    boolean getLargeArcFlag();
    
    void setLargeArcFlag(final boolean p0) throws DOMException;
    
    boolean getSweepFlag();
    
    void setSweepFlag(final boolean p0) throws DOMException;
}
