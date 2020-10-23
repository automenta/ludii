// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

public interface SVGTransform
{
    short SVG_TRANSFORM_UNKNOWN = 0;
    short SVG_TRANSFORM_MATRIX = 1;
    short SVG_TRANSFORM_TRANSLATE = 2;
    short SVG_TRANSFORM_SCALE = 3;
    short SVG_TRANSFORM_ROTATE = 4;
    short SVG_TRANSFORM_SKEWX = 5;
    short SVG_TRANSFORM_SKEWY = 6;
    
    short getType();
    
    SVGMatrix getMatrix();
    
    float getAngle();
    
    void setMatrix(final SVGMatrix p0);
    
    void setTranslate(final float p0, final float p1);
    
    void setScale(final float p0, final float p1);
    
    void setRotate(final float p0, final float p1, final float p2);
    
    void setSkewX(final float p0);
    
    void setSkewY(final float p0);
}
