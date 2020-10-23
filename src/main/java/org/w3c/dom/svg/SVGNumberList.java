// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGNumberList
{
    int getNumberOfItems();
    
    void clear() throws DOMException;
    
    SVGNumber initialize(final SVGNumber p0) throws DOMException, SVGException;
    
    SVGNumber getItem(final int p0) throws DOMException;
    
    SVGNumber insertItemBefore(final SVGNumber p0, final int p1) throws DOMException, SVGException;
    
    SVGNumber replaceItem(final SVGNumber p0, final int p1) throws DOMException, SVGException;
    
    SVGNumber removeItem(final int p0) throws DOMException;
    
    SVGNumber appendItem(final SVGNumber p0) throws DOMException, SVGException;
}
