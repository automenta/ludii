// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGZoomAndPan
{
    short SVG_ZOOMANDPAN_UNKNOWN = 0;
    short SVG_ZOOMANDPAN_DISABLE = 1;
    short SVG_ZOOMANDPAN_MAGNIFY = 2;
    
    short getZoomAndPan();
    
    void setZoomAndPan(final short p0) throws DOMException;
}
