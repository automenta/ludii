// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.IdentifierManager;

public class ImageRenderingManager extends IdentifierManager
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
        return "image-rendering";
    }
    
    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.AUTO_VALUE;
    }
    
    @Override
    public StringMap getIdentifiers() {
        return ImageRenderingManager.values;
    }
    
    static {
        (values = new StringMap()).put("auto", SVGValueConstants.AUTO_VALUE);
        ImageRenderingManager.values.put("optimizequality", SVGValueConstants.OPTIMIZEQUALITY_VALUE);
        ImageRenderingManager.values.put("optimizespeed", SVGValueConstants.OPTIMIZESPEED_VALUE);
    }
}
