// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.Value;

public class GlyphOrientationHorizontalManager extends GlyphOrientationManager
{
    @Override
    public String getPropertyName() {
        return "glyph-orientation-horizontal";
    }
    
    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.ZERO_DEGREE;
    }
}
