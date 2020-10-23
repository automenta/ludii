// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatableNumberListValue extends AnimatableValue
{
    protected float[] numbers;
    
    protected AnimatableNumberListValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableNumberListValue(final AnimationTarget target, final float[] numbers) {
        super(target);
        this.numbers = numbers;
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        final AnimatableNumberListValue toNumList = (AnimatableNumberListValue)to;
        final AnimatableNumberListValue accNumList = (AnimatableNumberListValue)accumulation;
        final boolean hasTo = to != null;
        final boolean hasAcc = accumulation != null;
        final boolean canInterpolate = (!hasTo || toNumList.numbers.length == this.numbers.length) && (!hasAcc || accNumList.numbers.length == this.numbers.length);
        float[] baseValues;
        if (!canInterpolate && hasTo && interpolation >= 0.5) {
            baseValues = toNumList.numbers;
        }
        else {
            baseValues = this.numbers;
        }
        final int len = baseValues.length;
        AnimatableNumberListValue res;
        if (result == null) {
            res = new AnimatableNumberListValue(this.target);
            res.numbers = new float[len];
        }
        else {
            res = (AnimatableNumberListValue)result;
            if (res.numbers == null || res.numbers.length != len) {
                res.numbers = new float[len];
            }
        }
        for (int i = 0; i < len; ++i) {
            float newValue = baseValues[i];
            if (canInterpolate) {
                if (hasTo) {
                    newValue += interpolation * (toNumList.numbers[i] - newValue);
                }
                if (hasAcc) {
                    newValue += multiplier * accNumList.numbers[i];
                }
            }
            if (res.numbers[i] != newValue) {
                res.numbers[i] = newValue;
                res.hasChanged = true;
            }
        }
        return res;
    }
    
    public float[] getNumbers() {
        return this.numbers;
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
        final float[] ns = new float[this.numbers.length];
        return new AnimatableNumberListValue(this.target, ns);
    }
    
    @Override
    public String getCssText() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.numbers[0]);
        for (int i = 1; i < this.numbers.length; ++i) {
            sb.append(' ');
            sb.append(this.numbers[i]);
        }
        return sb.toString();
    }
}
