// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatableStringValue extends AnimatableValue
{
    protected String string;
    
    protected AnimatableStringValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableStringValue(final AnimationTarget target, final String s) {
        super(target);
        this.string = s;
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        AnimatableStringValue res;
        if (result == null) {
            res = new AnimatableStringValue(this.target);
        }
        else {
            res = (AnimatableStringValue)result;
        }
        String newString;
        if (to != null && interpolation >= 0.5) {
            final AnimatableStringValue toValue = (AnimatableStringValue)to;
            newString = toValue.string;
        }
        else {
            newString = this.string;
        }
        if (res.string == null || !res.string.equals(newString)) {
            res.string = newString;
            res.hasChanged = true;
        }
        return res;
    }
    
    public String getString() {
        return this.string;
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
        return new AnimatableStringValue(this.target, "");
    }
    
    @Override
    public String getCssText() {
        return this.string;
    }
}
