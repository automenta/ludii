// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg12;

import org.apache.xmlgraphics.java2d.color.ColorSpaces;
import org.apache.batik.css.engine.value.AbstractValue;

public abstract class AbstractCIEColor extends AbstractValue
{
    protected float[] values;
    protected float[] whitepoint;
    
    protected AbstractCIEColor(final float[] components, final float[] whitepoint) {
        this.values = new float[3];
        this.whitepoint = ColorSpaces.getCIELabColorSpaceD50().getWhitePoint();
        System.arraycopy(components, 0, this.values, 0, this.values.length);
        if (whitepoint != null) {
            System.arraycopy(whitepoint, 0, this.whitepoint, 0, this.whitepoint.length);
        }
    }
    
    public float[] getColorValues() {
        final float[] copy = new float[3];
        System.arraycopy(this.values, 0, copy, 0, copy.length);
        return copy;
    }
    
    public float[] getWhitePoint() {
        final float[] copy = new float[3];
        System.arraycopy(this.whitepoint, 0, copy, 0, copy.length);
        return copy;
    }
    
    public abstract String getFunctionName();
    
    @Override
    public short getCssValueType() {
        return 3;
    }
    
    @Override
    public String getCssText() {
        final StringBuffer sb = new StringBuffer(this.getFunctionName());
        sb.append('(');
        sb.append(this.values[0]);
        sb.append(", ");
        sb.append(this.values[1]);
        sb.append(", ");
        sb.append(this.values[2]);
        sb.append(')');
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return this.getCssText();
    }
}
