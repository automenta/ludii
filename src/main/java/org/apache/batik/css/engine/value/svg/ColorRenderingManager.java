// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.IdentifierManager;

public class ColorRenderingManager extends IdentifierManager
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
        return "color-rendering";
    }
    
    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.AUTO_VALUE;
    }
    
    @Override
    public StringMap getIdentifiers() {
        return ColorRenderingManager.values;
    }
    
    static {
        (values = new StringMap()).put("auto", SVGValueConstants.AUTO_VALUE);
        ColorRenderingManager.values.put("optimizequality", SVGValueConstants.OPTIMIZEQUALITY_VALUE);
        ColorRenderingManager.values.put("optimizespeed", SVGValueConstants.OPTIMIZESPEED_VALUE);
    }
}
