// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGPointList
{
    int getNumberOfItems();
    
    void clear() throws DOMException;
    
    SVGPoint initialize(final SVGPoint p0) throws DOMException, SVGException;
    
    SVGPoint getItem(final int p0) throws DOMException;
    
    SVGPoint insertItemBefore(final SVGPoint p0, final int p1) throws DOMException, SVGException;
    
    SVGPoint replaceItem(final SVGPoint p0, final int p1) throws DOMException, SVGException;
    
    SVGPoint removeItem(final int p0) throws DOMException;
    
    SVGPoint appendItem(final SVGPoint p0) throws DOMException, SVGException;
}
