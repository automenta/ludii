// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.IdentifierManager;

public class StrokeLinecapManager extends IdentifierManager
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
        return "stroke-linecap";
    }
    
    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.BUTT_VALUE;
    }
    
    @Override
    public StringMap getIdentifiers() {
        return StrokeLinecapManager.values;
    }
    
    static {
        (values = new StringMap()).put("butt", SVGValueConstants.BUTT_VALUE);
        StrokeLinecapManager.values.put("round", SVGValueConstants.ROUND_VALUE);
        StrokeLinecapManager.values.put("square", SVGValueConstants.SQUARE_VALUE);
    }
}
