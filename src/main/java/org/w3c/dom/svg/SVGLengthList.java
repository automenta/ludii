// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGLengthList
{
    int getNumberOfItems();
    
    void clear() throws DOMException;
    
    SVGLength initialize(final SVGLength p0) throws DOMException, SVGException;
    
    SVGLength getItem(final int p0) throws DOMException;
    
    SVGLength insertItemBefore(final SVGLength p0, final int p1) throws DOMException, SVGException;
    
    SVGLength replaceItem(final SVGLength p0, final int p1) throws DOMException, SVGException;
    
    SVGLength removeItem(final int p0) throws DOMException;
    
    SVGLength appendItem(final SVGLength p0) throws DOMException, SVGException;
}
