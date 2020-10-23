// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.Value;

public class ColorInterpolationFiltersManager extends ColorInterpolationManager
{
    @Override
    public String getPropertyName() {
        return "color-interpolation-filters";
    }
    
    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.LINEARRGB_VALUE;
    }
}
