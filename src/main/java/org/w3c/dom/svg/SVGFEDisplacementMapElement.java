// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

public interface SVGFEDisplacementMapElement extends SVGElement, SVGFilterPrimitiveStandardAttributes
{
    short SVG_CHANNEL_UNKNOWN = 0;
    short SVG_CHANNEL_R = 1;
    short SVG_CHANNEL_G = 2;
    short SVG_CHANNEL_B = 3;
    short SVG_CHANNEL_A = 4;
    
    SVGAnimatedString getIn1();
    
    SVGAnimatedString getIn2();
    
    SVGAnimatedNumber getScale();
    
    SVGAnimatedEnumeration getXChannelSelector();
    
    SVGAnimatedEnumeration getYChannelSelector();
}
