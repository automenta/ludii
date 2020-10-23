// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.IdentifierManager;

public class DirectionManager extends IdentifierManager
{
    protected static final StringMap values;
    
    @Override
    public boolean isInheritedProperty() {
        return true;
    }
    
    @Override
    public boolean isAnimatableProperty() {
        return false;
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
        return "direction";
    }
    
    @Override
    public Value getDefaultValue() {
        return ValueConstants.LTR_VALUE;
    }
    
    @Override
    public StringMap getIdentifiers() {
        return DirectionManager.values;
    }
    
    static {
        (values = new StringMap()).put("ltr", ValueConstants.LTR_VALUE);
        DirectionManager.values.put("rtl", ValueConstants.RTL_VALUE);
    }
}
