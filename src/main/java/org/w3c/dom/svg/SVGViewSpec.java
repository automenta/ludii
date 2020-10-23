// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

public interface SVGViewSpec extends SVGZoomAndPan, SVGFitToViewBox
{
    SVGTransformList getTransform();
    
    SVGElement getViewTarget();
    
    String getViewBoxString();
    
    String getPreserveAspectRatioString();
    
    String getTransformString();
    
    String getViewTargetString();
}
