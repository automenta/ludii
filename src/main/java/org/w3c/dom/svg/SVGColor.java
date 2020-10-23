// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.RGBColor;

public interface SVGColor extends CSSValue
{
    short SVG_COLORTYPE_UNKNOWN = 0;
    short SVG_COLORTYPE_RGBCOLOR = 1;
    short SVG_COLORTYPE_RGBCOLOR_ICCCOLOR = 2;
    short SVG_COLORTYPE_CURRENTCOLOR = 3;
    
    short getColorType();
    
    RGBColor getRGBColor();
    
    SVGICCColor getICCColor();
    
    void setRGBColor(final String p0) throws SVGException;
    
    void setRGBColorICCColor(final String p0, final String p1) throws SVGException;
    
    void setColor(final short p0, final String p1, final String p2) throws SVGException;
}
