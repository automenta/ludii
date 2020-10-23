// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg12;

import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.AbstractValue;

public class ICCNamedColor extends AbstractValue
{
    public static final String ICC_NAMED_COLOR_FUNCTION = "icc-named-color";
    protected String colorProfile;
    protected String colorName;
    
    public ICCNamedColor(final String profileName, final String colorName) {
        this.colorProfile = profileName;
        this.colorName = colorName;
    }
    
    @Override
    public short getCssValueType() {
        return 3;
    }
    
    public String getColorProfile() throws DOMException {
        return this.colorProfile;
    }
    
    public String getColorName() throws DOMException {
        return this.colorName;
    }
    
    @Override
    public String getCssText() {
        final StringBuffer sb = new StringBuffer("icc-named-color");
        sb.append('(');
        sb.append(this.colorProfile);
        sb.append(", ");
        sb.append(this.colorName);
        sb.append(')');
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return this.getCssText();
    }
}
