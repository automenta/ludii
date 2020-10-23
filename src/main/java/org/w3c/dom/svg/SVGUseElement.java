// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.events.EventTarget;

public interface SVGUseElement extends SVGElement, SVGURIReference, SVGTests, SVGLangSpace, SVGExternalResourcesRequired, SVGStylable, SVGTransformable, EventTarget
{
    SVGAnimatedLength getX();
    
    SVGAnimatedLength getY();
    
    SVGAnimatedLength getWidth();
    
    SVGAnimatedLength getHeight();
    
    SVGElementInstance getInstanceRoot();
    
    SVGElementInstance getAnimatedInstanceRoot();
}
