// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatableBooleanValue extends AnimatableValue
{
    protected boolean value;
    
    protected AnimatableBooleanValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableBooleanValue(final AnimationTarget target, final boolean b) {
        super(target);
        this.value = b;
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        AnimatableBooleanValue res;
        if (result == null) {
            res = new AnimatableBooleanValue(this.target);
        }
        else {
            res = (AnimatableBooleanValue)result;
        }
        boolean newValue;
        if (to != null && interpolation >= 0.5) {
            final AnimatableBooleanValue toValue = (AnimatableBooleanValue)to;
            newValue = toValue.value;
        }
        else {
            newValue = this.value;
        }
        if (res.value != newValue) {
            res.value = newValue;
            res.hasChanged = true;
        }
        return res;
    }
    
    public boolean getValue() {
        return this.value;
    }
    
    @Override
    public boolean canPace() {
        return false;
    }
    
    @Override
    public float distanceTo(final AnimatableValue other) {
        return 0.0f;
    }
    
    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatableBooleanValue(this.target, false);
    }
    
    @Override
    public String getCssText() {
        return this.value ? "true" : "false";
    }
}
