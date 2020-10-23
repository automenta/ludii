// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGRect;

public interface SVGSVGContext extends SVGContext
{
    List getIntersectionList(final SVGRect p0, final Element p1);
    
    List getEnclosureList(final SVGRect p0, final Element p1);
    
    boolean checkIntersection(final Element p0, final SVGRect p1);
    
    boolean checkEnclosure(final Element p0, final SVGRect p1);
    
    void deselectAll();
    
    int suspendRedraw(final int p0);
    
    boolean unsuspendRedraw(final int p0);
    
    void unsuspendRedrawAll();
    
    void forceRedraw();
    
    void pauseAnimations();
    
    void unpauseAnimations();
    
    boolean animationsPaused();
    
    float getCurrentTime();
    
    void setCurrentTime(final float p0);
}
