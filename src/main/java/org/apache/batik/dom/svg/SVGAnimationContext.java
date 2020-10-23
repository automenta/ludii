// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.smil.ElementTimeControl;

public interface SVGAnimationContext extends SVGContext, ElementTimeControl
{
    SVGElement getTargetElement();
    
    float getStartTime();
    
    float getCurrentTime();
    
    float getSimpleDuration();
    
    float getHyperlinkBeginTime();
}
