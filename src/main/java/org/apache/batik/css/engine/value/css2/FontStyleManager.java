// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.IdentifierManager;

public class FontStyleManager extends IdentifierManager
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
        return "font-style";
    }
    
    @Override
    public Value getDefaultValue() {
        return ValueConstants.NORMAL_VALUE;
    }
    
    @Override
    public StringMap getIdentifiers() {
        return FontStyleManager.values;
    }
    
    static {
        (values = new StringMap()).put("all", ValueConstants.ALL_VALUE);
        FontStyleManager.values.put("italic", ValueConstants.ITALIC_VALUE);
        FontStyleManager.values.put("normal", ValueConstants.NORMAL_VALUE);
        FontStyleManager.values.put("oblique", ValueConstants.OBLIQUE_VALUE);
    }
}
