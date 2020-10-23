// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.DocumentCSS;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.EventTarget;

public interface SVGSVGElement extends SVGElement, SVGTests, SVGLangSpace, SVGExternalResourcesRequired, SVGStylable, SVGLocatable, SVGFitToViewBox, SVGZoomAndPan, EventTarget, DocumentEvent, ViewCSS, DocumentCSS
{
    SVGAnimatedLength getX();
    
    SVGAnimatedLength getY();
    
    SVGAnimatedLength getWidth();
    
    SVGAnimatedLength getHeight();
    
    String getContentScriptType();
    
    void setContentScriptType(final String p0) throws DOMException;
    
    String getContentStyleType();
    
    void setContentStyleType(final String p0) throws DOMException;
    
    SVGRect getViewport();
    
    float getPixelUnitToMillimeterX();
    
    float getPixelUnitToMillimeterY();
    
    float getScreenPixelToMillimeterX();
    
    float getScreenPixelToMillimeterY();
    
    boolean getUseCurrentView();
    
    void setUseCurrentView(final boolean p0) throws DOMException;
    
    SVGViewSpec getCurrentView();
    
    float getCurrentScale();
    
    void setCurrentScale(final float p0) throws DOMException;
    
    SVGPoint getCurrentTranslate();
    
    int suspendRedraw(final int p0);
    
    void unsuspendRedraw(final int p0) throws DOMException;
    
    void unsuspendRedrawAll();
    
    void forceRedraw();
    
    void pauseAnimations();
    
    void unpauseAnimations();
    
    boolean animationsPaused();
    
    float getCurrentTime();
    
    void setCurrentTime(final float p0);
    
    NodeList getIntersectionList(final SVGRect p0, final SVGElement p1);
    
    NodeList getEnclosureList(final SVGRect p0, final SVGElement p1);
    
    boolean checkIntersection(final SVGElement p0, final SVGRect p1);
    
    boolean checkEnclosure(final SVGElement p0, final SVGRect p1);
    
    void deselectAll();
    
    SVGNumber createSVGNumber();
    
    SVGLength createSVGLength();
    
    SVGAngle createSVGAngle();
    
    SVGPoint createSVGPoint();
    
    SVGMatrix createSVGMatrix();
    
    SVGRect createSVGRect();
    
    SVGTransform createSVGTransform();
    
    SVGTransform createSVGTransformFromMatrix(final SVGMatrix p0);
    
    Element getElementById(final String p0);
}
