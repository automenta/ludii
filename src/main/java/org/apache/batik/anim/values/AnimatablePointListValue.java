// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatablePointListValue extends AnimatableNumberListValue
{
    protected AnimatablePointListValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatablePointListValue(final AnimationTarget target, final float[] numbers) {
        super(target, numbers);
    }
    
    @Override
    public AnimatableValue interpolate(AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        if (result == null) {
            result = new AnimatablePointListValue(this.target);
        }
        return super.interpolate(result, to, interpolation, accumulation, multiplier);
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
        return new AnimatablePointListValue(this.target, ns);
    }
}
