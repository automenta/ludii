// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.IdentifierManager;

public class PointerEventsManager extends IdentifierManager
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
        return "pointer-events";
    }
    
    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.VISIBLEPAINTED_VALUE;
    }
    
    @Override
    public StringMap getIdentifiers() {
        return PointerEventsManager.values;
    }
    
    static {
        (values = new StringMap()).put("all", SVGValueConstants.ALL_VALUE);
        PointerEventsManager.values.put("fill", SVGValueConstants.FILL_VALUE);
        PointerEventsManager.values.put("fillstroke", SVGValueConstants.FILLSTROKE_VALUE);
        PointerEventsManager.values.put("none", SVGValueConstants.NONE_VALUE);
        PointerEventsManager.values.put("painted", SVGValueConstants.PAINTED_VALUE);
        PointerEventsManager.values.put("stroke", SVGValueConstants.STROKE_VALUE);
        PointerEventsManager.values.put("visible", SVGValueConstants.VISIBLE_VALUE);
        PointerEventsManager.values.put("visiblefill", SVGValueConstants.VISIBLEFILL_VALUE);
        PointerEventsManager.values.put("visiblefillstroke", SVGValueConstants.VISIBLEFILLSTROKE_VALUE);
        PointerEventsManager.values.put("visiblepainted", SVGValueConstants.VISIBLEPAINTED_VALUE);
        PointerEventsManager.values.put("visiblestroke", SVGValueConstants.VISIBLESTROKE_VALUE);
    }
}
