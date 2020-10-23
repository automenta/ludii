// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

public interface SVGFEGaussianBlurElement extends SVGElement, SVGFilterPrimitiveStandardAttributes
{
    SVGAnimatedString getIn1();
    
    SVGAnimatedNumber getStdDeviationX();
    
    SVGAnimatedNumber getStdDeviationY();
    
    void setStdDeviation(final float p0, final float p1);
}
