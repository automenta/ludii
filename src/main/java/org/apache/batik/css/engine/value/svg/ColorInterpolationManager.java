// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.IdentifierManager;

public class ColorInterpolationManager extends IdentifierManager
{
    protected static final StringMap values;
    
    @Override
    public boolean isInheritedProperty() {
        return true;
    }
    
    @Override
    public boolean isAnimatableProperty() {
        return true;
    }
    
    @Override
    public boolean isAdditiveProperty() {
        return false;
    }
    
    @Override
    public int getPropertyType() {
        return 15;
    }
    
    @Override
    public String getPropertyName() {
        return "color-interpolation";
    }
    
    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.SRGB_VALUE;
    }
    
    @Override
    public StringMap getIdentifiers() {
        return ColorInterpolationManager.values;
    }
    
    static {
        (values = new StringMap()).put("auto", SVGValueConstants.AUTO_VALUE);
        ColorInterpolationManager.values.put("linearrgb", SVGValueConstants.LINEARRGB_VALUE);
        ColorInterpolationManager.values.put("srgb", SVGValueConstants.SRGB_VALUE);
    }
}
