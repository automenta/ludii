// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGTransformList
{
    int getNumberOfItems();
    
    void clear() throws DOMException;
    
    SVGTransform initialize(final SVGTransform p0) throws DOMException, SVGException;
    
    SVGTransform getItem(final int p0) throws DOMException;
    
    SVGTransform insertItemBefore(final SVGTransform p0, final int p1) throws DOMException, SVGException;
    
    SVGTransform replaceItem(final SVGTransform p0, final int p1) throws DOMException, SVGException;
    
    SVGTransform removeItem(final int p0) throws DOMException;
    
    SVGTransform appendItem(final SVGTransform p0) throws DOMException, SVGException;
    
    SVGTransform createSVGTransformFromMatrix(final SVGMatrix p0);
    
    SVGTransform consolidate();
}
