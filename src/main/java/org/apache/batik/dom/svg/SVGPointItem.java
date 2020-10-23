// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPoint;

public class SVGPointItem extends AbstractSVGItem implements SVGPoint
{
    protected float x;
    protected float y;
    
    public SVGPointItem(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    protected String getStringValue() {
        return Float.toString(this.x) + ',' + Float.toString(this.y);
    }
    
    @Override
    public float getX() {
        return this.x;
    }
    
    @Override
    public float getY() {
        return this.y;
    }
    
    @Override
    public void setX(final float x) {
        this.x = x;
        this.resetAttribute();
    }
    
    @Override
    public void setY(final float y) {
        this.y = y;
        this.resetAttribute();
    }
    
    @Override
    public SVGPoint matrixTransform(final SVGMatrix matrix) {
        return SVGOMPoint.matrixTransform(this, matrix);
    }
}
