// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

public interface SVGFilterElement extends SVGElement, SVGURIReference, SVGLangSpace, SVGExternalResourcesRequired, SVGStylable, SVGUnitTypes
{
    SVGAnimatedEnumeration getFilterUnits();
    
    SVGAnimatedEnumeration getPrimitiveUnits();
    
    SVGAnimatedLength getX();
    
    SVGAnimatedLength getY();
    
    SVGAnimatedLength getWidth();
    
    SVGAnimatedLength getHeight();
    
    SVGAnimatedInteger getFilterResX();
    
    SVGAnimatedInteger getFilterResY();
    
    void setFilterRes(final int p0, final int p1);
}
