// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.IdentifierManager;

public class AlignmentBaselineManager extends IdentifierManager
{
    protected static final StringMap values;
    
    @Override
    public boolean isInheritedProperty() {
        return false;
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
        return "alignment-baseline";
    }
    
    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.AUTO_VALUE;
    }
    
    @Override
    public StringMap getIdentifiers() {
        return AlignmentBaselineManager.values;
    }
    
    static {
        (values = new StringMap()).put("after-edge", SVGValueConstants.AFTER_EDGE_VALUE);
        AlignmentBaselineManager.values.put("alphabetic", SVGValueConstants.ALPHABETIC_VALUE);
        AlignmentBaselineManager.values.put("auto", SVGValueConstants.AUTO_VALUE);
        AlignmentBaselineManager.values.put("baseline", SVGValueConstants.BASELINE_VALUE);
        AlignmentBaselineManager.values.put("before-edge", SVGValueConstants.BEFORE_EDGE_VALUE);
        AlignmentBaselineManager.values.put("hanging", SVGValueConstants.HANGING_VALUE);
        AlignmentBaselineManager.values.put("ideographic", SVGValueConstants.IDEOGRAPHIC_VALUE);
        AlignmentBaselineManager.values.put("mathematical", SVGValueConstants.MATHEMATICAL_VALUE);
        AlignmentBaselineManager.values.put("middle", SVGValueConstants.MIDDLE_VALUE);
        AlignmentBaselineManager.values.put("text-after-edge", SVGValueConstants.TEXT_AFTER_EDGE_VALUE);
        AlignmentBaselineManager.values.put("text-before-edge", SVGValueConstants.TEXT_BEFORE_EDGE_VALUE);
    }
}
