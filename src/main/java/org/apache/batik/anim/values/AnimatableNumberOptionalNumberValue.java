// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatableNumberOptionalNumberValue extends AnimatableValue
{
    protected float number;
    protected boolean hasOptionalNumber;
    protected float optionalNumber;
    
    protected AnimatableNumberOptionalNumberValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableNumberOptionalNumberValue(final AnimationTarget target, final float n) {
        super(target);
        this.number = n;
    }
    
    public AnimatableNumberOptionalNumberValue(final AnimationTarget target, final float n, final float on) {
        super(target);
        this.number = n;
        this.optionalNumber = on;
        this.hasOptionalNumber = true;
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        AnimatableNumberOptionalNumberValue res;
        if (result == null) {
            res = new AnimatableNumberOptionalNumberValue(this.target);
        }
        else {
            res = (AnimatableNumberOptionalNumberValue)result;
        }
        float newNumber;
        float newOptionalNumber;
        boolean newHasOptionalNumber;
        if (to != null && interpolation >= 0.5) {
            final AnimatableNumberOptionalNumberValue toValue = (AnimatableNumberOptionalNumberValue)to;
            newNumber = toValue.number;
            newOptionalNumber = toValue.optionalNumber;
            newHasOptionalNumber = toValue.hasOptionalNumber;
        }
        else {
            newNumber = this.number;
            newOptionalNumber = this.optionalNumber;
            newHasOptionalNumber = this.hasOptionalNumber;
        }
        if (res.number != newNumber || res.hasOptionalNumber != newHasOptionalNumber || res.optionalNumber != newOptionalNumber) {
            res.number = this.number;
            res.optionalNumber = this.optionalNumber;
            res.hasOptionalNumber = this.hasOptionalNumber;
            res.hasChanged = true;
        }
        return res;
    }
    
    public float getNumber() {
        return this.number;
    }
    
    public boolean hasOptionalNumber() {
        return this.hasOptionalNumber;
    }
    
    public float getOptionalNumber() {
        return this.optionalNumber;
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
        if (this.hasOptionalNumber) {
            return new AnimatableNumberOptionalNumberValue(this.target, 0.0f, 0.0f);
        }
        return new AnimatableNumberOptionalNumberValue(this.target, 0.0f);
    }
    
    @Override
    public String getCssText() {
        final StringBuffer sb = new StringBuffer();
        sb.append(AnimatableValue.formatNumber(this.number));
        if (this.hasOptionalNumber) {
            sb.append(' ');
            sb.append(AnimatableValue.formatNumber(this.optionalNumber));
        }
        return sb.toString();
    }
}
