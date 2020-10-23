// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatableNumberOrIdentValue extends AnimatableNumberValue
{
    protected boolean isIdent;
    protected String ident;
    protected boolean numericIdent;
    
    protected AnimatableNumberOrIdentValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableNumberOrIdentValue(final AnimationTarget target, final float v, final boolean numericIdent) {
        super(target, v);
        this.numericIdent = numericIdent;
    }
    
    public AnimatableNumberOrIdentValue(final AnimationTarget target, final String ident) {
        super(target);
        this.ident = ident;
        this.isIdent = true;
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
        return new AnimatableNumberOrIdentValue(this.target, 0.0f, this.numericIdent);
    }
    
    @Override
    public String getCssText() {
        if (this.isIdent) {
            return this.ident;
        }
        if (this.numericIdent) {
            return Integer.toString((int)this.value);
        }
        return super.getCssText();
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        AnimatableNumberOrIdentValue res;
        if (result == null) {
            res = new AnimatableNumberOrIdentValue(this.target);
        }
        else {
            res = (AnimatableNumberOrIdentValue)result;
        }
        if (to == null) {
            if (this.isIdent) {
                res.hasChanged = (!res.isIdent || !res.ident.equals(this.ident));
                res.ident = this.ident;
                res.isIdent = true;
            }
            else if (this.numericIdent) {
                res.hasChanged = (res.value != this.value || res.isIdent);
                res.value = this.value;
                res.isIdent = false;
                res.hasChanged = true;
                res.numericIdent = true;
            }
            else {
                final float oldValue = res.value;
                super.interpolate(res, to, interpolation, accumulation, multiplier);
                res.numericIdent = false;
                if (res.value != oldValue) {
                    res.hasChanged = true;
                }
            }
        }
        else {
            final AnimatableNumberOrIdentValue toValue = (AnimatableNumberOrIdentValue)to;
            if (this.isIdent || toValue.isIdent || this.numericIdent) {
                if (interpolation >= 0.5) {
                    if (res.isIdent != toValue.isIdent || res.value != toValue.value || (res.isIdent && toValue.isIdent && !toValue.ident.equals(this.ident))) {
                        res.isIdent = toValue.isIdent;
                        res.ident = toValue.ident;
                        res.value = toValue.value;
                        res.numericIdent = toValue.numericIdent;
                        res.hasChanged = true;
                    }
                }
                else if (res.isIdent != this.isIdent || res.value != this.value || (res.isIdent && this.isIdent && !res.ident.equals(this.ident))) {
                    res.isIdent = this.isIdent;
                    res.ident = this.ident;
                    res.value = this.value;
                    res.numericIdent = this.numericIdent;
                    res.hasChanged = true;
                }
            }
            else {
                super.interpolate(res, to, interpolation, accumulation, multiplier);
                res.numericIdent = false;
            }
        }
        return res;
    }
}
