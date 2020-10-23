// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGMatrix
{
    float getA();
    
    void setA(final float p0) throws DOMException;
    
    float getB();
    
    void setB(final float p0) throws DOMException;
    
    float getC();
    
    void setC(final float p0) throws DOMException;
    
    float getD();
    
    void setD(final float p0) throws DOMException;
    
    float getE();
    
    void setE(final float p0) throws DOMException;
    
    float getF();
    
    void setF(final float p0) throws DOMException;
    
    SVGMatrix multiply(final SVGMatrix p0);
    
    SVGMatrix inverse() throws SVGException;
    
    SVGMatrix translate(final float p0, final float p1);
    
    SVGMatrix scale(final float p0);
    
    SVGMatrix scaleNonUniform(final float p0, final float p1);
    
    SVGMatrix rotate(final float p0);
    
    SVGMatrix rotateFromVector(final float p0, final float p1) throws SVGException;
    
    SVGMatrix flipX();
    
    SVGMatrix flipY();
    
    SVGMatrix skewX(final float p0);
    
    SVGMatrix skewY(final float p0);
}
