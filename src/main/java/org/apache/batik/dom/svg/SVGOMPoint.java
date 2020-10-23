// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGPoint;

public class SVGOMPoint implements SVGPoint
{
    protected float x;
    protected float y;
    
    public SVGOMPoint() {
    }
    
    public SVGOMPoint(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public float getX() {
        return this.x;
    }
    
    @Override
    public void setX(final float x) throws DOMException {
        this.x = x;
    }
    
    @Override
    public float getY() {
        return this.y;
    }
    
    @Override
    public void setY(final float y) throws DOMException {
        this.y = y;
    }
    
    @Override
    public SVGPoint matrixTransform(final SVGMatrix matrix) {
        return matrixTransform(this, matrix);
    }
    
    public static SVGPoint matrixTransform(final SVGPoint point, final SVGMatrix matrix) {
        final float newX = matrix.getA() * point.getX() + matrix.getC() * point.getY() + matrix.getE();
        final float newY = matrix.getB() * point.getX() + matrix.getD() * point.getY() + matrix.getF();
        return new SVGOMPoint(newX, newY);
    }
}
