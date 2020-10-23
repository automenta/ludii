// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGStyleElement extends SVGElement
{
    String getXMLspace();
    
    void setXMLspace(final String p0) throws DOMException;
    
    String getType();
    
    void setType(final String p0) throws DOMException;
    
    String getMedia();
    
    void setMedia(final String p0) throws DOMException;
    
    String getTitle();
    
    void setTitle(final String p0) throws DOMException;
}
