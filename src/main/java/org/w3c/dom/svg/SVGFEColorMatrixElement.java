// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

public interface SVGFEColorMatrixElement extends SVGElement, SVGFilterPrimitiveStandardAttributes
{
    short SVG_FECOLORMATRIX_TYPE_UNKNOWN = 0;
    short SVG_FECOLORMATRIX_TYPE_MATRIX = 1;
    short SVG_FECOLORMATRIX_TYPE_SATURATE = 2;
    short SVG_FECOLORMATRIX_TYPE_HUEROTATE = 3;
    short SVG_FECOLORMATRIX_TYPE_LUMINANCETOALPHA = 4;
    
    SVGAnimatedString getIn1();
    
    SVGAnimatedEnumeration getType();
    
    SVGAnimatedNumberList getValues();
}
