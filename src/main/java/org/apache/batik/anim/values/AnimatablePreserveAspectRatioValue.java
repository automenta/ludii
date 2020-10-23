// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatablePreserveAspectRatioValue extends AnimatableValue
{
    protected static final String[] ALIGN_VALUES;
    protected static final String[] MEET_OR_SLICE_VALUES;
    protected short align;
    protected short meetOrSlice;
    
    protected AnimatablePreserveAspectRatioValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatablePreserveAspectRatioValue(final AnimationTarget target, final short align, final short meetOrSlice) {
        super(target);
        this.align = align;
        this.meetOrSlice = meetOrSlice;
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        AnimatablePreserveAspectRatioValue res;
        if (result == null) {
            res = new AnimatablePreserveAspectRatioValue(this.target);
        }
        else {
            res = (AnimatablePreserveAspectRatioValue)result;
        }
        short newAlign;
        short newMeetOrSlice;
        if (to != null && interpolation >= 0.5) {
            final AnimatablePreserveAspectRatioValue toValue = (AnimatablePreserveAspectRatioValue)to;
            newAlign = toValue.align;
            newMeetOrSlice = toValue.meetOrSlice;
        }
        else {
            newAlign = this.align;
            newMeetOrSlice = this.meetOrSlice;
        }
        if (res.align != newAlign || res.meetOrSlice != newMeetOrSlice) {
            res.align = this.align;
            res.meetOrSlice = this.meetOrSlice;
            res.hasChanged = true;
        }
        return res;
    }
    
    public short getAlign() {
        return this.align;
    }
    
    public short getMeetOrSlice() {
        return this.meetOrSlice;
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
        return new AnimatablePreserveAspectRatioValue(this.target, (short)1, (short)1);
    }
    
    @Override
    public String toStringRep() {
        if (this.align < 1 || this.align > 10) {
            return null;
        }
        final String value = AnimatablePreserveAspectRatioValue.ALIGN_VALUES[this.align];
        if (this.align == 1) {
            return value;
        }
        if (this.meetOrSlice < 1 || this.meetOrSlice > 2) {
            return null;
        }
        return value + ' ' + AnimatablePreserveAspectRatioValue.MEET_OR_SLICE_VALUES[this.meetOrSlice];
    }
    
    static {
        ALIGN_VALUES = new String[] { null, "none", "xMinYMin", "xMidYMin", "xMaxYMin", "xMinYMid", "xMidYMid", "xMaxYMid", "xMinYMax", "xMidYMax", "xMaxYMax" };
        MEET_OR_SLICE_VALUES = new String[] { null, "meet", "slice" };
    }
}
