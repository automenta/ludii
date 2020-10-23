// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatableLengthOrIdentValue extends AnimatableLengthValue
{
    protected boolean isIdent;
    protected String ident;
    
    protected AnimatableLengthOrIdentValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableLengthOrIdentValue(final AnimationTarget target, final short type, final float v, final short pcInterp) {
        super(target, type, v, pcInterp);
    }
    
    public AnimatableLengthOrIdentValue(final AnimationTarget target, final String ident) {
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
        return new AnimatableLengthOrIdentValue(this.target, (short)1, 0.0f, this.percentageInterpretation);
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
        AnimatableLengthOrIdentValue res;
        if (result == null) {
            res = new AnimatableLengthOrIdentValue(this.target);
        }
        else {
            res = (AnimatableLengthOrIdentValue)result;
        }
        if (to == null) {
            if (this.isIdent) {
                res.hasChanged = (!res.isIdent || !res.ident.equals(this.ident));
                res.ident = this.ident;
                res.isIdent = true;
            }
            else {
                final short oldLengthType = res.lengthType;
                final float oldLengthValue = res.lengthValue;
                final short oldPercentageInterpretation = res.percentageInterpretation;
                super.interpolate(res, to, interpolation, accumulation, multiplier);
                if (res.lengthType != oldLengthType || res.lengthValue != oldLengthValue || res.percentageInterpretation != oldPercentageInterpretation) {
                    res.hasChanged = true;
                }
            }
        }
        else {
            final AnimatableLengthOrIdentValue toValue = (AnimatableLengthOrIdentValue)to;
            if (this.isIdent || toValue.isIdent) {
                if (interpolation >= 0.5) {
                    if (res.isIdent != toValue.isIdent || res.lengthType != toValue.lengthType || res.lengthValue != toValue.lengthValue || (res.isIdent && toValue.isIdent && !toValue.ident.equals(this.ident))) {
                        res.isIdent = toValue.isIdent;
                        res.ident = toValue.ident;
                        res.lengthType = toValue.lengthType;
                        res.lengthValue = toValue.lengthValue;
                        res.hasChanged = true;
                    }
                }
                else if (res.isIdent != this.isIdent || res.lengthType != this.lengthType || res.lengthValue != this.lengthValue || (res.isIdent && this.isIdent && !res.ident.equals(this.ident))) {
                    res.isIdent = this.isIdent;
                    res.ident = this.ident;
                    res.ident = this.ident;
                    res.lengthType = this.lengthType;
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
