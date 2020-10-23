// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value;

import org.w3c.dom.DOMException;

public class FloatValue extends AbstractValue
{
    protected static final String[] UNITS;
    protected float floatValue;
    protected short unitType;
    
    public static String getCssText(final short unit, final float value) {
        if (unit < 0 || unit >= FloatValue.UNITS.length) {
            throw new DOMException((short)12, "");
        }
        String s = String.valueOf(value);
        if (s.endsWith(".0")) {
            s = s.substring(0, s.length() - 2);
        }
        return s + FloatValue.UNITS[unit - 1];
    }
    
    public FloatValue(final short unitType, final float floatValue) {
        this.unitType = unitType;
        this.floatValue = floatValue;
    }
    
    @Override
    public short getPrimitiveType() {
        return this.unitType;
    }
    
    @Override
    public float getFloatValue() {
        return this.floatValue;
    }
    
    @Override
    public String getCssText() {
        return getCssText(this.unitType, this.floatValue);
    }
    
    @Override
    public String toString() {
        return this.getCssText();
    }
    
    static {
        UNITS = new String[] { "", "%", "em", "ex", "px", "cm", "mm", "in", "pt", "pc", "deg", "rad", "grad", "ms", "s", "Hz", "kHz", "" };
    }
}
