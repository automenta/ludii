// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatableAngleOrIdentValue extends AnimatableAngleValue
{
    protected boolean isIdent;
    protected String ident;
    
    protected AnimatableAngleOrIdentValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableAngleOrIdentValue(final AnimationTarget target, final float v, final short unit) {
        super(target, v, unit);
    }
    
    public AnimatableAngleOrIdentValue(final AnimationTarget target, final String ident) {
        super(target);
        this.ident = ident;
        this.isIdent = true;
    }
    
    public boolean isIdent() {
        return this.isIdent;
    }
    
    public String getIdent() {
        return this.ident;
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
        return new AnimatableAngleOrIdentValue(this.target, 0.0f, (short)1);
    }
    
    @Override
    public String getCssText() {
        if (this.isIdent) {
            return this.ident;
        }
        return super.getCssText();
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        AnimatableAngleOrIdentValue res;
        if (result == null) {
            res = new AnimatableAngleOrIdentValue(this.target);
        }
        else {
            res = (AnimatableAngleOrIdentValue)result;
        }
        if (to == null) {
            if (this.isIdent) {
                res.hasChanged = (!res.isIdent || !res.ident.equals(this.ident));
                res.ident = this.ident;
                res.isIdent = true;
            }
            else {
                final short oldUnit = res.unit;
                final float oldValue = res.value;
                super.interpolate(res, to, interpolation, accumulation, multiplier);
                if (res.unit != oldUnit || res.value != oldValue) {
                    res.hasChanged = true;
                }
            }
        }
        else {
            final AnimatableAngleOrIdentValue toValue = (AnimatableAngleOrIdentValue)to;
            if (this.isIdent || toValue.isIdent) {
                if (interpolation >= 0.5) {
                    if (res.isIdent != toValue.isIdent || res.unit != toValue.unit || res.value != toValue.value || (res.isIdent && toValue.isIdent && !toValue.ident.equals(this.ident))) {
                        res.isIdent = toValue.isIdent;
                        res.ident = toValue.ident;
                        res.unit = toValue.unit;
                        res.value = toValue.value;
                        res.hasChanged = true;
                    }
                }
                else if (res.isIdent != this.isIdent || res.unit != this.unit || res.value != this.value || (res.isIdent && this.isIdent && !res.ident.equals(this.ident))) {
                    res.isIdent = this.isIdent;
                    res.ident = this.ident;
                    res.unit = this.unit;
                    res.value = this.value;
                    res.hasChanged = true;
                }
            }
            else {
                super.interpolate(res, to, interpolation, accumulation, multiplier);
            }
        }
        return res;
    }
}
