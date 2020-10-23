// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatableNumberValue extends AnimatableValue
{
    protected float value;
    
    protected AnimatableNumberValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableNumberValue(final AnimationTarget target, final float v) {
        super(target);
        this.value = v;
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        AnimatableNumberValue res;
        if (result == null) {
            res = new AnimatableNumberValue(this.target);
        }
        else {
            res = (AnimatableNumberValue)result;
        }
        float v = this.value;
        if (to != null) {
            final AnimatableNumberValue toNumber = (AnimatableNumberValue)to;
            v += interpolation * (toNumber.value - this.value);
        }
        if (accumulation != null) {
            final AnimatableNumberValue accNumber = (AnimatableNumberValue)accumulation;
            v += multiplier * accNumber.value;
        }
        if (res.value != v) {
            res.value = v;
            res.hasChanged = true;
        }
        return res;
    }
    
    public float getValue() {
        return this.value;
    }
    
    @Override
    public boolean canPace() {
        return true;
    }
    
    @Override
    public float distanceTo(final AnimatableValue other) {
        final AnimatableNumberValue o = (AnimatableNumberValue)other;
        return Math.abs(this.value - o.value);
    }
    
    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatableNumberValue(this.target, 0.0f);
    }
    
    @Override
    public String getCssText() {
        return AnimatableValue.formatNumber(this.value);
    }
}
