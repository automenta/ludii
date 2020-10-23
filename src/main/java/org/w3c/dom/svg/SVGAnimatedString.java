// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGAnimatedString
{
    String getBaseVal();
    
    void setBaseVal(final String p0) throws DOMException;
    
    String getAnimVal();
}
