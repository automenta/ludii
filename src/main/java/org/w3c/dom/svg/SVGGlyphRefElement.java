// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGGlyphRefElement extends SVGElement, SVGURIReference, SVGStylable
{
    String getGlyphRef();
    
    void setGlyphRef(final String p0) throws DOMException;
    
    String getFormat();
    
    void setFormat(final String p0) throws DOMException;
    
    float getX();
    
    void setX(final float p0) throws DOMException;
    
    float getY();
    
    void setY(final float p0) throws DOMException;
    
    float getDx();
    
    void setDx(final float p0) throws DOMException;
    
    float getDy();
    
    void setDy(final float p0) throws DOMException;
}
