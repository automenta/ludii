// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg12;

public class CIELCHColor extends AbstractCIEColor
{
    public static final String CIE_LCH_COLOR_FUNCTION = "cielch";
    
    public CIELCHColor(final float l, final float c, final float h, final float[] whitepoint) {
        super(new float[] { l, c, h }, whitepoint);
    }
    
    public CIELCHColor(final float l, final float c, final float h) {
        this(l, c, h, null);
    }
    
    @Override
    public String getFunctionName() {
        return "cielch";
    }
}
