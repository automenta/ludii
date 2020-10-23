// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatablePercentageValue extends AnimatableNumberValue
{
    protected AnimatablePercentageValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatablePercentageValue(final AnimationTarget target, final float v) {
        super(target, v);
    }
    
    @Override
    public AnimatableValue interpolate(AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        if (result == null) {
            result = new AnimatablePercentageValue(this.target);
        }
        return super.interpolate(result, to, interpolation, accumulation, multiplier);
    }
    
    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatablePercentageValue(this.target, 0.0f);
    }
    
    @Override
    public String getCssText() {
        return super.getCssText() + "%";
    }
}
