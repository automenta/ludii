// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.IdentifierManager;

public class VisibilityManager extends IdentifierManager
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
        return "visibility";
    }
    
    @Override
    public Value getDefaultValue() {
        return ValueConstants.VISIBLE_VALUE;
    }
    
    @Override
    public StringMap getIdentifiers() {
        return VisibilityManager.values;
    }
    
    static {
        (values = new StringMap()).put("visible", ValueConstants.VISIBLE_VALUE);
        VisibilityManager.values.put("hidden", ValueConstants.HIDDEN_VALUE);
        VisibilityManager.values.put("collapse", ValueConstants.COLLAPSE_VALUE);
    }
}
