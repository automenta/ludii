// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGColorProfileElement extends SVGElement, SVGURIReference, SVGRenderingIntent
{
    String getLocal();
    
    void setLocal(final String p0) throws DOMException;
    
    String getName();
    
    void setName(final String p0) throws DOMException;
    
    short getRenderingIntent();
    
    void setRenderingIntent(final short p0) throws DOMException;
}
