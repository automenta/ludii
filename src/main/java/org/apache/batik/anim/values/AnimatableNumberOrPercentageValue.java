// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatableNumberOrPercentageValue extends AnimatableNumberValue
{
    protected boolean isPercentage;
    
    protected AnimatableNumberOrPercentageValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableNumberOrPercentageValue(final AnimationTarget target, final float n) {
        super(target, n);
    }
    
    public AnimatableNumberOrPercentageValue(final AnimationTarget target, final float n, final boolean isPercentage) {
        super(target, n);
        this.isPercentage = isPercentage;
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        AnimatableNumberOrPercentageValue res;
        if (result == null) {
            res = new AnimatableNumberOrPercentageValue(this.target);
        }
        else {
            res = (AnimatableNumberOrPercentageValue)result;
        }
        final AnimatableNumberOrPercentageValue toValue = (AnimatableNumberOrPercentageValue)to;
        final AnimatableNumberOrPercentageValue accValue = (AnimatableNumberOrPercentageValue)accumulation;
        float newValue;
        boolean newIsPercentage;
        if (to != null) {
            if (toValue.isPercentage == this.isPercentage) {
                newValue = this.value + interpolation * (toValue.value - this.value);
                newIsPercentage = this.isPercentage;
            }
            else if (interpolation >= 0.5) {
                newValue = toValue.value;
                newIsPercentage = toValue.isPercentage;
            }
            else {
                newValue = this.value;
                newIsPercentage = this.isPercentage;
            }
        }
        else {
            newValue = this.value;
            newIsPercentage = this.isPercentage;
        }
        if (accumulation != null && accValue.isPercentage == newIsPercentage) {
            newValue += multiplier * accValue.value;
        }
        if (res.value != newValue || res.isPercentage != newIsPercentage) {
            res.value = newValue;
            res.isPercentage = newIsPercentage;
            res.hasChanged = true;
        }
        return res;
    }
    
    public boolean isPercentage() {
        return this.isPercentage;
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
        return new AnimatableNumberOrPercentageValue(this.target, 0.0f, this.isPercentage);
    }
    
    @Override
    public String getCssText() {
        final StringBuffer sb = new StringBuffer();
        sb.append(AnimatableValue.formatNumber(this.value));
        if (this.isPercentage) {
            sb.append('%');
        }
        return sb.toString();
    }
}
