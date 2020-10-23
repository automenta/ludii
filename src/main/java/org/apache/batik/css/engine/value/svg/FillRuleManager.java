// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.IdentifierManager;

public class FillRuleManager extends IdentifierManager
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
        return "fill-rule";
    }
    
    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.NONZERO_VALUE;
    }
    
    @Override
    public StringMap getIdentifiers() {
        return FillRuleManager.values;
    }
    
    static {
        (values = new StringMap()).put("evenodd", SVGValueConstants.EVENODD_VALUE);
        FillRuleManager.values.put("nonzero", SVGValueConstants.NONZERO_VALUE);
    }
}
