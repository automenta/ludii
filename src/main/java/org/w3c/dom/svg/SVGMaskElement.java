// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

public interface SVGMaskElement extends SVGElement, SVGTests, SVGLangSpace, SVGExternalResourcesRequired, SVGStylable, SVGUnitTypes
{
    SVGAnimatedEnumeration getMaskUnits();
    
    SVGAnimatedEnumeration getMaskContentUnits();
    
    SVGAnimatedLength getX();
    
    SVGAnimatedLength getY();
    
    SVGAnimatedLength getWidth();
    
    SVGAnimatedLength getHeight();
}
