// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGICCColor
{
    String getColorProfile();
    
    void setColorProfile(final String p0) throws DOMException;
    
    SVGNumberList getColors();
}
