// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.IdentifierManager;

public class StrokeLinejoinManager extends IdentifierManager
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
        return "stroke-linejoin";
    }
    
    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.MITER_VALUE;
    }
    
    @Override
    public StringMap getIdentifiers() {
        return StrokeLinejoinManager.values;
    }
    
    static {
        (values = new StringMap()).put("miter", SVGValueConstants.MITER_VALUE);
        StrokeLinejoinManager.values.put("round", SVGValueConstants.ROUND_VALUE);
        StrokeLinejoinManager.values.put("bevel", SVGValueConstants.BEVEL_VALUE);
    }
}
