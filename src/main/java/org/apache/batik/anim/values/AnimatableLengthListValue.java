// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatableLengthListValue extends AnimatableValue
{
    protected short[] lengthTypes;
    protected float[] lengthValues;
    protected short percentageInterpretation;
    
    protected AnimatableLengthListValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableLengthListValue(final AnimationTarget target, final short[] types, final float[] values, final short pcInterp) {
        super(target);
        this.lengthTypes = types;
        this.lengthValues = values;
        this.percentageInterpretation = pcInterp;
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        final AnimatableLengthListValue toLengthList = (AnimatableLengthListValue)to;
        final AnimatableLengthListValue accLengthList = (AnimatableLengthListValue)accumulation;
        final boolean hasTo = to != null;
        final boolean hasAcc = accumulation != null;
        final boolean canInterpolate = (!hasTo || toLengthList.lengthTypes.length == this.lengthTypes.length) && (!hasAcc || accLengthList.lengthTypes.length == this.lengthTypes.length);
        short[] baseLengthTypes;
        float[] baseLengthValues;
        if (!canInterpolate && hasTo && interpolation >= 0.5) {
            baseLengthTypes = toLengthList.lengthTypes;
            baseLengthValues = toLengthList.lengthValues;
        }
        else {
            baseLengthTypes = this.lengthTypes;
            baseLengthValues = this.lengthValues;
        }
        final int len = baseLengthTypes.length;
        AnimatableLengthListValue res;
        if (result == null) {
            res = new AnimatableLengthListValue(this.target);
            res.lengthTypes = new short[len];
            res.lengthValues = new float[len];
        }
        else {
            res = (AnimatableLengthListValue)result;
            if (res.lengthTypes == null || res.lengthTypes.length != len) {
                res.lengthTypes = new short[len];
                res.lengthValues = new float[len];
            }
        }
        res.hasChanged = (this.percentageInterpretation != res.percentageInterpretation);
        res.percentageInterpretation = this.percentageInterpretation;
        for (int i = 0; i < len; ++i) {
            float toV = 0.0f;
            float accV = 0.0f;
            short newLengthType = baseLengthTypes[i];
            float newLengthValue = baseLengthValues[i];
            if (canInterpolate) {
                if ((hasTo && !AnimatableLengthValue.compatibleTypes(newLengthType, this.percentageInterpretation, toLengthList.lengthTypes[i], toLengthList.percentageInterpretation)) || (hasAcc && !AnimatableLengthValue.compatibleTypes(newLengthType, this.percentageInterpretation, accLengthList.lengthTypes[i], accLengthList.percentageInterpretation))) {
                    newLengthValue = this.target.svgToUserSpace(newLengthValue, newLengthType, this.percentageInterpretation);
                    newLengthType = 1;
                    if (hasTo) {
                        toV = to.target.svgToUserSpace(toLengthList.lengthValues[i], toLengthList.lengthTypes[i], toLengthList.percentageInterpretation);
                    }
                    if (hasAcc) {
                        accV = accumulation.target.svgToUserSpace(accLengthList.lengthValues[i], accLengthList.lengthTypes[i], accLengthList.percentageInterpretation);
                    }
                }
                else {
                    if (hasTo) {
                        toV = toLengthList.lengthValues[i];
                    }
                    if (hasAcc) {
                        accV = accLengthList.lengthValues[i];
                    }
                }
                newLengthValue += interpolation * (toV - newLengthValue) + multiplier * accV;
            }
            if (!res.hasChanged) {
                res.hasChanged = (newLengthType != res.lengthTypes[i] || newLengthValue != res.lengthValues[i]);
            }
            res.lengthTypes[i] = newLengthType;
            res.lengthValues[i] = newLengthValue;
        }
        return res;
    }
    
    public short[] getLengthTypes() {
        return this.lengthTypes;
    }
    
    public float[] getLengthValues() {
        return this.lengthValues;
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
        final float[] vs = new float[this.lengthValues.length];
        return new AnimatableLengthListValue(this.target, this.lengthTypes, vs, this.percentageInterpretation);
    }
    
    @Override
    public String getCssText() {
        final StringBuffer sb = new StringBuffer();
        if (this.lengthValues.length > 0) {
            sb.append(AnimatableValue.formatNumber(this.lengthValues[0]));
            sb.append(AnimatableLengthValue.UNITS[this.lengthTypes[0] - 1]);
        }
        for (int i = 1; i < this.lengthValues.length; ++i) {
            sb.append(',');
            sb.append(AnimatableValue.formatNumber(this.lengthValues[i]));
            sb.append(AnimatableLengthValue.UNITS[this.lengthTypes[i] - 1]);
        }
        return sb.toString();
    }
}
