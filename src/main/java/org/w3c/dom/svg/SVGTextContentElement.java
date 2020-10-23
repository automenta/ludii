// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.events.EventTarget;

public interface SVGTextContentElement extends SVGElement, SVGTests, SVGLangSpace, SVGExternalResourcesRequired, SVGStylable, EventTarget
{
    short LENGTHADJUST_UNKNOWN = 0;
    short LENGTHADJUST_SPACING = 1;
    short LENGTHADJUST_SPACINGANDGLYPHS = 2;
    
    SVGAnimatedLength getTextLength();
    
    SVGAnimatedEnumeration getLengthAdjust();
    
    int getNumberOfChars();
    
    float getComputedTextLength();
    
    float getSubStringLength(final int p0, final int p1) throws DOMException;
    
    SVGPoint getStartPositionOfChar(final int p0) throws DOMException;
    
    SVGPoint getEndPositionOfChar(final int p0) throws DOMException;
    
    SVGRect getExtentOfChar(final int p0) throws DOMException;
    
    float getRotationOfChar(final int p0) throws DOMException;
    
    int getCharNumAtPosition(final SVGPoint p0);
    
    void selectSubString(final int p0, final int p1) throws DOMException;
}
