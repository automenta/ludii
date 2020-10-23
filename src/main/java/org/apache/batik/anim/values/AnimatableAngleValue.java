// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatableAngleValue extends AnimatableNumberValue
{
    protected static final String[] UNITS;
    protected short unit;
    
    public AnimatableAngleValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableAngleValue(final AnimationTarget target, final float v, final short unit) {
        super(target, v);
        this.unit = unit;
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        AnimatableAngleValue res;
        if (result == null) {
            res = new AnimatableAngleValue(this.target);
        }
        else {
            res = (AnimatableAngleValue)result;
        }
        float v = this.value;
        short u = this.unit;
        if (to != null) {
            final AnimatableAngleValue toAngle = (AnimatableAngleValue)to;
            if (toAngle.unit != u) {
                v = rad(v, u);
                v += interpolation * (rad(toAngle.value, toAngle.unit) - v);
                u = 3;
            }
            else {
                v += interpolation * (toAngle.value - v);
            }
        }
        if (accumulation != null) {
            final AnimatableAngleValue accAngle = (AnimatableAngleValue)accumulation;
            if (accAngle.unit != u) {
                v += multiplier * rad(accAngle.value, accAngle.unit);
                u = 3;
            }
            else {
                v += multiplier * accAngle.value;
            }
        }
        if (res.value != v || res.unit != u) {
            res.value = v;
            res.unit = u;
            res.hasChanged = true;
        }
        return res;
    }
    
    public short getUnit() {
        return this.unit;
    }
    
    @Override
    public float distanceTo(final AnimatableValue other) {
        final AnimatableAngleValue o = (AnimatableAngleValue)other;
        return Math.abs(rad(this.value, this.unit) - rad(o.value, o.unit));
    }
    
    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatableAngleValue(this.target, 0.0f, (short)1);
    }
    
    @Override
    public String getCssText() {
        return super.getCssText() + AnimatableAngleValue.UNITS[this.unit];
    }
    
    public static float rad(final float v, final short unit) {
        switch (unit) {
            case 3: {
                return v;
            }
            case 4: {
                return 3.1415927f * v / 200.0f;
            }
            default: {
                return 3.1415927f * v / 180.0f;
            }
        }
    }
    
    static {
        UNITS = new String[] { "", "", "deg", "rad", "grad" };
    }
}
