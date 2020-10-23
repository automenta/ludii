// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGStringList
{
    int getNumberOfItems();
    
    void clear() throws DOMException;
    
    String initialize(final String p0) throws DOMException, SVGException;
    
    String getItem(final int p0) throws DOMException;
    
    String insertItemBefore(final String p0, final int p1) throws DOMException, SVGException;
    
    String replaceItem(final String p0, final int p1) throws DOMException, SVGException;
    
    String removeItem(final int p0) throws DOMException;
    
    String appendItem(final String p0) throws DOMException, SVGException;
}
