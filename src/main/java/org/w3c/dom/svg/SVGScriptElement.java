// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGScriptElement extends SVGElement, SVGURIReference, SVGExternalResourcesRequired
{
    String getType();
    
    void setType(final String p0) throws DOMException;
}
