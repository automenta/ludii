// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.events.EventTarget;

public interface SVGRectElement extends SVGElement, SVGTests, SVGLangSpace, SVGExternalResourcesRequired, SVGStylable, SVGTransformable, EventTarget
{
    SVGAnimatedLength getX();
    
    SVGAnimatedLength getY();
    
    SVGAnimatedLength getWidth();
    
    SVGAnimatedLength getHeight();
    
    SVGAnimatedLength getRx();
    
    SVGAnimatedLength getRy();
}
