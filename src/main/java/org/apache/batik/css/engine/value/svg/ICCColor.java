// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.AbstractValue;

public class ICCColor extends AbstractValue
{
    public static final String ICC_COLOR_FUNCTION = "icc-color";
    protected String colorProfile;
    protected int count;
    protected float[] colors;
    
    public ICCColor(final String name) {
        this.colors = new float[5];
        this.colorProfile = name;
    }
    
    @Override
    public short getCssValueType() {
        return 3;
    }
    
    public String getColorProfile() throws DOMException {
        return this.colorProfile;
    }
    
    public int getNumberOfColors() throws DOMException {
        return this.count;
    }
    
    public float getColor(final int i) throws DOMException {
        return this.colors[i];
    }
    
    @Override
    public String getCssText() {
        final StringBuffer sb = new StringBuffer(this.count * 8);
        sb.append("icc-color").append('(');
        sb.append(this.colorProfile);
        for (int i = 0; i < this.count; ++i) {
            sb.append(", ");
            sb.append(this.colors[i]);
        }
        sb.append(')');
        return sb.toString();
    }
    
    public void append(final float c) {
        if (this.count == this.colors.length) {
            final float[] t = new float[this.count * 2];
            System.arraycopy(this.colors, 0, t, 0, this.count);
            this.colors = t;
        }
        this.colors[this.count++] = c;
    }
    
    @Override
    public String toString() {
        return this.getCssText();
    }
}
