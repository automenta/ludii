// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGAngle
{
    short SVG_ANGLETYPE_UNKNOWN = 0;
    short SVG_ANGLETYPE_UNSPECIFIED = 1;
    short SVG_ANGLETYPE_DEG = 2;
    short SVG_ANGLETYPE_RAD = 3;
    short SVG_ANGLETYPE_GRAD = 4;
    
    short getUnitType();
    
    float getValue();
    
    void setValue(final float p0) throws DOMException;
    
    float getValueInSpecifiedUnits();
    
    void setValueInSpecifiedUnits(final float p0) throws DOMException;
    
    String getValueAsString();
    
    void setValueAsString(final String p0) throws DOMException;
    
    void newValueSpecifiedUnits(final short p0, final float p1);
    
    void convertToSpecifiedUnits(final short p0);
}
