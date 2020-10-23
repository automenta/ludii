// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg12;

public class CIELabColor extends AbstractCIEColor
{
    public static final String CIE_LAB_COLOR_FUNCTION = "cielab";
    
    public CIELabColor(final float l, final float a, final float b, final float[] whitepoint) {
        super(new float[] { l, a, b }, whitepoint);
    }
    
    public CIELabColor(final float l, final float a, final float b) {
        this(l, a, b, null);
    }
    
    @Override
    public String getFunctionName() {
        return "cielab";
    }
}
