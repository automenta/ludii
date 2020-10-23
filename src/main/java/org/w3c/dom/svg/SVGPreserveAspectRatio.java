// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGPreserveAspectRatio
{
    short SVG_PRESERVEASPECTRATIO_UNKNOWN = 0;
    short SVG_PRESERVEASPECTRATIO_NONE = 1;
    short SVG_PRESERVEASPECTRATIO_XMINYMIN = 2;
    short SVG_PRESERVEASPECTRATIO_XMIDYMIN = 3;
    short SVG_PRESERVEASPECTRATIO_XMAXYMIN = 4;
    short SVG_PRESERVEASPECTRATIO_XMINYMID = 5;
    short SVG_PRESERVEASPECTRATIO_XMIDYMID = 6;
    short SVG_PRESERVEASPECTRATIO_XMAXYMID = 7;
    short SVG_PRESERVEASPECTRATIO_XMINYMAX = 8;
    short SVG_PRESERVEASPECTRATIO_XMIDYMAX = 9;
    short SVG_PRESERVEASPECTRATIO_XMAXYMAX = 10;
    short SVG_MEETORSLICE_UNKNOWN = 0;
    short SVG_MEETORSLICE_MEET = 1;
    short SVG_MEETORSLICE_SLICE = 2;
    
    short getAlign();
    
    void setAlign(final short p0) throws DOMException;
    
    short getMeetOrSlice();
    
    void setMeetOrSlice(final short p0) throws DOMException;
}
