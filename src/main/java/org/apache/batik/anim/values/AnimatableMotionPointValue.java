// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatableMotionPointValue extends AnimatableValue
{
    protected float x;
    protected float y;
    protected float angle;
    
    protected AnimatableMotionPointValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableMotionPointValue(final AnimationTarget target, final float x, final float y, final float angle) {
        super(target);
        this.x = x;
        this.y = y;
        this.angle = angle;
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        AnimatableMotionPointValue res;
        if (result == null) {
            res = new AnimatableMotionPointValue(this.target);
        }
        else {
            res = (AnimatableMotionPointValue)result;
        }
        float newX = this.x;
        float newY = this.y;
        float newAngle = this.angle;
        int angleCount = 1;
        if (to != null) {
            final AnimatableMotionPointValue toValue = (AnimatableMotionPointValue)to;
            newX += interpolation * (toValue.x - this.x);
            newY += interpolation * (toValue.y - this.y);
            newAngle += toValue.angle;
            ++angleCount;
        }
        if (accumulation != null && multiplier != 0) {
            final AnimatableMotionPointValue accValue = (AnimatableMotionPointValue)accumulation;
            newX += multiplier * accValue.x;
            newY += multiplier * accValue.y;
            newAngle += accValue.angle;
            ++angleCount;
        }
        newAngle /= angleCount;
        if (res.x != newX || res.y != newY || res.angle != newAngle) {
            res.x = newX;
            res.y = newY;
            res.angle = newAngle;
            res.hasChanged = true;
        }
        return res;
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    @Override
    public boolean canPace() {
        return true;
    }
    
    @Override
    public float distanceTo(final AnimatableValue other) {
        final AnimatableMotionPointValue o = (AnimatableMotionPointValue)other;
        final float dx = this.x - o.x;
        final float dy = this.y - o.y;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatableMotionPointValue(this.target, 0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public String toStringRep() {
        final StringBuffer sb = new StringBuffer();
        sb.append(AnimatableValue.formatNumber(this.x));
        sb.append(',');
        sb.append(AnimatableValue.formatNumber(this.y));
        sb.append(',');
        sb.append(AnimatableValue.formatNumber(this.angle));
        sb.append("rad");
        return sb.toString();
    }
}
