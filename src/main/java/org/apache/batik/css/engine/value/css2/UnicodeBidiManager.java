// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.IdentifierManager;

public class UnicodeBidiManager extends IdentifierManager
{
    protected static final StringMap values;
    
    @Override
    public boolean isInheritedProperty() {
        return false;
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
        return "unicode-bidi";
    }
    
    @Override
    public Value getDefaultValue() {
        return ValueConstants.NORMAL_VALUE;
    }
    
    @Override
    public StringMap getIdentifiers() {
        return UnicodeBidiManager.values;
    }
    
    static {
        (values = new StringMap()).put("bidi-override", ValueConstants.BIDI_OVERRIDE_VALUE);
        UnicodeBidiManager.values.put("embed", ValueConstants.EMBED_VALUE);
        UnicodeBidiManager.values.put("normal", ValueConstants.NORMAL_VALUE);
    }
}
