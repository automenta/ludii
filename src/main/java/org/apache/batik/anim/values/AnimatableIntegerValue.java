// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatableIntegerValue extends AnimatableValue
{
    protected int value;
    
    protected AnimatableIntegerValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableIntegerValue(final AnimationTarget target, final int v) {
        super(target);
        this.value = v;
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        AnimatableIntegerValue res;
        if (result == null) {
            res = new AnimatableIntegerValue(this.target);
        }
        else {
            res = (AnimatableIntegerValue)result;
        }
        int v = this.value;
        if (to != null) {
            final AnimatableIntegerValue toInteger = (AnimatableIntegerValue)to;
            v += (int)(this.value + interpolation * (toInteger.getValue() - this.value));
        }
        if (accumulation != null) {
            final AnimatableIntegerValue accInteger = (AnimatableIntegerValue)accumulation;
            v += multiplier * accInteger.getValue();
        }
        if (res.value != v) {
            res.value = v;
            res.hasChanged = true;
        }
        return res;
    }
    
    public int getValue() {
        return this.value;
    }
    
    @Override
    public boolean canPace() {
        return true;
    }
    
    @Override
    public float distanceTo(final AnimatableValue other) {
        final AnimatableIntegerValue o = (AnimatableIntegerValue)other;
        return (float)Math.abs(this.value - o.value);
    }
    
    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatableIntegerValue(this.target, 0);
    }
    
    @Override
    public String getCssText() {
        return Integer.toString(this.value);
    }
}
