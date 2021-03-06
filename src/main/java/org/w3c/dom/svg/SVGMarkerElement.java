// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

public interface SVGMarkerElement extends SVGElement, SVGLangSpace, SVGExternalResourcesRequired, SVGStylable, SVGFitToViewBox
{
    short SVG_MARKERUNITS_UNKNOWN = 0;
    short SVG_MARKERUNITS_USERSPACEONUSE = 1;
    short SVG_MARKERUNITS_STROKEWIDTH = 2;
    short SVG_MARKER_ORIENT_UNKNOWN = 0;
    short SVG_MARKER_ORIENT_AUTO = 1;
    short SVG_MARKER_ORIENT_ANGLE = 2;
    
    SVGAnimatedLength getRefX();
    
    SVGAnimatedLength getRefY();
    
    SVGAnimatedEnumeration getMarkerUnits();
    
    SVGAnimatedLength getMarkerWidth();
    
    SVGAnimatedLength getMarkerHeight();
    
    SVGAnimatedEnumeration getOrientType();
    
    SVGAnimatedAngle getOrientAngle();
    
    void setOrientToAuto();
    
    void setOrientToAngle(final SVGAngle p0);
}
