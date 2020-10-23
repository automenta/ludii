// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatableLengthValue extends AnimatableValue
{
    protected static final String[] UNITS;
    protected short lengthType;
    protected float lengthValue;
    protected short percentageInterpretation;
    
    protected AnimatableLengthValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableLengthValue(final AnimationTarget target, final short type, final float v, final short pcInterp) {
        super(target);
        this.lengthType = type;
        this.lengthValue = v;
        this.percentageInterpretation = pcInterp;
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        AnimatableLengthValue res;
        if (result == null) {
            res = new AnimatableLengthValue(this.target);
        }
        else {
            res = (AnimatableLengthValue)result;
        }
        final short oldLengthType = res.lengthType;
        final float oldLengthValue = res.lengthValue;
        final short oldPercentageInterpretation = res.percentageInterpretation;
        res.lengthType = this.lengthType;
        res.lengthValue = this.lengthValue;
        res.percentageInterpretation = this.percentageInterpretation;
        if (to != null) {
            final AnimatableLengthValue toLength = (AnimatableLengthValue)to;
            float toValue;
            if (!compatibleTypes(res.lengthType, res.percentageInterpretation, toLength.lengthType, toLength.percentageInterpretation)) {
                res.lengthValue = this.target.svgToUserSpace(res.lengthValue, res.lengthType, res.percentageInterpretation);
                res.lengthType = 1;
                toValue = toLength.target.svgToUserSpace(toLength.lengthValue, toLength.lengthType, toLength.percentageInterpretation);
            }
            else {
                toValue = toLength.lengthValue;
            }
            final AnimatableLengthValue animatableLengthValue = res;
            animatableLengthValue.lengthValue += interpolation * (toValue - res.lengthValue);
        }
        if (accumulation != null) {
            final AnimatableLengthValue accLength = (AnimatableLengthValue)accumulation;
            float accValue;
            if (!compatibleTypes(res.lengthType, res.percentageInterpretation, accLength.lengthType, accLength.percentageInterpretation)) {
                res.lengthValue = this.target.svgToUserSpace(res.lengthValue, res.lengthType, res.percentageInterpretation);
                res.lengthType = 1;
                accValue = accLength.target.svgToUserSpace(accLength.lengthValue, accLength.lengthType, accLength.percentageInterpretation);
            }
            else {
                accValue = accLength.lengthValue;
            }
            final AnimatableLengthValue animatableLengthValue2 = res;
            animatableLengthValue2.lengthValue += multiplier * accValue;
        }
        if (oldPercentageInterpretation != res.percentageInterpretation || oldLengthType != res.lengthType || oldLengthValue != res.lengthValue) {
            res.hasChanged = true;
        }
        return res;
    }
    
    public static boolean compatibleTypes(final short t1, final short pi1, final short t2, final short pi2) {
        return (t1 == t2 && (t1 != 2 || pi1 == pi2)) || (t1 == 1 && t2 == 5) || (t1 == 5 && t2 == 1);
    }
    
    public int getLengthType() {
        return this.lengthType;
    }
    
    public float getLengthValue() {
        return this.lengthValue;
    }
    
    @Override
    public boolean canPace() {
        return true;
    }
    
    @Override
    public float distanceTo(final AnimatableValue other) {
        final AnimatableLengthValue o = (AnimatableLengthValue)other;
        final float v1 = this.target.svgToUserSpace(this.lengthValue, this.lengthType, this.percentageInterpretation);
        final float v2 = this.target.svgToUserSpace(o.lengthValue, o.lengthType, o.percentageInterpretation);
        return Math.abs(v1 - v2);
    }
    
    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatableLengthValue(this.target, (short)1, 0.0f, this.percentageInterpretation);
    }
    
    @Override
    public String getCssText() {
        return AnimatableValue.formatNumber(this.lengthValue) + AnimatableLengthValue.UNITS[this.lengthType - 1];
    }
    
    static {
        UNITS = new String[] { "", "%", "em", "ex", "px", "cm", "mm", "in", "pt", "pc" };
    }
}
