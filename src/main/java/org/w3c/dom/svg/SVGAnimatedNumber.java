// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGAnimatedNumber
{
    float getBaseVal();
    
    void setBaseVal(final float p0) throws DOMException;
    
    float getAnimVal();
}
