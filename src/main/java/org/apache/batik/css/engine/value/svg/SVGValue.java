// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.Value;

public interface SVGValue extends Value
{
    short getPaintType() throws DOMException;
    
    String getUri() throws DOMException;
    
    short getColorType() throws DOMException;
    
    String getColorProfile() throws DOMException;
    
    int getNumberOfColors() throws DOMException;
    
    float getColor(final int p0) throws DOMException;
}
