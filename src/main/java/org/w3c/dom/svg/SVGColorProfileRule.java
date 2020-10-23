// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGColorProfileRule extends SVGCSSRule, SVGRenderingIntent
{
    String getSrc();
    
    void setSrc(final String p0) throws DOMException;
    
    String getName();
    
    void setName(final String p0) throws DOMException;
    
    short getRenderingIntent();
    
    void setRenderingIntent(final short p0) throws DOMException;
}
