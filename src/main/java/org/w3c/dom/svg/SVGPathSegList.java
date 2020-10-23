// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGPathSegList
{
    int getNumberOfItems();
    
    void clear() throws DOMException;
    
    SVGPathSeg initialize(final SVGPathSeg p0) throws DOMException, SVGException;
    
    SVGPathSeg getItem(final int p0) throws DOMException;
    
    SVGPathSeg insertItemBefore(final SVGPathSeg p0, final int p1) throws DOMException, SVGException;
    
    SVGPathSeg replaceItem(final SVGPathSeg p0, final int p1) throws DOMException, SVGException;
    
    SVGPathSeg removeItem(final int p0) throws DOMException;
    
    SVGPathSeg appendItem(final SVGPathSeg p0) throws DOMException, SVGException;
}
