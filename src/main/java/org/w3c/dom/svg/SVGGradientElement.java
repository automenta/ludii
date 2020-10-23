// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

public interface SVGGradientElement extends SVGElement, SVGURIReference, SVGExternalResourcesRequired, SVGStylable, SVGUnitTypes
{
    short SVG_SPREADMETHOD_UNKNOWN = 0;
    short SVG_SPREADMETHOD_PAD = 1;
    short SVG_SPREADMETHOD_REFLECT = 2;
    short SVG_SPREADMETHOD_REPEAT = 3;
    
    SVGAnimatedEnumeration getGradientUnits();
    
    SVGAnimatedTransformList getGradientTransform();
    
    SVGAnimatedEnumeration getSpreadMethod();
}
